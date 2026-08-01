package shiroroku.theaurorian.DataGen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataGenBlocks extends BlockStateProvider {

    public DataGenBlocks(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, TheAurorian.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // AUTO GENERATED
        List<RegistryObject<Block>> BASIC = new ArrayList<>();
        BASIC.addAll(BlockRegistry.BLOCKS_GEN.getEntries());
        BASIC.addAll(BlockRegistry.BLOCKS_GEN_NL.getEntries());
        BASIC.stream().map(Supplier::get).forEach(block -> {
            simpleBlock(block);
            simpleBlockItem(block);
        });
        BlockRegistry.BLOCKS_GEN_NL_PLANT.getEntries().stream().map(Supplier::get)
                .filter(block -> block != BlockRegistry.lavender_crop.get() && block != BlockRegistry.silkberry_crop.get())
                .forEach(block -> {
                    getVariantBuilder(block).partialState().setModels(new ConfiguredModel(models().cross(blockTexture(block).getPath(), blockTexture(block)).renderType("cutout")));
                    itemModels().getBuilder(ForgeRegistries.BLOCKS.getKey(block).getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", blockTexture(block));
                });
        cropBlock(BlockRegistry.lavender_crop.get(), "lavender_crop");
        cropBlock(BlockRegistry.silkberry_crop.get(), "silkberry_crop");

        // CUSTOM
        // axisBlock(block, side, end) — side = bark, end = rings/top
        axisBlock((RotatedPillarBlock) BlockRegistry.silentwood_log.get(), modLoc("block/silentwood_log_side"), modLoc("block/silentwood_log_end"));
        axisBlock((RotatedPillarBlock) BlockRegistry.weeping_willow_log.get(), modLoc("block/weeping_willow_log_side"), modLoc("block/weeping_willow_log_top"));
        axisBlock((RotatedPillarBlock) BlockRegistry.mushroom_stem.get(), modLoc("block/mushroom_stem_side"), modLoc("block/mushroom_stem_top"));
        ModelFile mushroom = models().cubeBottomTop(blockTexture(BlockRegistry.mushroom.get()).getPath(), modLoc("block/mushroom_side"), modLoc("block/mushroom_bottom"), modLoc("block/mushroom_side"));
        simpleBlock(BlockRegistry.mushroom.get(), mushroom);
        simpleBlockItem(BlockRegistry.mushroom.get());
        ModelFile willowLeaves = models().cubeAll(blockTexture(BlockRegistry.weeping_willow_leaves.get()).getPath(), blockTexture(BlockRegistry.weeping_willow_leaves.get())).renderType("cutout_mipped");
        simpleBlock(BlockRegistry.weeping_willow_leaves.get(), willowLeaves);
        simpleBlockItem(BlockRegistry.weeping_willow_leaves.get(), "cutout_mipped");
        barsBlock(BlockRegistry.runestone_bars.get());
        barsBlock(BlockRegistry.moon_temple_bars.get());
        glassPaneBlock(BlockRegistry.aurorian_glass_pane.get(), blockTexture(BlockRegistry.aurorian_glass.get()));
        glassPaneBlock(BlockRegistry.moon_glass_pane.get(), blockTexture(BlockRegistry.moon_glass.get()));
        fenceBlock(BlockRegistry.silentwood_fence.get(), blockTexture(BlockRegistry.silentwood_planks.get()));
        simpleBlockItem(BlockRegistry.boss_spawner.get());
        simpleBlockItem(BlockRegistry.fog_wall.get());
        simpleBlockItem(BlockRegistry.silentwood_log.get());
        simpleBlockItem(BlockRegistry.weeping_willow_log.get());
        simpleBlockItem(BlockRegistry.mushroom_stem.get());
        simpleBlockItem(BlockRegistry.aurorian_portal.get());
        slabBlock(BlockRegistry.aurorian_cobblestone_slab.get(), blockTexture(BlockRegistry.aurorian_cobblestone.get()));
        slabBlock(BlockRegistry.aurorian_deepslate_slab.get(), blockTexture(BlockRegistry.aurorian_deepslate.get()));
        slabBlock(BlockRegistry.silentwood_slab.get(), blockTexture(BlockRegistry.silentwood_planks.get()));
        stairsBlock(BlockRegistry.aurorian_cobblestone_stairs.get(), blockTexture(BlockRegistry.aurorian_cobblestone.get()));
        stairsBlock(BlockRegistry.aurorian_deepslate_stairs.get(), blockTexture(BlockRegistry.aurorian_deepslate.get()));
        stairsBlock(BlockRegistry.darkstone_stairs.get(), blockTexture(BlockRegistry.darkstone.get()));
        stairsBlock(BlockRegistry.runestone_stairs.get(), blockTexture(BlockRegistry.runestone.get()));
        stairsBlock(BlockRegistry.moon_temple_stairs.get(), blockTexture(BlockRegistry.moon_temple_bricks.get()));
        stairsBlock(BlockRegistry.silentwood_stairs.get(), blockTexture(BlockRegistry.silentwood_planks.get()));
        stairsBlock(BlockRegistry.umbra_stone_roof_stairs.get(), blockTexture(BlockRegistry.umbra_stone_roof_tiles.get()));
        stairsBlock(BlockRegistry.peridotite_smooth_stairs.get(), blockTexture(BlockRegistry.peridotite_smooth.get()));
        stairsBlock(BlockRegistry.aurorian_stone_brick_stairs.get(), blockTexture(BlockRegistry.aurorian_stone_brick.get()));
        stairsBlock(BlockRegistry.aurorian_stone_stairs.get(), blockTexture(BlockRegistry.aurorian_stone.get()));
        stairsBlock(BlockRegistry.weeping_willow_stairs.get(), blockTexture(BlockRegistry.weeping_willow_planks.get()));
        wallBlock(BlockRegistry.aurorian_cobblestone_wall.get(), blockTexture(BlockRegistry.aurorian_cobblestone.get()));
        wallBlock(BlockRegistry.aurorian_deepslate_wall.get(), blockTexture(BlockRegistry.aurorian_deepslate.get()));
        torchBlock(BlockRegistry.silentwood_torch.get(), blockTexture(BlockRegistry.silentwood_torch.get()));
        torchBlock(BlockRegistry.moon_torch.get(), blockTexture(BlockRegistry.moon_torch.get()));
        ladderBlock(BlockRegistry.silentwood_ladder.get(), blockTexture(BlockRegistry.silentwood_ladder.get()));
    }

    private void cropBlock(Block block, String name) {
        // 4 upstream stage textures reused for the 8 age stages
        for (int age = 0; age <= 7; age++) {
            int stage = Math.min(age, 3);
            ModelFile model = models().cross(name + "_stage" + stage, modLoc("block/" + name + "_stage" + stage)).renderType("cutout");
            getVariantBuilder(block).partialState().with(net.minecraft.world.level.block.CropBlock.AGE, age).modelForState().modelFile(model).addModel();
        }
    }

    private void torchBlock(Block parent, ResourceLocation texture) {
        ModelFile torch = models().torch(ForgeRegistries.BLOCKS.getKey(parent).getPath(), texture).renderType("cutout");
        getVariantBuilder(parent).partialState().modelForState().modelFile(torch).addModel();
        itemModels().getBuilder(ForgeRegistries.BLOCKS.getKey(parent).getPath()).parent(torch);
    }

    private void ladderBlock(Block parent, ResourceLocation texture) {
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(parent);
        ModelFile ladder = models().withExistingParent(location.getPath(), mcLoc("block/ladder")).texture("texture", texture).texture("particle", texture).renderType("cutout");
        getVariantBuilder(parent).forAllStates(state -> {
            int yRot = switch (state.getValue(LadderBlock.FACING)) {
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            return ConfiguredModel.builder().modelFile(ladder).rotationY(yRot).build();
        });
        itemModels().getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    private void wallBlock(Block parent, ResourceLocation texture) {
        super.wallBlock((WallBlock) parent, texture);
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(parent);
        itemModels().getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile(mcLoc("block/wall_inventory"))).texture("wall", texture);
    }

    private void fenceBlock(Block parent, ResourceLocation texture) {
        super.fenceBlock((FenceBlock) parent, texture);
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(parent);
        itemModels().getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile(mcLoc("block/fence_inventory"))).texture("texture", texture);
    }

    private void stairsBlock(Block parent, ResourceLocation texture) {
        super.stairsBlock((StairBlock) parent, texture);
        simpleBlockItem(parent);
    }

    private void simpleBlockItem(Block parent) {
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(parent);
        itemModels().getBuilder(location.getPath()).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + location.getPath())));
    }

    private void simpleBlockItem(Block parent, String renderType) {
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(parent);
        itemModels().getBuilder(location.getPath()).renderType(renderType).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + location.getPath())));
    }

    private void barsBlock(Block block) {
        ResourceLocation texture = blockTexture(block);
        this.paneBlockWithRenderType((IronBarsBlock) block, texture, texture, "cutout");
        itemModels().getBuilder(ForgeRegistries.BLOCKS.getKey(block).getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    private void glassPaneBlock(Block block, ResourceLocation glassTexture) {
        ResourceLocation pane = modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block).getPath() + "_top");
        this.paneBlockWithRenderType((IronBarsBlock) block, glassTexture, pane, "translucent");
        itemModels().getBuilder(ForgeRegistries.BLOCKS.getKey(block).getPath()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", glassTexture);
    }

    private void slabBlock(Block block, ResourceLocation texture) {
        this.slabBlock((SlabBlock) block, texture, texture);
        simpleBlockItem(block);
    }

    private static ResourceLocation append(ResourceLocation loc, String value) {
        return new ResourceLocation(loc.getNamespace(), loc.getPath() + value);
    }
}
