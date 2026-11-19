# 1.21.1 收尾修正（粒子/标签/死代码/复数 recipes 审计）

- 状态：已完成（2026-11-18）
- 日期：2026-11-18
- 目标树：`Aurorian`（1.21.1 / NeoForge 21.1.248）

## 计划

1. 校验 `assets/theaurorian/particles/*.json` 的 sprite 贴图与 `sounds.json` 的 ogg 是否齐全。
2. 全量校验 JSON 中 `c:` 标签引用，对照 NeoForge universal jar 与本项目 `data/c/tags/`。
3. 删除 `MoonShieldItem` 中 `Monster` 内层 `instanceof Player` 的不可达分支。
4. 分析 `data/theaurorian/recipes/`（复数）残留，做三分类，仅报告。
5. `validate_resources.py` + `compileJava` 最终验证。

## 结果记录

- 任务 1：`particles/` 仅 2 个定义；`theaurorian:aurorian_slime_ball` 贴图存在（PASS），3 个 `minecraft:` 原版 sprite 不在本模组资源内；`sounds.json` 6 个 ogg 全部存在。
- 任务 2：11 个唯一 `c:` 标签引用全部可解析（NeoForge 或本项目 tags），无缺失。
- 任务 3：已删除 `MoonShieldItem.java` 中不可达的内层 `if (e instanceof Player ...)`。
- 任务 4：复数 `recipes/` 26 个文件全部为 `tconstruct:*` 类型，且 1.21.1 构建中 Tinkers 集成被显式关闭（`gradle.properties` / `build.gradle`），全部归为类别③；无类别②。
- 任务 5：见下方验证输出。

## 未决事项

- 无（本任务范围内）。复数 `recipes/` 是否删除/迁移属产品决策，留给人工。
