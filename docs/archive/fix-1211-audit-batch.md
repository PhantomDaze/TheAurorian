# 1.21.1 缺陷修复批次（A–O）— 已完成

完成：2026-11-15
范围：`Aurorian/`（1.21.1 NeoForge）15 组缺陷；参考 `TheAurorian-1.20.1f/`（只读）。

## 结果
- A 猪/兔熟食：`entity_properties` 条件改为 `predicate.flags.is_on_fire`。
- B/C 墙火把：新增 `AurorianWallTorchBlock`（1.21.1 构造 `(ParticleTypes.FLAME, props)`）、`regBlockOnly`、
  `StandingAndWallBlockItem(..., Direction.DOWN)`、4 变体 blockstate（east0/north270/south90/west180）、
  `template_torch_wall` 模型、平面手持模型、墙→站立火把掉落、cutout 渲染层、lang/描述键。
- D 结构箱：11 个 NBT 的 palette 与 BE id 改为 `theaurorian:silentwood_chest`；4 个结构类 filterBlocks 同时匹配两种箱子。
  `StructureChests` 未硬编码 `Blocks.CHEST`（用 `instanceof ChestBlock`），无需改。
- E 月后：改掉静木箱。
- F 尖刺胸甲：新增 scrapper 配方，产出 9 umbra_scrap。
- G Scrapper 分面：`AbstractInventoryBlockEntity` 增加按面 `IItemHandler` 包装器；
  `CapabilityRegistry` 传 `side`；UP={1}、DOWN={2}、其它={0}；仅槽 2 可抽、槽 2 不可插；side=null 返回完整 handler。
- H 矿 tag：补 item `c:ores/coal`，item+block `c:ores/cerulean`、`c:ores/moonstone`，并挂到 `c:ores` 父标签。
- I 燃烧时间：crafting_table/ladder/chest 300、torch 100、coal_block 16000。
- J 茶：`alwaysEdible()` + `getUseDuration(ItemStack, LivingEntity)=16`。
- K 亡灵骑士：补 HEAD、盔甲掉率 0.085F、覆写 1.21.1 的 4 参 `finalizeSpawn` 调用 `populateDefaultEquipmentSlots`；
  月侍同样补 `finalizeSpawn`。`undead_knight` 已 `.fireImmune()`（无需改）。
- L 扰动空壳：`!level.canSeeSky(pos)`。
- M 月盾：删除清冷却行，仅 `Monster` 触发。
- N 蜘蛛母：移植整列通行校验、脚部钳制、`move(MoverType.SELF, ...)`、最大高度限制。
- O 刷怪笼：方块实体 id `minecraft:spawner` → `minecraft:mob_spawner`，palette 方块名不变。
  额外修正审计清单未列出的 `runestone/bottom.nbt`（22 个）与 `ruins/graveyard.nbt`（1 个）。

## 与说明不一致（以代码为准）
- 1.21.1 `Mob.finalizeSpawn` 只有 4 个参数（无 CompoundTag），按 4 参覆写。
- `WallTorchBlock` 构造确认为 `(SimpleParticleType, Properties)`。
- `WallTorchBlock#getDescriptionId` 复用 vanilla 火把键，已在子类改为自身 lang key。
- 仓库校验脚本把正确的 `minecraft:mob_spawner` 误判为 legacy，且不识别 `regBlockOnly`；已修正脚本语义。

## 验证
- `compileJava` exit 0；`runData` exit 0；`test` exit 0；`validate_resources.py` 0 error / 0 warning。
- D 5 条断言与 O 断言全部 PASS（脚本输出见任务报告）。
- 所有修改/新增 JSON 通过 `python3 -m json.tool`。

## 剩余风险
- NBT / 结构生成、刷怪笼、双箱合并、Scrapper 物流分面、实体装备需进游戏验证。
- 结构模板缺少 `type` 属性时依赖 vanilla 默认值 `single`。
