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
    # fluid blocks are registered in FluidRegistry.java via BlockRegistry.BLOCKS
    if (JAVA / "Registry" / "FluidRegistry.java").exists():
        fluid_text = read_text(JAVA / "Registry" / "FluidRegistry.java")
        ids |= set(re.findall(r'BLOCKS\.register\("([a-z0-9_]+)"', fluid_text))
    return ids


def parse_item_ids() -> set[str]:
    text = read_text(JAVA / "Registry" / "ItemRegistry.java")
    ids = set(re.findall(r'\.register\("([a-z0-9_]+)"', text))
    # fluid buckets are registered in FluidRegistry.java via ItemRegistry.ITEMS
    if (JAVA / "Registry" / "FluidRegistry.java").exists():
        fluid_text = read_text(JAVA / "Registry" / "FluidRegistry.java")
        ids |= set(re.findall(r'ITEMS\.register\("([a-z0-9_]+)"', fluid_text))
    return ids


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
    loot_dir = MAIN / "data" / MODID / "loot_tables" / "entities"
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
    struct_root = MAIN / "data" / MODID / "structures"
    if not struct_root.exists():
        err(cat, "missing structures directory")
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
        if raw[:2] == b"\x1f\x8b":
            try:
                decompressed = gzip.decompress(raw)
                if len(decompressed) < 4:
                    err(cat, f"structure gzip empty: {nbt.relative_to(ROOT)}")
            except Exception as exc:  # noqa: BLE001
                err(cat, f"structure gzip invalid {nbt.relative_to(ROOT)}: {exc}")

    stats["structure_nbt"] = str(total)
    if total < 50:
        err(cat, f"expected >= 50 structure NBTs, found {total}")
    for folder, minimum in STRUCTURE_NBT_MIN.items():
        got = by_folder.get(folder, 0)
        if got < minimum:
            err(cat, f"structures/{folder}: expected >= {minimum}, found {got}")

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


def _surface_rule_blocks(
    node: object,
    *,
    water_offsets: tuple[int, ...] = (),
    preliminary_surface_guard: bool = False,
) -> list[tuple[str, tuple[int, ...], bool]]:
    """Collect block results and the surface guards active on each result."""
    if isinstance(node, list):
        out: list[tuple[str, tuple[int, ...], bool]] = []
        for child in node:
            out.extend(
                _surface_rule_blocks(
                    child,
                    water_offsets=water_offsets,
                    preliminary_surface_guard=preliminary_surface_guard,
                )
            )
        return out
    if not isinstance(node, dict):
        return []

    node_type = node.get("type")
    if node_type == "minecraft:block":
        state = node.get("result_state")
        if isinstance(state, dict) and isinstance(state.get("Name"), str):
            return [(state["Name"], water_offsets, preliminary_surface_guard)]
        return []

    if node_type == "minecraft:condition":
        condition = node.get("if_true")
        next_water_offsets = water_offsets
        if isinstance(condition, dict) and condition.get("type") == "minecraft:water":
            offset = condition.get("offset")
            if isinstance(offset, int):
                next_water_offsets = (*water_offsets, offset)
        return _surface_rule_blocks(
            node.get("then_run"),
            water_offsets=next_water_offsets,
            preliminary_surface_guard=preliminary_surface_guard
            or (
                isinstance(condition, dict)
                and condition.get("type") == "minecraft:above_preliminary_surface"
            ),
        )

    if node_type == "minecraft:sequence":
        return _surface_rule_blocks(
            node.get("sequence"),
            water_offsets=water_offsets,
            preliminary_surface_guard=preliminary_surface_guard,
        )
    return []


def _surface_rule_has_y_anchor(node: object, absolute: int) -> bool:
    if isinstance(node, list):
        return any(_surface_rule_has_y_anchor(child, absolute) for child in node)
    if not isinstance(node, dict):
        return False
    if (
        node.get("type") == "minecraft:y_above"
        and isinstance(node.get("anchor"), dict)
        and node["anchor"].get("absolute") == absolute
    ):
        return True
    return any(_surface_rule_has_y_anchor(value, absolute) for value in node.values())


def _validate_surface_rule(cat: str, noise: Path) -> None:
    data = load_json(noise)
    if not isinstance(data, dict):
        return
    if data.get("sea_level") != 63:
        err(cat, f"noise sea_level changed: expected 63, found {data.get('sea_level')}")
    rule = data.get("surface_rule")
    if not isinstance(rule, dict):
        err(cat, "noise surface_rule missing")
        return

    blocks = _surface_rule_blocks(rule)
    names = {name for name, *_ in blocks}
    required = {
        f"{MODID}:aurorian_grass",
        f"{MODID}:aurorian_grass_light",
        f"{MODID}:aurorian_dirt",
        f"{MODID}:moon_sand",
    }
    missing = sorted(required - names)
    if missing:
        err(cat, f"surface_rule missing expected land blocks: {missing}")

    moon_sand = f"{MODID}:moon_sand"
    surface_materials = {
        f"{MODID}:aurorian_grass",
        f"{MODID}:aurorian_grass_light",
        f"{MODID}:aurorian_dirt",
        moon_sand,
    }
    for name, water_offsets, preliminary_guard in blocks:
        if name in surface_materials and not preliminary_guard:
            err(cat, f"surface_rule {name} lacks minecraft:above_preliminary_surface guard")
        if name == moon_sand and water_offsets != ():
            err(cat, "surface_rule moon_sand must be the fallback (no water guard) after a water offset 0 land rule")
        if name in {f"{MODID}:aurorian_grass", f"{MODID}:aurorian_grass_light"}:
            if water_offsets and water_offsets != (0,):
                err(cat, f"surface_rule {name} under a non-zero water guard")

    if _surface_rule_has_y_anchor(rule, 55):
        err(cat, "surface_rule contains the forbidden global y_above absolute 55 moon_sand band")


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
    else:
        _validate_surface_rule(cat, noise)

    dim_type = MAIN / "data" / MODID / "dimension_type"
    if not dim_type.exists() or not list(dim_type.glob("*.json")):
        err(cat, "missing dimension_type")


# ---------------------------------------------------------------------------
# F — recipes + chest loot
# ---------------------------------------------------------------------------
def cat_recipes(blocks: set[str], items: set[str]) -> None:
    cat = "F-recipes"
    recipes_dir = MAIN / "data" / MODID / "recipes"
    if not recipes_dir.exists():
        err(cat, "missing recipes directory")
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

    required_furnace_recipes = {
        "smelting/moon_sand.json",
        "blasting/moon_sand.json",
        "smoking/cooked_aurorian_pork.json",
        "campfire/cooked_aurorian_pork.json",
        "blasting/aurorian_stone.json",
        "smelting/silentwood_charcoal.json",
        "smelting/weeping_willow_charcoal.json",
    }
    missing_furnace = sorted(
        name for name in required_furnace_recipes
        if not (recipes_dir / name).is_file()
    )
    if missing_furnace:
        err(cat, f"missing furnace-family recipes: {missing_furnace}")

    # chest loot
    chests = MAIN / "data" / MODID / "loot_tables" / "chests"
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
    adv_dir = MAIN / "data" / MODID / "advancements"
    if not adv_dir.exists():
        err(cat, "missing advancements directory")
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
    shears = GEN / "data" / "forge" / "tags" / "items" / "shears.json"
    if shears.exists():
        data = load_json(shears)
        if isinstance(data, dict):
            values = data.get("values") or []
            if not any("sickle" in str(v) for v in values):
                warn(cat, "forge:shears tag has no sickle entry")
    else:
        warn(cat, "generated forge:shears tag missing (run runData?)")

    # block loot tables for a sample of blocks
    block_loot_dir = GEN / "data" / MODID / "loot_tables" / "blocks"
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

    mods_toml = MAIN / "META-INF" / "mods.toml"
    if not mods_toml.exists():
        err(cat, "missing META-INF/mods.toml")
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
