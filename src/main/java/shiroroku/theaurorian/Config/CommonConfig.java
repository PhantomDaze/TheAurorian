package shiroroku.theaurorian.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

    public static final ForgeConfigSpec config;

    public static final ForgeConfigSpec.ConfigValue<Boolean> absorption_orb_repairs_all;
    public static final ForgeConfigSpec.ConfigValue<Boolean> crystalline_shield_repairs_all;
    public static final ForgeConfigSpec.ConfigValue<Double> aurorianite_shovel_resistance_difference;
    public static final ForgeConfigSpec.ConfigValue<Integer> aurorianite_shovel_dig_radius;
    public static final ForgeConfigSpec.ConfigValue<Double> aurorian_steel_level_multiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> boss_damage_per_player;
    public static final ForgeConfigSpec.ConfigValue<Double> boss_health_per_player;
    public static final ForgeConfigSpec.ConfigValue<Double> boss_speed_per_player;
    public static final ForgeConfigSpec.ConfigValue<Double> chimney_multiplier;
    public static final ForgeConfigSpec.ConfigValue<Double> crystalline_pickaxe_treasure_chance;
    public static final ForgeConfigSpec.ConfigValue<Double> cystalline_sword_beam_damage;
    public static final ForgeConfigSpec.ConfigValue<Double> cystalline_sword_beam_velocity;
    public static final ForgeConfigSpec.ConfigValue<Double> crystalline_sprite_beam_damage;
    public static final ForgeConfigSpec.ConfigValue<Double> moonstone_damage_chance;
    public static final ForgeConfigSpec.ConfigValue<Integer> runestone_dungeon_mob_density;
    public static final ForgeConfigSpec.ConfigValue<Integer> moon_temple_mob_density;
    public static final ForgeConfigSpec.ConfigValue<Integer> darkstone_dungeon_mob_density;
    public static final ForgeConfigSpec.ConfigValue<Integer> slime_boots_cooldown;
    public static final ForgeConfigSpec.ConfigValue<Double> scrapper_crystal_break_chance;
    public static final ForgeConfigSpec.ConfigValue<Double> scrapper_crystal_speed_discount;
    public static final ForgeConfigSpec.ConfigValue<Double> spectral_armor_cleanse_chance;
    public static final ForgeConfigSpec.ConfigValue<Double> umbra_pickaxe_speed_multiplier;
    public static final ForgeConfigSpec.ConfigValue<Integer> aurorian_steel_base_level;
    public static final ForgeConfigSpec.ConfigValue<Integer> chimney_max;
    public static final ForgeConfigSpec.ConfigValue<Integer> scrapper_base_craft_duration;
    public static final ForgeConfigSpec.ConfigValue<Integer> umbra_pickaxe_selection_cost;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enable_darkstone_dungeon;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enable_moon_temple;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enable_ruins;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enable_umbra_tower;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enable_runestone_dungeon;
    public static final ForgeConfigSpec.ConfigValue<Integer> runestone_dungeon_floors;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("The Aurorian");
        builder.push("Tools and Armor");
        absorption_orb_repairs_all = builder.define("absorption_orb_repairs_all", false);
        aurorian_steel_base_level = builder.comment("Base max XP required to level up").defineInRange("aurorian_steel_base_level", 100, 1, Integer.MAX_VALUE);
        aurorian_steel_level_multiplier = builder.comment("The next max required XP is this multiplied by the last max required XP").defineInRange("aurorian_steel_level_multiplier", 1.25, 1, Integer.MAX_VALUE);
        crystalline_pickaxe_treasure_chance = builder.defineInRange("crystalline_pickaxe_treasure_chance", 0.2, 0, 1);
        crystalline_shield_repairs_all = builder.define("crystalline_shield_repairs_all", false);
        cystalline_sword_beam_damage = builder.defineInRange("cystalline_sword_beam_damage", 8f, 1f, Integer.MAX_VALUE);
        cystalline_sword_beam_velocity = builder.defineInRange("cystalline_sword_beam_velocity", 2f, 0.25f, Integer.MAX_VALUE);
        crystalline_sprite_beam_damage = builder.defineInRange("crystalline_sprite_beam_damage", 2f, 0f, Integer.MAX_VALUE);
        moonstone_damage_chance = builder.comment("% to take damage, day adds +1 damage to this after, night does not").defineInRange("moonstone_damage_chance", 0.5, 0, 1);
        slime_boots_cooldown = builder.comment("Ticks between sneaking slime boot high jumps").defineInRange("slime_boots_cooldown", 100, 0, Integer.MAX_VALUE);
        spectral_armor_cleanse_chance = builder.comment("+% per armor piece to cleanse negative effects when attacking").defineInRange("spectral_armor_cleanse_chance", 0.06, 0, 0.25);
        umbra_pickaxe_selection_cost = builder.defineInRange("umbra_pickaxe_selection_cost", 15, 0, Integer.MAX_VALUE);
        umbra_pickaxe_speed_multiplier = builder.defineInRange("umbra_pickaxe_selection_speed_multiplier", 1.5f, 1, Integer.MAX_VALUE);
        aurorianite_shovel_resistance_difference = builder.comment("How different the speed of diggable blocks can be for them to also be mined. 0 = only blocks of the same resistance can be mined together, 0.15 allows for sand/gravel/clay/dirt to all be mined together").defineInRange("aurorianite_shovel_resistance_difference", 0.15, 0, Integer.MAX_VALUE);
        aurorianite_shovel_dig_radius = builder.defineInRange("aurorianite_shovel_dig_radius", 1, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("Blocks");
        chimney_max = builder.comment("Max chimneys you can stack on the aurorian furnace").defineInRange("chimney_max", 10, 0, Integer.MAX_VALUE);
        chimney_multiplier = builder.comment("Fuel multiplier when at max chimneys").defineInRange("chimney_multiplier", 2.5, 0, Integer.MAX_VALUE);
        scrapper_base_craft_duration = builder.defineInRange("scrapper_base_craft_duration", 240, 2, Integer.MAX_VALUE);
        scrapper_crystal_break_chance = builder.defineInRange("scrapper_crystal_break_chance", 0.25, 0, 1);
        scrapper_crystal_speed_discount = builder.defineInRange("scrapper_crystal_speed_discount", 0.25, 0, 1);
        builder.pop();
        builder.push("Scaling").comment("Boss scaling is applied when a boss spawner spawns a boss AND when there is more than 1 player. the number of players is multiplied by this, +1, then multiplied by the base value");
        boss_damage_per_player = builder.defineInRange("boss_damage_per_player", 0.2, 0, Integer.MAX_VALUE);
        boss_health_per_player = builder.defineInRange("boss_health_per_player", 0.75, 0, Integer.MAX_VALUE);
        boss_speed_per_player = builder.defineInRange("boss_speed_per_player", 0.2, 0, Integer.MAX_VALUE);
        builder.pop();
        builder.push("Mobs").comment("Nearby-density multipliers for dungeon/temple mobs.");
        runestone_dungeon_mob_density = builder.defineInRange("runestone_dungeon_mob_density", 1, 0, 10);
        moon_temple_mob_density = builder.defineInRange("moon_temple_mob_density", 1, 0, 10);
        darkstone_dungeon_mob_density = builder.defineInRange("darkstone_dungeon_mob_density", 1, 0, 10);
        builder.pop();
        builder.push("World Gen").comment("Toggles for Aurorian structure generation. Spacing of every structure is configured in data/theaurorian/worldgen/structure_set/. Runestone uses a custom Structure (upstream 15×15 quadrants), not jigsaw.");
        enable_darkstone_dungeon = builder.define("enable_darkstone_dungeon", true);
        enable_moon_temple = builder.define("enable_moon_temple", true);
        enable_ruins = builder.define("enable_ruins", true);
        enable_umbra_tower = builder.define("enable_umbra_tower", true);
        enable_runestone_dungeon = builder.define("enable_runestone_dungeon", true);
        runestone_dungeon_floors = builder.comment("Number of intermediate floors between base and boss (forced even so stairs line up).").defineInRange("runestone_dungeon_floors", 4, 2, 32);
        builder.pop();
        builder.pop();
        config = builder.build();
    }
}
