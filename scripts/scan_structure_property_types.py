#!/usr/bin/env python3
"""扫描/修复结构 NBT 中 palette 方块状态属性的 tag 类型错误。

1.19.2 的 NbtUtils.readBlockState 要求 Properties 的值是 TAG_String；
   早期移植脚本把布尔属性写成了 TAG_Byte（0/1），导致属性被丢弃。

用法：
  python3 scripts/scan_structure_property_types.py <dir_or_file> [--apply]

--apply 时仅把 palette 内 TAG_Byte 的 0/1 转成 "false"/"true"；
其它非 string 类型只报告，不修改。
"""

from __future__ import annotations

import argparse
import gzip
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from remap_structure_nbt import (  # noqa: E402
    NBT,
    NBTReader,
    NBTWriter,
    TAG_COMPOUND,
    write_nbt,
)

BOOL_MAP = {0: "false", 1: "true"}


def load(path: Path) -> tuple[bytes, bool]:
    raw = path.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        return gzip.decompress(raw), True
    return raw, False


def parse(data: bytes):
    name, root = NBTReader(data).read_root()
    if root is None or root.kind != "compound":
        raise ValueError(f"unexpected root: {root.kind if root else None}")
    return name, root


def serialize(name: str, root: NBT) -> bytes:
    w = NBTWriter()
    write_nbt(w, TAG_COMPOUND, name, root)
    return bytes(w.buf)


def iter_palette_props(root: NBT):
    """(palette_index, key, value_node) for every palette[*].Properties entry."""
    pal = root.value.get("palette")
    if pal is None or pal.kind != "list":
        return
    for idx, entry in enumerate(pal.value):
        if entry.kind != "compound":
            continue
        props = entry.value.get("Properties")
        if props is None or props.kind != "compound":
            continue
        for key, val in props.value.items():
            yield idx, key, val


def scan_props_anywhere(root: NBT):
    """报告所有名为 Properties 的 compound（含非 palette 位置）。"""
    found = []

    def walk(node: NBT, where: str):
        if node.kind == "compound":
            for k, v in node.value.items():
                if k == "Properties" and v.kind == "compound":
                    found.append((where, v))
                walk(v, f"{where}/{k}")
        elif node.kind == "list":
            for i, v in enumerate(node.value):
                walk(v, f"{where}[{i}]")

    walk(root, "")
    return found


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("paths", nargs="+", type=Path)
    ap.add_argument("--apply", action="store_true", help="写回修复（默认只扫描）")
    args = ap.parse_args()

    files: list[Path] = []
    for p in args.paths:
        if p.is_dir():
            files.extend(sorted(p.rglob("*.nbt")))
        elif p.is_file():
            files.append(p)
        else:
            print(f"missing: {p}", file=sys.stderr)
            return 2

    bad_total = 0
    other_total = 0
    files_touched = 0
    per_file = []

    for path in files:
        data, compressed = load(path)
        name, root = parse(data)
        # 最强前置断言：无改动 round-trip 必须与原解压字节完全一致
        if serialize(name, root) != data:
            print(f"!! ROUNDTRIP MISMATCH, refuse to touch: {path}")
            return 3

        entries = list(iter_palette_props(root))
        bad = [(i, k, v) for (i, k, v) in entries if v.kind != "string"]
        any_props = scan_props_anywhere(root)
        non_palette = []
        pal_count = sum(
            1
            for e in (root.value.get("palette").value if root.value.get("palette") else [])
            for k, v in (e.value.get("Properties").value.items() if e.kind == "compound" and e.value.get("Properties") and e.value.get("Properties").kind == "compound" else [])
        )
        all_count = sum(len(p.value) for _, p in any_props)
        if all_count != pal_count:
            non_palette.append((all_count, pal_count))

        if bad:
            per_file.append((path, bad))
            bad_total += len(bad)
            for i, k, v in bad:
                if v.kind != "byte":
                    other_total += 1
                print(f"{path}: palette[{i}].Properties.{k} = {v.kind} {v.value!r}")

        if not args.apply:
            continue

        changed = 0
        skipped = []
        for i, k, v in bad:
            if v.kind == "byte" and v.value in BOOL_MAP:
                # 定位并替换为 TAG_String
                node = root.value["palette"].value[i].value["Properties"].value
                node[k] = NBT("string", BOOL_MAP[v.value])
                changed += 1
            else:
                skipped.append((i, k, v.kind, v.value))

        if changed == 0:
            continue

        new_data = serialize(name, root)
        delta = len(new_data) - len(data)
        expected_delta = 0
        for i, k, v in bad:
            if v.kind == "byte" and v.value in BOOL_MAP:
                # TAG_Byte 负载 1 字节；TAG_String 负载 2 + len
                expected_delta += (2 + len(BOOL_MAP[v.value])) - 1
        if delta != expected_delta:
            print(f"!! DELTA MISMATCH {path}: got {delta}, expected {expected_delta}")
            return 4

        out = gzip.compress(new_data) if compressed else new_data
        path.write_bytes(out)

        # 写后验证：重新解析，目标属性均为 string；并与预期序列化逐字节比对
        reread_data, _ = load(path)
        if reread_data != new_data:
            print(f"!! POST-WRITE DECOMPRESSED MISMATCH: {path}")
            return 5
        rname, rroot = parse(reread_data)
        for i, k, v in bad:
            if v.kind == "byte" and v.value in BOOL_MAP:
                nv = rroot.value["palette"].value[i].value["Properties"].value[k]
                assert nv.kind == "string" and nv.value == BOOL_MAP[v.value], (path, i, k, nv)
        if skipped:
            for s in skipped:
                print(f"  SKIP (non-byte, not modified): {path}: palette[{s[0]}].Properties.{s[1]} {s[2]} {s[3]!r}")
        files_touched += 1
        print(f"FIXED {path}: {changed} props, decompressed {len(data)} -> {len(new_data)} (delta {delta})")

    print(
        f"\nSUMMARY files={len(files)} nonstring_palette_props={bad_total} "
        f"non_byte={other_total} files_touched={files_touched} apply={args.apply}"
    )
    for path, bad in per_file:
        kinds = {}
        for _, _, v in bad:
            kinds[v.kind] = kinds.get(v.kind, 0) + 1
        print(f"  {path.relative_to(args.paths[0] if args.paths[0].is_dir() else args.paths[0].parent)}: {kinds}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
