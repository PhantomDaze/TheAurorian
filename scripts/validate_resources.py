#!/usr/bin/env python3
"""Resource integrity gate for The Aurorian 1.19.2 port.

Validates datapack/asset completeness without launching Minecraft.
Exit 0 on success, 1 on failure. Intended for `./gradlew validateResources` / CI.
"""
from __future__ import annotations

import gzip
import json
import re
import sys
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAIN = ROOT / "src" / "main" / "resources"
GEN = ROOT / "src" / "generated" / "resources"
MODID = "theaurorian"

errors: list[str] = []
warnings: list[str] = []


def err(msg: str) -> None:
    errors.append(msg)


def warn(msg: str) -> None:
    warnings.append(msg)


def load_json(path: Path) -> object | None:
    try:
        with path.open(encoding="utf-8") as fh:
            return json.load(fh)
    except Exception as exc:  # noqa: BLE001
        err(f"invalid JSON {path.relative_to(ROOT)}: {exc}")
        return None


def iter_json(base: Path) -> list[Path]:
    if not base.exists():
        return []
    return sorted(p for p in base.rglob("*.json") if p.is_file())


def resource_roots() -> list[Path]:
    roots = [MAIN]
    if GEN.exists():
        roots.append(GEN)
    return roots


def all_data_paths(*parts: str) -> list[Path]:
    out: list[Path] = []
    for root in resource_roots():
        p = root.joinpath(*parts)
        if p.exists():
            out.append(p)
    return out


def first_existing(*parts: str) -> Path | None:
    for root in resource_roots():
        p = root.joinpath(*parts)
        if p.exists():
            return p
    return None


def validate_all_json_parse() -> None:
    count = 0
    for root in resource_roots():
        for path in iter_json(root):
            load_json(path)
            count += 1
    if count < 100:
        err(f"expected many JSON resources, found only {count}")


def validate_lang() -> dict[str, dict]:
    lang_dir = MAIN / "assets" / MODID / "lang"
    required = ("en_us.json", "zh_cn.json", "es_es.json")
    langs: dict[str, dict] = {}
    for name in required:
        path = lang_dir / name
        if not path.exists():
            err(f"missing lang file: {path.relative_to(ROOT)}")
            continue
        data = load_json(path)
        if not isinstance(data, dict):
            err(f"lang file is not an object: {path.name}")
            continue
        langs[name] = data
    if "en_us.json" in langs:
        en = langs["en_us.json"]
        if len(en) < 300:
            err(f"en_us.json has only {len(en)} keys (expected >= 300)")
        for name, data in langs.items():
            if name == "en_us.json":
                continue
            missing = sorted(set(en) - set(data))
            extra = sorted(set(data) - set(en))
            if missing:
                err(f"{name} missing {len(missing)} keys vs en_us (e.g. {missing[:5]})")
            if extra:
                warn(f"{name} has {len(extra)} extra keys vs en_us")
    return langs


def validate_sounds(langs: dict[str, dict]) -> None:
    sounds_json = MAIN / "assets" / MODID / "sounds.json"
    if not sounds_json.exists():
        err("missing assets/theaurorian/sounds.json")
        return
    data = load_json(sounds_json)
    if not isinstance(data, dict):
        return
    sounds_dir = MAIN / "assets" / MODID / "sounds"
    for key, entry in data.items():
        if not isinstance(entry, dict):
            continue
        sounds = entry.get("sounds", [])
        if not sounds:
            err(f"sounds.json entry '{key}' has no sounds")
            continue
        for s in sounds:
            name = s if isinstance(s, str) else (s.get("name") if isinstance(s, dict) else None)
            if not name:
                err(f"sounds.json entry '{key}' has invalid sound ref: {s}")
                continue
            # name like theaurorian:music/aurorian_1
            rel = name.split(":", 1)[-1]
            ogg = sounds_dir / f"{rel}.ogg"
            # also allow nested path under sounds/
            if not ogg.exists():
                # try without assuming .ogg only once
                candidates = list(sounds_dir.rglob(Path(rel).name + ".ogg"))
                if not any(c.as_posix().endswith(rel + ".ogg") for c in candidates) and not ogg.exists():
                    # direct path
                    if not (MAIN / "assets" / MODID / "sounds" / f"{rel}.ogg").exists():
                        # check common layout sounds/<path>.ogg
                        p2 = MAIN / "assets" / MODID / "sounds" / Path(rel + ".ogg")
                        if not p2.exists():
                            err(f"missing ogg for sound '{key}': {rel}.ogg")
    ogg_count = len(list(sounds_dir.rglob("*.ogg"))) if sounds_dir.exists() else 0
    if ogg_count < 6:
        err(f"expected >= 6 ogg files, found {ogg_count}")


def validate_particles() -> None:
    path = MAIN / "assets" / MODID / "particles.json"
    if not path.exists():
        err("missing assets/theaurorian/particles.json")
        return
    data = load_json(path)
    if not isinstance(data, dict) or not data:
        err("particles.json empty or invalid")


def validate_advancements(langs: dict[str, dict]) -> None:
    adv_dir = MAIN / "data" / MODID / "advancements"
    if not adv_dir.exists():
        err("missing advancements directory")
        return
    files = list(adv_dir.glob("*.json"))
    if len(files) < 15:
        err(f"expected >= 15 advancements, found {len(files)}")
    en = langs.get("en_us.json", {})
    boss = {"liberated", "exterminated", "dethroned"}
    found_boss = set()
    for path in files:
        data = load_json(path)
        if not isinstance(data, dict):
            continue
        stem = path.stem
        if stem in boss:
            found_boss.add(stem)
            reqs = data.get("requirements")
            if reqs != [["kill", "inv"]]:
                # allow any OR of two criteria
                if not (isinstance(reqs, list) and len(reqs) == 1 and isinstance(reqs[0], list) and len(reqs[0]) >= 2):
                    err(f"advancement {stem}: expected OR requirements for kill/inventory, got {reqs}")
        display = data.get("display") or {}
        title = display.get("title") or {}
        desc = display.get("description") or {}
        for field, node in (("title", title), ("description", desc)):
            if isinstance(node, dict) and node.get("translate"):
                key = node["translate"]
                if key not in en:
                    err(f"advancement {stem} {field} lang missing: {key}")
    missing_boss = boss - found_boss
    if missing_boss:
        err(f"missing boss advancements: {sorted(missing_boss)}")


def validate_mirror(langs: dict[str, dict]) -> None:
    mirror_dir = MAIN / "data" / MODID / "mirror_of_guidance"
    if not mirror_dir.exists():
        err("missing mirror_of_guidance directory")
        return
    files = {p.stem: p for p in mirror_dir.glob("*.json")}
    if len(files) < 18:
        err(f"expected >= 18 mirror nodes, found {len(files)}")
    required = {
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
    }
    missing = sorted(required - set(files))
    if missing:
        err(f"missing required mirror nodes: {missing}")
    en = langs.get("en_us.json", {})
    nodes: dict[str, dict] = {}
    for stem, path in files.items():
        data = load_json(path)
        if not isinstance(data, dict):
            continue
        nodes[stem] = data
        if "icon" not in data or "x" not in data or "y" not in data:
            err(f"mirror node {stem}: missing icon/x/y")
        name_key = f"mirror_of_guidance.{MODID}.{stem}.name"
        desc_key = f"mirror_of_guidance.{MODID}.{stem}.desc"
        if name_key not in en:
            err(f"mirror node {stem}: missing lang {name_key}")
        if desc_key not in en:
            err(f"mirror node {stem}: missing lang {desc_key}")
        for child in data.get("children") or []:
            if not isinstance(child, str):
                err(f"mirror node {stem}: invalid child {child}")
                continue
            child_id = child.split(":", 1)[-1]
            if child_id not in files:
                err(f"mirror node {stem}: child '{child}' not found")


def validate_entities_and_loot() -> None:
    entity_registry = ROOT / "src" / "main" / "java" / "shiroroku" / "theaurorian" / "Registry" / "EntityRegistry.java"
    if not entity_registry.exists():
        err("EntityRegistry.java missing")
        return
    text = entity_registry.read_text(encoding="utf-8")
    ids = re.findall(r'ENTITIES\.register\("([a-z0-9_]+)"', text)
    if len(ids) < 15:
        err(f"EntityRegistry has only {len(ids)} entities")
    # projectiles/misc may legitimately lack loot
    no_loot_ok = {
        "cerulean_arrow",
        "crystal_arrow",
        "crystalline_beam",
        "sticky_spiker",
        "webbing",
    }
    loot_dir = MAIN / "data" / MODID / "loot_tables" / "entities"
    loot_files = {p.stem for p in loot_dir.glob("*.json")} if loot_dir.exists() else set()
    for eid in ids:
        if eid in no_loot_ok:
            continue
        if eid not in loot_files:
            err(f"entity '{eid}' missing loot table")
    if len(loot_files) < 12:
        err(f"expected >= 12 entity loot tables, found {len(loot_files)}")


def validate_structures() -> None:
    struct_root = MAIN / "data" / MODID / "structures"
    if not struct_root.exists():
        err("missing structures directory")
        return
    expected_min = {
        "runestone": 20,
        "darkstone": 14,
        "moontemple": 11,
        "umbratower": 1,
        "ruins": 3,
        "weepingwillow": 5,
    }
    by_folder: dict[str, int] = defaultdict(int)
    total = 0
    for nbt in struct_root.rglob("*.nbt"):
        total += 1
        rel = nbt.relative_to(struct_root)
        folder = rel.parts[0] if len(rel.parts) > 1 else "_root"
        by_folder[folder] += 1
        # gzip or raw NBT must be non-empty and parseable as gzip or start with NBT header
        raw = nbt.read_bytes()
        if len(raw) < 8:
            err(f"structure too small: {nbt.relative_to(ROOT)}")
            continue
        if raw[:2] == b"\x1f\x8b":
            try:
                decompressed = gzip.decompress(raw)
                if len(decompressed) < 4:
                    err(f"structure gzip empty: {nbt.relative_to(ROOT)}")
            except Exception as exc:  # noqa: BLE001
                err(f"structure gzip invalid {nbt.relative_to(ROOT)}: {exc}")
        elif raw[0] not in (0x0A, 0x09, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08):
            # uncompressed NBT compounds start with TAG_Compound (0x0A)
            warn(f"structure may not be NBT: {nbt.relative_to(ROOT)} header={raw[:4]!r}")
    if total < 50:
        err(f"expected >= 50 structure NBTs, found {total}")
    for folder, minimum in expected_min.items():
        got = by_folder.get(folder, 0)
        if got < minimum:
            err(f"structures/{folder}: expected >= {minimum} nbt, found {got}")

    # structure definitions + sets
    wg_struct = MAIN / "data" / MODID / "worldgen" / "structure"
    wg_set = MAIN / "data" / MODID / "worldgen" / "structure_set"
    if not wg_struct.exists() or len(list(wg_struct.glob("*.json"))) < 5:
        err("expected >= 5 worldgen/structure definitions")
    if not wg_set.exists() or len(list(wg_set.glob("*.json"))) < 5:
        err("expected >= 5 worldgen/structure_set definitions")


def validate_biomes() -> None:
    biome_dir = MAIN / "data" / MODID / "worldgen" / "biome"
    if not biome_dir.exists():
        err("missing biome directory")
        return
    files = list(biome_dir.glob("*.json"))
    if len(files) < 7:
        err(f"expected >= 7 biomes, found {len(files)}")
    required = {
        "aurorian_forest",
        "aurorian_plains",
        "aurorian_rough_forest",
        "aurorian_forest_hills",
        "aurorian_lakes",
        "aurorian_overgrowth",
        "weeping_willow_forest",
    }
    stems = {p.stem for p in files}
    missing = sorted(required - stems)
    if missing:
        err(f"missing biomes: {missing}")
    for path in files:
        data = load_json(path)
        if not isinstance(data, dict):
            continue
        effects = data.get("effects") or {}
        if "music" not in effects:
            err(f"biome {path.stem}: missing effects.music")
        else:
            music = effects["music"]
            event = music.get("sound") if isinstance(music, dict) else None
            # 1.19 format may nest event
            if event is None and isinstance(music, dict):
                nested = music.get("event")
                if isinstance(nested, dict):
                    event = nested.get("sound")
                elif isinstance(nested, str):
                    event = nested
            if isinstance(event, str) and not event.startswith(f"{MODID}:"):
                warn(f"biome {path.stem}: music event not mod-namespaced: {event}")


def validate_dimension() -> None:
    dim = MAIN / "data" / MODID / "dimension" / "the_aurorian.json"
    if not dim.exists():
        err("missing dimension/the_aurorian.json")
        return
    data = load_json(dim)
    if not isinstance(data, dict):
        return
    # multi_noise biomes should include willow etc
    try:
        biomes = data["generator"]["biome_source"]["biomes"]
        biome_ids = {b.get("biome") for b in biomes if isinstance(b, dict)}
    except Exception:  # noqa: BLE001
        err("dimension JSON missing multi_noise biomes")
        return
    need = {
        f"{MODID}:weeping_willow_forest",
        f"{MODID}:aurorian_forest",
        f"{MODID}:aurorian_plains",
    }
    missing = sorted(need - biome_ids)
    if missing:
        err(f"dimension multi_noise missing biomes: {missing}")


def validate_chest_loot() -> None:
    chests = MAIN / "data" / MODID / "loot_tables" / "chests"
    required_dirs = {
        "runestone": 3,
        "darkstone": 3,
        "moontemple": 3,
        "ruins": 1,
    }
    for name, minimum in required_dirs.items():
        d = chests / name
        if not d.exists():
            err(f"missing chest loot dir: chests/{name}")
            continue
        n = len(list(d.glob("*.json")))
        if n < minimum:
            err(f"chests/{name}: expected >= {minimum} loot tables, found {n}")


def validate_recipes() -> None:
    recipes = MAIN / "data" / MODID / "recipes"
    if not recipes.exists():
        err("missing recipes directory")
        return
    files = list(recipes.rglob("*.json"))
    if len(files) < 150:
        err(f"expected >= 150 recipes, found {len(files)}")
    # Boss weapon MF recipes
    mf = recipes / "moonlight_forge"
    for name in ("keepers_bow.json", "queens_chipper.json", "moon_shield.json"):
        if not (mf / name).exists():
            err(f"missing moonlight_forge recipe: {name}")


def validate_registry_java_sync() -> None:
    """Spot-check that critical registry IDs still exist in Java sources."""
    checks = {
        "ItemRegistry.java": [
            "dungeon_locator",
            "keepers_bow",
            "queens_chipper",
            "moon_shield",
            "slime_boots",
            "mirror_of_guidance",
            "trophy_keeper",
            "trophy_moon_queen",
            "trophy_spider",
        ],
        "BlockRegistry.java": [
            "boss_spawner",
            "weeping_willow_leaves",
            "aurorian_farm_tile",
            "umbra_stone",
            "silentwood_chest",
        ],
        "SoundRegistry.java": ["music", "weepingwillowbell"],
        "ParticleRegistry.java": ["weeping_willow_drip"],
    }
    base = ROOT / "src" / "main" / "java" / "shiroroku" / "theaurorian"
    for rel, ids in checks.items():
        path = next(base.rglob(rel), None)
        if path is None:
            err(f"missing Java registry file: {rel}")
            continue
        text = path.read_text(encoding="utf-8")
        for rid in ids:
            if f'"{rid}"' not in text:
                err(f"{rel}: missing registration id '{rid}'")


def main() -> int:
    print(f"Validating resources under {ROOT}")
    langs = validate_lang()
    validate_all_json_parse()
    validate_sounds(langs)
    validate_particles()
    validate_advancements(langs)
    validate_mirror(langs)
    validate_entities_and_loot()
    validate_structures()
    validate_biomes()
    validate_dimension()
    validate_chest_loot()
    validate_recipes()
    validate_registry_java_sync()

    for w in warnings:
        print(f"WARN: {w}")
    if errors:
        print(f"\nFAILED with {len(errors)} error(s):")
        for e in errors:
            print(f"  - {e}")
        return 1
    print(f"OK — resource integrity passed ({len(warnings)} warning(s))")
    return 0


if __name__ == "__main__":
    sys.exit(main())
