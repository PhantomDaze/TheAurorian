# 修复 1.21 指引之镜 GUI 模糊问题

- **任务**：mirror-blur-fix
- **分支**：1.21.1
- **日期**：2026-08-14
- **状态**：已完成

## 问题描述

用户报告：1.21 版本打开指引之镜（MirrorOfGuidance）GUI 时，镜面内容（节点、文字、图标）变得模糊，GUI 被当作背景处理。

## 根因分析

`MirrorOGScreen.render()` 的结构是：

1. 顶部调用 `renderTransparentBackground(graphics)`（旧修复，来自 c0d9bc65，只挡住了第一处背景）
2. 绘制所有镜面内容（星空、月亮、节点、文字、边框）
3. **末尾调用 `super.render(graphics, ...)`**

在 1.21.1 中，`Screen.render()` 实现为：

```java
public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(guiGraphics, mouseX, mouseY, partialTick); // ← 会模糊
    for (Renderable renderable : this.renderables) { ... }
}
```

而 `Screen.renderBackground`（1.21.1，NeoForge 21.1.248）：

```java
public void renderBackground(...) {
    if (level == null) renderPanorama(...);
    this.renderBlurredBackground(partialTick);   // processBlurEffect — 高斯模糊当前帧缓冲
    this.renderMenuBackground(guiGraphics);      // 铺深色菜单纹理
    ...ScreenEvent.BackgroundRendered...
}
```

因此 **`super.render()` 在内容绘制完成后再次调用 `renderBackground` → `processBlurEffect`，把已经画进帧缓冲的镜面内容整体模糊掉**——即用户所说的"GUI 被当作背景处理"。c0d9bc65 只把顶部那处背景换成了 `renderTransparentBackground`，没处理末尾 `super.render()` 里的二次背景。

对照：ScrapperScreen / MoonlightForgeScreen 在内容之前调用 `renderBackground`（模糊只作用于容器背后的世界），且容器贴图由 `renderBg` 在之后覆盖内容区，故不受影响——无需改动。

## 修复方案

在 `MirrorOGScreen` 中重写 `renderBackground` 为空操作：

```java
@Override
public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    // No-op ...
}
```

这样末尾 `super.render()` 内对 `this.renderBackground(...)` 的动态分派走空操作，不会触发 `processBlurEffect`，镜面内容保持清晰。顶部 `renderTransparentBackground` 保留，用于透视玻璃的暗色渐变。

## 改动文件

- `src/main/java/shiroroku/theaurorian/Items/MirrorOfGuidance/MirrorOGScreen.java`

## 验证

- [x] `export JAVA_HOME=/usr/lib/jvm/java-21-openjdk && ./gradlew compileJava` — BUILD SUCCESSFUL
- [x] IDE 检查 `get_file_problems` — 无错误
- [ ] （可选）运行游戏目视检查镜面文字清晰、背景不模糊

## 搁置项 / 待办

- 暂无
