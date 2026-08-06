package shiroroku.theaurorian.World.Structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import shiroroku.theaurorian.Registry.StructureRegistry;
import shiroroku.theaurorian.TheAurorian;

import java.util.Optional;

/** Generic single-template structure placed on the surface. */
public class SingleTemplateStructure extends Structure {

    public static final Codec<SingleTemplateStructure> CODEC = RecordCodecBuilder.<SingleTemplateStructure>mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    ResourceLocation.CODEC.fieldOf("template").forGetter(s -> s.template)
            ).apply(instance, SingleTemplateStructure::new)).codec();

    private static final ResourceLocation UMBRA_TERRAIN = new ResourceLocation(TheAurorian.MODID, "umbratower/umbratowerterrain");
    private static final int UMBRA_TERRAIN_LAYERS = 16;

    private final ResourceLocation template;

    public SingleTemplateStructure(StructureSettings settings, ResourceLocation template) {
        super(settings);
        this.template = template;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (template.getPath().startsWith("umbratower") && !shiroroku.theaurorian.Config.CommonConfig.enable_umbra_tower.get()) {
            return Optional.empty();
        }
        if (!template.getPath().startsWith("umbratower") && !shiroroku.theaurorian.Config.CommonConfig.enable_ruins.get()) {
            return Optional.empty();
        }
        BlockPos center = new BlockPos(context.chunkPos().getMinBlockX() + 8, 0, context.chunkPos().getMinBlockZ() + 8);
        int y = context.chunkGenerator().getBaseHeight(center.getX(), center.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        if (template.getPath().contains("umbratower") && !template.getPath().contains("terrain")) {
            y += 3;
        }
        BlockPos pos = new BlockPos(center.getX(), y, center.getZ());
        Rotation rotation = Rotation.getRandom(context.random());
        return Optional.of(new GenerationStub(pos, builder -> builder.addPiece(new SingleTemplatePiece(context.structureTemplateManager(), pos, template, rotation))));
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.SINGLE_TEMPLATE_TYPE.get();
    }

    public static class SingleTemplatePiece extends StructurePiece {

        private final BlockPos pos;
        private final ResourceLocation template;
        private final Rotation rotation;
        private StructureTemplate loaded;
        private StructureTemplate terrain;

        public SingleTemplatePiece(StructureTemplateManager manager, BlockPos pos, ResourceLocation template, Rotation rotation) {
            super(StructureRegistry.SINGLE_TEMPLATE_PIECE.get(), 0, makeBox(pos, template, manager, rotation));
            this.pos = pos;
            this.template = template;
            this.rotation = rotation;
            this.loaded = manager.get(template).orElse(null);
            if (isUmbraTower(template)) {
                this.terrain = manager.get(UMBRA_TERRAIN).orElse(null);
            }
        }

        private static boolean isUmbraTower(ResourceLocation template) {
            String path = template.getPath();
            return path.contains("umbratower") && !path.contains("terrain");
        }

        private static BoundingBox makeBox(BlockPos pos, ResourceLocation template, StructureTemplateManager manager, Rotation rotation) {
            StructureTemplate loaded = manager.get(template).orElse(null);
            int minY = isUmbraTower(template) ? pos.getY() - UMBRA_TERRAIN_LAYERS : pos.getY();
            if (loaded == null) {
                return new BoundingBox(pos.getX(), minY, pos.getZ(), pos.getX() + 16, pos.getY() + 32, pos.getZ() + 16);
            }
            BoundingBox box = loaded.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), pos);
            return new BoundingBox(box.minX(), Math.min(box.minY(), minY), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
        }

        public static SingleTemplatePiece load(StructurePieceSerializationContext context, CompoundTag tag) {
            BlockPos pos = new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ"));
            ResourceLocation template = new ResourceLocation(tag.getString("template"));
            Rotation rotation = Rotation.valueOf(tag.getString("rot"));
            return new SingleTemplatePiece(context.structureTemplateManager(), pos, template, rotation);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putInt("posX", pos.getX());
            tag.putInt("posY", pos.getY());
            tag.putInt("posZ", pos.getZ());
            tag.putString("template", template.toString());
            tag.putString("rot", rotation.name());
        }

        @Override
        public void postProcess(WorldGenLevel level, net.minecraft.world.level.StructureManager structureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            if (loaded == null) {
                return;
            }
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(rotation)
                    .setRandom(random)
                    .setBoundingBox(box)
                    .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE);
            if (loaded.getBoundingBox(settings, this.pos).intersects(box)) {
                loaded.placeInWorld(level, this.pos, this.pos, settings, random, 2);
                populateChests(level, settings, random, box);
            }

            if (terrain != null) {
                StructurePlaceSettings terrainSettings = new StructurePlaceSettings()
                        .setRotation(Rotation.NONE)
                        .setRandom(random)
                        .setBoundingBox(box);
                for (int i = 1; i <= UMBRA_TERRAIN_LAYERS; i++) {
                    BlockPos at = this.pos.below(i);
                    if (terrain.getBoundingBox(terrainSettings, at).intersects(box)) {
                        terrain.placeInWorld(level, at, at, terrainSettings, random, 2);
                    }
                }
            }
        }

        private void populateChests(WorldGenLevel level, StructurePlaceSettings settings, RandomSource random, BoundingBox box) {
            ResourceLocation loot = new ResourceLocation(TheAurorian.MODID, "chests/ruins/common");
            for (StructureTemplate.StructureBlockInfo info : loaded.filterBlocks(this.pos, settings, Blocks.STRUCTURE_BLOCK)) {
                if (!box.isInside(info.pos)) {
                    continue;
                }
                String data = info.nbt != null ? info.nbt.getString("metadata") : "";
                if (!data.isEmpty() && !data.startsWith("chest")) {
                    continue;
                }
                level.setBlock(info.pos, Blocks.AIR.defaultBlockState(), 3);
                BlockEntity below = level.getBlockEntity(info.pos.below());
                if (below instanceof ChestBlockEntity chest) {
                    chest.setLootTable(loot, random.nextLong());
                } else if (level.getBlockEntity(info.pos) instanceof ChestBlockEntity chest) {
                    chest.setLootTable(loot, random.nextLong());
                }
            }
            for (StructureTemplate.StructureBlockInfo info : loaded.filterBlocks(this.pos, settings, Blocks.CHEST)) {
                if (box.isInside(info.pos)) {
                    if (level.getBlockEntity(info.pos) instanceof ChestBlockEntity chest) {
                        chest.setLootTable(loot, random.nextLong());
                    }
                }
            }
        }
    }
}
