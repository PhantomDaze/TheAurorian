package shiroroku.theaurorian.World.Structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import shiroroku.theaurorian.Registry.StructureRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Darkstone dungeon, replicating the 1.12 char-map generator (5x5x2 floors plus
 * entrance, stairs and boss room). Templates come from
 * {@code data/theaurorian/structures/darkstone/*.nbt} (already ID-remapped).
 */
public class DarkstoneDungeonStructure extends Structure {

    public static final Codec<DarkstoneDungeonStructure> CODEC = RecordCodecBuilder.<DarkstoneDungeonStructure>mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, DarkstoneDungeonStructure::new)).codec();

    private static final String[][] MAP_A = {
            {"AAQIH", "EOHAJ", "GIFEN", "LBGBJ", "EIKIF"},
            {"GM GB", "JEIKH", "DGIFJ", "GOBCF", "EIMIB"}
    };
    private static final String[][] MAP_B = {
            {"GHCHA", "JSAEN", "EMKIF", "GFEMH", "DCMFD"},
            {"GIMHA", "J JEN", "JJDGF", "EFGFA", "CIKIF"}
    };

    private static final int MAP_WIDTH = 5;
    private static final int MAP_LENGTH = 5;
    private static final int MAP_FLOORS = 2;
    private static final int MAP_OFFSET_X = 6;
    private static final int MAP_OFFSET_Z = -2;
    private static final int FLOOR_HEIGHT = 14;
    private static final int MIN_HEIGHT = 40;

    public DarkstoneDungeonStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!shiroroku.theaurorian.Config.CommonConfig.enable_darkstone_dungeon.get()) {
            return Optional.empty();
        }
        BlockPos center = new BlockPos(context.chunkPos().getMinBlockX() + 8, 0, context.chunkPos().getMinBlockZ() + 8);
        List<Slot> slots = computeSlots(context, center);
        BlockPos origin = slots.get(0).pos;
        return Optional.of(new GenerationStub(origin, builder -> builder.addPiece(new DarkstoneDungeonPiece(context.structureTemplateManager(), origin, context.seed(), slots))));
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.DARKSTONE_DUNGEON_TYPE.get();
    }

    private static String[][] mapForHeight(int height) {
        return height % 2 == 0 ? MAP_B : MAP_A;
    }

    private static int surfaceY(GenerationContext context, int x, int z) {
        int h = context.chunkGenerator().getBaseHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        return Math.max(h - 1, MIN_HEIGHT);
    }

    private static int sourceSurfaceY(GenerationContext context, int ax, int az, int sourceOffsetX, int sourceOffsetZ) {
        int anchorChunkX = Math.floorDiv(ax - 8, 16);
        int anchorChunkZ = Math.floorDiv(az - 8, 16);
        int sourceChunkX = anchorChunkX - sourceOffsetX;
        int sourceChunkZ = anchorChunkZ - sourceOffsetZ;
        int probeX = sourceChunkX * 16 + sourceOffsetX * 16 + 24;
        int probeZ = sourceChunkZ * 16 + sourceOffsetZ * 16 + 16;
        return surfaceY(context, probeX, probeZ);
    }

    private static List<Slot> computeSlots(GenerationContext context, BlockPos center) {
        List<Slot> slots = new ArrayList<>();
        RandomSource random = RandomSource.create(context.seed());
        int ax = center.getX();
        int az = center.getZ();

        int entranceY = sourceSurfaceY(context, ax, az, 0, 0);
        slots.add(new Slot(new ResourceLocation("theaurorian:darkstone/darkstone_entrance"), Rotation.NONE, new BlockPos(ax, entranceY, az), null));
        int stairsY = sourceSurfaceY(context, ax, az, 1, 0);
        slots.add(new Slot(new ResourceLocation("theaurorian:darkstone/darkstone_stairs"), Rotation.NONE, new BlockPos(ax - 16, stairsY - FLOOR_HEIGHT, az), null));

        // Map floors use each source cell's upstream height probe and anchor-relative position.
        for (int floor = 0; floor < MAP_FLOORS; floor++) {
            for (int ix = 0; ix < MAP_LENGTH; ix++) {
                for (int iz = 0; iz < MAP_WIDTH; iz++) {
                    int sourceOffsetX = -ix + MAP_OFFSET_X;
                    int sourceOffsetZ = iz + MAP_OFFSET_Z;
                    int sourceY = sourceSurfaceY(context, ax, az, sourceOffsetX, sourceOffsetZ);
                    int py = sourceY - FLOOR_HEIGHT * (floor + 1);
                    char c = mapForHeight(py)[floor][ix].charAt(iz);
                    if (c == ' ') {
                        continue;
                    }
                    int px = ax - sourceOffsetX * 16;
                    int pz = az - sourceOffsetZ * 16;
                    buildSlot(slots, c, px, py, pz, floor, random);
                }
            }
        }

        // Boss room
        addBossRoom(slots, context, ax, az, 0, 0, "darkstone/darkstone_bossroom_back");
        addBossRoom(slots, context, ax, az, 0, 1, "darkstone/darkstone_bossroom_backleft");
        addBossRoom(slots, context, ax, az, 0, -1, "darkstone/darkstone_bossroom_backright");
        addBossRoom(slots, context, ax, az, 1, 0, "darkstone/darkstone_bossroom_front");
        addBossRoom(slots, context, ax, az, 1, 1, "darkstone/darkstone_bossroom_frontleft");
        addBossRoom(slots, context, ax, az, 1, -1, "darkstone/darkstone_bossroom_frontright");

        return slots;
    }

    private static void addBossRoom(List<Slot> slots, GenerationContext context, int ax, int az, int sourceOffsetX, int sourceOffsetZ, String name) {
        int px = ax - sourceOffsetX * 16;
        int pz = az - sourceOffsetZ * 16;
        int py = sourceSurfaceY(context, ax, az, sourceOffsetX, sourceOffsetZ) - FLOOR_HEIGHT * 2;
        slots.add(new Slot(new ResourceLocation("theaurorian:" + name), Rotation.NONE, new BlockPos(px, py, pz), "theaurorian:chests/darkstone/high"));
    }

    private static void buildSlot(List<Slot> slots, char c, int px, int py, int pz, int floor, RandomSource random) {
        String loot = floor == 0 ? "theaurorian:chests/darkstone/low" : "theaurorian:chests/darkstone/med";
        ResourceLocation template;
        Rotation rotation = Rotation.NONE;
        int offx = 0;
        int offz = 0;
        int offy = 0;

        String slotLoot = null;
        switch (c) {
            case 'K' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_cross"); slotLoot = loot; }
            case 'E' -> template = new ResourceLocation("theaurorian:darkstone/darkstone_corner");
            case 'F' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_corner"); rotation = Rotation.COUNTERCLOCKWISE_90; offz = 15; }
            case 'G' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_corner"); rotation = Rotation.CLOCKWISE_90; offx = 15; }
            case 'H' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_corner"); rotation = Rotation.CLOCKWISE_180; offx = 15; offz = 15; }
            case 'I' -> {
                boolean straight = random.nextBoolean();
                template = straight ? new ResourceLocation("theaurorian:darkstone/darkstone_straight") : new ResourceLocation("theaurorian:darkstone/darkstone_straight_b");
                if (straight) slotLoot = loot;
            }
            case 'J' -> {
                boolean straight = random.nextBoolean();
                template = straight ? new ResourceLocation("theaurorian:darkstone/darkstone_straight") : new ResourceLocation("theaurorian:darkstone/darkstone_straight_b");
                rotation = Rotation.COUNTERCLOCKWISE_90;
                offz = 15;
                if (straight) slotLoot = loot;
            }
            case 'A' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_end"); slotLoot = loot; }
            case 'B' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_end"); rotation = Rotation.CLOCKWISE_90; offx = 15; slotLoot = loot; }
            case 'C' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_end"); rotation = Rotation.COUNTERCLOCKWISE_90; offz = 15; slotLoot = loot; }
            case 'D' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_end"); rotation = Rotation.CLOCKWISE_180; offx = 15; offz = 15; slotLoot = loot; }
            case 'L' -> template = new ResourceLocation("theaurorian:darkstone/darkstone_t");
            case 'M' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_t"); rotation = Rotation.CLOCKWISE_90; offx = 15; }
            case 'N' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_t"); rotation = Rotation.CLOCKWISE_180; offx = 15; offz = 15; }
            case 'O' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_t"); rotation = Rotation.COUNTERCLOCKWISE_90; offz = 15; }
            case 'P' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_stairs"); offy = -FLOOR_HEIGHT; }
            case 'Q' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_stairs"); rotation = Rotation.COUNTERCLOCKWISE_90; offz = 15; offy = -FLOOR_HEIGHT; }
            case 'R' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_stairs"); rotation = Rotation.CLOCKWISE_90; offx = 15; offy = -FLOOR_HEIGHT; }
            case 'S' -> { template = new ResourceLocation("theaurorian:darkstone/darkstone_stairs"); rotation = Rotation.CLOCKWISE_180; offx = 15; offz = 15; offy = -FLOOR_HEIGHT; }
            default -> { return; }
        }

        slots.add(new Slot(template, rotation, new BlockPos(px + offx, py + offy, pz + offz), slotLoot));
    }

    private static BoundingBox boundingBoxOf(StructureTemplateManager manager, List<Slot> slots) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (Slot s : slots) {
            StructureTemplate template = manager.get(s.templateId).orElse(null);
            BoundingBox box = template == null
                    ? new BoundingBox(s.pos.getX(), s.pos.getY(), s.pos.getZ(), s.pos.getX() + 16, s.pos.getY() + 16, s.pos.getZ() + 16)
                    : template.getBoundingBox(new StructurePlaceSettings().setRotation(s.rotation), s.pos);
            minX = Math.min(minX, box.minX());
            minY = Math.min(minY, box.minY());
            minZ = Math.min(minZ, box.minZ());
            maxX = Math.max(maxX, box.maxX());
            maxY = Math.max(maxY, box.maxY());
            maxZ = Math.max(maxZ, box.maxZ());
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static class DarkstoneDungeonPiece extends StructurePiece {

        private final long seed;
        private final List<Slot> slots;

        public DarkstoneDungeonPiece(StructureTemplateManager manager, BlockPos origin, long seed, List<Slot> slots) {
            super(StructureRegistry.DARKSTONE_DUNGEON_PIECE.get(), 0, boundingBoxOf(manager, slots));
            this.seed = seed;
            this.slots = slots;
            loadTemplates(manager);
        }

        private void loadTemplates(StructureTemplateManager manager) {
            for (Slot s : slots) {
                if (s.template == null) {
                    s.template = manager.get(s.templateId).orElse(null);
                }
            }
        }

        public static DarkstoneDungeonPiece load(StructurePieceSerializationContext context, CompoundTag tag) {
            long seed = tag.getLong("seed");
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; tag.contains("slot" + i); i++) {
                CompoundTag st = tag.getCompound("slot" + i);
                BlockPos pos = new BlockPos(st.getInt("posX"), st.getInt("posY"), st.getInt("posZ"));
                String loot = st.contains("loot") ? st.getString("loot") : null;
                slots.add(new Slot(new ResourceLocation(st.getString("template")), Rotation.valueOf(st.getString("rot")), pos, loot));
            }
            return new DarkstoneDungeonPiece(context.structureTemplateManager(), slots.get(0).pos, seed, slots);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
            tag.putLong("seed", seed);
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
                tag.put("slot" + i, st);
            }
        }

        @Override
        public void postProcess(WorldGenLevel level, net.minecraft.world.level.StructureManager structureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            for (Slot s : slots) {
                if (s.template == null) {
                    continue;
                }
                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setRotation(s.rotation)
                        .setRandom(random)
                        .setBoundingBox(box)
                        .setKeepLiquids(false)
                        .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE_CLEAR_FLUID);
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

        private void populateChests(WorldGenLevel level, Slot slot, StructurePlaceSettings settings, RandomSource random, BoundingBox box) {
            if (slot.template == null || slot.loot == null) {
                return;
            }
            ResourceLocation lootTable = new ResourceLocation(slot.loot);
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.STRUCTURE_BLOCK)) {
                if (!box.isInside(info.pos())) {
                    continue;
                }
                String metadata = info.nbt() == null ? "" : info.nbt().getString("metadata");
                if (!metadata.isEmpty() && !metadata.startsWith("chest")) {
                    continue;
                }
                level.setBlock(info.pos(), Blocks.AIR.defaultBlockState(), 3);
                BlockEntity blockEntity = level.getBlockEntity(info.pos().below());
                if (blockEntity instanceof ChestBlockEntity chest) {
                    chest.setLootTable(lootTable, random.nextLong());
                } else if (level.getBlockEntity(info.pos()) instanceof ChestBlockEntity chest) {
                    chest.setLootTable(lootTable, random.nextLong());
                }
            }
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.CHEST)) {
                if (!box.isInside(info.pos())) {
                    continue;
                }
                if (level.getBlockEntity(info.pos()) instanceof ChestBlockEntity chest) {
                    chest.setLootTable(lootTable, random.nextLong());
                }
            }
        }
    }

    private static class Slot {
        final ResourceLocation templateId;
        final Rotation rotation;
        final BlockPos pos;
        final String loot;
        StructureTemplate template;

        Slot(ResourceLocation templateId, Rotation rotation, BlockPos pos, String loot) {
            this.templateId = templateId;
            this.rotation = rotation;
            this.pos = pos;
            this.loot = loot;
        }
    }
}
