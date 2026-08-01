package shiroroku.theaurorian.content;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Categorized full-port content tests (no Minecraft bootstrap).
 * Mirrors {@code scripts/validate_resources.py} categories for IDE/JUnit UX.
 */
@DisplayName("Port content (categorized)")
class PortContentCategoriesTest {

    private static final Path ROOT = ContentTestSupport.projectRoot();
    private static final Path MAIN = ROOT.resolve("src/main/resources");
    private static final Path GEN = ROOT.resolve("src/generated/resources");
    private static final Path JAVA = ROOT.resolve("src/main/java/shiroroku/theaurorian");
    private static final String MODID = "theaurorian";

    private static final Set<String> PROJECTILES = Set.of(
            "cerulean_arrow", "crystal_arrow", "crystalline_beam", "sticky_spiker", "webbing"
    );

    private static final Set<String> REQUIRED_BIOMES = Set.of(
            "aurorian_forest", "aurorian_plains", "aurorian_rough_forest",
            "aurorian_forest_hills", "aurorian_lakes", "aurorian_overgrowth",
            "weeping_willow_forest"
    );

    private static final Set<String> REQUIRED_STRUCTURES = Set.of(
            "runestone_dungeon", "darkstone_dungeon", "moon_temple", "umbra_tower",
            "ruins_1", "ruins_2", "graveyard", "ruined_house"
    );

    @Nested
    @DisplayName("A — Language")
    class Lang {
        @Test
        void threeLocalesPresentAndKeyAligned() throws Exception {
            Map<String, JsonObject> langs = ContentTestSupport.loadLangs(MAIN);
            assertEquals(Set.of("en_us", "zh_cn", "es_es"), langs.keySet());
            JsonObject en = langs.get("en_us");
            assertTrue(en.size() >= 350, "en_us key count " + en.size());
            assertEquals(en.keySet(), langs.get("zh_cn").keySet());
            assertEquals(en.keySet(), langs.get("es_es").keySet());
        }
    }

    @Nested
    @DisplayName("B — Blocks & Items")
    class BlocksItems {
        @Test
        void blockRegistrationsHaveBlockstatesAndLang() throws Exception {
            Set<String> blocks = ContentTestSupport.parseBlockIds(JAVA);
            assertTrue(blocks.size() >= 90, "blocks " + blocks.size());
            Set<String> bs = ContentTestSupport.jsonStems(GEN, MAIN, "assets/" + MODID + "/blockstates");
            assertTrue(bs.containsAll(blocks), "missing blockstates: " + diff(blocks, bs));
            JsonObject en = ContentTestSupport.loadLangs(MAIN).get("en_us");
            List<String> missingLang = blocks.stream()
                    .filter(b -> !en.has("block." + MODID + "." + b))
                    .sorted().toList();
            assertTrue(missingLang.isEmpty(), "blocks missing lang: " + missingLang);
            for (String id : List.of("boss_spawner", "weeping_willow_leaves", "aurorian_farm_tile",
                    "umbra_stone", "silentwood_chest", "moonlight_forge", "scrapper",
                    "aurorian_portal", "lavender_crop", "silkberry_crop", "mushroom")) {
                assertTrue(blocks.contains(id), "required block " + id);
            }
        }

        @Test
        void itemRegistrationsHaveModelsAndLang() throws Exception {
            Set<String> items = ContentTestSupport.parseItemIds(JAVA);
            Set<String> blocks = ContentTestSupport.parseBlockIds(JAVA);
            assertTrue(items.size() >= 120, "items " + items.size());
            Set<String> models = ContentTestSupport.jsonStems(GEN, MAIN, "assets/" + MODID + "/models/item");
            Set<String> need = new HashSet<>(items);
            need.addAll(blocks);
            List<String> missing = need.stream().filter(id -> !models.contains(id)).sorted().toList();
            // models may lag until runData for brand-new eggs — require pure items always
            List<String> missingPureItems = items.stream().filter(id -> !models.contains(id)).sorted().toList();
            assertTrue(missingPureItems.isEmpty(), "items missing models: " + missingPureItems);
            JsonObject en = ContentTestSupport.loadLangs(MAIN).get("en_us");
            List<String> missingLang = items.stream()
                    .filter(i -> !en.has("item." + MODID + "." + i) && !en.has("block." + MODID + "." + i))
                    .sorted().toList();
            assertTrue(missingLang.isEmpty(), "items missing lang: " + missingLang);
            for (String id : List.of("dungeon_locator", "keepers_bow", "queens_chipper", "moon_shield",
                    "slime_boots", "mirror_of_guidance", "trophy_keeper", "trophy_moon_queen", "trophy_spider")) {
                assertTrue(items.contains(id), "required item " + id);
            }
        }
    }

    @Nested
    @DisplayName("C — Entities")
    class Entities {
        @Test
        void entitiesHaveLangLootAndSpawnEggs() throws Exception {
            List<String> entities = ContentTestSupport.parseEntityIds(JAVA);
            assertTrue(entities.size() >= 19, "entities " + entities.size());
            Set<String> items = ContentTestSupport.parseItemIds(JAVA);
            JsonObject en = ContentTestSupport.loadLangs(MAIN).get("en_us");
            Set<String> loot = ContentTestSupport.jsonStems(MAIN, MAIN, "data/" + MODID + "/loot_tables/entities");
            for (String e : entities) {
                assertTrue(en.has("entity." + MODID + "." + e), "entity lang " + e);
                if (PROJECTILES.contains(e)) {
                    continue;
                }
                assertTrue(loot.contains(e), "entity loot " + e);
                assertTrue(items.contains("spawn_egg_" + e), "spawn egg " + e);
                assertTrue(en.has("item." + MODID + ".spawn_egg_" + e), "spawn egg lang " + e);
            }
            assertTrue(entities.containsAll(List.of("dungeon_keeper", "dungeon_spider", "moon_queen")));
            assertTrue(entities.containsAll(List.of("aurorian_pig", "aurorian_rabbit", "aurorian_sheep")));
        }
    }

    @Nested
    @DisplayName("D — Structures")
    class Structures {
        @Test
        void nbtFoldersAndDefinitions() throws Exception {
            Path structures = MAIN.resolve("data/" + MODID + "/structures");
            assertMinNbt(structures.resolve("runestone"), 20);
            assertMinNbt(structures.resolve("darkstone"), 14);
            assertMinNbt(structures.resolve("moontemple"), 11);
            assertMinNbt(structures.resolve("umbratower"), 1);
            assertMinNbt(structures.resolve("ruins"), 3);
            assertMinNbt(structures.resolve("weepingwillow"), 5);

            Set<String> defs = ContentTestSupport.jsonStems(MAIN, MAIN, "data/" + MODID + "/worldgen/structure");
            Set<String> sets = ContentTestSupport.jsonStems(MAIN, MAIN, "data/" + MODID + "/worldgen/structure_set");
            assertTrue(defs.containsAll(REQUIRED_STRUCTURES), "missing structure defs " + diff(REQUIRED_STRUCTURES, defs));
            assertTrue(sets.containsAll(REQUIRED_STRUCTURES), "missing structure sets " + diff(REQUIRED_STRUCTURES, sets));

            // structure_set -> structure
            Path setDir = MAIN.resolve("data/" + MODID + "/worldgen/structure_set");
            try (Stream<Path> stream = Files.list(setDir)) {
                stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                    try {
                        JsonObject obj = ContentTestSupport.readObject(path);
                        for (JsonElement el : obj.getAsJsonArray("structures")) {
                            String sid = el.getAsJsonObject().get("structure").getAsString();
                            String name = sid.substring(sid.indexOf(':') + 1);
                            assertTrue(defs.contains(name), path.getFileName() + " -> missing " + sid);
                        }
                    } catch (IOException e) {
                        fail(e);
                    }
                });
            }

            // single_template NBT
            Path defDir = MAIN.resolve("data/" + MODID + "/worldgen/structure");
            try (Stream<Path> stream = Files.list(defDir)) {
                stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                    try {
                        JsonObject obj = ContentTestSupport.readObject(path);
                        if ("theaurorian:single_template".equals(stringOrNull(obj, "type"))) {
                            String template = obj.get("template").getAsString();
                            String rel = template.substring(template.indexOf(':') + 1);
                            Path nbt = structures.resolve(rel + ".nbt");
                            assertTrue(Files.isRegularFile(nbt), path.getFileName() + " missing nbt " + nbt);
                        }
                        JsonElement biomes = obj.get("biomes");
                        if (biomes != null && biomes.isJsonArray()) {
                            assertFalse(biomes.getAsJsonArray().isEmpty(), path.getFileName() + " empty biomes");
                        }
                    } catch (IOException e) {
                        fail(e);
                    }
                });
            }
        }
    }

    @Nested
    @DisplayName("E — Worldgen")
    class Worldgen {
        @Test
        void biomesFeaturesDimension() throws Exception {
            Set<String> biomes = ContentTestSupport.jsonStems(MAIN, MAIN, "data/" + MODID + "/worldgen/biome");
            assertTrue(biomes.containsAll(REQUIRED_BIOMES), "missing biomes " + diff(REQUIRED_BIOMES, biomes));

            Set<String> cf = ContentTestSupport.jsonStems(MAIN, MAIN, "data/" + MODID + "/worldgen/configured_feature");
            Set<String> pf = ContentTestSupport.jsonStems(MAIN, MAIN, "data/" + MODID + "/worldgen/placed_feature");
            assertTrue(cf.containsAll(pf), "placed without configured " + diff(pf, cf));
            for (String f : List.of("silentwood_tree", "weeping_willow_tree", "mushroom_cave",
                    "lavender_patch", "silkberry_patch", "urn", "ore_cerulean", "ore_moonstone",
                    "ore_geode", "bright_bulb_patch")) {
                assertTrue(pf.contains(f), "placed feature " + f);
            }

            Path biomeDir = MAIN.resolve("data/" + MODID + "/worldgen/biome");
            try (Stream<Path> stream = Files.list(biomeDir)) {
                stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                    try {
                        JsonObject biome = ContentTestSupport.readObject(path);
                        assertTrue(biome.getAsJsonObject("effects").has("music"), path.getFileName() + " music");
                        if (biome.has("features")) {
                            for (JsonElement step : biome.getAsJsonArray("features")) {
                                if (!step.isJsonArray()) continue;
                                for (JsonElement f : step.getAsJsonArray()) {
                                    if (f.isJsonPrimitive()) {
                                        String id = f.getAsString();
                                        if (id.startsWith(MODID + ":")) {
                                            String name = id.substring(id.indexOf(':') + 1);
                                            assertTrue(pf.contains(name), path.getFileName() + " missing feature " + id);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        fail(e);
                    }
                });
            }

            JsonObject dim = ContentTestSupport.readObject(MAIN.resolve("data/" + MODID + "/dimension/the_aurorian.json"));
            JsonArray entries = dim.getAsJsonObject("generator").getAsJsonObject("biome_source").getAsJsonArray("biomes");
            Set<String> dimBiomes = new HashSet<>();
            entries.forEach(e -> dimBiomes.add(e.getAsJsonObject().get("biome").getAsString()));
            for (String b : REQUIRED_BIOMES) {
                assertTrue(dimBiomes.contains(MODID + ":" + b), "dimension missing " + b);
            }
            assertTrue(Files.isRegularFile(MAIN.resolve("data/" + MODID + "/worldgen/noise_settings/the_aurorian.json")));
        }
    }

    @Nested
    @DisplayName("F — Recipes & chest loot")
    class Recipes {
        @Test
        void recipeVolumeTypesAndBossMf() throws Exception {
            Path recipes = MAIN.resolve("data/" + MODID + "/recipes");
            long count;
            try (Stream<Path> stream = Files.walk(recipes)) {
                count = stream.filter(p -> p.toString().endsWith(".json")).count();
            }
            assertTrue(count >= 180, "recipes " + count);
            assertTrue(Files.isRegularFile(recipes.resolve("moonlight_forge/keepers_bow.json")));
            assertTrue(Files.isRegularFile(recipes.resolve("moonlight_forge/queens_chipper.json")));
            assertTrue(Files.isRegularFile(recipes.resolve("moonlight_forge/moon_shield.json")));

            int mf = 0, scrapper = 0;
            try (Stream<Path> stream = Files.walk(recipes)) {
                for (Path p : stream.filter(x -> x.toString().endsWith(".json")).toList()) {
                    JsonObject o = ContentTestSupport.readObject(p);
                    String type = o.has("type") ? o.get("type").getAsString() : "";
                    if ("theaurorian:moonlight_forge".equals(type)) mf++;
                    if ("theaurorian:scrapper".equals(type)) scrapper++;
                }
            }
            assertTrue(mf >= 20, "moonlight_forge recipes " + mf);
            assertTrue(scrapper >= 40, "scrapper recipes " + scrapper);

            Path chests = MAIN.resolve("data/" + MODID + "/loot_tables/chests");
            assertMinJson(chests.resolve("runestone"), 3);
            assertMinJson(chests.resolve("darkstone"), 3);
            assertMinJson(chests.resolve("moontemple"), 3);
            assertMinJson(chests.resolve("ruins"), 1);
        }

        @Test
        void recipeResultsAndChestLootReferenceKnownIds() throws Exception {
            Set<String> blocks = ContentTestSupport.parseBlockIds(JAVA);
            Set<String> items = ContentTestSupport.parseItemIds(JAVA);
            Path recipes = MAIN.resolve("data/" + MODID + "/recipes");
            try (Stream<Path> stream = Files.walk(recipes)) {
                for (Path p : stream.filter(x -> x.toString().endsWith(".json")).toList()) {
                    JsonObject o = ContentTestSupport.readObject(p);
                    if (!o.has("result")) continue;
                    JsonElement result = o.get("result");
                    String id = null;
                    if (result.isJsonPrimitive()) id = result.getAsString();
                    else if (result.isJsonObject()) {
                        JsonObject ro = result.getAsJsonObject();
                        if (ro.has("item")) id = ro.get("item").getAsString();
                    }
                    if (id != null && id.startsWith(MODID + ":")) {
                        String name = id.substring(id.indexOf(':') + 1);
                        assertTrue(blocks.contains(name) || items.contains(name),
                                p.getFileName() + " unknown result " + id);
                    }
                }
            }
            Path chests = MAIN.resolve("data/" + MODID + "/loot_tables/chests");
            try (Stream<Path> stream = Files.walk(chests)) {
                for (Path p : stream.filter(x -> x.toString().endsWith(".json")).toList()) {
                    assertLootItemsKnown(p, blocks, items);
                }
            }
            Path entities = MAIN.resolve("data/" + MODID + "/loot_tables/entities");
            try (Stream<Path> stream = Files.list(entities)) {
                for (Path p : stream.filter(x -> x.toString().endsWith(".json")).toList()) {
                    assertLootItemsKnown(p, blocks, items);
                }
            }
        }
    }

    @Nested
    @DisplayName("G — Advancements")
    class Advancements {
        @Test
        void treeLangAndBossOrLogic() throws Exception {
            Path dir = MAIN.resolve("data/" + MODID + "/advancements");
            Set<String> stems;
            try (Stream<Path> stream = Files.list(dir)) {
                stems = stream.filter(p -> p.toString().endsWith(".json"))
                        .map(p -> p.getFileName().toString().replace(".json", ""))
                        .collect(Collectors.toSet());
            }
            assertTrue(stems.size() >= 15);
            assertTrue(stems.contains("root"));
            assertTrue(stems.containsAll(Set.of("liberated", "exterminated", "dethroned")));
            JsonObject en = ContentTestSupport.loadLangs(MAIN).get("en_us");
            for (String stem : stems) {
                JsonObject adv = ContentTestSupport.readObject(dir.resolve(stem + ".json"));
                assertTrue(adv.has("criteria"), stem + " criteria");
                if (adv.has("parent")) {
                    String parent = adv.get("parent").getAsString();
                    if (parent.startsWith(MODID + ":")) {
                        String pname = parent.substring(parent.indexOf(':') + 1);
                        assertTrue(stems.contains(pname), stem + " missing parent " + parent);
                    }
                }
                if (Set.of("liberated", "exterminated", "dethroned").contains(stem)) {
                    JsonArray req = adv.getAsJsonArray("requirements");
                    assertEquals(1, req.size());
                    assertTrue(req.get(0).getAsJsonArray().size() >= 2, stem + " OR requirements");
                }
                if (adv.has("display")) {
                    JsonObject display = adv.getAsJsonObject("display");
                    for (String field : List.of("title", "description")) {
                        if (display.has(field) && display.getAsJsonObject(field).has("translate")) {
                            String key = display.getAsJsonObject(field).get("translate").getAsString();
                            assertTrue(en.has(key), stem + " missing lang " + key);
                        }
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("H — Mirror of Guidance")
    class Mirror {
        @Test
        void nodesGraphAndLang() throws Exception {
            Path dir = MAIN.resolve("data/" + MODID + "/mirror_of_guidance");
            Set<String> nodes;
            try (Stream<Path> stream = Files.list(dir)) {
                nodes = stream.filter(p -> p.toString().endsWith(".json"))
                        .map(p -> p.getFileName().toString().replace(".json", ""))
                        .collect(Collectors.toSet());
            }
            assertTrue(nodes.size() >= 18);
            for (String req : List.of("aurorian", "dungeons", "dungeon_runestone", "dungeon_darkstone",
                    "dungeon_moon_temple", "agriculture", "passives", "boss_loot", "locator",
                    "crystalline", "aurorianite", "umbra", "crafting", "ores")) {
                assertTrue(nodes.contains(req), "mirror node " + req);
            }
            JsonObject en = ContentTestSupport.loadLangs(MAIN).get("en_us");
            for (String node : nodes) {
                JsonObject json = ContentTestSupport.readObject(dir.resolve(node + ".json"));
                assertTrue(json.has("icon"));
                assertTrue(json.has("x"));
                assertTrue(json.has("y"));
                assertTrue(en.has("mirror_of_guidance." + MODID + "." + node + ".name"), node + " name");
                assertTrue(en.has("mirror_of_guidance." + MODID + "." + node + ".desc"), node + " desc");
                if (json.has("children")) {
                    for (JsonElement child : json.getAsJsonArray("children")) {
                        String id = child.getAsString();
                        String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
                        assertTrue(nodes.contains(path), node + " dangling " + id);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("I — Sounds & particles")
    class Audio {
        @Test
        void soundsParticlesAndRegistries() throws Exception {
            JsonObject sounds = ContentTestSupport.readObject(MAIN.resolve("assets/" + MODID + "/sounds.json"));
            assertFalse(sounds.entrySet().isEmpty());
            Path soundsDir = MAIN.resolve("assets/" + MODID + "/sounds");
            long ogg;
            try (Stream<Path> stream = Files.walk(soundsDir)) {
                ogg = stream.filter(p -> p.getFileName().toString().endsWith(".ogg")).count();
            }
            assertTrue(ogg >= 6, "ogg count " + ogg);
            for (var entry : sounds.entrySet()) {
                JsonArray arr = entry.getValue().getAsJsonObject().getAsJsonArray("sounds");
                assertNotNull(arr, entry.getKey());
                for (JsonElement s : arr) {
                    String name = s.isJsonPrimitive() ? s.getAsString() : s.getAsJsonObject().get("name").getAsString();
                    String rel = name.contains(":") ? name.substring(name.indexOf(':') + 1) : name;
                    assertTrue(Files.isRegularFile(soundsDir.resolve(rel + ".ogg")), "missing ogg " + rel);
                }
            }
            JsonObject particles = ContentTestSupport.readObject(MAIN.resolve("assets/" + MODID + "/particles.json"));
            assertFalse(particles.entrySet().isEmpty());
            String soundJava = Files.readString(JAVA.resolve("Registry/SoundRegistry.java"), StandardCharsets.UTF_8);
            assertTrue(soundJava.contains("\"music\""));
            assertTrue(soundJava.contains("\"weepingwillowbell\""));
            String particleJava = Files.readString(JAVA.resolve("Registry/ParticleRegistry.java"), StandardCharsets.UTF_8);
            assertTrue(particleJava.contains("\"weeping_willow_drip\""));
        }
    }

    @Nested
    @DisplayName("J — Tags & generated data")
    class Tags {
        @Test
        void generatedTagsAndBlockLootPresent() throws Exception {
            assertTrue(Files.isDirectory(GEN.resolve("data")), "run runData to generate tags/loot");
            long tags;
            try (Stream<Path> stream = Files.walk(GEN.resolve("data"))) {
                tags = stream.filter(p -> p.toString().replace('\\', '/').contains("/tags/") && p.toString().endsWith(".json")).count();
            }
            assertTrue(tags >= 40, "tag files " + tags);
            Path blockLoot = GEN.resolve("data/" + MODID + "/loot_tables/blocks");
            assertTrue(Files.isDirectory(blockLoot));
            long bloot;
            try (Stream<Path> stream = Files.list(blockLoot)) {
                bloot = stream.filter(p -> p.toString().endsWith(".json")).count();
            }
            assertTrue(bloot >= 80, "block loot " + bloot);

            String featureJava = Files.readString(JAVA.resolve("Registry/FeatureRegistry.java"), StandardCharsets.UTF_8);
            assertTrue(featureJava.contains("weeping_willow") || featureJava.contains("WEEPING_WILLOW"));
            String structureJava = Files.readString(JAVA.resolve("Registry/StructureRegistry.java"), StandardCharsets.UTF_8);
            assertTrue(structureJava.toLowerCase().contains("darkstone"));
            assertTrue(structureJava.toLowerCase().contains("moon_temple") || structureJava.toLowerCase().contains("moontemple"));
        }
    }

    // --- helpers ---

    private static void assertMinNbt(Path dir, int min) throws IOException {
        assertTrue(Files.isDirectory(dir), "missing " + dir);
        try (Stream<Path> stream = Files.walk(dir)) {
            long n = stream.filter(p -> p.getFileName().toString().endsWith(".nbt")).count();
            assertTrue(n >= min, dir.getFileName() + " nbt " + n + " < " + min);
        }
    }

    private static void assertMinJson(Path dir, int min) throws IOException {
        assertTrue(Files.isDirectory(dir), "missing " + dir);
        try (Stream<Path> stream = Files.list(dir)) {
            long n = stream.filter(p -> p.toString().endsWith(".json")).count();
            assertTrue(n >= min, dir.getFileName() + " json " + n + " < " + min);
        }
    }

    private static void assertLootItemsKnown(Path path, Set<String> blocks, Set<String> items) throws IOException {
        String raw = Files.readString(path, StandardCharsets.UTF_8);
        Matcher m = Pattern.compile("\"name\"\\s*:\\s*\"theaurorian:([a-z0-9_/]+)\"").matcher(raw);
        while (m.find()) {
            String id = m.group(1);
            // skip loot table refs if any
            if (id.contains("/")) continue;
            assertTrue(blocks.contains(id) || items.contains(id), path.getFileName() + " unknown " + id);
        }
    }

    private static String stringOrNull(JsonObject o, String key) {
        return o.has(key) && o.get(key).isJsonPrimitive() ? o.get(key).getAsString() : null;
    }

    private static Set<String> diff(Set<String> need, Set<String> have) {
        Set<String> d = new HashSet<>(need);
        d.removeAll(have);
        return d;
    }
}
