#!/usr/bin/env python3
"""Mechanical Forge → NeoForge import/type renames for 1.21.1 port.

Safe mechanical layer only. Semantic API changes still need compile-fix passes.
Does NOT delete content. Run from repo root.
"""
from __future__ import annotations

import re
from pathlib import Path

ROOT = Path("src/main/java")

# Exact import line replacements (without trailing semicolon)
IMPORT_MAP: dict[str, str] = {
    "import net.minecraftforge.api.distmarker.Dist": "import net.neoforged.api.distmarker.Dist",
    "import net.minecraftforge.api.distmarker.OnlyIn": "import net.neoforged.api.distmarker.OnlyIn",
    "import net.minecraftforge.client.event.EntityRenderersEvent": "import net.neoforged.neoforge.client.event.EntityRenderersEvent",
    "import net.minecraftforge.client.event.RegisterColorHandlersEvent": "import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent",
    "import net.minecraftforge.client.event.RegisterItemDecorationsEvent": "import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent",
    "import net.minecraftforge.client.event.RegisterParticleProvidersEvent": "import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent",
    "import net.minecraftforge.client.event.RenderHighlightEvent": "import net.neoforged.neoforge.client.event.RenderHighlightEvent",
    "import net.minecraftforge.client.extensions.common.IClientItemExtensions": "import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions",
    "import net.minecraftforge.client.model.data.ModelData": "import net.neoforged.neoforge.client.model.data.ModelData",
    "import net.minecraftforge.client.model.generators.BlockStateProvider": "import net.neoforged.neoforge.client.model.generators.BlockStateProvider",
    "import net.minecraftforge.client.model.generators.ConfiguredModel": "import net.neoforged.neoforge.client.model.generators.ConfiguredModel",
    "import net.minecraftforge.client.model.generators.ItemModelProvider": "import net.neoforged.neoforge.client.model.generators.ItemModelProvider",
    "import net.minecraftforge.client.model.generators.ModelFile": "import net.neoforged.neoforge.client.model.generators.ModelFile",
    "import net.minecraftforge.common.ForgeConfigSpec": "import net.neoforged.neoforge.common.ModConfigSpec",
    "import net.minecraftforge.common.ForgeSpawnEggItem": "import net.neoforged.neoforge.common.DeferredSpawnEggItem",
    "import net.minecraftforge.common.ForgeTier": "import net.neoforged.neoforge.common.SimpleTier",
    "import net.minecraftforge.common.IPlantable": "import net.neoforged.neoforge.common.IPlantable",
    "import net.minecraftforge.common.PlantType": "import net.neoforged.neoforge.common.PlantType",
    "import net.minecraftforge.common.Tags": "import net.neoforged.neoforge.common.Tags",
    "import net.minecraftforge.common.ToolAction": "import net.neoforged.neoforge.common.ItemAbility",
    "import net.minecraftforge.common.ToolActions": "import net.neoforged.neoforge.common.ItemAbilities",
    "import net.minecraftforge.common.capabilities.Capability": "import net.neoforged.neoforge.capabilities.BlockCapability",
    "import net.minecraftforge.common.capabilities.ForgeCapabilities": "import net.neoforged.neoforge.capabilities.Capabilities",
    "import net.minecraftforge.common.data.BlockTagsProvider": "import net.neoforged.neoforge.common.data.BlockTagsProvider",
    "import net.minecraftforge.common.data.ExistingFileHelper": "import net.neoforged.neoforge.common.data.ExistingFileHelper",
    "import net.minecraftforge.common.extensions.IForgeMenuType": "import net.neoforged.neoforge.common.extensions.IMenuTypeExtension",
    "import net.minecraftforge.common.util.FakePlayer": "import net.neoforged.neoforge.common.util.FakePlayer",
    "import net.minecraftforge.common.util.FakePlayerFactory": "import net.neoforged.neoforge.common.util.FakePlayerFactory",
    "import net.minecraftforge.common.util.ITeleporter": "import net.neoforged.neoforge.common.util.ITeleporter",
    "import net.minecraftforge.common.util.LazyOptional": "import net.neoforged.neoforge.common.util.LazyOptional",
    "import net.minecraftforge.data.event.GatherDataEvent": "import net.neoforged.neoforge.data.event.GatherDataEvent",
    "import net.minecraftforge.event.AddReloadListenerEvent": "import net.neoforged.neoforge.event.AddReloadListenerEvent",
    "import net.minecraftforge.event.BuildCreativeModeTabContentsEvent": "import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent",
    "import net.minecraftforge.event.ForgeEventFactory": "import net.neoforged.neoforge.event.EventHooks",
    "import net.minecraftforge.event.RegisterCommandsEvent": "import net.neoforged.neoforge.event.RegisterCommandsEvent",
    "import net.minecraftforge.event.TickEvent": "import net.neoforged.neoforge.event.tick.PlayerTickEvent",
    "import net.minecraftforge.event.entity.EntityAttributeCreationEvent": "import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent",
    "import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent": "import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent",
    "import net.minecraftforge.event.entity.living.LivingDamageEvent": "import net.neoforged.neoforge.event.entity.living.LivingDamageEvent",
    "import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent": "import net.neoforged.neoforge.event.entity.living.LivingEvent",
    "import net.minecraftforge.event.entity.living.LivingFallEvent": "import net.neoforged.neoforge.event.entity.living.LivingFallEvent",
    "import net.minecraftforge.event.entity.player.PlayerEvent": "import net.neoforged.neoforge.event.entity.player.PlayerEvent",
    "import net.minecraftforge.eventbus.api.IEventBus": "import net.neoforged.bus.api.IEventBus",
    "import net.minecraftforge.eventbus.api.SubscribeEvent": "import net.neoforged.bus.api.SubscribeEvent",
    "import net.minecraftforge.fml.DistExecutor": "import net.neoforged.fml.util.thread.EffectiveSide",  # placeholder; callers rewritten
    "import net.minecraftforge.fml.InterModComms": "import net.neoforged.fml.InterModComms",
    "import net.minecraftforge.fml.ModLoadingContext": "import net.neoforged.fml.ModLoadingContext",
    "import net.minecraftforge.fml.common.Mod": "import net.neoforged.fml.common.Mod",
    "import net.minecraftforge.fml.config.ModConfig": "import net.neoforged.fml.config.ModConfig",
    "import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent": "import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent",
    "import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent": "import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent",
    "import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext": "import net.neoforged.fml.ModContainer",
    "import net.minecraftforge.gametest.GameTestHolder": "import net.neoforged.neoforge.gametest.GameTestHolder",
    "import net.minecraftforge.gametest.PrefixGameTestTemplate": "import net.neoforged.neoforge.gametest.PrefixGameTestTemplate",
    "import net.minecraftforge.items.IItemHandler": "import net.neoforged.neoforge.items.IItemHandler",
    "import net.minecraftforge.items.ItemStackHandler": "import net.neoforged.neoforge.items.ItemStackHandler",
    "import net.minecraftforge.items.SlotItemHandler": "import net.neoforged.neoforge.items.SlotItemHandler",
    "import net.minecraftforge.items.wrapper.InvWrapper": "import net.neoforged.neoforge.items.wrapper.InvWrapper",
    "import net.minecraftforge.network.NetworkHooks": "import net.neoforged.neoforge.network.NetworkHooks",  # may need MenuProvider open rewrite
    "import net.minecraftforge.registries.DeferredRegister": "import net.neoforged.neoforge.registries.DeferredRegister",
    "import net.minecraftforge.registries.ForgeRegistries": "import net.neoforged.neoforge.registries.NeoForgeRegistries",  # many call sites need BuiltInRegistries
    "import net.minecraftforge.registries.RegistryObject": "import net.neoforged.neoforge.registries.DeferredHolder",
}

# Non-import token replacements (order matters)
TOKEN_REPLACEMENTS: list[tuple[str, str]] = [
    ("ForgeConfigSpec", "ModConfigSpec"),
    ("ForgeSpawnEggItem", "DeferredSpawnEggItem"),
    ("ForgeTier", "SimpleTier"),
    ("ToolActions", "ItemAbilities"),
    ("ToolAction", "ItemAbility"),
    ("IForgeMenuType", "IMenuTypeExtension"),
    ("ForgeCapabilities", "Capabilities"),
    ("ForgeEventFactory", "EventHooks"),
    ("SpawnPlacementRegisterEvent", "RegisterSpawnPlacementsEvent"),
    ("RegistryObject<", "DeferredHolder<?, "),  # imperfect; fixed below with smarter pass
    ("MinecraftForge.EVENT_BUS", "NeoForge.EVENT_BUS"),
    ("@Mod.EventBusSubscriber", "@EventBusSubscriber"),
    ("Mod.EventBusSubscriber.Bus.FORGE", "EventBusSubscriber.Bus.GAME"),
    ("Mod.EventBusSubscriber.Bus.MOD", "EventBusSubscriber.Bus.MOD"),
    ("Bus.FORGE", "Bus.GAME"),
]


def rewrite_resource_location(text: str) -> str:
    # new ResourceLocation(MODID, "x") / new ResourceLocation(TheAurorian.MODID, "x")
    text = re.sub(
        r"new\s+ResourceLocation\(\s*([^,\n]+?)\s*,\s*([^)\n]+?)\s*\)",
        r"ResourceLocation.fromNamespaceAndPath(\1, \2)",
        text,
    )
    # new ResourceLocation("ns:path") single-arg — rare
    text = re.sub(
        r"new\s+ResourceLocation\(\s*(\"[^\"]+\")\s*\)",
        r"ResourceLocation.parse(\1)",
        text,
    )
    return text


def rewrite_registry_object(text: str) -> str:
    # RegistryObject<Foo> x -> DeferredHolder<Foo, Foo> is wrong for some;
    # Neo often uses DeferredHolder<Block, Block> etc.
    # Prefer: RegistryObject<T> -> DeferredHolder<?, T> then compile-fix.
    text = text.replace("RegistryObject<", "DeferredHolder<?, ")
    # Fix double-rewrite
    text = text.replace("DeferredHolder<?, DeferredHolder<?, ", "DeferredHolder<?, ")
    return text


def process_file(path: Path) -> bool:
    original = path.read_text(encoding="utf-8")
    text = original

    lines = text.splitlines(keepends=True)
    out_lines = []
    for line in lines:
        stripped = line.strip().rstrip(";")
        # map imports that end with ;
        key = stripped if stripped.startswith("import ") else None
        if key and key in IMPORT_MAP:
            indent = line[: len(line) - len(line.lstrip())]
            line = f"{indent}{IMPORT_MAP[key]};\n" if line.endswith("\n") else f"{indent}{IMPORT_MAP[key]};"
        out_lines.append(line)
    text = "".join(out_lines)

    for old, new in TOKEN_REPLACEMENTS:
        if old == "RegistryObject<":
            continue  # handled specially
        text = text.replace(old, new)

    text = rewrite_registry_object(text)
    text = rewrite_resource_location(text)

    # Ensure EventBusSubscriber import when annotation present
    if "@EventBusSubscriber" in text and "import net.neoforged.fml.common.EventBusSubscriber" not in text:
        text = text.replace(
            "import net.neoforged.fml.common.Mod;",
            "import net.neoforged.fml.common.EventBusSubscriber;\nimport net.neoforged.fml.common.Mod;",
        )

    # NeoForge EVENT_BUS import helper
    if "NeoForge.EVENT_BUS" in text and "import net.neoforged.neoforge.common.NeoForge" not in text:
        # add after package
        text = re.sub(
            r"(package [^;]+;\n)",
            r"\1\nimport net.neoforged.neoforge.common.NeoForge;\n",
            text,
            count=1,
        )

    if text != original:
        path.write_text(text, encoding="utf-8")
        return True
    return False


def main() -> None:
    changed = 0
    for path in sorted(ROOT.rglob("*.java")):
        if process_file(path):
            changed += 1
            print(f"updated {path}")
    print(f"files_changed={changed}")


if __name__ == "__main__":
    main()
