package shiroroku.theaurorian.Demo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import shiroroku.theaurorian.Blocks.BossSpawner.BossSpawnerBlockEntity;
import shiroroku.theaurorian.Blocks.FogWallBlock;
import shiroroku.theaurorian.Blocks.MoonlightForge.MoonlightForgeBlockEntity;
import shiroroku.theaurorian.Blocks.Scrapper.ScrapperBlockEntity;
import shiroroku.theaurorian.Items.Silentwood.SilentwoodPickaxe;
import shiroroku.theaurorian.Items.SlimeBoots.SlimeBootsItem;
import shiroroku.theaurorian.Registry.BlockRegistry;
import shiroroku.theaurorian.Registry.EntityRegistry;
import shiroroku.theaurorian.Registry.ItemRegistry;
import shiroroku.theaurorian.Registry.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Watchable demo playlist. Order is the on-screen sequence.
 */
public final class VisibleDemoCases {

    private VisibleDemoCases() {
    }

    public static List<VisibleDemoCase> all() {
        List<VisibleDemoCase> list = new ArrayList<>();
        list.add(machinesPlace());
        list.add(fogWall());
        list.add(mushroomBounce());
        list.add(agriculture());
        list.add(keyholeOpen());
        list.add(keyholeReject());
        list.add(queensChipper());
        list.add(bossKeeper());
        list.add(bossQueen());
        list.add(bossSpider());
        list.add(livingParade());
        list.add(scrapper());
        list.add(moonlightForge());
        list.add(silentwoodPick());
        list.add(locatorCycle());
        list.add(slimeBoots());
        list.add(portalBlocks());
        return list;
    }

    // ---------------------------------------------------------------- helpers

    private static void useBlock(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        state.use(level, player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, true));
    }

    private static void useItemOn(ServerPlayer player, ServerLevel level, BlockPos pos) {
        var ctx = new net.minecraft.world.item.context.UseOnContext(
                level, player, InteractionHand.MAIN_HAND, player.getMainHandItem(),
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, true));
        player.getMainHandItem().useOn(ctx);
    }

    private static <T extends Entity> T spawn(VisibleDemoRunner.DemoContext ctx, EntityType<T> type, BlockPos pos) {
        T e = type.create(ctx.level);
        if (e == null) {
            throw new IllegalStateException("Failed to create " + type);
        }
        e.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        ctx.level.addFreshEntity(e);
        ctx.track(e);
        ctx.focusOn(e);
        return e;
    }

    // ---------------------------------------------------------------- cases

    private static VisibleDemoCase machinesPlace() {
        return new VisibleDemoCase() {
            @Override public String id() { return "machines_place"; }
            @Override public String title() { return "Core machines"; }
            @Override public String subtitle() { return "MF · Scrapper · Furnace · Spawner · Chest"; }
            @Override public int stageWidth() { return 9; }
            @Override public int stageDepth() { return 5; }

            BlockPos mf, sc, fu, sp, ch;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                mf = ctx.stage.pos(1, 1, 2);
                sc = ctx.stage.pos(3, 1, 2);
                fu = ctx.stage.pos(5, 1, 2);
                sp = ctx.stage.pos(7, 1, 2);
                ch = ctx.stage.pos(4, 1, 3);
                ctx.stage.set(ctx.level, 1, 1, 2, BlockRegistry.moonlight_forge.get().defaultBlockState());
                ctx.stage.set(ctx.level, 3, 1, 2, BlockRegistry.scrapper.get().defaultBlockState());
                ctx.stage.set(ctx.level, 5, 1, 2, BlockRegistry.aurorian_furnace.get().defaultBlockState());
                ctx.stage.set(ctx.level, 7, 1, 2, BlockRegistry.boss_spawner.get().defaultBlockState());
                ctx.stage.set(ctx.level, 4, 1, 3, BlockRegistry.silentwood_chest.get().defaultBlockState());
                ctx.focusOn(ctx.stage.pos(4, 2, 2));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                // visual beat — nothing else
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!(ctx.level.getBlockEntity(mf) instanceof MoonlightForgeBlockEntity)) {
                    ctx.fail("MF BE missing");
                    return false;
                }
                if (!(ctx.level.getBlockEntity(sc) instanceof ScrapperBlockEntity)) {
                    ctx.fail("Scrapper BE missing");
                    return false;
                }
                if (!(ctx.level.getBlockEntity(sp) instanceof BossSpawnerBlockEntity)) {
                    ctx.fail("Spawner BE missing");
                    return false;
                }
                return ctx.level.getBlockState(fu).is(BlockRegistry.aurorian_furnace.get())
                        && ctx.level.getBlockState(ch).is(BlockRegistry.silentwood_chest.get());
            }
        };
    }

    private static VisibleDemoCase fogWall() {
        return new VisibleDemoCase() {
            @Override public String id() { return "fog_wall"; }
            @Override public String title() { return "Fog wall repel"; }
            @Override public String subtitle() { return "North-facing wall pushes +Z by 0.20"; }

            BlockPos wallPos;
            double resultZ;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                wallPos = ctx.stage.pos(3, 1, 3);
                ctx.stage.set(ctx.level, 3, 1, 3,
                        BlockRegistry.fog_wall.get().defaultBlockState().setValue(FogWallBlock.FACING, Direction.NORTH));
                ctx.stage.set(ctx.level, 3, 2, 3,
                        BlockRegistry.fog_wall.get().defaultBlockState().setValue(FogWallBlock.FACING, Direction.NORTH));
                ctx.focusOn(wallPos.above());
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                BlockState state = ctx.level.getBlockState(wallPos);
                FogWallBlock wall = (FogWallBlock) state.getBlock();
                // Show a pig shoved by the wall force
                var pig = spawn(ctx, EntityType.PIG, wallPos.above().north());
                pig.setDeltaMovement(0, 0, -0.5);
                var forced = wall.getRepellingForce(state, pig.getDeltaMovement());
                pig.setDeltaMovement(forced);
                resultZ = forced.z;
                ctx.focusOn(pig);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (Math.abs(resultZ - (-0.3)) > 1.0e-6) {
                    ctx.fail("expected Z≈-0.3 got " + resultZ);
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase mushroomBounce() {
        return new VisibleDemoCase() {
            @Override public String id() { return "mushroom_bounce"; }
            @Override public String title() { return "Indigo mushroom bounce"; }
            @Override public String subtitle() { return "Entity falling on cap bounces up"; }

            double yd;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                ctx.stage.set(ctx.level, 3, 1, 3, BlockRegistry.mushroom.get().defaultBlockState());
                ctx.focusOn(ctx.stage.pos(3, 2, 3));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                var pig = spawn(ctx, EntityType.PIG, ctx.stage.pos(3, 3, 3));
                pig.setDeltaMovement(0, -1.0, 0);
                BlockRegistry.mushroom.get().updateEntityAfterFallOn(ctx.level, pig);
                yd = pig.getDeltaMovement().y;
                ctx.focusOn(pig);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!(yd > 0)) {
                    ctx.fail("bounce yd=" + yd);
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase agriculture() {
        return new VisibleDemoCase() {
            @Override public String id() { return "agriculture"; }
            @Override public String title() { return "Aurorian crops"; }
            @Override public String subtitle() { return "Need farm tile + sky; dirt fails"; }
            @Override public int stageWidth() { return 9; }

            boolean dirtOk;
            boolean farmOk;
            boolean silkOk;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                // left: dirt (bad)
                ctx.stage.set(ctx.level, 1, 1, 2, Blocks.DIRT.defaultBlockState());
                ctx.stage.set(ctx.level, 1, 2, 2, BlockRegistry.lavender_crop.get().defaultBlockState());
                // middle: farm tile (good)
                ctx.stage.set(ctx.level, 4, 1, 2, BlockRegistry.aurorian_farm_tile.get().defaultBlockState());
                ctx.stage.set(ctx.level, 4, 2, 2, BlockRegistry.lavender_crop.get().defaultBlockState().setValue(CropBlock.AGE, 3));
                // right: farm tile silkberry
                ctx.stage.set(ctx.level, 7, 1, 2, BlockRegistry.aurorian_farm_tile.get().defaultBlockState());
                ctx.stage.set(ctx.level, 7, 2, 2, BlockRegistry.silkberry_crop.get().defaultBlockState().setValue(CropBlock.AGE, 3));
                ctx.focusOn(ctx.stage.pos(4, 2, 2));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                BlockPos dirtCrop = ctx.stage.pos(1, 2, 2);
                BlockPos farmCrop = ctx.stage.pos(4, 2, 2);
                BlockPos silkCrop = ctx.stage.pos(7, 2, 2);
                dirtOk = !ctx.level.getBlockState(dirtCrop).canSurvive(ctx.level, dirtCrop);
                farmOk = ctx.level.getBlockState(farmCrop).canSurvive(ctx.level, farmCrop);
                silkOk = ctx.level.getBlockState(silkCrop).canSurvive(ctx.level, silkCrop);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!dirtOk) {
                    ctx.fail("lavender survived on dirt");
                    return false;
                }
                if (!farmOk) {
                    ctx.fail("lavender failed on farm tile");
                    return false;
                }
                if (!silkOk) {
                    ctx.fail("silkberry failed on farm tile");
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase keyholeOpen() {
        return new VisibleDemoCase() {
            @Override public String id() { return "keyhole_open"; }
            @Override public String title() { return "Runestone keyhole"; }
            @Override public String subtitle() { return "Correct key breaks gates"; }
            @Override public int stageWidth() { return 7; }
            @Override public int stageDepth() { return 7; }

            BlockPos keyhole;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                keyhole = ctx.stage.pos(3, 1, 3);
                ctx.stage.set(ctx.level, 3, 1, 3, BlockRegistry.runestone_gate_keyhole.get().defaultBlockState());
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        BlockPos p = keyhole.offset(dx, 0, dz);
                        ctx.level.setBlock(p, BlockRegistry.runestone_gate.get().defaultBlockState(), 3);
                        ctx.stage.touched.add(p.immutable());
                    }
                }
                ctx.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.runestone_key.get()));
                ctx.focusOn(keyhole.above());
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                useBlock(ctx.player, ctx.level, keyhole);
                ctx.focusOn(keyhole);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!ctx.level.getBlockState(keyhole).isAir()) {
                    ctx.fail("keyhole still present");
                    return false;
                }
                if (!ctx.level.getBlockState(keyhole.offset(1, 0, 0)).isAir()) {
                    ctx.fail("gate not cleared");
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase keyholeReject() {
        return new VisibleDemoCase() {
            @Override public String id() { return "keyhole_reject"; }
            @Override public String title() { return "Wrong key rejected"; }
            @Override public String subtitle() { return "Runestone key ≠ darkstone keyhole"; }

            BlockPos keyhole;
            BlockPos gate;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                keyhole = ctx.stage.pos(3, 1, 3);
                gate = keyhole.offset(1, 0, 0);
                ctx.stage.set(ctx.level, 3, 1, 3, BlockRegistry.darkstone_gate_keyhole.get().defaultBlockState());
                ctx.level.setBlock(gate, BlockRegistry.darkstone_gate.get().defaultBlockState(), 3);
                ctx.stage.touched.add(gate.immutable());
                ctx.player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.runestone_key.get()));
                ctx.focusOn(keyhole.above());
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                useBlock(ctx.player, ctx.level, keyhole);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!ctx.level.getBlockState(keyhole).is(BlockRegistry.darkstone_gate_keyhole.get())) {
                    ctx.fail("keyhole was destroyed with wrong key");
                    return false;
                }
                if (!ctx.level.getBlockState(gate).is(BlockRegistry.darkstone_gate.get())) {
                    ctx.fail("gate was destroyed with wrong key");
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase queensChipper() {
        return new VisibleDemoCase() {
            @Override public String id() { return "queens_chipper"; }
            @Override public String title() { return "Queen's Chipper"; }
            @Override public String subtitle() { return "Breaks dungeon blocks; ignores stone"; }
            @Override public int stageWidth() { return 7; }

            BlockPos rune;
            BlockPos stone;
            ItemStack chipper;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                rune = ctx.stage.pos(2, 1, 3);
                stone = ctx.stage.pos(5, 1, 3);
                ctx.stage.set(ctx.level, 2, 1, 3, BlockRegistry.runestone.get().defaultBlockState());
                ctx.stage.set(ctx.level, 5, 1, 3, Blocks.STONE.defaultBlockState());
                chipper = new ItemStack(ItemRegistry.queens_chipper.get());
                ctx.player.setItemInHand(InteractionHand.MAIN_HAND, chipper);
                ctx.focusOn(rune.above());
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                useItemOn(ctx.player, ctx.level, rune);
                useItemOn(ctx.player, ctx.level, stone);
                ctx.focusOn(rune);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!ctx.level.getBlockState(rune).isAir()) {
                    ctx.fail("runestone not broken");
                    return false;
                }
                if (!ctx.level.getBlockState(stone).is(Blocks.STONE)) {
                    ctx.fail("stone should remain");
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase bossKeeper() {
        return bossCase("boss_keeper", "Boss: Dungeon Keeper", EntityRegistry.dungeon_keeper.get());
    }

    private static VisibleDemoCase bossQueen() {
        return bossCase("boss_queen", "Boss: Moon Queen", EntityRegistry.moon_queen.get());
    }

    private static VisibleDemoCase bossSpider() {
        return bossCase("boss_spider", "Boss: Spider Mother", EntityRegistry.dungeon_spider.get());
    }

    private static VisibleDemoCase bossCase(String id, String title, EntityType<? extends LivingEntity> type) {
        return new VisibleDemoCase() {
            @Override public String id() { return id; }
            @Override public String title() { return title; }
            @Override public String subtitle() { return "Spawner consumes block and summons boss"; }
            @Override public int stageWidth() { return 9; }
            @Override public int stageDepth() { return 9; }
            @Override public int stageHeight() { return 6; }

            BlockPos spawnerPos;
            boolean spawned;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                spawnerPos = ctx.stage.pos(4, 1, 4);
                ctx.stage.set(ctx.level, 4, 1, 4, BlockRegistry.boss_spawner.get().defaultBlockState());
                BlockEntity be = ctx.level.getBlockEntity(spawnerPos);
                if (be instanceof BossSpawnerBlockEntity spawner) {
                    spawner.setBoss(type);
                    CompoundTag tag = be.saveWithoutMetadata();
                    tag.putString("boss", net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
                    be.load(tag);
                }
                ctx.focusOn(spawnerPos.above(2));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                BlockEntity be = ctx.level.getBlockEntity(spawnerPos);
                if (be instanceof BossSpawnerBlockEntity spawner) {
                    spawner.setBoss(type);
                    spawner.spawnBoss();
                }
                // find boss near stage
                for (LivingEntity e : ctx.level.getEntitiesOfClass(LivingEntity.class, ctx.stage.bounds().inflate(3))) {
                    if (e.getType() == type) {
                        ctx.track(e);
                        ctx.focusOn(e);
                        spawned = true;
                        break;
                    }
                }
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!ctx.level.getBlockState(spawnerPos).isAir()) {
                    ctx.fail("spawner block not consumed");
                    return false;
                }
                if (!spawned) {
                    // second chance scan
                    long count = ctx.level.getEntitiesOfClass(LivingEntity.class, ctx.stage.bounds().inflate(4))
                            .stream().filter(e -> e.getType() == type).count();
                    if (count < 1) {
                        ctx.fail("boss not present");
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase livingParade() {
        return new VisibleDemoCase() {
            @Override public String id() { return "living_parade"; }
            @Override public String title() { return "Living entities"; }
            @Override public String subtitle() { return "Hollow · Knight · Pig · Spirit · Spiderling"; }
            @Override public int stageWidth() { return 11; }
            @Override public int stageDepth() { return 7; }

            int count;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                EntityType<?>[] types = {
                        EntityRegistry.hollow.get(),
                        EntityRegistry.undead_knight.get(),
                        EntityRegistry.aurorian_pig.get(),
                        EntityRegistry.spirit.get(),
                        EntityRegistry.spiderling.get()
                };
                for (int i = 0; i < types.length; i++) {
                    BlockPos p = ctx.stage.pos(1 + i * 2, 1, 3);
                    Entity e = types[i].create(ctx.level);
                    if (e != null) {
                        e.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, 180, 0);
                        ctx.level.addFreshEntity(e);
                        ctx.track(e);
                        count++;
                    }
                }
                ctx.focusOn(ctx.stage.pos(5, 2, 3));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                // idle watch
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (count < 5) {
                    ctx.fail("spawned " + count);
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase scrapper() {
        return new VisibleDemoCase() {
            @Override public String id() { return "scrapper"; }
            @Override public String title() { return "Scrapper machine"; }
            @Override public String subtitle() { return "Crystal + input ready; recipes loaded"; }

            boolean ready;
            int recipes;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                ctx.stage.set(ctx.level, 3, 1, 3, BlockRegistry.scrapper.get().defaultBlockState());
                ctx.stage.set(ctx.level, 3, 2, 3, BlockRegistry.crystal.get().defaultBlockState());
                BlockPos pos = ctx.stage.pos(3, 1, 3);
                if (ctx.level.getBlockEntity(pos) instanceof ScrapperBlockEntity be) {
                    be.getItemHandler().setStackInSlot(0, new ItemStack(BlockRegistry.crystal.get()));
                    be.getItemHandler().setStackInSlot(1, new ItemStack(ItemRegistry.aurorianite_sword.get()));
                    ready = !be.isMissingPrerequisites();
                    be.tryStartCraft();
                }
                recipes = ctx.level.getRecipeManager().getAllRecipesFor(RecipeRegistry.scrapper.get()).size();
                ctx.focusOn(pos.above());
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                // visual — crystal on top already placed
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!ready) {
                    ctx.fail("prereqs not satisfied");
                    return false;
                }
                if (recipes < 40) {
                    ctx.fail("scrapper recipes=" + recipes);
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase moonlightForge() {
        return new VisibleDemoCase() {
            @Override public String id() { return "moonlight_forge"; }
            @Override public String title() { return "Moonlight Forge"; }
            @Override public String subtitle() { return "Needs open moonlight; recipes loaded"; }

            int recipes;
            boolean nightSet;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                ctx.stage.set(ctx.level, 3, 1, 3, BlockRegistry.moonlight_forge.get().defaultBlockState());
                // ensure sky above
                for (int y = 2; y < 8; y++) {
                    ctx.stage.set(ctx.level, 3, y, 3, Blocks.AIR.defaultBlockState());
                }
                BlockPos pos = ctx.stage.pos(3, 1, 3);
                if (ctx.level.getBlockEntity(pos) instanceof MoonlightForgeBlockEntity be) {
                    be.getItemHandler().setStackInSlot(0, new ItemStack(ItemRegistry.silentwood_bow.get()));
                    be.getItemHandler().setStackInSlot(1, new ItemStack(ItemRegistry.trophy_keeper.get()));
                }
                ctx.level.setDayTime(18000); // night
                nightSet = true;
                recipes = ctx.level.getRecipeManager().getAllRecipesFor(RecipeRegistry.moonlight_forge.get()).size();
                ctx.focusOn(pos.above());
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                // allow a few ticks of night
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!nightSet) {
                    ctx.fail("night not set");
                    return false;
                }
                if (recipes < 20) {
                    ctx.fail("MF recipes=" + recipes);
                    return false;
                }
                BlockPos pos = ctx.stage.pos(3, 1, 3);
                if (!(ctx.level.getBlockEntity(pos) instanceof MoonlightForgeBlockEntity)) {
                    ctx.fail("MF BE missing");
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase silentwoodPick() {
        return new VisibleDemoCase() {
            @Override public String id() { return "silentwood_pick"; }
            @Override public String title() { return "Silentwood pickaxe levels"; }
            @Override public String subtitle() { return "Harvest level climbs with damage 0→3"; }

            int l0, l1, l2, l3;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                // pedestal of stone to "mine"
                ctx.stage.set(ctx.level, 3, 1, 3, Blocks.STONE.defaultBlockState());
                ctx.stage.set(ctx.level, 3, 2, 3, Blocks.STONE.defaultBlockState());
                ItemStack pick = new ItemStack(ItemRegistry.silentwood_pickaxe.get());
                ctx.player.setItemInHand(InteractionHand.MAIN_HAND, pick);
                ctx.focusOn(ctx.stage.pos(3, 2, 3));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                ItemStack pick = ctx.player.getMainHandItem();
                BlockState state = Blocks.STONE.defaultBlockState();
                BlockPos p = ctx.stage.pos(3, 2, 3);
                l0 = SilentwoodPickaxe.getHarvestLevel(pick);

                pick.setDamageValue((int) (pick.getMaxDamage() * 0.30));
                pick.getItem().mineBlock(pick, ctx.level, state, p, ctx.player);
                l1 = SilentwoodPickaxe.getHarvestLevel(pick);

                pick.setDamageValue((int) (pick.getMaxDamage() * 0.60));
                pick.getItem().mineBlock(pick, ctx.level, state, p, ctx.player);
                l2 = SilentwoodPickaxe.getHarvestLevel(pick);

                pick.setDamageValue((int) (pick.getMaxDamage() * 0.80));
                pick.getItem().mineBlock(pick, ctx.level, state, p, ctx.player);
                l3 = SilentwoodPickaxe.getHarvestLevel(pick);

                // break the top stone for spectacle
                ctx.level.destroyBlock(p, false);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (l0 != 0 || l1 != 1 || l2 != 2 || l3 != 3) {
                    ctx.fail("levels " + l0 + "," + l1 + "," + l2 + "," + l3);
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase locatorCycle() {
        return new VisibleDemoCase() {
            @Override public String id() { return "locator_cycle"; }
            @Override public String title() { return "Dungeon Locator"; }
            @Override public String subtitle() { return "Sneak-use cycles Runestone→Darkstone→MoonTemple"; }

            String d1, d2, d3;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                ItemStack locator = new ItemStack(ItemRegistry.dungeon_locator.get());
                ctx.player.setItemInHand(InteractionHand.MAIN_HAND, locator);
                ctx.player.setShiftKeyDown(true);
                // small pillar as visual anchor
                ctx.stage.set(ctx.level, 3, 1, 3, BlockRegistry.crystal.get().defaultBlockState());
                ctx.focusOn(ctx.stage.pos(3, 2, 3));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                ItemStack locator = ctx.player.getMainHandItem();
                ctx.player.setShiftKeyDown(true);
                locator.use(ctx.level, ctx.player, InteractionHand.MAIN_HAND);
                d1 = locator.getOrCreateTag().getString("dungeon");
                locator.use(ctx.level, ctx.player, InteractionHand.MAIN_HAND);
                d2 = locator.getOrCreateTag().getString("dungeon");
                locator.use(ctx.level, ctx.player, InteractionHand.MAIN_HAND);
                d3 = locator.getOrCreateTag().getString("dungeon");
                ctx.player.setShiftKeyDown(false);
                ctx.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§7Locator cycle: §f" + d1 + " §7→ §f" + d2 + " §7→ §f" + d3));
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (d1 == null || d1.isEmpty() || d1.equals(d2) || d2.equals(d3)) {
                    ctx.fail("cycle " + d1 + " / " + d2 + " / " + d3);
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase slimeBoots() {
        return new VisibleDemoCase() {
            @Override public String id() { return "slime_boots"; }
            @Override public String title() { return "Slime Boots"; }
            @Override public String subtitle() { return "Fall >3 cancelled + bounce impulse"; }

            boolean cancelled;
            boolean bounced;

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                ctx.player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemRegistry.slime_boots.get()));
                // platform edge visual
                ctx.stage.set(ctx.level, 3, 1, 2, Blocks.SLIME_BLOCK.defaultBlockState());
                ctx.focusOn(ctx.stage.pos(3, 2, 2));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
                var event = new net.minecraftforge.event.entity.living.LivingFallEvent(ctx.player, 5f, 1f);
                SlimeBootsItem.handleFallEvent(event);
                cancelled = event.isCanceled();
                bounced = ctx.player.getDeltaMovement().y > 0;
                // fling a demo pig the same way for visibility
                var pig = spawn(ctx, EntityType.PIG, ctx.stage.pos(3, 3, 3));
                pig.setDeltaMovement(0, 0.75, 0);
                ctx.focusOn(pig);
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!cancelled) {
                    ctx.fail("fall not cancelled");
                    return false;
                }
                if (!bounced) {
                    ctx.fail("no bounce impulse");
                    return false;
                }
                return true;
            }
        };
    }

    private static VisibleDemoCase portalBlocks() {
        return new VisibleDemoCase() {
            @Override public String id() { return "portal_blocks"; }
            @Override public String title() { return "Portal blocks"; }
            @Override public String subtitle() { return "Frame + portal place; dimension key exists"; }
            @Override public int stageWidth() { return 7; }
            @Override public int stageDepth() { return 5; }
            @Override public int stageHeight() { return 6; }

            @Override
            public void setup(VisibleDemoRunner.DemoContext ctx) {
                // simple frame outline
                for (int y = 1; y <= 4; y++) {
                    ctx.stage.set(ctx.level, 2, y, 2, BlockRegistry.aurorian_portal_frame.get().defaultBlockState());
                    ctx.stage.set(ctx.level, 5, y, 2, BlockRegistry.aurorian_portal_frame.get().defaultBlockState());
                }
                for (int x = 2; x <= 5; x++) {
                    ctx.stage.set(ctx.level, x, 1, 2, BlockRegistry.aurorian_portal_frame.get().defaultBlockState());
                    ctx.stage.set(ctx.level, x, 4, 2, BlockRegistry.aurorian_portal_frame.get().defaultBlockState());
                }
                ctx.stage.set(ctx.level, 3, 2, 2, BlockRegistry.aurorian_portal.get().defaultBlockState());
                ctx.stage.set(ctx.level, 4, 2, 2, BlockRegistry.aurorian_portal.get().defaultBlockState());
                ctx.stage.set(ctx.level, 3, 3, 2, BlockRegistry.aurorian_portal.get().defaultBlockState());
                ctx.stage.set(ctx.level, 4, 3, 2, BlockRegistry.aurorian_portal.get().defaultBlockState());
                ctx.focusOn(ctx.stage.pos(3, 3, 2));
            }

            @Override
            public void run(VisibleDemoRunner.DemoContext ctx) {
            }

            @Override
            public boolean verify(VisibleDemoRunner.DemoContext ctx) {
                if (!ctx.level.getBlockState(ctx.stage.pos(3, 2, 2)).is(BlockRegistry.aurorian_portal.get())) {
                    ctx.fail("portal block missing");
                    return false;
                }
                if (shiroroku.theaurorian.TheAurorian.the_aurorian == null) {
                    ctx.fail("dimension key null");
                    return false;
                }
                // boss attrs sanity on a quick spawn
                var keeper = spawn(ctx, EntityRegistry.dungeon_keeper.get(), ctx.stage.pos(1, 1, 1));
                if (keeper.getAttribute(Attributes.MAX_HEALTH) == null
                        || keeper.getAttribute(Attributes.MAX_HEALTH).getValue() <= 20) {
                    ctx.fail("keeper health");
                    return false;
                }
                return true;
            }
        };
    }
}
