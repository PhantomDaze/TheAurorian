#!/usr/bin/env bash
# Full pre-launch → client flow for structure layout inspection.
# 1) Delete old ta_structure_layout world
# 2) Launch NeoForge client with theaurorian.structureLayoutTest=true
#    (auto superflat create + place 3 boss structures; game stays open)
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

# Prefer JDK 21 when available (NeoForge 1.21.1).
if [[ -z "${JAVA_HOME:-}" ]]; then
  for candidate in /usr/lib/jvm/java-21-openjdk /usr/lib/jvm/java-21-jdk /usr/lib/jvm/temurin-21; do
    if [[ -x "$candidate/bin/java" ]]; then
      export JAVA_HOME="$candidate"
      export PATH="$JAVA_HOME/bin:$PATH"
      break
    fi
  done
fi

bash "$ROOT/scripts/prepare_structure_layout_test.sh"

echo "[run_structure_layout_test] starting Gradle runClientStructureLayout …"
exec ./gradlew runClientStructureLayout --no-daemon "$@"
