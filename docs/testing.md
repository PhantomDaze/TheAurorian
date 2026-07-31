# Automated Tests

Fast gates that do **not** boot a full Minecraft client/server.

## Commands

```bash
# Resource / datapack integrity only (Python, ~1s)
./gradlew validateResources
# or
python3 scripts/validate_resources.py

# Full unit + datapack smoke suite (includes validateResources)
./gradlew test

# Full verification lifecycle
./gradlew check
```

## What is covered

| Layer | Location | Scope |
|-------|----------|--------|
| Resource gate | `scripts/validate_resources.py` | Lang key parity (en/zh/es), sounds+ogg, particles, 15 advancements (boss OR-logic), 18 Mirror nodes + graph links, entity loot coverage, structure NBT minima per dungeon, 7 biomes+music, dimension multi_noise, chest loot dirs, MF boss recipes, critical Java registry IDs |
| JUnit datapack smoke | `src/test/java/.../DatapackSmokeTest.java` | Same themes in-JVM via Gson/files |
| JUnit resource bridge | `src/test/java/.../ResourceIntegrityTest.java` | Invokes the Python gate from JUnit |
| Pure logic | `SimpleTimerTest`, `ModUtilMathTest`, `RenderUtilMouseTest` | Timer/wave/mouse hitbox helpers |

## What is intentionally out of scope

- In-game `/locate`, boss fights, portal round-trips, GUI click paths (see `docs/port-plan.md` §8.8)
- Forge GameTest world harness (heavier; optional follow-up)
- TCon/ConArm/CT compat (§9 exempt)

## CI suggestion

```yaml
- run: ./gradlew test --no-daemon
```
