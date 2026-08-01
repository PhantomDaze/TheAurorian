#!/usr/bin/env bash
# Non-interactive sync of upstream 1.12 structure NBTs into the 1.19 datapack tree,
# then ID-remap with scripts/remap_structure_nbt.py.
#
# Why this exists:
#   zsh often aliases `cp` -> `cp -iv`, which prompts on overwrite and stalls
#   unattended / agent runs. Always use /usr/bin/cp with -f here.
#
# Usage:
#   bash scripts/sync_structures.sh           # copy + remap
#   bash scripts/sync_structures.sh --dry-run # show plan only
#   bash scripts/sync_structures.sh --remap-only
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

UP_STRUCT="${ROOT}/upstream/src/main/resources/assets/theaurorian/structures"
DST_STRUCT="${ROOT}/src/main/resources/data/theaurorian/structures"
REMAP_PY="${ROOT}/scripts/remap_structure_nbt.py"
CP="/usr/bin/cp"
MKDIR="/usr/bin/mkdir"
PYTHON="${PYTHON:-python3}"

DRY_RUN=0
REMAP_ONLY=0
for arg in "$@"; do
  case "$arg" in
    --dry-run) DRY_RUN=1 ;;
    --remap-only) REMAP_ONLY=1 ;;
    -h|--help)
      sed -n '2,20p' "$0"
      exit 0
      ;;
    *)
      echo "Unknown arg: $arg" >&2
      exit 2
      ;;
  esac
done

if [[ ! -d "$UP_STRUCT" ]]; then
  echo "ERROR: upstream structures missing: $UP_STRUCT" >&2
  echo "Clone first: git clone --branch 1.12.2 --single-branch --depth 1 https://github.com/shiroroku/TheAurorian.git upstream" >&2
  exit 1
fi

if [[ ! -x "$CP" ]]; then
  echo "ERROR: /usr/bin/cp not found" >&2
  exit 1
fi

copy_tree() {
  local src_rel="$1"
  local dst_rel="$2"
  local src="${UP_STRUCT}/${src_rel}"
  local dst="${DST_STRUCT}/${dst_rel}"
  if [[ ! -d "$src" ]]; then
    echo "WARN: skip missing upstream dir: ${src_rel}"
    return 0
  fi
  local count
  count="$(find "$src" -type f -name '*.nbt' | wc -l | tr -d ' ')"
  echo "  ${src_rel}/  ->  structures/${dst_rel}/  (${count} nbt)"
  if [[ "$DRY_RUN" -eq 1 ]]; then
    return 0
  fi
  "$MKDIR" -p "$dst"
  # -f: never prompt; -a: preserve times; do not use shell `cp` alias
  # shellcheck disable=SC2086
  find "$src" -type f -name '*.nbt' -print0 | while IFS= read -r -d '' f; do
    base="$(basename "$f")"
    "$CP" -f "$f" "${dst}/${base}"
  done
}

echo "=== Structure NBT sync (non-interactive) ==="
echo "upstream: $UP_STRUCT"
echo "dest:     $DST_STRUCT"
echo "cp:       $CP -f"
echo

if [[ "$REMAP_ONLY" -eq 0 ]]; then
  echo "-- copy --"
  copy_tree "darkstone" "darkstone"
  copy_tree "moontemple" "moontemple"
  copy_tree "ruins" "ruins"
  copy_tree "umbratower" "umbratower"
  copy_tree "weepingwillow" "weepingwillow"
  # Runestone pieces kept under upstream_ref (port uses custom bottom/top jigsaw pieces)
  copy_tree "runestonedungeon" "runestone/upstream_ref"
  echo
fi

if [[ "$DRY_RUN" -eq 1 ]]; then
  echo "-- remap (dry-run) --"
  "$PYTHON" "$REMAP_PY" --dry-run \
    "$DST_STRUCT/darkstone" \
    "$DST_STRUCT/moontemple" \
    "$DST_STRUCT/ruins" \
    "$DST_STRUCT/umbratower" \
    "$DST_STRUCT/weepingwillow" \
    "$DST_STRUCT/runestone/upstream_ref" \
    2>&1 | tail -30
  echo
  echo "DRY-RUN complete (no files written)."
  exit 0
fi

echo "-- remap IDs (1.12 -> 1.19) --"
"$PYTHON" "$REMAP_PY" \
  "$DST_STRUCT/darkstone" \
  "$DST_STRUCT/moontemple" \
  "$DST_STRUCT/ruins" \
  "$DST_STRUCT/umbratower" \
  "$DST_STRUCT/weepingwillow" \
  "$DST_STRUCT/runestone/upstream_ref"

echo
echo "-- inventory --"
for d in darkstone moontemple ruins umbratower weepingwillow runestone/upstream_ref; do
  n="$(find "${DST_STRUCT}/${d}" -name '*.nbt' 2>/dev/null | wc -l | tr -d ' ')"
  printf '  %-28s %s nbt\n' "$d" "$n"
done
total="$(find "$DST_STRUCT" -name '*.nbt' | wc -l | tr -d ' ')"
echo "  TOTAL (incl gametest/jigsaw): ${total} nbt"
echo
echo "OK — structure sync finished (no interactive prompts)."
