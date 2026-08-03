package shiroroku.theaurorian;

import com.google.gson.JsonArray;
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

    /**
     * Descriptions that mean "any one of" must not use multi-predicate AND inventory checks.
     * Multiple entries in inventory_changed conditions.items are AND; multiple ids inside one
     * ItemPredicate items array (or a tag) are OR.
     */
    @Test
    void anyOfAdvancementsUseSingleItemPredicate() throws Exception {
        // sickle: craft a sickle (any tier)
        JsonArray sickle = invItems("sickle");
        assertEquals(1, sickle.size(), "sickle should OR sickle tiers in one predicate");
        assertTrue(sickle.get(0).getAsJsonObject().getAsJsonArray("items").size() >= 3);

        // scrapper: create an ingot from scrapped loot (any scrap ingot)
        JsonArray scrapper = invItems("scrapper");
        assertEquals(1, scrapper.size(), "scrapper should OR scrap ingots in one predicate");
        assertTrue(scrapper.get(0).getAsJsonObject().getAsJsonArray("items").size() >= 3);

        // tea: make some tea (any tea) — tag or multi-id single predicate
        JsonArray tea = invItems("tea");
        assertEquals(1, tea.size(), "tea should match any tea via one predicate");
        JsonObject teaPred = tea.get(0).getAsJsonObject();
        assertTrue(teaPred.has("tag") || (teaPred.has("items") && teaPred.getAsJsonArray("items").size() >= 2),
                "tea predicate should use tag or multi-id items");
    }

    /** Geologist description lists four materials — require all four (AND). */
    @Test
    void geologistRequiresAllListedMaterials() throws Exception {
        JsonArray items = invItems("geologist");
        assertEquals(4, items.size(), "geologist should AND four material predicates");
    }

    /**
     * 1.20 location trigger ignores top-level dimension; player predicate / changed_dimension required.
     */
    @Test
    void rootUsesValidAurorianVisitCriteria() throws Exception {
        JsonObject root = readObject(MAIN.resolve("data/theaurorian/advancements/root.json"));
        JsonObject criteria = root.getAsJsonObject("criteria");
        boolean ok = false;
        for (String key : criteria.keySet()) {
            JsonObject c = criteria.getAsJsonObject(key);
            String trigger = c.get("trigger").getAsString();
            if ("minecraft:changed_dimension".equals(trigger)) {
                assertEquals("theaurorian:the_aurorian",
                        c.getAsJsonObject("conditions").get("to").getAsString());
                ok = true;
            }
            if ("minecraft:location".equals(trigger)) {
                // Must not rely on obsolete top-level dimension key alone
                JsonObject cond = c.getAsJsonObject("conditions");
                assertFalse(cond.has("dimension") && !cond.has("player"),
                        "location must nest dimension under player entity_properties");
                ok = true;
            }
        }
        assertTrue(ok, "root needs changed_dimension and/or nested location");
    }

    @Test
    void bossAdvancementKillTargetsMatchDescriptions() throws Exception {
        assertEquals("theaurorian:dungeon_keeper", killType("liberated"));
        assertEquals("theaurorian:dungeon_spider", killType("exterminated"));
        assertEquals("theaurorian:moon_queen", killType("dethroned"));
    }

    private static JsonArray invItems(String id) throws IOException {
        JsonObject adv = readObject(MAIN.resolve("data/theaurorian/advancements/" + id + ".json"));
        return adv.getAsJsonObject("criteria").getAsJsonObject("inv")
                .getAsJsonObject("conditions").getAsJsonArray("items");
    }

    private static String killType(String id) throws IOException {
        JsonObject adv = readObject(MAIN.resolve("data/theaurorian/advancements/" + id + ".json"));
        return adv.getAsJsonObject("criteria").getAsJsonObject("kill")
                .getAsJsonObject("conditions").getAsJsonObject("entity").get("type").getAsString();
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
