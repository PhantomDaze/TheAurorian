# 同步 1.20.1 最近 6 次提交到 1.21.1

## 1.20.1 最近 6 次提交核对
| commit | 主题 | 1.21.1 状态 |
|---|---|---|
| 68b8b23f | Migrate Patchouli guide to resource-pack data | ✅ 已同步（book.json `use_resource_pack:true`，条目在 `assets/`，version 20） |
| 71f0d14c | Align dungeon entity scale and hitboxes | ✅ 已同步（Spider/Keeper scale 2.0，moon_queen sized 0.6×1.95） |
| 03a75abe | Add throwable dungeon loot and rework item behavior | ✅ 已同步（KeepersBow onUseTick 连发、StickySpiker/Webbing 投掷、UmbraGreatsword 四效果、UmbraShield 过热冷却、lang） |
| c8f60d9f | Rebalance Tinkers handle durability modifiers | ✅ 已同步（5 材料_handle.durability 全部对齐） |
| 008443b6 | Match Crystalline Sprite rendering to upstream | ✅ 已同步（ground item 模型渲染、texture/item 资源齐全） |
| 0cc70e4b | Fix Strange Meat consumption by maids | ✅ 已同步（`instanceof Player` 守卫） |

## 漏掉的项
- `.gitignore` 缺 `/libs/`（1.20.1 有，1.21.1 漏）。已补上。

## 不属于本次同步范围的改动
- `gradle.properties` 的 `mod_version` 由 1.0.5 → 1.0.6（非本次会话改动，来源不明；未处理，留待确认）。

## 验证
- `validate_resources.py` 0 warning。
