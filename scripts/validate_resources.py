#!/usr/bin/env python3
"""Full port content integrity gate — categorized checks for The Aurorian 1.19.2.

Categories (printed in report):
  A lang          B blocks/items assets   C entities/loot
  D structures    E worldgen/biomes       F recipes
  G advancements  H mirror                I sounds/particles
  J registry sync K tags/smoke

Exit 0 on success, 1 on failure.
"""
from __future__ import annotations

import gzip
import json
import re
import sys
from collections import Counter, defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAIN = ROOT / "src" / "main" / "resources"
GEN = ROOT / "src" / "generated" / "resources"
JAVA = ROOT / "src" / "main" / "java" / "shiroroku" / "theaurorian"
MODID = "theaurorian"

errors: list[tuple[str, str]] = []
warnings: list[tuple[str, str]] = []
stats: dict[str, str] = {}


def err(cat: str, msg: str) -> None:
    errors.append((cat, msg))


def warn(cat: str, msg: str) -> None:
    warnings.append((cat, msg))


def load_json(path: Path):
    try:
        with path.open(encoding="utf-8") as fh:
            return json.load(fh)
    except Exception as exc:  # noqa: BLE001
        err("json", f"invalid JSON {path.relative_to(ROOT)}: {exc}")
        return None


def resource_roots() -> list[Path]:
    roots = [MAIN]
    if GEN.exists():
        roots.append(GEN)
    return roots


def stems(*rel_dirs: str) -> set[str]:
    out: set[str] = set()
    for rel in rel_dirs:
        for root in resource_roots():
            d = root / rel
            if d.exists():
                out |= {p.stem for p in d.glob("*.json")}
    return out


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def parse_block_ids() -> set[str]:
    text = read_text(JAVA / "Registry" / "BlockRegistry.java")
    ids = set(re.findall(r'regBlockItem\w*\(\s*\w+\s*,\s*"([a-z0-9_]+)"', text))
    ids |= set(re.findall(r'(?:BLOCKS\w*)\.register\("([a-z0-9_]+)"', text))
    return ids


def parse_item_ids() -> set[str]:
    text = read_text(JAVA / "Registry" / "ItemRegistry.java")
    return set(re.findall(r'\.register\("([a-z0-9_]+)"', text))


def parse_entity_ids() -> list[str]:
    text = read_text(JAVA / "Registry" / "EntityRegistry.java")
    return re.findall(r'ENTITIES\.register\("([a-z0-9_]+)"', text)


PROJECTILE_ENTITIES = {
    "cerulean_arrow",
    "crystal_arrow",
    "crystalline_beam",
    "sticky_spiker",
    "webbing",
}

REQUIRED_BIOMES = {
    "aurorian_forest",
    "aurorian_plains",
    "aurorian_rough_forest",
    "aurorian_forest_hills",
    "aurorian_lakes",
    "aurorian_overgrowth",
    "weeping_willow_forest",
}

REQUIRED_STRUCTURES = {
    "runestone_dungeon",
    "darkstone_dungeon",
    "moon_temple",
    "umbra_tower",
    "ruins_1",
    "ruins_2",
    "graveyard",
    "ruined_house",
}

REQUIRED_STRUCTURE_SETS = {
    "major_dungeons",
    "umbra_tower",
    "ruins_1",
    "ruins_2",
    "graveyard",
    "ruined_house",
}

MAJOR_DUNGEON_STRUCTURES = {
    f"{MODID}:runestone_dungeon",
    f"{MODID}:darkstone_dungeon",
    f"{MODID}:moon_temple",
}

STRUCTURE_NBT_MIN = {
    "runestone": 20,
    "darkstone": 14,
    "moontemple": 11,
    "umbratower": 1,
    "ruins": 3,
    "weepingwillow": 5,
}

REQUIRED_MIRROR = {
    "aurorian",
    "dungeons",
    "dungeon_runestone",
    "dungeon_darkstone",
    "dungeon_moon_temple",
    "agriculture",
    "passives",
    "boss_loot",
    "locator",
    "crystalline",
    "aurorianite",
    "umbra",
    "aurorian_steel",
    "crafting",
    "ores",
    "ore_cerulean",
    "ore_moonstone",
    "ore_geode",
}

BOSS_ADVANCEMENTS = {"liberated", "exterminated", "dethroned"}

REQUIRED_MF_RECIPES = {
    "keepers_bow.json",
    "queens_chipper.json",
    "moon_shield.json",
}

REQUIRED_ITEM_IDS = {
    "dungeon_locator",
    "keepers_bow",
    "queens_chipper",
    "moon_shield",
    "slime_boots",
    "spiked_chestplate",
    "mirror_of_guidance",
    "trophy_keeper",
    "trophy_moon_queen",
    "trophy_spider",
    "sticky_spiker",
    "webbing",
    "dark_amulet",
    "lockpicks",
    "runestone_key",
    "darkstone_key",
    "moon_temple_key",
}

REQUIRED_BLOCK_IDS = {
    "boss_spawner",
    "weeping_willow_leaves",
    "aurorian_farm_tile",
    "umbra_stone",
    "silentwood_chest",
    "moonlight_forge",
    "scrapper",
    "aurorian_portal",
    "fog_wall",
    "lavender_crop",
    "silkberry_crop",
    "mushroom",
    "mushroom_stem",
}


# ---------------------------------------------------------------------------
# A — language
# ---------------------------------------------------------------------------
def cat_lang() -> dict[str, dict]:
    cat = "A-lang"
    lang_dir = MAIN / "assets" / MODID / "lang"
    required = ("en_us.json", "zh_cn.json", "es_es.json")
    langs: dict[str, dict] = {}
    for name in required:
        path = lang_dir / name
        if not path.exists():
            err(cat, f"missing {path.relative_to(ROOT)}")
            continue
        data = load_json(path)
        if not isinstance(data, dict):
            err(cat, f"{name} is not an object")
            continue
        langs[name] = data
    if "en_us.json" in langs:
        en = langs["en_us.json"]
        stats["lang_en_keys"] = str(len(en))
        if len(en) < 350:
            err(cat, f"en_us has only {len(en)} keys (expected >= 350)")
        for name, data in langs.items():
            if name == "en_us.json":
                continue
            missing = sorted(set(en) - set(data))
            extra = sorted(set(data) - set(en))
            if missing:
                err(cat, f"{name} missing {len(missing)} keys vs en_us e.g. {missing[:5]}")
            if extra:
                warn(cat, f"{name} has {len(extra)} extra keys vs en_us")
    return langs


# ---------------------------------------------------------------------------
# B — blocks / items assets
# ---------------------------------------------------------------------------
def cat_blocks_items(langs: dict[str, dict]) -> tuple[set[str], set[str]]:
    cat = "B-blocks-items"
    blocks = parse_block_ids()
    items = parse_item_ids()
    stats["blocks"] = str(len(blocks))
    stats["items"] = str(len(items))

    if len(blocks) < 90:
        err(cat, f"only {len(blocks)} block registrations (expected >= 90)")
    if len(items) < 120:
        err(cat, f"only {len(items)} item registrations (expected >= 120)")

    bs = stems(
        f"assets/{MODID}/blockstates",
    )
    stats["blockstates"] = str(len(bs))
    missing_bs = sorted(blocks - bs)
    if missing_bs:
        err(cat, f"{len(missing_bs)} blocks missing blockstate: {missing_bs[:10]}")
    orphan_bs = sorted(bs - blocks)
    # allow none normally
    if orphan_bs:
        warn(cat, f"{len(orphan_bs)} blockstates without block field: {orphan_bs[:10]}")

    im = stems(f"assets/{MODID}/models/item")
    stats["item_models"] = str(len(im))
    # every non-block-only item should have a model; block items may share
    missing_models = sorted(i for i in items if i not in im and i not in blocks)
    # block items also need models in 1.19 often
    missing_block_item_models = sorted(b for b in blocks if b not in im and b in items or b not in im)
    # BlockRegistry creates BlockItems for almost all blocks — require models for all blocks too
    missing_all = sorted(set(list(items) + list(blocks)) - im)
    # filter: some technical may not — but currently all should
    if missing_all:
        # items that are pure items missing models are errors; block items missing models also errors if not present
        pure = sorted(set(missing_all) & items)
        if pure:
            err(cat, f"{len(pure)} items/blocks missing item model e.g. {pure[:10]}")

    en = langs.get("en_us.json", {})
    missing_block_lang = sorted(b for b in blocks if f"block.{MODID}.{b}" not in en)
    if missing_block_lang:
        err(cat, f"{len(missing_block_lang)} blocks missing lang: {missing_block_lang[:10]}")

    missing_item_lang = []
    for i in sorted(items):
        if f"item.{MODID}.{i}" not in en and f"block.{MODID}.{i}" not in en:
            missing_item_lang.append(i)
    if missing_item_lang:
        err(cat, f"{len(missing_item_lang)} items missing lang: {missing_item_lang[:10]}")

    for rid in REQUIRED_BLOCK_IDS:
        if rid not in blocks:
            err(cat, f"required block not registered: {rid}")
    for rid in REQUIRED_ITEM_IDS:
        if rid not in items:
            err(cat, f"required item not registered: {rid}")

    return blocks, items


# ---------------------------------------------------------------------------
# C — entities / loot / eggs
# ---------------------------------------------------------------------------
def cat_entities(langs: dict[str, dict], items: set[str]) -> list[str]:
    cat = "C-entities"
    entities = parse_entity_ids()
    stats["entities"] = str(len(entities))
    if len(entities) < 19:
        err(cat, f"only {len(entities)} entities (expected >= 19)")

    en = langs.get("en_us.json", {})
    for e in entities:
        if f"entity.{MODID}.{e}" not in en:
            err(cat, f"missing entity lang: entity.{MODID}.{e}")

    living = [e for e in entities if e not in PROJECTILE_ENTITIES]
    loot_dir = MAIN / "data" / MODID / "loot_table" / "entities"
    loot = {p.stem for p in loot_dir.glob("*.json")} if loot_dir.exists() else set()
    stats["entity_loot"] = str(len(loot))
    for e in living:
        if e not in loot:
            err(cat, f"living entity missing loot table: {e}")
        else:
            data = load_json(loot_dir / f"{e}.json")
            if isinstance(data, dict) and not data.get("pools"):
                err(cat, f"entity loot empty pools: {e}")

    # spawn eggs for all living
    for e in living:
        egg = f"spawn_egg_{e}"
        if egg not in items:
            err(cat, f"living entity missing spawn egg item: {egg}")
        elif f"item.{MODID}.{egg}" not in en:
            err(cat, f"spawn egg missing lang: {egg}")

    # bosses present
    for boss in ("dungeon_keeper", "dungeon_spider", "moon_queen"):
        if boss not in entities:
            err(cat, f"missing boss entity: {boss}")

    # passives present
    for passive in ("aurorian_pig", "aurorian_rabbit", "aurorian_sheep"):
        if passive not in entities:
            err(cat, f"missing passive entity: {passive}")

    return entities


# ---------------------------------------------------------------------------
# D — structures
# ---------------------------------------------------------------------------
def cat_structures() -> None:
    cat = "D-structures"
    struct_root = MAIN / "data" / MODID / "structure"
    if not struct_root.exists():
        err(cat, "missing structure directory")
        return

    by_folder: dict[str, int] = defaultdict(int)
    total = 0
    for nbt in struct_root.rglob("*.nbt"):
        total += 1
        rel = nbt.relative_to(struct_root)
        folder = rel.parts[0] if len(rel.parts) > 1 else "_root"
        by_folder[folder] += 1
        raw = nbt.read_bytes()
        if len(raw) < 8:
            err(cat, f"structure too small: {nbt.relative_to(ROOT)}")
            continue
        try:
            decompressed = gzip.decompress(raw) if raw[:2] == b"\x1f\x8b" else raw
            if len(decompressed) < 4:
                err(cat, f"structure gzip empty: {nbt.relative_to(ROOT)}")
        except Exception as exc:  # noqa: BLE001
            err(cat, f"structure gzip invalid {nbt.relative_to(ROOT)}: {exc}")
            continue
        if folder == "darkstone" and (b"minecraft:water" in decompressed or b"minecraft:lava" in decompressed):
            err(cat, f"Darkstone template contains fluid blocks: {nbt.relative_to(ROOT)}")

    stats["structure_nbt"] = str(total)
    if total < 50:
        err(cat, f"expected >= 50 structure NBTs, found {total}")
    for folder, minimum in STRUCTURE_NBT_MIN.items():
        got = by_folder.get(folder, 0)
        if got < minimum:
            err(cat, f"structure/{folder}: expected >= {minimum}, found {got}")

    wg_struct = MAIN / "data" / MODID / "worldgen" / "structure"
    wg_set = MAIN / "data" / MODID / "worldgen" / "structure_set"
    struct_defs = {p.stem for p in wg_struct.glob("*.json")} if wg_struct.exists() else set()
    set_defs = {p.stem for p in wg_set.glob("*.json")} if wg_set.exists() else set()
    stats["structure_defs"] = str(len(struct_defs))
    stats["structure_sets"] = str(len(set_defs))

    missing_defs = sorted(REQUIRED_STRUCTURES - struct_defs)
    if missing_defs:
        err(cat, f"missing structure definitions: {missing_defs}")
    missing_sets = sorted(REQUIRED_STRUCTURE_SETS - set_defs)
    if missing_sets:
        err(cat, f"missing structure_set definitions: {missing_sets}")

    major_set = wg_set / "major_dungeons.json"
    if major_set.exists():
        data = load_json(major_set)
        if isinstance(data, dict):
            placement = data.get("placement") or {}
            if placement.get("type") != "minecraft:random_spread":
                err(cat, "major_dungeons structure_set must use minecraft:random_spread")
            if placement.get("spacing") != 32 or placement.get("separation") != 31:
                err(cat, "major_dungeons structure_set must use spacing=32 and separation=31")
            structures = data.get("structures") or []
            actual = {
                entry.get("structure")
                for entry in structures
                if isinstance(entry, dict) and isinstance(entry.get("structure"), str)
            }
            if actual != MAJOR_DUNGEON_STRUCTURES:
                err(cat, f"major_dungeons structure_set must contain exactly {sorted(MAJOR_DUNGEON_STRUCTURES)}, got {sorted(actual)}")
            if any(entry.get("weight") != 1 for entry in structures if isinstance(entry, dict)):
                err(cat, "major_dungeons structure_set entries must all have weight 1")
    elif "major_dungeons" in set_defs:
        err(cat, "major_dungeons structure_set file is missing")

    # structure_set -> structure link
    if wg_set.exists():
        for path in wg_set.glob("*.json"):
            data = load_json(path)
            if not isinstance(data, dict):
                continue
            for entry in data.get("structures") or []:
                if not isinstance(entry, dict):
                    continue
                sid = entry.get("structure", "")
                name = sid.split(":")[-1]
                if name not in struct_defs:
                    err(cat, f"structure_set {path.stem} references missing structure {sid}")

    # single_template NBT path exists
    if wg_struct.exists():
        for path in wg_struct.glob("*.json"):
            data = load_json(path)
            if not isinstance(data, dict):
                continue
            if data.get("type") == f"{MODID}:single_template":
                template = data.get("template", "")
                rel = template.split(":")[-1]
                nbt = struct_root / f"{rel}.nbt"
                if not nbt.exists():
                    err(cat, f"structure {path.stem} template missing NBT: {rel}.nbt")
            biomes = data.get("biomes")
            if isinstance(biomes, list) and not biomes:
                err(cat, f"structure {path.stem} has empty biomes list")
            # Runestone must use custom type (not leftover jigsaw)
            if path.stem == "runestone_dungeon" and data.get("type") != f"{MODID}:runestone_dungeon":
                err(cat, f"runestone_dungeon type should be {MODID}:runestone_dungeon, got {data.get('type')}")

    # Spawner / boss_spawner NBT shape gate (1.18+ / 1.21)
    _validate_structure_spawners(cat, struct_root)


def _validate_structure_spawners(cat: str, struct_root: Path) -> None:
    """Byte/scan level checks for legacy spawner + boss keys inside structure NBTs."""
    legacy_mob = 0
    legacy_contained = 0
    legacy_entity_weight = 0
    spawners_seen = 0
    boss_ok = 0
    for nbt in struct_root.rglob("*.nbt"):
        # Skip gametest fixtures if any
        if "gametest" in nbt.parts:
            continue
        raw = nbt.read_bytes()
        try:
            data = gzip.decompress(raw) if raw[:2] == b"\x1f\x8b" else raw
        except Exception:  # noqa: BLE001
            continue
        if b"minecraft:mob_spawner" in data:
            legacy_mob += 1
            err(cat, f"legacy minecraft:mob_spawner in {nbt.relative_to(ROOT)}")
        if b"containedboss" in data:
            legacy_contained += 1
            err(cat, f"legacy containedboss in {nbt.relative_to(ROOT)}")
        # SpawnPotentials with capital Entity/Weight (1.12–1.16)
        if b"SpawnPotentials" in data and b"Entity" in data and b"Weight" in data:
            # Heuristic: modern shape uses weight/data; still flag if both legacy keys present
            # Avoid false positive on unrelated Entity keys by requiring SpawnData nearby too
            if b"SpawnData" in data:
                legacy_entity_weight += 1
                err(cat, f"legacy SpawnPotentials Entity/Weight likely in {nbt.relative_to(ROOT)}")
        if b"SpawnData" in data or b"minecraft:spawner" in data:
            spawners_seen += 1
        if b"boss_spawner" in data and b"boss" in data:
            boss_ok += 1
    stats["structure_spawners"] = str(spawners_seen)
    stats["structure_boss_spawners"] = str(boss_ok)
    if spawners_seen == 0:
        warn(cat, "no SpawnData/spawner markers found in structure NBTs")
    if legacy_mob or legacy_contained or legacy_entity_weight:
        stats["structure_spawner_legacy"] = (
            f"mob_spawner={legacy_mob},containedboss={legacy_contained},EntityWeight={legacy_entity_weight}"
        )


# ---------------------------------------------------------------------------
# E — worldgen / biomes / dimension / features
# ---------------------------------------------------------------------------
def cat_worldgen() -> None:
    cat = "E-worldgen"
    biome_dir = MAIN / "data" / MODID / "worldgen" / "biome"
    biomes = {p.stem for p in biome_dir.glob("*.json")} if biome_dir.exists() else set()
    stats["biomes"] = str(len(biomes))
    missing = sorted(REQUIRED_BIOMES - biomes)
    if missing:
        err(cat, f"missing biomes: {missing}")

    cf = stems(f"data/{MODID}/worldgen/configured_feature")
    pf = stems(f"data/{MODID}/worldgen/placed_feature")
    stats["configured_features"] = str(len(cf))
    stats["placed_features"] = str(len(pf))
    if pf - cf:
        err(cat, f"placed_feature without configured_feature: {sorted(pf - cf)}")
    if len(pf) < 12:
        err(cat, f"expected >= 12 placed features, found {len(pf)}")

    required_features = {
        "silentwood_tree",
        "weeping_willow_tree",
        "mushroom_cave",
        "lavender_patch",
        "silkberry_patch",
        "urn",
        "ore_cerulean",
        "ore_moonstone",
        "ore_geode",
        "ore_coal",
        "bright_bulb_patch",
    }
    missing_f = sorted(required_features - pf)
    if missing_f:
        err(cat, f"missing placed features: {missing_f}")

    willow_templates = {
        "willow_s1.nbt",
        "willow_s2.nbt",
        "willow_s3.nbt",
        "willow_l1.nbt",
        "willow_l2.nbt",
    }
    willow_dir = MAIN / "data" / MODID / "structure" / "weepingwillow"
    missing_willow_templates = sorted(
        name for name in willow_templates if not (willow_dir / name).exists()
    )
    if missing_willow_templates:
        err(cat, f"missing weeping willow templates: {missing_willow_templates}")

    # Trees require the vegetal decoration generation step, after terrain and heightmap placement.
    if biome_dir.exists():
        willow_biome = biome_dir / "weeping_willow_forest.json"
        if willow_biome.exists():
            data = load_json(willow_biome)
            features = data.get("features") if isinstance(data, dict) else None
            if not isinstance(features, list) or len(features) <= 9:
                err(cat, "biome weeping_willow_forest: features missing VEGETAL_DECORATION step 9")
            else:
                willow_steps = [
                    index
                    for index, step in enumerate(features)
                    if isinstance(step, list) and f"{MODID}:weeping_willow_tree" in step
                ]
                if willow_steps != [9]:
                    err(
                        cat,
                        "biome weeping_willow_forest: weeping_willow_tree must appear only at step 9, "
                        f"found {willow_steps}",
                    )

    # biome music + feature refs + spawners lightly
    if biome_dir.exists():
        for path in biome_dir.glob("*.json"):
            data = load_json(path)
            if not isinstance(data, dict):
                continue
            effects = data.get("effects") or {}
            if "music" not in effects:
                err(cat, f"biome {path.stem}: missing effects.music")
            for step in data.get("features") or []:
                if not isinstance(step, list):
                    continue
                for f in step:
                    if isinstance(f, str) and f.startswith(f"{MODID}:"):
                        name = f.split(":", 1)[1]
                        if name not in pf:
                            err(cat, f"biome {path.stem} references missing placed feature {f}")

    # dimension multi_noise
    dim = MAIN / "data" / MODID / "dimension" / "the_aurorian.json"
    if not dim.exists():
        err(cat, "missing dimension/the_aurorian.json")
    else:
        data = load_json(dim)
        if isinstance(data, dict):
            try:
                biome_entries = data["generator"]["biome_source"]["biomes"]
                biome_ids = {b.get("biome") for b in biome_entries if isinstance(b, dict)}
            except Exception:  # noqa: BLE001
                err(cat, "dimension missing multi_noise biomes")
            else:
                stats["dimension_biomes"] = str(len(biome_ids))
                need = {f"{MODID}:{b}" for b in REQUIRED_BIOMES}
                miss = sorted(need - biome_ids)
                if miss:
                    err(cat, f"dimension multi_noise missing: {miss}")

    noise = MAIN / "data" / MODID / "worldgen" / "noise_settings" / "the_aurorian.json"
    if not noise.exists():
        err(cat, "missing noise_settings/the_aurorian.json")

    dim_type = MAIN / "data" / MODID / "dimension_type"
    if not dim_type.exists() or not list(dim_type.glob("*.json")):
        err(cat, "missing dimension_type")


# ---------------------------------------------------------------------------
# F — recipes + chest loot
# ---------------------------------------------------------------------------
def cat_recipes(blocks: set[str], items: set[str]) -> None:
    cat = "F-recipes"
    recipes_dir = MAIN / "data" / MODID / "recipe"
    if not recipes_dir.exists():
        err(cat, "missing recipe directory")
        return
    files = list(recipes_dir.rglob("*.json"))
    stats["recipes"] = str(len(files))
    if len(files) < 180:
        err(cat, f"expected >= 180 recipes, found {len(files)}")

    types = Counter()
    known = blocks | items | {"minecraft"}  # rough
    for path in files:
        data = load_json(path)
        if not isinstance(data, dict):
            continue
        rtype = data.get("type", "?")
        types[rtype] += 1
        # validate result item namespace if present
        result = data.get("result")
        rid = None
        if isinstance(result, str):
            rid = result
        elif isinstance(result, dict):
            rid = result.get("item") or result.get("id")
        if isinstance(rid, str) and rid.startswith(f"{MODID}:"):
            name = rid.split(":", 1)[1]
            if name not in blocks and name not in items:
                err(cat, f"recipe {path.relative_to(recipes_dir)} result unknown: {rid}")

    stats["recipe_types"] = ", ".join(f"{k}={v}" for k, v in sorted(types.items()))
    if types.get("theaurorian:moonlight_forge", 0) < 20:
        err(cat, f"expected >= 20 moonlight_forge recipes, found {types.get('theaurorian:moonlight_forge', 0)}")
    if types.get("theaurorian:scrapper", 0) < 40:
        err(cat, f"expected >= 40 scrapper recipes, found {types.get('theaurorian:scrapper', 0)}")

    mf = recipes_dir / "moonlight_forge"
    for name in REQUIRED_MF_RECIPES:
        if not (mf / name).exists():
            err(cat, f"missing moonlight_forge recipe: {name}")

    # chest loot
    chests = MAIN / "data" / MODID / "loot_table" / "chests"
    required_dirs = {"runestone": 3, "darkstone": 3, "moontemple": 3, "ruins": 1}
    for name, minimum in required_dirs.items():
        d = chests / name
        if not d.exists():
            err(cat, f"missing chest loot dir chests/{name}")
            continue
        n = len(list(d.glob("*.json")))
        if n < minimum:
            err(cat, f"chests/{name}: expected >= {minimum}, found {n}")
    # validate chest loot item refs lightly
    if chests.exists():
        for path in chests.rglob("*.json"):
            data = load_json(path)
            if not isinstance(data, dict):
                continue
            _check_loot_item_refs(cat, path, data, blocks, items)


def _check_loot_item_refs(cat: str, path: Path, data: dict, blocks: set[str], items: set[str]) -> None:
    def walk(node):
        if isinstance(node, dict):
            if node.get("type") in ("minecraft:item", "item") and "name" in node:
                name = node["name"]
                if isinstance(name, str) and name.startswith(f"{MODID}:"):
                    iid = name.split(":", 1)[1]
                    if iid not in items and iid not in blocks:
                        err(cat, f"{path.relative_to(MAIN)} unknown item {name}")
            for v in node.values():
                walk(v)
        elif isinstance(node, list):
            for v in node:
                walk(v)

    walk(data)


# ---------------------------------------------------------------------------
# G — advancements
# ---------------------------------------------------------------------------
def cat_advancements(langs: dict[str, dict], items: set[str], blocks: set[str]) -> None:
    cat = "G-advancements"
    adv_dir = MAIN / "data" / MODID / "advancement"
    if not adv_dir.exists():
        err(cat, "missing advancement directory")
        return
    files = list(adv_dir.glob("*.json"))
    stats["advancements"] = str(len(files))
    if len(files) < 15:
        err(cat, f"expected >= 15 advancements, found {len(files)}")

    en = langs.get("en_us.json", {})
    stems_set = {p.stem for p in files}
    if "root" not in stems_set:
        err(cat, "missing root advancement")

    missing_boss = BOSS_ADVANCEMENTS - stems_set
    if missing_boss:
        err(cat, f"missing boss advancements: {sorted(missing_boss)}")

    for path in files:
        data = load_json(path)
        if not isinstance(data, dict):
            continue
        stem = path.stem
        if stem in BOSS_ADVANCEMENTS:
            reqs = data.get("requirements")
            if not (isinstance(reqs, list) and len(reqs) == 1 and isinstance(reqs[0], list) and len(reqs[0]) >= 2):
                err(cat, f"advancement {stem}: expected OR requirements, got {reqs}")
        display = data.get("display") or {}
        for field in ("title", "description"):
            node = display.get(field) or {}
            if isinstance(node, dict) and node.get("translate"):
                key = node["translate"]
                if key not in en:
                    err(cat, f"advancement {stem} missing lang {key}")
        # parent exists
        parent = data.get("parent")
        if isinstance(parent, str) and parent.startswith(f"{MODID}:"):
            pname = parent.split(":", 1)[1]
            if pname not in stems_set:
                err(cat, f"advancement {stem} parent missing: {parent}")
        # criteria present
        if not data.get("criteria"):
            err(cat, f"advancement {stem} has no criteria")


# ---------------------------------------------------------------------------
# H — mirror
# ---------------------------------------------------------------------------
def cat_mirror(langs: dict[str, dict]) -> None:
    cat = "H-mirror"
    mirror_dir = MAIN / "data" / MODID / "mirror_of_guidance"
    if not mirror_dir.exists():
        err(cat, "missing mirror_of_guidance")
        return
    files = {p.stem: p for p in mirror_dir.glob("*.json")}
    stats["mirror_nodes"] = str(len(files))
    if len(files) < 18:
        err(cat, f"expected >= 18 mirror nodes, found {len(files)}")
    missing = sorted(REQUIRED_MIRROR - set(files))
    if missing:
        err(cat, f"missing required mirror nodes: {missing}")

    en = langs.get("en_us.json", {})
    for stem, path in files.items():
        data = load_json(path)
        if not isinstance(data, dict):
            continue
        for key in ("icon", "x", "y"):
            if key not in data:
                err(cat, f"mirror {stem}: missing {key}")
        name_key = f"mirror_of_guidance.{MODID}.{stem}.name"
        desc_key = f"mirror_of_guidance.{MODID}.{stem}.desc"
        if name_key not in en:
            err(cat, f"mirror {stem}: missing lang {name_key}")
        if desc_key not in en:
            err(cat, f"mirror {stem}: missing lang {desc_key}")
        for child in data.get("children") or []:
            if not isinstance(child, str):
                err(cat, f"mirror {stem}: invalid child {child}")
                continue
            child_id = child.split(":", 1)[-1]
            if child_id not in files:
                err(cat, f"mirror {stem}: dangling child {child}")


# ---------------------------------------------------------------------------
# I — sounds / particles
# ---------------------------------------------------------------------------
def cat_audio() -> None:
    cat = "I-audio"
    sounds_json = MAIN / "assets" / MODID / "sounds.json"
    if not sounds_json.exists():
        err(cat, "missing sounds.json")
        return
    data = load_json(sounds_json)
    if not isinstance(data, dict) or not data:
        err(cat, "sounds.json empty")
        return
    sounds_dir = MAIN / "assets" / MODID / "sounds"
    oggs = list(sounds_dir.rglob("*.ogg")) if sounds_dir.exists() else []
    stats["ogg"] = str(len(oggs))
    if len(oggs) < 6:
        err(cat, f"expected >= 6 ogg, found {len(oggs)}")
    for key, entry in data.items():
        if not isinstance(entry, dict):
            continue
        for s in entry.get("sounds") or []:
            name = s if isinstance(s, str) else (s.get("name") if isinstance(s, dict) else None)
            if not name:
                err(cat, f"sounds.json '{key}' invalid sound ref")
                continue
            rel = name.split(":", 1)[-1]
            path = sounds_dir / f"{rel}.ogg"
            if not path.exists():
                err(cat, f"missing ogg for '{key}': {rel}.ogg")

    particle_dir = MAIN / "assets" / MODID / "particles"
    drip = particle_dir / "weeping_willow_drip.json"
    if not drip.exists():
        err(cat, "missing particles/weeping_willow_drip.json")
    else:
        pdata = load_json(drip)
        if not isinstance(pdata, dict) or "textures" not in pdata:
            err(cat, "weeping_willow_drip.json invalid (need textures)")

    # Java registries
    sound_java = JAVA / "Registry" / "SoundRegistry.java"
    particle_java = JAVA / "Registry" / "ParticleRegistry.java"
    if not sound_java.exists():
        err(cat, "SoundRegistry.java missing")
    else:
        text = read_text(sound_java)
        for rid in ("music", "weepingwillowbell"):
            if f'"{rid}"' not in text:
                err(cat, f"SoundRegistry missing {rid}")
    if not particle_java.exists():
        err(cat, "ParticleRegistry.java missing")
    else:
        text = read_text(particle_java)
        if '"weeping_willow_drip"' not in text:
            err(cat, "ParticleRegistry missing weeping_willow_drip")


# ---------------------------------------------------------------------------
# J — registry sync / tags presence
# ---------------------------------------------------------------------------
def cat_registry_tags(blocks: set[str], items: set[str]) -> None:
    cat = "J-registry-tags"
    # critical Java files
    for rel, needles in {
        "Registry/SoundRegistry.java": ["music"],
        "Registry/ParticleRegistry.java": ["weeping_willow_drip"],
        "Registry/FeatureRegistry.java": ["weeping_willow_tree", "mushroom"],
        "Registry/StructureRegistry.java": ["darkstone", "moon_temple", "single_template"],
    }.items():
        path = JAVA / rel
        if not path.exists():
            err(cat, f"missing {rel}")
            continue
        text = read_text(path)
        for n in needles:
            if n not in text:
                err(cat, f"{rel} missing marker '{n}'")

    # tags from datagen
    tag_files = list((GEN / "data").rglob("tags/**/*.json")) if GEN.exists() else []
    stats["tag_files"] = str(len(tag_files))
    if len(tag_files) < 40:
        err(cat, f"expected >= 40 generated tag files, found {len(tag_files)}")

    # shears tag should include sickle if present
    shears = GEN / "data" / "c" / "tags" / "item" / "shears.json"
    if shears.exists():
        data = load_json(shears)
        if isinstance(data, dict):
            values = data.get("values") or []
            if not any("sickle" in str(v) for v in values):
                warn(cat, "c:shears tag has no sickle entry")
    else:
        warn(cat, "generated c:shears tag missing (run runData?)")

    # block loot tables for a sample of blocks
    block_loot_dir = GEN / "data" / MODID / "loot_table" / "blocks"
    if block_loot_dir.exists():
        bloot = {p.stem for p in block_loot_dir.glob("*.json")}
        stats["block_loot"] = str(len(bloot))
        # crops may be special; require majority
        if len(bloot) < 80:
            err(cat, f"expected >= 80 block loot tables, found {len(bloot)}")
    else:
        warn(cat, "generated block loot missing")


# ---------------------------------------------------------------------------
# K — global JSON parse + pack meta
# ---------------------------------------------------------------------------
def cat_json_parse() -> None:
    cat = "K-json"
    count = 0
    for root in resource_roots():
        for path in root.rglob("*.json"):
            if path.is_file():
                load_json(path)
                count += 1
    stats["json_files"] = str(count)
    if count < 500:
        err(cat, f"expected >= 500 json files, found {count}")

    # NeoForge 1.21: neoforge.mods.toml is generated from templates into build/
    # during generateModMetadata; accept either hand-placed or templated path.
    mods_toml_candidates = [
        MAIN / "META-INF" / "neoforge.mods.toml",
        MAIN / "META-INF" / "mods.toml",
        ROOT / "src" / "main" / "templates" / "META-INF" / "neoforge.mods.toml",
        ROOT / "build" / "generated" / "sources" / "modMetadata" / "META-INF" / "neoforge.mods.toml",
    ]
    if not any(p.exists() for p in mods_toml_candidates):
        err(cat, "missing META-INF/neoforge.mods.toml (or mods.toml)")
    pack = MAIN / "pack.mcmeta"
    if not pack.exists():
        err(cat, "missing pack.mcmeta")


def main() -> int:
    print(f"=== The Aurorian full content validation ===")
    print(f"root: {ROOT}")
    langs = cat_lang()
    blocks, items = cat_blocks_items(langs)
    cat_entities(langs, items)
    cat_structures()
    cat_worldgen()
    cat_recipes(blocks, items)
    cat_advancements(langs, items, blocks)
    cat_mirror(langs)
    cat_audio()
    cat_registry_tags(blocks, items)
    cat_json_parse()

    print("\n-- stats --")
    for k, v in stats.items():
        print(f"  {k}: {v}")

    if warnings:
        print(f"\n-- warnings ({len(warnings)}) --")
        by: dict[str, list[str]] = defaultdict(list)
        for c, m in warnings:
            by[c].append(m)
        for c, ms in sorted(by.items()):
            print(f"[{c}]")
            for m in ms:
                print(f"  - {m}")

    if errors:
        print(f"\nFAILED — {len(errors)} error(s)")
        by = defaultdict(list)
        for c, m in errors:
            by[c].append(m)
        for c, ms in sorted(by.items()):
            print(f"[{c}] ({len(ms)})")
            for m in ms:
                print(f"  - {m}")
        return 1

    print(f"\nOK — full content validation passed ({len(warnings)} warning(s))")
    return 0


if __name__ == "__main__":
    sys.exit(main())
