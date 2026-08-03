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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import shiroroku.theaurorian.Registry.StructureRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Moon Temple: floating island temple with a spiral path to the surface.
 * Replicates the 1.12 MoonTempleWorldGenerator layout (center, two towers,
 * courtyard, back parkour room, terrain, islands and spiral path).
 */
public class MoonTempleStructure extends Structure {

    public static final MapCodec<MoonTempleStructure> CODEC = simpleCodec(MoonTempleStructure::new);

    private static final int TEMPLE_HEIGHT = 200;
    private static final int FLOATING_ISLAND_DROP = 27;

    public MoonTempleStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!shiroroku.theaurorian.Config.CommonConfig.enable_moon_temple.get()) {
            return Optional.empty();
        }
        BlockPos center = new BlockPos(context.chunkPos().getMinBlockX() + 8, TEMPLE_HEIGHT, context.chunkPos().getMinBlockZ() + 8);
        List<Slot> slots = computeSlots(center, RandomSource.create(context.seed()));
        return Optional.of(new GenerationStub(center, builder -> builder.addPiece(new MoonTemplePiece(context.structureTemplateManager(), slots))));
    }

    @Override
    public StructureType<?> type() {
        return StructureRegistry.MOON_TEMPLE_TYPE.get();
    }

    private static List<Slot> computeSlots(BlockPos center, RandomSource random) {
        List<Slot> slots = new ArrayList<>();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        int x = 0, z = 0, y = 0;

        // main temple parts at absolute offset (dx, dy, dz) from center
        add(slots, "moontemple/moontemplev2_center", Rotation.NONE, cx + 0, cy + 0, cz + 0);
        add(slots, "moontemple/moontemplev2_left", Rotation.NONE, cx - 16, cy + 0, cz + 0);
        add(slots, "moontemple/moontemplev2_right", Rotation.NONE, cx + 16, cy + 0, cz + 0);
        add(slots, "moontemple/moontemplev2_courtyard", Rotation.NONE, cx + 0, cy + 0, cz + 16);
        add(slots, "moontemple/moontemplev2_courtyardl", Rotation.NONE, cx - 16, cy + 0, cz + 16);
        add(slots, "moontemple/moontemplev2_courtyardr", Rotation.NONE, cx + 16, cy + 0, cz + 16);
        add(slots, "moontemple/moontemplev2_room", Rotation.NONE, cx + 0, cy + 0, cz - 16);

        // terrain pillars below main parts
        add(slots, "moontemple/moontemple_terrain", Rotation.NONE, cx + 0, cy - 12, cz + 0);
        add(slots, "moontemple/moontemple_terrain", Rotation.NONE, cx - 16, cy - 12, cz + 0);
        add(slots, "moontemple/moontemple_terrain", Rotation.NONE, cx + 16, cy - 12, cz + 0);
        add(slots, "moontemple/moontemple_terrain", Rotation.NONE, cx + 0, cy - 12, cz + 16);
        add(slots, "moontemple/moontemple_terrain", Rotation.NONE, cx + 0, cy - 12, cz - 16);

        // floating islands
        int[][] islands = {{-16, -48}, {16, -48}, {-48, -16}, {48, 16}, {-48, 16}, {48, -16}, {-16, 48}, {16, 48}};
        for (int[] iso : islands) {
            slots.add(new Slot(ResourceLocation.parse("theaurorian:moontemple/moontemple_island"), Rotation.NONE, new BlockPos(cx + iso[0], cy - FLOATING_ISLAND_DROP - random.nextInt(10), cz + iso[1])));
        }

        // spiral path (16 segments, wrapping the 5x5 chunk square)
        int yoffset = 7;
        int h = cy - 1;
        add(slots, "moontemple/moontemple_path_turn", Rotation.NONE, cx + 0, h - yoffset, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.NONE, cx + 0, h - yoffset * 17, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.NONE, cx + 16, h - yoffset * 2, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.NONE, cx + 16, h - yoffset * 18, cz + 32);
        add(slots, "moontemple/moontemple_path_turn", Rotation.COUNTERCLOCKWISE_90, cx + 32, h - yoffset * 3, cz + 47);
        add(slots, "moontemple/moontemple_path_straight", Rotation.COUNTERCLOCKWISE_90, cx + 32, h - yoffset * 4, cz + 47);
        add(slots, "moontemple/moontemple_path_straight", Rotation.COUNTERCLOCKWISE_90, cx + 32, h - yoffset * 5, cz + 47);
        add(slots, "moontemple/moontemple_path_straight", Rotation.COUNTERCLOCKWISE_90, cx + 32, h - yoffset * 6, cz + 47);
        add(slots, "moontemple/moontemple_path_turn", Rotation.CLOCKWISE_180, cx + 47, h - yoffset * 7, cz + 47);
        add(slots, "moontemple/moontemple_path_straight", Rotation.CLOCKWISE_180, cx + 47, h - yoffset * 8, cz + 47);
        add(slots, "moontemple/moontemple_path_straight", Rotation.CLOCKWISE_180, cx + 47, h - yoffset * 9, cz + 47);
        add(slots, "moontemple/moontemple_path_straight", Rotation.CLOCKWISE_180, cx + 47, h - yoffset * 10, cz + 47);
        add(slots, "moontemple/moontemple_path_turn", Rotation.CLOCKWISE_90, cx + 47, h - yoffset * 11, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.CLOCKWISE_90, cx + 47, h - yoffset * 12, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.CLOCKWISE_90, cx + 47, h - yoffset * 13, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.CLOCKWISE_90, cx + 47, h - yoffset * 14, cz + 32);
        add(slots, "moontemple/moontemple_path_turn", Rotation.NONE, cx + 0, h - yoffset * 15, cz + 32);
        add(slots, "moontemple/moontemple_path_straight", Rotation.NONE, cx + 0, h - yoffset * 16, cz + 32);

        return slots;
    }

    private static void add(List<Slot> slots, String name, Rotation rot, int ax, int ay, int az) {
        slots.add(new Slot(ResourceLocation.parse("theaurorian:" + name), rot, new BlockPos(ax, ay, az)));
    }

    private static BoundingBox boundingBoxOf(List<Slot> slots) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (Slot s : slots) {
            minX = Math.min(minX, s.pos.getX());
            minY = Math.min(minY, s.pos.getY());
            minZ = Math.min(minZ, s.pos.getZ());
            maxX = Math.max(maxX, s.pos.getX() + 16);
            maxY = Math.max(maxY, s.pos.getY() + 16);
            maxZ = Math.max(maxZ, s.pos.getZ() + 16);
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static class MoonTemplePiece extends StructurePiece {

        private final long seed;
        private final List<Slot> slots;

        public MoonTemplePiece(StructureTemplateManager manager, List<Slot> slots) {
            super(StructureRegistry.MOON_TEMPLE_PIECE.get(), 0, boundingBoxOf(slots));
            this.seed = 0;
            this.slots = slots;
            for (Slot s : slots) {
                s.template = manager.get(s.templateId).orElse(null);
            }
        }

        private MoonTemplePiece(StructureTemplateManager manager, long seed, List<Slot> slots) {
            this(manager, slots);
            // seed is only used for chest randomisation; keep stored for save/load symmetry
        }

        public static MoonTemplePiece load(StructurePieceSerializationContext context, CompoundTag tag) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; tag.contains("slot" + i); i++) {
                CompoundTag st = tag.getCompound("slot" + i);
                Slot s = new Slot(ResourceLocation.parse(st.getString("template")), Rotation.valueOf(st.getString("rot")), new BlockPos(st.getInt("posX"), st.getInt("posY"), st.getInt("posZ")));
                s.template = context.structureTemplateManager().get(s.templateId).orElse(null);
                slots.add(s);
            }
            return new MoonTemplePiece(context.structureTemplateManager(), slots);
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
                tag.put("slot" + i, st);
            }
        }

        @Override
        public void postProcess(WorldGenLevel level, net.minecraft.world.level.StructureManager structureManager, ChunkGenerator chunkGen, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
            StructurePlaceSettings settings = new StructurePlaceSettings().setRandom(random)
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                    .addProcessor(IgnoreBlockStructureProcessor.AURORIAN_STONE);
            for (Slot s : slots) {
                if (s.template == null || !new ChunkPos(s.pos).equals(chunkPos)) {
                    continue;
                }
                settings.setRotation(s.rotation);
                s.template.placeInWorld(level, s.pos, s.pos, settings, random, 2);
                populateChests(level, s, settings, random);
            }
        }

        private void populateChests(WorldGenLevel level, Slot slot, StructurePlaceSettings settings, RandomSource random) {
            if (slot.template == null) {
                return;
            }
            for (StructureTemplate.StructureBlockInfo info : slot.template.filterBlocks(slot.pos, settings, Blocks.STRUCTURE_BLOCK)) {
                String data = info.nbt() != null ? info.nbt().getString("metadata") : "";
                String loot = switch (data) {
                    case "chest_low" -> "theaurorian:chests/moontemple/low";
                    case "chest_med" -> "theaurorian:chests/moontemple/med";
                    default -> null;
                };
                if (loot == null) {
                    continue;
                }
                level.setBlock(info.pos(), Blocks.AIR.defaultBlockState(), 3);
                BlockEntity te = level.getBlockEntity(info.pos().below());
                if (te instanceof ChestBlockEntity chest) {
                    chest.setLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, ResourceLocation.parse(loot)), random.nextLong());
                }
            }
        }
    }

    private static class Slot {
        final ResourceLocation templateId;
        final Rotation rotation;
        final BlockPos pos;
        StructureTemplate template;

        Slot(ResourceLocation templateId, Rotation rotation, BlockPos pos) {
            this.templateId = templateId;
            this.rotation = rotation;
            this.pos = pos;
        }
    }
}
