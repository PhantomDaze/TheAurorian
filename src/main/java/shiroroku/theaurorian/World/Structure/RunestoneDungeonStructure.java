package shiroroku.theaurorian.World.Structure;

import com.mojang.serialization.MapCodec;
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

/**
 * Runestone dungeon tower, ported from 1.12 {@code RunestoneTowerWorldGenerator}.
 * Four 15×15 quadrants (TL/TR/BL/BR) with terrain pillars, base, alternating
 * floor/floor2, and top boss room. Templates live under
 * {@code structure/runestone/upstream_ref/}.
 */
public class RunestoneDungeonStructure extends Structure {

    public static final MapCodec<RunestoneDungeonStructure> CODEC = simpleCodec(RunestoneDungeonStructure::new);

    private static final String REF = "theaurorian:runestone/upstream_ref/";
    private static final int TERRAIN_FLOOR = 50;
    private static final int FLOOR_HEIGHT = 6;
    private static final int MIN_SURFACE = 60;
    /** Upstream templates are 15×15; 180° placement uses corner offset size-1. */
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
        // Structure-set chunk is the BR (0,0) home quadrant (upstream density anchor).
        ChunkPos home = context.chunkPos();
        int x = home.getMinBlockX() + 8;
        int z = home.getMinBlockZ() + 8;
        if (StructurePlacementChecks.isWaterCovered(context, x, z)) {
            return Optional.empty();
        }
        int y0 = surfaceY(context, x + 15, z + 16);

        int floors = CommonConfig.runestone_dungeon_floors.get();
        // Upstream RunestoneTowerWorldGenerator forces an even floor count
        // (odd config values are bumped by one) before the alt stair loop.
        if ((floors & 1) != 0) {
            floors += 1;
        }

        List<Slot> slots = new ArrayList<>();
        // Collapse the four 1.12 source-chunk branches onto the BR density
        // anchor (this structure-set chunk). Origins match upstream
        // generateTower place positions relative to BR chunk center (x,z):
        //   TR (x-15, z+1)   BR (x, z+1)
        //   TL (x-15, z+16)  BL (x, z+16)
        // Same-name templates; solid L-edges face outward so the four arcs
        // form one ring (the earlier radial-to-center map was inverted).
        // BR terrain places at y=i; the other three use y=i-1.
        addQuadrant(slots, x - 15, y0, z + 1, "tr", false, floors);
        addQuadrant(slots, x - 15, y0, z + 16, "tl", false, floors);
        addQuadrant(slots, x, y0, z + 1, "br", true, floors);
        addQuadrant(slots, x, y0, z + 16, "bl", false, floors);

        BlockPos origin = new BlockPos(x, y0, z);
        return Optional.of(new GenerationStub(origin, builder ->
                builder.addPiece(new RunestoneDungeonPiece(context.structureTemplateManager(), slots))));
    }

    private static int surfaceY(GenerationContext context, int x, int z) {
        return Math.max(
                context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState()),
                MIN_SURFACE);
    }

    /**
     * @param pieceQuad template quadrant id (tl/tr/bl/br), matching the source
     *                   generator's per-chunk branch.
     * @param brStyleTerrain BR-position terrain places at y=i; the other
     *                       branches place at y=i-1 (upstream quirk).
     */
    private static void addQuadrant(List<Slot> slots, int px, int y0, int pz, String pieceQuad, boolean brStyleTerrain, int floors) {
        String terrain = REF + "runestonetower_terrain_" + pieceQuad;
        String base = REF + "runestonetower_base_" + pieceQuad + "v2";
        String floor = REF + "runestonetower_floor_" + pieceQuad + "v2";
        // floor2 uses opposite quadrant template with 180° (upstream)
        String floor2 = REF + "runestonetower_floor_2_" + opposite(pieceQuad) + "v2";
        String top = REF + "runestonetower_top_" + pieceQuad + "v2";
        String topAlt = REF + "runestonetower_top_" + opposite(pieceQuad) + "v2";

        // One terrain pillar slot: postProcess stacks the template from y0 down to TERRAIN_FLOOR.
        slots.add(new Slot(ResourceLocation.parse(terrain), Rotation.NONE, new BlockPos(px, y0, pz), null, true, brStyleTerrain));

        // Base
        slots.add(new Slot(ResourceLocation.parse(base), Rotation.NONE, new BlockPos(px, y0, pz), LOOT_LOW, false, false));

        // Floors — replicate upstream alt loop so stairs meet the top room.
        boolean alt = true;
        int floorIdx = 1;
        while (floorIdx <= floors) {
            String loot = floorIdx < floors / 2 ? LOOT_LOW : LOOT_MED;
            if (alt) {
                slots.add(new Slot(ResourceLocation.parse(floor), Rotation.NONE,
                        new BlockPos(px, y0 + FLOOR_HEIGHT * floorIdx, pz), loot, false, false));
                floorIdx++;
                alt = false;
            } else {
                // floor2: 180° rotation with +14,+14 pivot compensation (upstream)
                slots.add(new Slot(ResourceLocation.parse(floor2), Rotation.CLOCKWISE_180,
                        new BlockPos(px + ROT180_PIVOT, y0 + FLOOR_HEIGHT * floorIdx, pz + ROT180_PIVOT),
                        loot, false, false));
                floorIdx += 2;
                alt = true;
            }
        }

        int bossY = y0 + FLOOR_HEIGHT * floorIdx;
        if (alt) {
            slots.add(new Slot(ResourceLocation.parse(top), Rotation.NONE, new BlockPos(px, bossY, pz), LOOT_HIGH, false, false));
        } else {
            slots.add(new Slot(ResourceLocation.parse(topAlt), Rotation.CLOCKWISE_180,
                    new BlockPos(px + ROT180_PIVOT, bossY, pz + ROT180_PIVOT), LOOT_HIGH, false, false));
        }
    }

    private static String opposite(String quad) {
        return switch (quad) {
            case "tl" -> "br";
            case "tr" -> "bl";
            case "bl" -> "tr";
            case "br" -> "tl";
            default -> quad;
        };
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.RUNESTONE_DUNGEON_TYPE.get();
    }

    private static BoundingBox boundingBoxOf(List<Slot> slots) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (Slot s : slots) {
            minX = Math.min(minX, s.pos.getX());
            minY = Math.min(minY, s.terrainPillar ? TERRAIN_FLOOR - 1 : s.pos.getY());
            minZ = Math.min(minZ, s.pos.getZ());
            maxX = Math.max(maxX, s.pos.getX() + 16);
            maxY = Math.max(maxY, s.pos.getY() + 16);
            maxZ = Math.max(maxZ, s.pos.getZ() + 16);
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static class RunestoneDungeonPiece extends StructurePiece {

        private final List<Slot> slots;

        public RunestoneDungeonPiece(StructureTemplateManager manager, List<Slot> slots) {
            super(StructureRegistry.RUNESTONE_DUNGEON_PIECE.get(), 0, boundingBoxOf(slots));
            this.slots = slots;
            for (Slot s : slots) {
                s.template = manager.get(s.templateId).orElse(null);
            }
        }

        public static RunestoneDungeonPiece load(StructurePieceSerializationContext context, CompoundTag tag) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; tag.contains("slot" + i); i++) {
                CompoundTag st = tag.getCompound("slot" + i);
                String loot = st.contains("loot") ? st.getString("loot") : null;
                boolean terrain = st.getBoolean("terrain");
                boolean brTerrain = st.getBoolean("brTerrain");
                slots.add(new Slot(
                        ResourceLocation.parse(st.getString("template")),
                        Rotation.valueOf(st.getString("rot")),
                        new BlockPos(st.getInt("posX"), st.getInt("posY"), st.getInt("posZ")),
                        loot, terrain, brTerrain));
            }
            return new RunestoneDungeonPiece(context.structureTemplateManager(), slots);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            for (int i = 0; i < slots.size(); i++) {
                Slot s = slots.get(i);
                CompoundTag st = new CompoundTag();
                st.putString("template", s.templateId.toString());
                st.putString("rot", s.rotation.name());
                st.putInt("posX", s.pos.getX());
                st.putInt("posY", s.pos.getY());
                st.putInt("posZ", s.pos.getZ());
                if (s.loot != null) {
                    st.putString("loot", s.loot);
                }
                if (s.terrainPillar) {
                    st.putBoolean("terrain", true);
                    st.putBoolean("brTerrain", s.brStyleTerrain);
                }
                tag.put("slot" + i, st);
            }
        }

        @Override
        public void postProcess(WorldGenLevel level, net.minecraft.world.level.StructureManager structureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            for (Slot s : slots) {
                if (s.template == null) {
                    continue;
                }
                if (s.terrainPillar) {
                    placeTerrainPillar(level, s, random, box);
                    continue;
                }
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setRotation(s.rotation)
                        .setRandom(random)
                        .setBoundingBox(box)
                        .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE);
                BoundingBox templateBox = s.template.getBoundingBox(settings, s.pos);
                if (!templateBox.intersects(box)) {
                    continue;
                }
                s.template.placeInWorld(level, s.pos, s.pos, settings, random, 2);
                if (s.loot != null) {
                    populateChests(level, s, settings, random, box);
                }
            }
        }

        private void placeTerrainPillar(WorldGenLevel level, Slot s, RandomSource random, BoundingBox box) {
            int topY = s.pos.getY();
            int bottom = TERRAIN_FLOOR;
            // Upstream: BR position uses y=i; others use y=i-1 for each step from surface down to 50.
            for (int i = topY; i >= bottom; i--) {
                int placeY = s.brStyleTerrain ? i : i - 1;
                BlockPos at = new BlockPos(s.pos.getX(), placeY, s.pos.getZ());
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setRotation(s.rotation)
                        .setRandom(random)
                        .setBoundingBox(box)
                        .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE);
                BoundingBox templateBox = s.template.getBoundingBox(settings, at);
                if (!templateBox.intersects(box)) {
                    continue;
                }
                s.template.placeInWorld(level, at, at, settings, random, 2);
            }
        }

        private void populateChests(WorldGenLevel level, Slot slot, StructurePlaceSettings settings, RandomSource random, BoundingBox box) {
            if (slot.template == null || slot.loot == null) {
                return;
            }
            var lootKey = net.minecraft.resources.ResourceKey.create(
                    net.minecraft.core.registries.Registries.LOOT_TABLE, ResourceLocation.parse(slot.loot));
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.STRUCTURE_BLOCK)) {
                if (!box.isInside(info.pos())) {
                    continue;
                }
                String data = info.nbt() != null ? info.nbt().getString("metadata") : "";
                if (!data.isEmpty() && !data.startsWith("chest")) {
                    continue;
                }
                level.setBlock(info.pos(), Blocks.AIR.defaultBlockState(), 3);
                BlockEntity te = level.getBlockEntity(info.pos().below());
                if (te instanceof ChestBlockEntity chest) {
                    chest.setLootTable(lootKey, random.nextLong());
                } else if (level.getBlockEntity(info.pos()) instanceof ChestBlockEntity chest) {
                    chest.setLootTable(lootKey, random.nextLong());
                }
            }
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.CHEST)) {
                if (!box.isInside(info.pos())) {
                    continue;
                }
                if (level.getBlockEntity(info.pos()) instanceof ChestBlockEntity chest && chest.getLootTable() == null) {
                    chest.setLootTable(lootKey, random.nextLong());
                }
            }
            // Join adjacent single chests into large (double) chests (template states are single).
            StructureChests.connectChests(level, StructureChests.templateBox(slot.template, slot.rotation, slot.pos));
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

        Slot(ResourceLocation templateId, Rotation rotation, BlockPos pos, String loot, boolean terrainPillar, boolean brStyleTerrain) {
            this.templateId = templateId;
            this.rotation = rotation;
            this.pos = pos;
            this.loot = loot;
            this.terrainPillar = terrainPillar;
            this.brStyleTerrain = brStyleTerrain;
        }
    }
}
