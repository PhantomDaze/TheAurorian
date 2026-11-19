# 任务：1.21.1 / NeoForge 21.1.248 运行时验证（已完成）

## 结论速览
- A 无头服务端：**成功**，`Done (3.626s)!`；结构模板/方块实体/默认装备/战利品谓词/配方均实测通过。
- B xvfb 软件渲染客户端：**成功启动**（llvmpipe GL 4.6），0 ERROR、无模型/贴图加载告警。
- 未 commit / push / stash / reset / checkout；未改 src/ 与构建文件；未触碰 1.19.2 / 1.20.1f 两棵树。

## 环境
- JDK21 `/usr/lib/jvm/java-21-openjdk` 21.0.13
- xvfb-run / Xvfb / glxinfo 可用；Mesa llvmpipe，OpenGL 4.6 core
- 端口 25565 被另一棵 1.19.2 树的 runServer 占用 → 本树改用 server-port=25777，rcon.port=25775

## A 部分命令与结果
| 项 | 结果 |
|---|---|
| `./gradlew runServer --offline` | EXIT=0（正常 stop），`Done (3.626s)` |
| `place template theaurorian:ruins/ruins_2 10 100 10` | ✅ Loaded template |
| `place template theaurorian:runestone/bottom 50 100 50` | ✅ Loaded template |
| `place template theaurorian:darkstone/darkstone_corner 90 100 90` | ✅ Loaded template |
| `data get block 11 101 11` | ✅ `id: theaurorian:silentwood_chest` |
| `data get block 55 103 55` | ✅ `id: minecraft:mob_spawner`，SpawnData=theaurorian:undead_knight |
| `data get block 93 103 93` | ✅ `id: minecraft:mob_spawner`，SpawnData=theaurorian:spiderling |
| `/summon theaurorian:undead_knight ... ArmorItems` | ⚠️ `[{},{},{},{}]`（`/summon` 不触发 finalizeSpawn，方法无效） |
| 发射器+刷怪蛋生成亡灵骑士 ArmorItems | ✅ `[knight_boots, knight_leggings, knight_chestplate, knight_helmet]`（HEAD 为头盔） |
| 发射器+刷怪蛋生成亡灵骑士 HandItems | ✅ `[aurorian_stone_sword, {}]` |
| 发射器+刷怪蛋生成月之侍僧 ArmorItems | ⚠️ `[{},{},{},{}]`（代码未设置头盔） |
| 月之侍僧 HandItems | ✅ `[aurorian_stone_sword(击退II), {}]` |
| 着火猪 loot spawn | ✅ `Cooked Aurorian Pork` |
| 未着火猪 loot spawn | ✅ `Raw Aurorian Pork` |
| 羊/兔 loot spawn（首次） | ✅ 成功掉落（当时在落水中，火被浇灭，仅验证解析） |
| Crafter 合成 mirror_of_guidance（`c:glass_blocks`） | ✅ 产出 `theaurorian:mirror_of_guidance` |
| `setblock theaurorian:silentwood_wall_torch[facing=north]` | ✅（该方块属性是 `facing`，没有 `rotation`） |
| `setblock theaurorian:scrapper` + `data get block` | ✅ BE 含 `crafting_progress`、`items` |
| `recipe give @a` | ⚠️ 无玩家，改由 Crafter 验证成功 |

## A4 日志审查
- `grep -i theaurorian` 42 行，唯一相关 WARN：`theaurorian.refmap.json ... could not be read`（开发环境，可忽略）。
- `Unknown block entity` / `Failed to load` / `Couldn't parse` / `Failed to parse` / `Invalid condition` / `loot table` / `Missing texture` / `Unable to load model`：**均为 0**。
- 无害 WARN：fml.toml correcting、命令参数歧义、VanillaPackResourcesBuilder schema、离线模式提示、NeoForge 版本检查失败。
- `neoforge:needs_wood_tool` 标签缺失 WARN：本树 src 中无任何引用，判定为 NeoForge/外部数据包问题，与本 mod 无关。

## B 部分
- 命令：`LIBGL_ALWAYS_SOFTWARE=1 MESA_GL_VERSION_OVERRIDE=4.5 GALLIUM_DRIVER=llvmpipe xvfb-run -a -s "-screen 0 1280x720x24" ./gradlew runClient --offline`，EXIT=124（存活到 420s 超时，预期）。
- 证据：`Backend library: LWJGL 3.3.3`、`Setting user: Dev`、`Sound engine started`、全部 `textures/atlas/*.png-atlas` 创建、`Loaded 0 entity animations`；进程存活至超时未崩溃。
- 0 ERROR / 20 WARN；**未出现** `Unable to load model` / `Missing texture` / `Texture ... not found` / `No model for` / `Missing sprite`。
- 墙火把：blockstate/model 存在于 `build/resources/main`，`parent=minecraft:block/template_torch_wall`，纹理 `theaurorian:block/silentwood_torch` / `moon_torch` 均存在并成功加载（无告警）。
- 粒子：`particles/aurorian_slime.json` 引用 `theaurorian:aurorian_slime_ball`，纹理 `textures/particle/aurorian_slime_ball.png` 存在，particles atlas 成功创建，无 sprite 缺失告警。

## 发现的运行时问题（未改代码）
1. **MoonAcolyte 无头盔**：`src/main/java/shiroroku/theaurorian/Entities/MoonAcolyte/MoonAcolyteEntity.java:85-90` 的 `populateDefaultEquipmentSlots` 只设置主手剑；若需求要求“HEAD 非空头盔”，此处缺 HEAD 赋值。亡灵骑士已正确（`UndeadKnightEntity.java:71-84`）。
2. **测试方法陷阱**：`/summon` 不调用 `finalizeSpawn`（randomizeProperties=false），因此 `ArmorItems` 为空不能作为修复失效的证据；需用发射器刷怪蛋/自然生成/刷怪笼验证。
3. **现有 GameTest 断言偏弱**：`src/main/java/shiroroku/theaurorian/GameTests/AurorianGameTests.java:267-282` 的 `entities_undeadKnightWearsGear` 在装备为空时也会 pass。
4. `silentwood_wall_torch` 无 `rotation` 属性（任务给的指令示例不适用，实际用 `facing`）；非缺陷。
5. 首次启动失败是端口 25565 被另一棵树占用（环境问题），非 mod 缺陷。

## 清理
- 世界 `run/smoke_world` 已删除；崩溃报告已复制到 `/tmp/rt-1211-crash-bind.txt` 并移除。
- 日志：`/tmp/rt-1211-boot-A4.log`、`/tmp/rt-1211-boot-craft.log`、`/tmp/rt-1211-client.log`。
- `git status -- src/` 与任务开始时基线 diff 为空；build.gradle / gradle.properties / settings.gradle 未改；无提交。
