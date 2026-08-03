package shiroroku.theaurorian;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lightweight in-JVM datapack smoke tests (no Minecraft bootstrap).
 */
class DatapackSmokeTest {

    private static final Path ROOT = findRoot();
    private static final Path MAIN = ROOT.resolve("src/main/resources");

    @Test
    void langFilesShareKeySet() throws Exception {
        JsonObject en = readObject(MAIN.resolve("assets/theaurorian/lang/en_us.json"));
        JsonObject zh = readObject(MAIN.resolve("assets/theaurorian/lang/zh_cn.json"));
        JsonObject es = readObject(MAIN.resolve("assets/theaurorian/lang/es_es.json"));
        assertTrue(en.size() >= 300, "en_us keys");
        assertEquals(en.keySet(), zh.keySet(), "zh_cn key set mismatch");
        assertEquals(en.keySet(), es.keySet(), "es_es key set mismatch");
    }

    @Test
    void bossAdvancementsUseOrRequirements() throws Exception {
        for (String id : new String[]{"liberated", "exterminated", "dethroned"}) {
            JsonObject adv = readObject(MAIN.resolve("data/theaurorian/advancements/" + id + ".json"));
            assertTrue(adv.has("requirements"), id + " missing requirements");
            assertEquals(1, adv.getAsJsonArray("requirements").size(), id + " requirements outer size");
            assertTrue(adv.getAsJsonArray("requirements").get(0).getAsJsonArray().size() >= 2,
                    id + " should OR multiple criteria");
        }
    }

    /**
     * 1.19+ ItemPredicate uses {@code items:[id,...]}, not the pre-1.13 singular {@code item}.
     * A bare {@code item} key is ignored, so the predicate matches every stack and any pickup
     * unlocks every inventory_changed advancement.
     */
    @Test
    void inventoryChangedAdvancementsUseItemsArrayNotSingularItem() throws Exception {
        Path dir = MAIN.resolve("data/theaurorian/advancements");
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    JsonObject adv = readObject(path);
                    if (!adv.has("criteria")) {
                        return;
                    }
                    assertNoSingularItemInItemPredicates(adv.getAsJsonObject("criteria"), path.getFileName().toString());
                } catch (IOException e) {
                    fail(e);
                }
            });
        }
    }

    private static void assertNoSingularItemInItemPredicates(JsonElement el, String file) {
        if (el == null || el.isJsonNull()) {
            return;
        }
        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            // ItemPredicate shape: has "item" string but not "items"/"tag" → would match ANY item
            if (obj.has("item") && obj.get("item").isJsonPrimitive() && obj.get("item").getAsJsonPrimitive().isString()
                    && !obj.has("items") && !obj.has("tag")) {
                fail(file + " uses obsolete ItemPredicate key \"item\" (use \"items\": [id]); would match any stack");
            }
            for (String key : obj.keySet()) {
                assertNoSingularItemInItemPredicates(obj.get(key), file);
            }
        } else if (el.isJsonArray()) {
            for (JsonElement child : el.getAsJsonArray()) {
                assertNoSingularItemInItemPredicates(child, file);
            }
        }
    }

    @Test
    void mirrorGraphHasNoDanglingChildren() throws Exception {
        Path dir = MAIN.resolve("data/theaurorian/mirror_of_guidance");
        Set<String> nodes = new HashSet<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> nodes.add(p.getFileName().toString().replace(".json", "")));
        }
        assertTrue(nodes.size() >= 18, "mirror node count");
        for (String node : nodes) {
            JsonObject json = readObject(dir.resolve(node + ".json"));
            assertTrue(json.has("icon"), node + " icon");
            if (!json.has("children")) {
                continue;
            }
            for (JsonElement child : json.getAsJsonArray("children")) {
                String id = child.getAsString();
                String path = id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
                assertTrue(nodes.contains(path), node + " -> missing child " + id);
            }
        }
    }

    @Test
    void structureFoldersMeetMinimums() throws Exception {
        Path structures = MAIN.resolve("data/theaurorian/structures");
        assertMinNbt(structures.resolve("runestone"), 20);
        assertMinNbt(structures.resolve("darkstone"), 14);
        assertMinNbt(structures.resolve("moontemple"), 11);
        assertMinNbt(structures.resolve("umbratower"), 1);
        assertMinNbt(structures.resolve("ruins"), 3);
        assertMinNbt(structures.resolve("weepingwillow"), 5);
    }

    @Test
    void biomesDeclareMusic() throws Exception {
        Path biomeDir = MAIN.resolve("data/theaurorian/worldgen/biome");
        try (Stream<Path> stream = Files.list(biomeDir)) {
            long count = stream.filter(p -> p.toString().endsWith(".json")).peek(path -> {
                try {
                    JsonObject biome = readObject(path);
                    assertTrue(biome.getAsJsonObject("effects").has("music"),
                            path.getFileName() + " missing music");
                } catch (IOException e) {
                    fail(e);
                }
            }).count();
            assertTrue(count >= 7, "biome count");
        }
    }

    @Test
    void criticalMoonlightForgeRecipesExist() {
        Path mf = MAIN.resolve("data/theaurorian/recipes/moonlight_forge");
        assertTrue(Files.isRegularFile(mf.resolve("keepers_bow.json")));
        assertTrue(Files.isRegularFile(mf.resolve("queens_chipper.json")));
        assertTrue(Files.isRegularFile(mf.resolve("moon_shield.json")));
    }

    @Test
    void dungeonKeyRecipesMatchUpstream() throws Exception {
        Path shapeless = MAIN.resolve("data/theaurorian/recipes/shapeless");
        JsonObject dark = readObject(shapeless.resolve("darkstone_key.json"));
        assertEquals("minecraft:crafting_shapeless", dark.get("type").getAsString());
        assertEquals("theaurorian:darkstone_key", dark.getAsJsonObject("result").get("item").getAsString());
        String darkIng = dark.getAsJsonArray("ingredients").toString();
        assertTrue(darkIng.contains("theaurorian:keepers_amulet"), darkIng);
        assertTrue(darkIng.contains("theaurorian:crystal"), darkIng);
        assertTrue(darkIng.contains("theaurorian:cerulean_nugget"), darkIng);

        JsonObject moon = readObject(shapeless.resolve("moon_temple_key.json"));
        assertEquals("theaurorian:moon_temple_key", moon.getAsJsonObject("result").get("item").getAsString());
        String moonIng = moon.getAsJsonArray("ingredients").toString();
        assertTrue(moonIng.contains("theaurorian:dark_amulet"), moonIng);
        assertTrue(moonIng.contains("theaurorian:crystal"), moonIng);
        assertTrue(moonIng.contains("theaurorian:moon_gem"), moonIng);

        JsonObject interior = readObject(shapeless.resolve("moon_temple_interior_key.json"));
        String intIng = interior.getAsJsonArray("ingredients").toString();
        assertTrue(intIng.contains("theaurorian:moon_temple_key_fragment"), intIng);
        assertTrue(intIng.contains("theaurorian:moon_gem"), intIng);
    }

    @Test
    void rootUsesChangedDimensionToAurorian() throws Exception {
        JsonObject root = readObject(MAIN.resolve("data/theaurorian/advancements/root.json"));
        JsonObject arrived = root.getAsJsonObject("criteria").getAsJsonObject("arrived");
        assertEquals("minecraft:changed_dimension", arrived.get("trigger").getAsString());
        assertEquals("theaurorian:the_aurorian", arrived.getAsJsonObject("conditions").get("to").getAsString());
    }

    @Test
    void multiOptionInventoryAdvancementsUseSinglePredicateOr() throws Exception {
        for (String id : new String[]{"sickle", "tea", "scrapper"}) {
            JsonObject adv = readObject(MAIN.resolve("data/theaurorian/advancements/" + id + ".json"));
            var items = adv.getAsJsonObject("criteria").getAsJsonObject("inv")
                    .getAsJsonObject("conditions").getAsJsonArray("items");
            assertEquals(1, items.size(), id + " should OR items in one predicate, not AND many");
            assertTrue(items.get(0).getAsJsonObject().getAsJsonArray("items").size() >= 2, id);
        }
    }

    private static void assertMinNbt(Path dir, int min) throws IOException {
        assertTrue(Files.isDirectory(dir), "missing " + dir);
        try (Stream<Path> stream = Files.walk(dir)) {
            long n = stream.filter(p -> p.getFileName().toString().endsWith(".nbt")).count();
            assertTrue(n >= min, dir.getFileName() + " nbt count " + n + " < " + min);
        }
    }

    private static JsonObject readObject(Path path) throws IOException {
        assertTrue(Files.isRegularFile(path), "missing " + path);
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement el = JsonParser.parseReader(reader);
            assertTrue(el.isJsonObject(), path + " not object");
            return el.getAsJsonObject();
        }
    }

    private static Path findRoot() {
        Path dir = Path.of("").toAbsolutePath();
        for (int i = 0; i < 6; i++) {
            if (Files.isRegularFile(dir.resolve("build.gradle"))) {
                return dir;
            }
            Path parent = dir.getParent();
            if (parent == null) {
                break;
            }
            dir = parent;
        }
        return Path.of("").toAbsolutePath();
    }
}
