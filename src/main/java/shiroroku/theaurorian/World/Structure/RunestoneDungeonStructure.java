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
import shiroroku.theaurorian.Config.CommonConfig;
import shiroroku.theaurorian.Registry.StructureRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Runestone dungeon tower assembled from the 15x15 upstream quadrant templates. */
public class RunestoneDungeonStructure extends Structure {

    public static final Codec<RunestoneDungeonStructure> CODEC = RecordCodecBuilder.<RunestoneDungeonStructure>mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, RunestoneDungeonStructure::new)).codec();

    private static final String REF = "theaurorian:runestone/upstream_ref/";
    private static final int TERRAIN_FLOOR = 50;
    private static final int FLOOR_HEIGHT = 6;
    private static final int MIN_SURFACE = 60;
    private static final int ROT180_PIVOT = 14;
    private static final String LOOT_LOW = "theaurorian:chests/runestone/common";
    private static final String LOOT_MED = "theaurorian:chests/runestone/uncommon";
    private static final String LOOT_HIGH = "theaurorian:chests/runestone/rare";

    public RunestoneDungeonStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!CommonConfig.enable_runestone_dungeon.get()) {
            return Optional.empty();
        }

        ChunkPos home = context.chunkPos();
        int x = home.getMinBlockX() + 8;
        int z = home.getMinBlockZ() + 8;
        if (StructurePlacementChecks.isWaterCovered(context, x, z)) {
            return Optional.empty();
        }
        int y0 = surfaceY(context, x + 15, z + 16);
        int floors = Math.max(2, CommonConfig.runestone_dungeon_floors.get());
        if ((floors & 1) != 0) {
            floors++;
        }

        List<Slot> slots = new ArrayList<>();
        addQuadrant(slots, x - 15, y0, z + 1, "tr", false, floors);
        addQuadrant(slots, x - 15, y0, z + 16, "tl", false, floors);
        addQuadrant(slots, x, y0, z + 1, "br", true, floors);
        addQuadrant(slots, x, y0, z + 16, "bl", false, floors);

        BlockPos origin = new BlockPos(x, y0, z);
        return Optional.of(new GenerationStub(origin,
                builder -> builder.addPiece(new RunestoneDungeonPiece(context.structureTemplateManager(), slots))));
    }

    private static int surfaceY(GenerationContext context, int x, int z) {
        return Math.max(context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState()), MIN_SURFACE);
    }

    private static void addQuadrant(List<Slot> slots, int px, int y0, int pz, String quadrant,
                                    boolean brStyleTerrain, int floors) {
        ResourceLocation terrain = id("runestonetower_terrain_" + quadrant);
        ResourceLocation base = id("runestonetower_base_" + quadrant + "v2");
        ResourceLocation floor = id("runestonetower_floor_" + quadrant + "v2");
        ResourceLocation floor2 = id("runestonetower_floor_2_" + opposite(quadrant) + "v2");
        ResourceLocation top = id("runestonetower_top_" + quadrant + "v2");
        ResourceLocation topAlt = id("runestonetower_top_" + opposite(quadrant) + "v2");

        slots.add(new Slot(terrain, Rotation.NONE, new BlockPos(px, y0, pz), null, true, brStyleTerrain));
        slots.add(new Slot(base, Rotation.NONE, new BlockPos(px, y0, pz), LOOT_LOW, false, false));

        boolean normal = true;
        int floorIndex = 1;
        while (floorIndex <= floors) {
            String loot = floorIndex < floors / 2 ? LOOT_LOW : LOOT_MED;
            if (normal) {
                slots.add(new Slot(floor, Rotation.NONE,
                        new BlockPos(px, y0 + FLOOR_HEIGHT * floorIndex, pz), loot, false, false));
                floorIndex++;
                normal = false;
            } else {
                slots.add(new Slot(floor2, Rotation.CLOCKWISE_180,
                        new BlockPos(px + ROT180_PIVOT, y0 + FLOOR_HEIGHT * floorIndex,
                                pz + ROT180_PIVOT), loot, false, false));
                floorIndex += 2;
                normal = true;
            }
        }

        int bossY = y0 + FLOOR_HEIGHT * floorIndex;
        if (normal) {
            slots.add(new Slot(top, Rotation.NONE, new BlockPos(px, bossY, pz), LOOT_HIGH, false, false));
        } else {
            slots.add(new Slot(topAlt, Rotation.CLOCKWISE_180,
                    new BlockPos(px + ROT180_PIVOT, bossY, pz + ROT180_PIVOT), LOOT_HIGH, false, false));
        }
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(REF + path);
    }

    private static String opposite(String quadrant) {
        return switch (quadrant) {
            case "tl" -> "br";
            case "tr" -> "bl";
            case "bl" -> "tr";
            case "br" -> "tl";
            default -> quadrant;
        };
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.RUNESTONE_DUNGEON_TYPE.get();
    }

    private static BoundingBox boundingBoxOf(StructureTemplateManager manager, List<Slot> slots) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (Slot slot : slots) {
            StructureTemplate template = manager.get(slot.templateId).orElse(null);
            BoundingBox box = template == null
                    ? new BoundingBox(slot.pos.getX(), slot.pos.getY(), slot.pos.getZ(),
                    slot.pos.getX() + 15, slot.pos.getY() + 15, slot.pos.getZ() + 15)
                    : template.getBoundingBox(new StructurePlaceSettings().setRotation(slot.rotation), slot.pos);
            minX = Math.min(minX, box.minX());
            minY = Math.min(minY, slot.terrainPillar ? TERRAIN_FLOOR - 1 : box.minY());
            minZ = Math.min(minZ, box.minZ());
            maxX = Math.max(maxX, box.maxX());
            maxY = Math.max(maxY, box.maxY());
            maxZ = Math.max(maxZ, box.maxZ());
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static class RunestoneDungeonPiece extends StructurePiece {
        private final List<Slot> slots;

        public RunestoneDungeonPiece(StructureTemplateManager manager, List<Slot> slots) {
            super(StructureRegistry.RUNESTONE_DUNGEON_PIECE.get(), 0, boundingBoxOf(manager, slots));
            this.slots = slots;
            for (Slot slot : slots) {
                slot.template = manager.get(slot.templateId).orElse(null);
            }
        }

        public static RunestoneDungeonPiece load(StructurePieceSerializationContext context, CompoundTag tag) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; tag.contains("slot" + i); i++) {
                CompoundTag st = tag.getCompound("slot" + i);
                slots.add(new Slot(new ResourceLocation(st.getString("template")),
                        Rotation.valueOf(st.getString("rot")),
                        new BlockPos(st.getInt("posX"), st.getInt("posY"), st.getInt("posZ")),
                        st.contains("loot") ? st.getString("loot") : null,
                        st.getBoolean("terrain"), st.getBoolean("brTerrain")));
            }
            return new RunestoneDungeonPiece(context.structureTemplateManager(), slots);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            for (int i = 0; i < slots.size(); i++) {
                Slot slot = slots.get(i);
                CompoundTag st = new CompoundTag();
                st.putString("template", slot.templateId.toString());
                st.putString("rot", slot.rotation.name());
                st.putInt("posX", slot.pos.getX());
                st.putInt("posY", slot.pos.getY());
                st.putInt("posZ", slot.pos.getZ());
                if (slot.loot != null) {
                    st.putString("loot", slot.loot);
                }
                if (slot.terrainPillar) {
                    st.putBoolean("terrain", true);
                    st.putBoolean("brTerrain", slot.brStyleTerrain);
                }
                tag.put("slot" + i, st);
            }
        }

        @Override
        public void postProcess(WorldGenLevel level, net.minecraft.world.level.StructureManager structureManager,
                                ChunkGenerator chunkGen, RandomSource random, BoundingBox box, ChunkPos chunkPos,
                                BlockPos pos) {
            for (Slot slot : slots) {
                if (slot.template == null) {
                    continue;
                }
                if (slot.terrainPillar) {
                    placeTerrainPillar(level, slot, random, box);
                    continue;
                }
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setRotation(slot.rotation)
                        .setRandom(random)
                        .setBoundingBox(box)
                        .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE);
                if (!slot.template.getBoundingBox(settings, slot.pos).intersects(box)) {
                    continue;
                }
                slot.template.placeInWorld(level, slot.pos, slot.pos, settings, random, 2);
                if (slot.loot != null) {
                    populateChests(level, slot, settings, random, box);
                }
            }
        }

        private void placeTerrainPillar(WorldGenLevel level, Slot slot, RandomSource random, BoundingBox box) {
            for (int y = slot.pos.getY(); y >= TERRAIN_FLOOR; y--) {
                int placeY = slot.brStyleTerrain ? y : y - 1;
                BlockPos at = new BlockPos(slot.pos.getX(), placeY, slot.pos.getZ());
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setRotation(slot.rotation)
                        .setRandom(random)
                        .setBoundingBox(box)
                        .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE);
                if (slot.template.getBoundingBox(settings, at).intersects(box)) {
                    slot.template.placeInWorld(level, at, at, settings, random, 2);
                }
            }
        }

        private void populateChests(WorldGenLevel level, Slot slot, StructurePlaceSettings settings,
                                    RandomSource random, BoundingBox box) {
            ResourceLocation loot = new ResourceLocation(slot.loot);
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.STRUCTURE_BLOCK)) {
                if (!box.isInside(info.pos)) {
                    continue;
                }
                String metadata = info.nbt != null ? info.nbt.getString("metadata") : "";
                if (!metadata.isEmpty() && !metadata.startsWith("chest")) {
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
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.CHEST)) {
                if (box.isInside(info.pos) && level.getBlockEntity(info.pos) instanceof ChestBlockEntity chest) {
                    chest.setLootTable(loot, random.nextLong());
                }
            }
        }
    }

    private static class Slot {
        final ResourceLocation templateId;
        final Rotation rotation;
        final BlockPos pos;
        final String loot;
        final boolean terrainPillar;
        final boolean brStyleTerrain;
        StructureTemplate template;

        Slot(ResourceLocation templateId, Rotation rotation, BlockPos pos, String loot,
             boolean terrainPillar, boolean brStyleTerrain) {
            this.templateId = templateId;
            this.rotation = rotation;
            this.pos = pos;
            this.loot = loot;
            this.terrainPillar = terrainPillar;
            this.brStyleTerrain = brStyleTerrain;
        }
    }
}
