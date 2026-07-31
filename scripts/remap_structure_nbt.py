#!/usr/bin/env python3
"""Remap 1.12 The Aurorian structure NBT block/item/entity IDs to 1.19 names.

This is a proper NBT parser/serializer: string values are rewritten and
re-encoded with correct length prefixes (raw byte substitution corrupts NBT).

Usage:
  python3 scripts/remap_structure_nbt.py --dry-run path/to/dir_or_file.nbt
  python3 scripts/remap_structure_nbt.py src/main/resources/data/theaurorian/structures/darkstone
"""

from __future__ import annotations

import argparse
import gzip
import struct
import sys
from pathlib import Path

# Longer keys sort first within a length-group pass; the dict is applied in
# descending key length so partial clobber (aurorianstone inside
# aurorianstonebrick) can't happen.
BLOCK_REMAP: dict[str, str] = {
    # terrain / wood
    "theaurorian:auroriancobblestonestairs": "theaurorian:aurorian_cobblestone_stairs",
    "theaurorian:aurorianperidotitesmoothstairs": "theaurorian:peridotite_smooth_stairs",
    "theaurorian:aurorianperidotitesmooth": "theaurorian:peridotite_smooth",
    "theaurorian:aurorianperidotite": "theaurorian:peridotite",
    "theaurorian:aurorianperidotite": "theaurorian:peridotite",
    "theaurorian:auroriancobblestone": "theaurorian:aurorian_cobblestone",
    "theaurorian:aurorianfurnacechimney": "theaurorian:chimney",
    "theaurorian:aurorianfurnace": "theaurorian:aurorian_furnace",
    "theaurorian:auroriantallgrasslight": "theaurorian:aurorian_tallgrass_light",
    "theaurorian:auroriantallgrass": "theaurorian:aurorian_tallgrass",
    "theaurorian:auroriangrasslight": "theaurorian:aurorian_grass_light",
    "theaurorian:auroriangrass": "theaurorian:aurorian_grass",
    "theaurorian:aurorianstonestairs": "theaurorian:aurorian_stone_stairs",
    "theaurorian:aurorianstonebrick": "theaurorian:aurorian_stone_brick",
    "theaurorian:aurorianstone": "theaurorian:aurorian_stone",
    "theaurorian:auroriandirt": "theaurorian:aurorian_dirt",
    "theaurorian:silentwoodstairs": "theaurorian:silentwood_stairs",
    "theaurorian:silentwoodplanks": "theaurorian:silentwood_planks",
    "theaurorian:silentwoodleaves": "theaurorian:silentwood_leaves",
    "theaurorian:silentwoodchest": "theaurorian:silentwood_chest",
    "theaurorian:silentwoodladder": "theaurorian:silentwood_ladder",
    "theaurorian:silentwoodtorch": "theaurorian:silentwood_torch",
    "theaurorian:silentwoodlog": "theaurorian:silentwood_log",
    "theaurorian:aurorianfarmtile": "theaurorian:aurorian_farm_tile",
    # darkstone
    "theaurorian:darkstonegatekeyhole": "theaurorian:darkstone_gate_keyhole",
    "theaurorian:darkstonegate": "theaurorian:darkstone_gate",
    "theaurorian:darkstonestairs": "theaurorian:darkstone_stairs",
    "theaurorian:darkstonelayers": "theaurorian:darkstone_pillar",
    "theaurorian:darkstonelamp": "theaurorian:darkstone_lamp",
    "theaurorian:darkstonefancy": "theaurorian:darkstone_chipped",
    "theaurorian:darkstonebricks": "theaurorian:darkstone",
    # moon temple
    "theaurorian:moontemplecellgatekeyhole": "theaurorian:moon_temple_interior_gate_keyhole",
    "theaurorian:moontemplegatekeyhole": "theaurorian:moon_temple_gate_keyhole",
    "theaurorian:moontemplebrickssmooth": "theaurorian:moon_temple_bricks_smooth",
    "theaurorian:moontemplecellgate": "theaurorian:moon_temple_interior_gate",
    "theaurorian:moontemplestairs": "theaurorian:moon_temple_stairs",
    "theaurorian:moontemplebricks": "theaurorian:moon_temple_bricks",
    "theaurorian:moontemplebars": "theaurorian:moon_temple_bars",
    "theaurorian:moontemplegate": "theaurorian:moon_temple_gate",
    "theaurorian:moontemplelamp": "theaurorian:moon_temple_lamp",
    "theaurorian:moonglasspane": "theaurorian:moon_glass_pane",
    "theaurorian:moonglass": "theaurorian:moon_glass",
    "theaurorian:moongem": "theaurorian:moon_gem",
    "theaurorian:moontorch": "theaurorian:moon_torch",
    "theaurorian:moonsand": "theaurorian:moon_sand",
    # runestone (upstream_ref)
    "theaurorian:runestonelootgatekeyhole": "theaurorian:runestone_gate_loot_keyhole",
    "theaurorian:runestonegatekeyhole": "theaurorian:runestone_gate_keyhole",
    "theaurorian:runestonelootgate": "theaurorian:runestone_gate",
    "theaurorian:runestonesmooth": "theaurorian:runestone_smooth",
    "theaurorian:runestonestairs": "theaurorian:runestone_stairs",
    "theaurorian:runestonebars": "theaurorian:runestone_bars",
    "theaurorian:runestonegate": "theaurorian:runestone_gate",
    "theaurorian:runestonelamp": "theaurorian:runestone_lamp",
    # umbra / glass / misc
    "theaurorian:umbrastoneroofstairs": "theaurorian:umbra_stone_roof_stairs",
    "theaurorian:umbrastonerooftiles": "theaurorian:umbra_stone_roof_tiles",
    "theaurorian:umbrastonecracked": "theaurorian:umbra_stone_cracked",
    "theaurorian:umbrastone": "theaurorian:umbra_stone",
    "theaurorian:aurorianglasspane": "theaurorian:aurorian_glass_pane",
    "theaurorian:aurorianglass": "theaurorian:aurorian_glass",
    "theaurorian:auroriancoalblock": "theaurorian:aurorian_coal_block",
    "theaurorian:auroriansteelblock": "theaurorian:aurorian_steel_block",
    "theaurorian:ceruleanblock": "theaurorian:cerulean_block",
    "theaurorian:moonstoneblock": "theaurorian:moonstone_block",
    "theaurorian:lavendercrop": "theaurorian:lavender_crop",
    "theaurorian:silkberrycrop": "theaurorian:silkberry_crop",
    "theaurorian:mysticalbarrier": "theaurorian:fog_wall",
    "theaurorian:lavenderplant": "theaurorian:lavender_block",
    "theaurorian:petuniaplant": "theaurorian:petunia",
    "theaurorian:silkberryplant": "theaurorian:silkberry_block",
    "theaurorian:urn": "theaurorian:urn",
    # boss spawners → unified block (BE boss tag still needs care)
    "theaurorian:bossspawnerkeeper": "theaurorian:boss_spawner",
    "theaurorian:bossspawnermoonqueen": "theaurorian:boss_spawner",
    "theaurorian:bossspawnerspider": "theaurorian:boss_spawner",
    "theaurorian:bossspawner": "theaurorian:boss_spawner",
    # vanilla
    "minecraft:mob_spawner": "minecraft:spawner",
    "minecraft:web": "minecraft:cobweb",
    # entities referenced in spawners / structure
    "theaurorian:undeadknight": "theaurorian:undead_knight",
    "theaurorian:aurorianslime": "theaurorian:dungeon_slime",
    "theaurorian:spiderling": "theaurorian:spiderling",
    "theaurorian:spirit": "theaurorian:spirit",
    # chest loot paths (1.12 → nested 1.19)
    "theaurorian:chests/darkstonelow": "theaurorian:chests/darkstone/low",
    "theaurorian:chests/darkstonemed": "theaurorian:chests/darkstone/med",
    "theaurorian:chests/darkstonehigh": "theaurorian:chests/darkstone/high",
    "theaurorian:chests/moontemplelow": "theaurorian:chests/moontemple/low",
    "theaurorian:chests/moontemplemed": "theaurorian:chests/moontemple/med",
    "theaurorian:chests/moontemplehigh": "theaurorian:chests/moontemple/high",
    "theaurorian:chests/ruins": "theaurorian:chests/ruins/common",
    # items inside NBT chest loot / spawners
    "theaurorian:auroriancoalnugget": "theaurorian:aurorian_coal_nugget",
    "theaurorian:moontemplecellkeyfragment": "theaurorian:moon_temple_key_fragment",
    # moon water fluid (safe worldgen substitute until a fluid is ported)
    "theaurorian:tamoonwater": "minecraft:water",
}

# 1.12 BossSpawner block stored the boss entity name under "containedboss".
# Transform -> key "boss" + full registered id.
BOSS_CONTAINED: dict[str, str] = {
    "spider": "theaurorian:dungeon_spider",
    "moonqueen": "theaurorian:moon_queen",
    "keeper": "theaurorian:dungeon_keeper",
}


# ---------------------------------------------------------------------------
# NBT reader / writer
# ---------------------------------------------------------------------------

class NBTWriter:
    def __init__(self):
        self.buf = bytearray()

    def tag(self, t):
        self.buf.append(t)

    def name(self, s: str):
        b = s.encode("utf-8")
        self.buf += struct.pack(">H", len(b))
        self.buf += b

    def byte(self, v): self.buf += struct.pack(">b", v)
    def short(self, v): self.buf += struct.pack(">h", v)
    def int(self, v): self.buf += struct.pack(">i", v)
    def long(self, v): self.buf += struct.pack(">q", v)
    def float(self, v): self.buf += struct.pack(">f", v)
    def double(self, v): self.buf += struct.pack(">d", v)
    def string(self, v): self.name(v)
    def bytes_(self, b): self.buf += b


def remap_str(s: str) -> tuple[str, bool]:
    """Return (possibly mapped string, changed). Exact value match only."""
    if not isinstance(s, str):
        return s, False
    for old, new in sorted(BLOCK_REMAP.items(), key=lambda kv: -len(kv[0])):
        if old == s:
            return new, True
    return s, False


class NBT:
    """Parsed NBT value. Kind: 'compound'|'list'|'string'|'int'|'long'|
    'byte'|'short'|'float'|'double'|'bytearray'|'intarray'|'longarray'"""
    __slots__ = ("kind", "value", "list_type")

    def __init__(self, kind, value, list_type=None):
        self.kind = kind
        self.value = value          # dict / list / scalar / bytes
        self.list_type = list_type  # element tag id for lists

    def walk(self, fn):
        if self.kind == "compound":
            out = {}
            for k, v in self.value.items():
                nv = v.walk(fn)
                out[fn(k)] = nv
            self.value = out
        elif self.kind == "list":
            self.value = [v.walk(fn) for v in self.value]
        elif self.kind == "string":
            new, changed = remap_str(self.value)
            if changed:
                self.value = new
        return self

    def boss_contained(self):
        """Convert 1.12 `containedboss` on boss_spawner block nbt to `boss`."""
        if self.kind != "compound":
            return
        d = self.value
        cb = d.get("containedboss")
        if cb is not None and cb.kind == "string" and cb.value in BOSS_CONTAINED:
            d.pop("containedboss")
            d["boss"] = NBT("string", BOSS_CONTAINED[cb.value])
        # drop 1.12 chest Items marker and metadata-only structure blocks leftovers
        for k in ("metadata", "mirror", "ignoreEntities", "powered", "seed", "author",
                  "rotation", "posX", "mode", "posY", "sizeX", "posZ", "integrity",
                  "showair", "name", "sizeY", "sizeZ", "showboundingbox"):
            d.pop(k, None)


TAG_END, TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE, \
    TAG_BYTEARRAY, TAG_STRING, TAG_LIST, TAG_COMPOUND, TAG_INTARRAY, TAG_LONGARRAY = range(13)


class NBTReader:
    def __init__(self, data: bytes):
        self.d = data
        self.i = 0

    def u8(self):
        v = self.d[self.i]; self.i += 1; return v

    def take(self, n):
        v = self.d[self.i:self.i + n]; self.i += n; return v

    def read_name(self):
        ln = struct.unpack(">H", self.take(2))[0]
        return self.take(ln).decode("utf-8")

    def read_payload(self, t):
        if t == TAG_BYTE: return NBT("byte", struct.unpack(">b", self.take(1))[0])
        if t == TAG_SHORT: return NBT("short", struct.unpack(">h", self.take(2))[0])
        if t == TAG_INT: return NBT("int", struct.unpack(">i", self.take(4))[0])
        if t == TAG_LONG: return NBT("long", struct.unpack(">q", self.take(8))[0])
        if t == TAG_FLOAT: return NBT("float", struct.unpack(">f", self.take(4))[0])
        if t == TAG_DOUBLE: return NBT("double", struct.unpack(">d", self.take(8))[0])
        if t == TAG_BYTEARRAY:
            n = struct.unpack(">i", self.take(4))[0]
            return NBT("bytearray", self.take(n))
        if t == TAG_STRING:
            ln = struct.unpack(">H", self.take(2))[0]
            return NBT("string", self.take(ln).decode("utf-8"))
        if t == TAG_LIST:
            et = self.u8()
            n = struct.unpack(">i", self.take(4))[0]
            items = []
            for _ in range(n):
                if et == TAG_END: raise ValueError("bad list: end element")
                items.append(self.read_payload(et))
            return NBT("list", items, et)
        if t == TAG_COMPOUND:
            d = {}
            while True:
                t2 = self.u8()
                if t2 == TAG_END:
                    break
                name = self.read_name()
                d[name] = self.read_payload(t2)
            return NBT("compound", d)
        if t == TAG_INTARRAY:
            n = struct.unpack(">i", self.take(4))[0]
            return NBT("intarray", list(struct.unpack(f">{n}i", self.take(4 * n))))
        if t == TAG_LONGARRAY:
            n = struct.unpack(">i", self.take(4))[0]
            return NBT("longarray", list(struct.unpack(f">{n}q", self.take(8 * n))))
        raise ValueError(f"unknown tag {t}")

    def read_root(self):
        t = self.u8()
        if t == TAG_END:
            return None
        name = self.read_name()
        return name, self.read_payload(t)


def _transform_boss_blocks(node: NBT) -> bool:
    changed = False
    if node.kind != "compound":
        return changed
    blks = node.value.get("blocks")
    if blks is None or blks.kind != "list":
        return changed
    for b in blks.value:
        if b.kind != "compound":
            continue
        bd = b.value
        nbt = bd.get("nbt")
        if nbt is None or nbt.kind != "compound":
            continue
        bid = nbt.value.get("id")
        if bid is not None and bid.kind == "string" and bid.value == "theaurorian:boss_spawner":
            before = nbt.value.get("containedboss")
            nbt.boss_contained()
            after = nbt.value.get("boss")
            if before is not None and after is not None:
                changed = True
    return changed


def write_nbt(w: NBTWriter, t: int, name: str, node: NBT):
    w.tag(t)
    w.name(name)
    _write_payload(w, node)


def _write_payload(w: NBTWriter, node: NBT):
    if node.kind == "byte": w.byte(node.value)
    elif node.kind == "short": w.short(node.value)
    elif node.kind == "int": w.int(node.value)
    elif node.kind == "long": w.long(node.value)
    elif node.kind == "float": w.float(node.value)
    elif node.kind == "double": w.double(node.value)
    elif node.kind == "bytearray":
        w.int(len(node.value)); w.bytes_(node.value)
    elif node.kind == "string":
        b = node.value.encode("utf-8")
        w.short(len(b)); w.bytes_(b)
    elif node.kind == "list":
        et = node.list_type
        w.byte(et)
        w.int(len(node.value))
        for item in node.value:
            _write_payload(w, item)
    elif node.kind == "compound":
        for k, v in node.value.items():
            _write_payload_named(w, k, v)
        w.tag(TAG_END)
    elif node.kind == "intarray":
        w.int(len(node.value))
        for v in node.value: w.int(v)
    elif node.kind == "longarray":
        w.int(len(node.value))
        for v in node.value: w.long(v)
    else:
        raise ValueError(f"cannot write {node.kind}")


def _write_payload_named(w: NBTWriter, name: str, node: NBT):
    t = TAG_COMPOUND if node.kind == "compound" else \
        TAG_LIST if node.kind == "list" else \
        TAG_STRING if node.kind == "string" else \
        TAG_BYTE if node.kind == "byte" else \
        TAG_SHORT if node.kind == "short" else \
        TAG_INT if node.kind == "int" else \
        TAG_LONG if node.kind == "long" else \
        TAG_FLOAT if node.kind == "float" else \
        TAG_DOUBLE if node.kind == "double" else \
        TAG_BYTEARRAY if node.kind == "bytearray" else \
        TAG_INTARRAY if node.kind == "intarray" else \
        TAG_LONGARRAY if node.kind == "longarray" else TAG_END
    write_nbt(w, t, name, node)


def remap_file(path: Path, dry_run: bool) -> int:
    raw = path.read_bytes()
    try:
        data = gzip.decompress(raw)
        compressed = True
    except OSError:
        data = raw
        compressed = False

    reader = NBTReader(data)
    root = reader.read_root()
    if root is None:
        print(f"{path}: empty/invalid")
        return 0
    name, node = root
    if node.kind != "compound":
        print(f"{path}: unexpected root kind {node.kind}")
        return 0

    changes: list[str] = []
    seen_old: set[str] = set()

    def collect(node: NBT, prefix: str):
        if node.kind == "compound":
            for k, v in node.value.items():
                if k == "Name" and v.kind == "string" and v.value.startswith("theaurorian:"):
                    old = v.value
                    if old not in seen_old:
                        seen_old.add(old)
                        if old in BLOCK_REMAP:
                            changes.append(f"{old} -> {BLOCK_REMAP[old]}")
                elif k in ("LootTable", "boss", "Entity", "id", "Item") and v.kind == "string":
                    old = v.value
                    new, ch = remap_str(old)
                    if ch and old not in seen_old:
                        seen_old.add(old)
                        changes.append(f"[{k}] {old} -> {new}")
                collect(v, prefix + k + ".")
        elif node.kind == "list":
            for v in node.value:
                collect(v, prefix + "[].")

    collect(node, "")

    node.walk(lambda k: k)
    # boss spawner block entities
    boss_changed = _transform_boss_blocks(node)
    writer = NBTWriter()
    write_nbt(writer, TAG_COMPOUND, name, node)
    out = bytes(writer.buf)
    if compressed:
        out = gzip.compress(out)

    if changes or boss_changed:
        print(f"\n{path}:")
        for c in changes[:40]:
            print(f"  {c}")
        if boss_changed:
            print("  [boss_spawner] containedboss -> boss")
        if len(changes) > 40:
            print(f"  ... and {len(changes) - 40} more")
        if dry_run:
            print("  (dry-run, not written)")
            return 0
        path.write_bytes(out)
        print("  written")
        return 1

    print(f"{path}: no changes")
    return 0


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("paths", nargs="+", type=Path)
    ap.add_argument("--dry-run", action="store_true")
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
    written = 0
    for f in files:
        written += remap_file(f, args.dry_run)
    print(f"\nDone. files_touched={written} dry_run={args.dry_run}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
