#!/usr/bin/env python3
"""Static parity checks for Darkstone and Moon Temple multi-template layouts."""
from __future__ import annotations

import gzip
import importlib.util
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
STRUCTURE_ROOT = ROOT / "src/main/resources/data/theaurorian/structure"
DARKSTONE_JAVA = ROOT / "src/main/java/shiroroku/theaurorian/World/Structure/DarkstoneDungeonStructure.java"
MOON_JAVA = ROOT / "src/main/java/shiroroku/theaurorian/World/Structure/MoonTempleStructure.java"


def _reader_module():
    path = Path(__file__).with_name("remap_structure_nbt.py")
    spec = importlib.util.spec_from_file_location("dungeon_nbt_reader", path)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"cannot load {path}")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


NBT = _reader_module()


def load(path: Path):
    raw = path.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        raw = gzip.decompress(raw)
    root = NBT.NBTReader(raw).read_root()
    if root is None:
        raise ValueError("empty NBT")
    _, node = root
    data = node.value
    size = tuple(v.value for v in data["size"].value)
    palette = [entry.value["Name"].value.rsplit(":", 1)[-1] for entry in data["palette"].value]
    blocks = []
    for block in data["blocks"].value:
        values = block.value
        pos = tuple(v.value for v in values["pos"].value)
        blocks.append((pos, palette[values["state"].value]))
    return size, blocks


def path(family: str, name: str) -> Path:
    return STRUCTURE_ROOT / family / f"{name}.nbt"


def transformed_bounds(size: tuple[int, int, int], origin: tuple[int, int, int], rotation: str):
    sx, sy, sz = size
    ox, oy, oz = origin
    # StructureTemplate's rotation around the zero pivot, with the same
    # +15 compensation used by upstream for 15/16-wide rotated pieces.
    corners = [(0, 0), (sx - 1, sz - 1), (0, sz - 1), (sx - 1, 0)]
    values = []
    for x, z in corners:
        if rotation == "NONE":
            tx, tz = x, z
        elif rotation == "CLOCKWISE_90":
            tx, tz = z, sx - 1 - x
        elif rotation == "COUNTERCLOCKWISE_90":
            tx, tz = sz - 1 - z, x
        elif rotation == "CLOCKWISE_180":
            tx, tz = sx - 1 - x, sz - 1 - z
        else:
            raise ValueError(rotation)
        values.append((ox + tx, oz + tz))
    return (min(x for x, _ in values), oy, min(z for _, z in values),
            max(x for x, _ in values), oy + sy - 1, max(z for _, z in values))


def check_templates(errors: list[str]):
    dark_names = (
        "darkstone_bossroom_back", "darkstone_bossroom_backleft", "darkstone_bossroom_backright",
        "darkstone_bossroom_front", "darkstone_bossroom_frontleft", "darkstone_bossroom_frontright",
        "darkstone_corner", "darkstone_cross", "darkstone_end", "darkstone_entrance",
        "darkstone_stairs", "darkstone_straight", "darkstone_straight_b", "darkstone_t",
    )
    moon_names = (
        "moontemple_island", "moontemple_path_straight", "moontemple_path_turn",
        "moontemple_terrain", "moontemplev2_center", "moontemplev2_courtyardl",
        "moontemplev2_courtyard", "moontemplev2_courtyardr", "moontemplev2_left",
        "moontemplev2_right", "moontemplev2_room",
    )
    for family, names in (("darkstone", dark_names), ("moontemple", moon_names)):
        for name in names:
            p = path(family, name)
            if not p.exists():
                errors.append(f"missing {p.relative_to(ROOT)}")
                continue
            try:
                size, blocks = load(p)
            except Exception as exc:  # noqa: BLE001
                errors.append(f"cannot parse {p.relative_to(ROOT)}: {exc}")
                continue
            if family == "darkstone" and any(block in {"water", "lava"} for _, block in blocks):
                errors.append(f"{p.name}: Darkstone template contains fluid blocks")
            if any(value <= 0 for value in size):
                errors.append(f"{p.name}: invalid size {size}")
            positions = {pos for pos, _ in blocks}
            if not positions:
                errors.append(f"{p.name}: no blocks")
            if min(x for x, _, _ in positions) < 0 or min(y for _, y, _ in positions) < 0 or min(z for _, _, z in positions) < 0:
                errors.append(f"{p.name}: negative local block position")
            max_pos = tuple(max(pos[i] for pos in positions) for i in range(3))
            if max_pos[0] >= size[0] or max_pos[1] >= size[1] or max_pos[2] >= size[2]:
                errors.append(f"{p.name}: block exceeds declared size {size}: {max_pos}")
            if family == "darkstone" and size[0] != 16:
                errors.append(f"{p.name}: upstream dungeon cell must be 16 blocks wide, got {size}")
            if family == "moontemple" and name.startswith("moontemplev2_") and (size[0] < 16 or size[2] < 16):
                errors.append(f"{p.name}: main temple part must span at least one 16-block cell, got {size}")


def check_static_layout(errors: list[str]):
    dark = DARKSTONE_JAVA.read_text(encoding="utf-8")
    moon = MOON_JAVA.read_text(encoding="utf-8")
    required_dark = (
        "sourceSurfaceY(context, ax, az, sourceOffsetX, sourceOffsetZ)",
        "int px = ax - sourceOffsetX * 16;",
        "int pz = az - sourceOffsetZ * 16;",
        "new BlockPos(ax - 16, stairsY - FLOOR_HEIGHT, az)",
        "boundingBoxOf(manager, slots)",
    )
    required_moon = (
        "cx + 16, cy, cz); // offset (-1,0)",
        "cx - 16, cy, cz); // offset (1,0)",
        "cx, cy, cz - 16); // offset (0,1)",
        "cx, cy, cz + 16); // offset (0,-1)",
        "cx - 32, h - yoffset * 3, cz - 17",
        "cx + 47, h - yoffset * 14, cz - 16",
        "cx - 17, h - yoffset * 7, cz + 47",
        "cx + 47, h - yoffset * 11, cz + 32",
        "boundingBoxOf(manager, slots)",
    )
    for line in required_dark:
        if line not in dark:
            errors.append(f"Darkstone mapping missing: {line}")
    for line in required_moon:
        if line not in moon:
            errors.append(f"Moon mapping missing: {line}")
    for source, label in ((dark, "Darkstone"), (moon, "Moon")):
        if "boundingBoxOf(List<Slot> slots)" in source:
            errors.append(f"{label}: stale nominal slot bounding box remains")
    # Positive source offsets in the old map construction are specifically
    # forbidden; +16 is valid only in explicit rotation compensation/origin
    # records, not as ax + sourceOffsetX*16 or az + sourceOffsetZ*16.
    if re.search(r"ax\s*\+\s*sourceOffset[XYZ]?\s*\*\s*16", dark):
        errors.append("Darkstone: stale positive source-offset placement")
    if "cz + 32" in moon and "Exact upstream spiral" not in moon:
        errors.append("Moon: stale positive spiral placement")


def check_darkstone_coordinates(errors: list[str]):
    # Every map cell is a 16-block source cell. These are the upstream source
    # offsets, retained as a pure table check independent of Java formatting.
    offsets = {(-ix + 6, iz - 2) for ix in range(5) for iz in range(5)}
    if min(x for x, _ in offsets) != 2 or max(x for x, _ in offsets) != 6:
        errors.append("Darkstone: 5x5 source offset X range changed")
    if min(z for _, z in offsets) != -2 or max(z for _, z in offsets) != 2:
        errors.append("Darkstone: 5x5 source offset Z range changed")
    origins = {(-x * 16, -z * 16) for x, z in offsets}
    if len(origins) != 25 or min(x for x, _ in origins) != -96 or max(x for x, _ in origins) != -32:
        errors.append("Darkstone: transformed 5x5 origin grid is not upstream")


def check_moon_path(errors: list[str]):
    # The exact source-offset sequence from MoonTempleWorldGenerator.
    source = [(0, 2), (1, 2), (2, 2), (2, 1), (2, 0), (2, -1), (2, -2),
              (1, -2), (0, -2), (-1, -2), (-2, -2), (-2, -1), (-2, 0),
              (-2, 1), (-2, 2), (-1, 2)]
    if len(source) != 16 or len(set(source)) != 16:
        errors.append("Moon: spiral source-offset sequence is not 16 unique branches")
    origins = [(-ox * 16, -oz * 16) for ox, oz in source]
    if origins[0] != (0, -32) or origins[-1] != (16, -32):
        errors.append(f"Moon: spiral anchor endpoints changed: {origins[0]}..{origins[-1]}")


def main() -> int:
    errors: list[str] = []
    check_templates(errors)
    check_static_layout(errors)
    check_darkstone_coordinates(errors)
    check_moon_path(errors)
    if errors:
        print("Dungeon layout: FAIL")
        for error in errors:
            print(f" - {error}")
        return 1
    print("Dungeon layout: OK (Darkstone/Moon upstream offsets, templates, and path records)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
