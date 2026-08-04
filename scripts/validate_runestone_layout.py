#!/usr/bin/env python3
"""Validate the Runestone Tower's four-template geometry.

The 1.12 generator stores each 15x15 quadrant in a different source chunk.
This check reconstructs the BR-anchor origins (upstream same-name), the
outward-facing solid L-edges, and floor2/top opposite+180 rotations.
"""
from __future__ import annotations

import gzip
import importlib.util
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
STRUCTURE_ROOT = ROOT / "src/main/resources/data/theaurorian/structure/runestone/upstream_ref"
JAVA_FILE = ROOT / "src/main/java/shiroroku/theaurorian/World/Structure/RunestoneDungeonStructure.java"
STRUCTURE_SET_FILE = ROOT / "src/main/resources/data/theaurorian/worldgen/structure_set/major_dungeons.json"
QUADS = ("tl", "tr", "bl", "br")
OPPOSITE = {"tl": "br", "tr": "bl", "bl": "tr", "br": "tl"}
# Physical slots relative to the BR density anchor (x, z) — upstream place pos:
#   TR (x-15, z+1), TL (x-15, z+16), BR (x, z+1), BL (x, z+16)
SLOT_OFFSETS = {
    "tr": (-15, 1),
    "tl": (-15, 16),
    "br": (0, 1),
    "bl": (0, 16),
}
# Template file name equals the physical slot key (upstream same-name).
SLOT_TEMPLATES = {"tl": "tl", "tr": "tr", "bl": "bl", "br": "br"}
HEIGHTS = {"terrain": 1, "base": 6, "floor": 6, "floor_2": 12, "top": 15}


def _reader_module():
    path = Path(__file__).with_name("remap_structure_nbt.py")
    spec = importlib.util.spec_from_file_location("runestone_nbt_reader", path)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"cannot load {path}")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


NBT = _reader_module()


def _load(path: Path):
    raw = path.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        raw = gzip.decompress(raw)
    root = NBT.NBTReader(raw).read_root()
    if root is None:
        raise ValueError("empty NBT")
    _, node = root
    data = node.value
    size = tuple(v.value for v in data["size"].value)
    positions = set()
    for block in data["blocks"].value:
        pos = block.value["pos"].value
        positions.add(tuple(v.value for v in pos))
    return size, positions


def _path(kind: str, quad: str) -> Path:
    suffix = "" if kind == "terrain" else "v2"
    return STRUCTURE_ROOT / f"runestonetower_{kind}_{quad}{suffix}.nbt"


def _load_states(path: Path, y: int):
    raw = path.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        raw = gzip.decompress(raw)
    root = NBT.NBTReader(raw).read_root()
    if root is None:
        raise ValueError("empty NBT")
    _, node = root
    data = node.value
    palette = [entry.value["Name"].value.rsplit(":", 1)[-1] for entry in data["palette"].value]
    result = {}
    for block in data["blocks"].value:
        values = block.value
        pos = values["pos"].value
        if pos[1].value == y:
            result[(pos[0].value, pos[2].value)] = palette[values["state"].value]
    return result


def _radial_edges(quad: str):
    """Edges that are nearly fully solid (the two legs of each L)."""
    states = _load_states(_path("floor", quad), 1)
    occupied = {(x, z) for (x, z), name in states.items() if name != "air"}
    edges = {
        "N": {(x, 0) for x in range(15)},
        "E": {(14, z) for z in range(15)},
        "S": {(x, 14) for x in range(15)},
        "W": {(0, z) for z in range(15)},
    }
    return {side for side, cells in edges.items() if len(occupied & cells) >= 13}


def _transformed(kind: str, physical_quad: str):
    """Place unrotated same-name templates at the physical slot origins."""
    template_quad = SLOT_TEMPLATES[physical_quad]
    size, positions = _load(_path(kind, template_quad))
    ox, oz = SLOT_OFFSETS[physical_quad]
    result = {(ox + x, y, oz + z) for x, y, z in positions}
    return size, result


def validate() -> list[str]:
    errors: list[str] = []

    for kind, height in HEIGHTS.items():
        for quad in QUADS:
            path = _path(kind, quad)
            if not path.exists():
                errors.append(f"missing {path.relative_to(ROOT)}")
                continue
            try:
                size, positions = _load(path)
            except Exception as exc:  # noqa: BLE001
                errors.append(f"cannot parse {path.relative_to(ROOT)}: {exc}")
                continue
            if size != (15, height, 15):
                errors.append(f"{path.name}: expected size (15,{height},15), got {size}")
            local_xz = {(x, 0, z) for x, _, z in positions}
            if len(positions) != 15 * height * 15:
                errors.append(f"{path.name}: expected full template block grid, got {len(positions)} blocks")
            if not {(x, 0, z) for x in range(15) for z in range(15)} <= local_xz:
                errors.append(f"{path.name}: block positions do not cover the complete 15x15 footprint")

        # Combined transformed footprint for unrotated kinds
        if kind in ("terrain", "base", "floor"):
            combined = set()
            for quad in QUADS:
                try:
                    _, blocks = _transformed(kind, quad)
                except Exception as exc:  # noqa: BLE001
                    errors.append(f"cannot transform {kind}/{quad}: {exc}")
                    continue
                combined.update(blocks)
            if combined:
                xs = {x for x, _, _ in combined}
                zs = {z for _, _, z in combined}
                if (min(xs), max(xs)) != (-15, 14):
                    errors.append(f"{kind}: transformed x extent should be -15..14, got {min(xs)}..{max(xs)}")
                if (min(zs), max(zs)) != (1, 30):
                    errors.append(f"{kind}: transformed z extent should be 1..30, got {min(zs)}..{max(zs)}")

    # Solid L-legs face outward so the four pieces form one ring.
    # TR NW: N+W, TL SW: S+W, BR NE: N+E, BL SE: S+E
    expected_radial = {"tr": {"N", "W"}, "tl": {"S", "W"}, "br": {"N", "E"}, "bl": {"S", "E"}}
    for physical, expected in expected_radial.items():
        try:
            actual = _radial_edges(SLOT_TEMPLATES[physical])
        except Exception as exc:  # noqa: BLE001
            errors.append(f"cannot inspect radial edges for {physical}: {exc}")
            continue
        if not expected <= actual:
            errors.append(f"{physical}: expected outward solid edges {sorted(expected)}, got {sorted(actual)}")

    if STRUCTURE_SET_FILE.exists():
        try:
            data = json.loads(STRUCTURE_SET_FILE.read_text(encoding="utf-8"))
            placement = data["placement"]
            if placement.get("type") != "minecraft:random_spread":
                errors.append("major_dungeons structure_set must use minecraft:random_spread")
            if placement.get("spacing") != 32 or placement.get("separation") != 31:
                errors.append("major_dungeons structure_set must use spacing=32 and separation=31")
            structures = data.get("structures") or []
            runestone_entries = [
                entry for entry in structures
                if isinstance(entry, dict) and entry.get("structure") == "theaurorian:runestone_dungeon"
            ]
            if len(runestone_entries) != 1 or runestone_entries[0].get("weight") != 1:
                errors.append("major_dungeons structure_set must contain one weight-1 Runestone entry")
        except Exception as exc:  # noqa: BLE001
            errors.append(f"cannot parse {STRUCTURE_SET_FILE.relative_to(ROOT)}: {exc}")
    else:
        errors.append(f"missing {STRUCTURE_SET_FILE.relative_to(ROOT)}")

    if not JAVA_FILE.exists():
        errors.append(f"missing {JAVA_FILE.relative_to(ROOT)}")
    else:
        java = JAVA_FILE.read_text(encoding="utf-8")
        required = (
            'int y0 = surfaceY(context, x + 15, z + 16);',
            'if ((floors & 1) != 0) {',
            'addQuadrant(slots, x - 15, y0, z + 1, "tr", false, floors);',
            'addQuadrant(slots, x - 15, y0, z + 16, "tl", false, floors);',
            'addQuadrant(slots, x, y0, z + 1, "br", true, floors);',
            'addQuadrant(slots, x, y0, z + 16, "bl", false, floors);',
            'String floor2 = REF + "runestonetower_floor_2_" + opposite(pieceQuad) + "v2";',
            'String topAlt = REF + "runestonetower_top_" + opposite(pieceQuad) + "v2";',
        )
        for line in required:
            if line not in java:
                errors.append(f"Java mapping missing: {line}")
        for bad in (
            "x + 17",
            'z + 16, "tr"',
            'z + 1, "bl"',
            'z + 16, "br"',
            'z + 1, "tl"',
            'z - 16',
            'if ((floors & 1) == 0) {',
            'pieceQuad, true, floors);       // was',
            "NW <- bl",
        ):
            if bad in java:
                errors.append(f"obsolete Runestone mapping remains: {bad}")

    return errors


def main() -> int:
    errors = validate()
    if errors:
        print("Runestone layout: FAIL")
        for error in errors:
            print(f" - {error}")
        return 1
    print("Runestone layout: OK (20 templates, upstream same-name, outward L-edges)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
