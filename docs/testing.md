# Automated Tests — Full Port Content Gate

Fast, headless verification of the 1.12.2 → 1.19.2 port. **Does not** boot a Minecraft client/server.

## Commands

```bash
# Categorized resource / datapack integrity (Python)
./gradlew validateResources
# or
python3 scripts/validate_resources.py

# Full suite: validateResources + JUnit (logic + categorized content)
./gradlew test

# Verification lifecycle
./gradlew check
```

After adding blocks/items/entities, regenerate assets if needed:

```bash
./gradlew runData
./gradlew test
```

## Categories

| ID | Category | Checks |
|----|----------|--------|
| **A** | Language | `en_us` / `zh_cn` / `es_es` present; key sets identical; ≥350 en keys |
| **B** | Blocks & items | ≥90 blocks / ≥120 items; every block has blockstate + lang; items have models + lang; required IDs (portal, farm tile, MF, locator, trophies, boss weapons…) |
| **C** | Entities | ≥19 entities; all have lang; living have loot + spawn egg + egg lang; 3 bosses + 3 passives present |
| **D** | Structures | NBT minima (runestone≥20, darkstone≥14, moontemple≥11, …); 8 structure defs + sets; set→structure links; single_template NBT exists |
| **E** | Worldgen | 7 biomes + music; placed↔configured features; biome feature refs; dimension multi_noise includes all biomes; noise_settings |
| **F** | Recipes & chest loot | ≥180 recipes; ≥20 MF + ≥40 scrapper; boss MF recipes; chest dirs; recipe results + loot item IDs resolve |
| **G** | Advancements | ≥15; root; boss OR-requirements; parent links; display lang keys |
| **H** | Mirror | ≥18 nodes; required mainline nodes; icon/x/y; name/desc lang; no dangling children |
| **I** | Sounds & particles | sounds.json + ≥6 ogg; particles.json; SoundRegistry / ParticleRegistry IDs |
| **J** | Tags & generated | ≥40 tag files; ≥80 block loot; Feature/Structure registry markers |
| **K** | JSON / pack | all JSON parse; mods.toml + pack.mcmeta |

## Code layout

| Path | Role |
|------|------|
| `scripts/validate_resources.py` | Full categorized gate (source of truth for CI stats) |
| `src/test/.../content/PortContentCategoriesTest.java` | Same categories as nested JUnit tests |
| `src/test/.../content/ContentTestSupport.java` | Shared path/JSON/registry parsers |
| `src/test/.../DatapackSmokeTest.java` | Lightweight smoke subset |
| `src/test/.../ResourceIntegrityTest.java` | JUnit bridge → Python script |
| `src/test/.../Util/*Test.java` | Pure logic (`SimpleTimer`, `ModUtil.wave`, mouse hitbox) |

## Gaps this suite already fixed

- Missing `disturbed_hollow` entity loot
- Missing Mirror lang (`aurorian_steel`, `crafting`, `ore_*`, `umbra`)
- Missing spawn eggs for spiderling / acolyte / sprite / spirit / disturbed hollow / passives (+ lang + datagen models)
- Projectile entity lang keys

## Out of scope (needs game)

See `docs/port-plan.md` §8.8: `/locate`, boss fights, portal round-trips, machine GUI playtest.  
Optional later: Forge GameTest world harness.

## CI

```yaml
- run: ./gradlew test --no-daemon
```
