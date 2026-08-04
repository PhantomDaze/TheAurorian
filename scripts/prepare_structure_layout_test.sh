#!/usr/bin/env bash
# Pre-launch cleanup for StructureLayoutTest.
# Deletes the previous auto-created test world so every run starts fresh.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORLD_NAME="${TA_STRUCTURE_WORLD_NAME:-ta_structure_layout}"
SAVES_DIR="${TA_RUN_SAVES:-$ROOT/run/saves}"
WORLD_DIR="$SAVES_DIR/$WORLD_NAME"

echo "[prepare_structure_layout_test] saves=$SAVES_DIR world=$WORLD_NAME"

if [[ -e "$WORLD_DIR" ]]; then
  rm -rf "$WORLD_DIR"
  echo "[prepare_structure_layout_test] deleted $WORLD_DIR"
else
  echo "[prepare_structure_layout_test] no previous world at $WORLD_DIR"
fi

# Drop lock / leftover crash markers that can block world create.
rm -f "$SAVES_DIR/${WORLD_NAME}.lock" 2>/dev/null || true

mkdir -p "$SAVES_DIR"
echo "[prepare_structure_layout_test] ready"
