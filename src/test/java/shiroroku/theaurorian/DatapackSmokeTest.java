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
            JsonObject adv = readObject(MAIN.resolve("data/theaurorian/advancement/" + id + ".json"));
            assertTrue(adv.has("requirements"), id + " missing requirements");
            assertEquals(1, adv.getAsJsonArray("requirements").size(), id + " requirements outer size");
            assertTrue(adv.getAsJsonArray("requirements").get(0).getAsJsonArray().size() >= 2,
                    id + " should OR multiple criteria");
        }
    }

    /**
     * Descriptions must match unlock logic:
     * - root: enter/visit the dimension (changed_dimension OR location)
     * - sickle/scrapper/tea: any one matching item (single ItemPredicate list/tag)
     * - geologist: all four materials at once (multiple ItemPredicates = AND)
     * - bosses: kill OR inventory (requirements OR row), and lang mentions both paths
     */
    @Test
    void advancementConditionsMatchDescriptions() throws Exception {
        JsonObject en = readObject(MAIN.resolve("assets/theaurorian/lang/en_us.json"));
        Path dir = MAIN.resolve("data/theaurorian/advancement");

        // root: visit dimension
        JsonObject root = readObject(dir.resolve("root.json"));
        assertTrue(root.getAsJsonObject("criteria").has("entered"), "root needs changed_dimension criterion");
        assertEquals("minecraft:changed_dimension",
                root.getAsJsonObject("criteria").getAsJsonObject("entered").get("trigger").getAsString());
        assertEquals("theaurorian:the_aurorian",
                root.getAsJsonObject("criteria").getAsJsonObject("entered")
                        .getAsJsonObject("conditions").get("to").getAsString());
        assertTrue(root.getAsJsonObject("criteria").has("located"), "root needs location fallback");
        String rootDesc = en.get("advancement.theaurorian.root.description").getAsString().toLowerCase();
        assertTrue(rootDesc.contains("visit") || rootDesc.contains("aurorian"), "root description should mention visiting");

        // any-one-item advancements: one ItemPredicate whose items list/tag covers alternatives
        assertAnyOfItems(dir.resolve("sickle.json"),
                "theaurorian:aurorian_stone_sickle", "theaurorian:silentwood_sickle", "theaurorian:moonstone_sickle");
        assertAnyOfItems(dir.resolve("scrapper.json"),
                "theaurorian:aurorianite_ingot", "theaurorian:crystalline_ingot", "theaurorian:umbra_ingot");
        JsonObject tea = readObject(dir.resolve("tea.json"));
        JsonObject teaPred = tea.getAsJsonObject("criteria").getAsJsonObject("inv")
                .getAsJsonObject("conditions").getAsJsonArray("items").get(0).getAsJsonObject();
        assertEquals(1, tea.getAsJsonObject("criteria").getAsJsonObject("inv")
                .getAsJsonObject("conditions").getAsJsonArray("items").size(),
                "tea should be a single any-of predicate, not AND of all teas");
        assertTrue(teaPred.has("items"), "tea predicate items");
        String teaItems = teaPred.get("items").toString();
        assertTrue(teaItems.contains("theaurorian:tea") || teaItems.contains("silkberry_tea"),
                "tea should accept tea tag or tea items, got " + teaItems);
        String teaDesc = en.get("advancement.theaurorian.tea.description").getAsString().toLowerCase();
        assertTrue(teaDesc.contains("tea"), "tea description");
        String sickleDesc = en.get("advancement.theaurorian.sickle.description").getAsString().toLowerCase();
        assertTrue(sickleDesc.contains("sickle") || sickleDesc.contains("a sickle"), "sickle description is singular");
        String scrapperDesc = en.get("advancement.theaurorian.scrapper.description").getAsString().toLowerCase();
        assertTrue(scrapperDesc.contains("an ingot") || scrapperDesc.contains("ingot"), "scrapper description is singular ingot");

        // geologist: all four required (AND via multiple predicates)
        JsonObject geo = readObject(dir.resolve("geologist.json"));
        var geoItems = geo.getAsJsonObject("criteria").getAsJsonObject("inv")
                .getAsJsonObject("conditions").getAsJsonArray("items");
        assertEquals(4, geoItems.size(), "geologist requires all four materials");
        String geoDesc = en.get("advancement.theaurorian.geologist.description").getAsString().toLowerCase();
        assertTrue(geoDesc.contains("crystal") && geoDesc.contains("coal")
                && geoDesc.contains("cerulean") && geoDesc.contains("moonstone"), "geologist lists all four");

        // bosses: kill OR loot, descriptions mention both
        assertBossOr(dir, en, "liberated", "theaurorian:dungeon_keeper", "theaurorian:keepers_amulet",
                "dungeon keeper", "amulet");
        assertBossOr(dir, en, "exterminated", "theaurorian:dungeon_spider", "theaurorian:dark_amulet",
                "spider", "amulet");
        assertBossOr(dir, en, "dethroned", "theaurorian:moon_queen", "theaurorian:trophy_moon_queen",
                "moon queen", "trophy");

        // single-item craft/obtain — description item id present in criteria
        assertSingleItemInv(dir.resolve("auroriansteel.json"), "theaurorian:aurorian_steel_ingot");
        assertSingleItemInv(dir.resolve("darkstonekey.json"), "theaurorian:darkstone_key");
        assertSingleItemInv(dir.resolve("lockpicks.json"), "theaurorian:lockpicks");
        assertSingleItemInv(dir.resolve("moonlightforge.json"), "theaurorian:moonlight_forge");
        assertSingleItemInv(dir.resolve("moontemplekey.json"), "theaurorian:moon_temple_key");
        assertSingleItemInv(dir.resolve("lavenderbread.json"), "theaurorian:lavender_bread");
        assertSingleItemInv(dir.resolve("silkberryjam.json"), "theaurorian:silkberry_jam");
    }

    private static void assertAnyOfItems(Path path, String... expectedIds) throws IOException {
        JsonObject adv = readObject(path);
        var items = adv.getAsJsonObject("criteria").getAsJsonObject("inv")
                .getAsJsonObject("conditions").getAsJsonArray("items");
        assertEquals(1, items.size(), path.getFileName() + " should use one any-of ItemPredicate (not AND list)");
        JsonObject pred = items.get(0).getAsJsonObject();
        assertTrue(pred.has("items"), path.getFileName() + " missing items");
        String blob = pred.get("items").toString();
        for (String id : expectedIds) {
            assertTrue(blob.contains(id), path.getFileName() + " missing " + id + " in " + blob);
        }
    }

    private static void assertSingleItemInv(Path path, String itemId) throws IOException {
        JsonObject adv = readObject(path);
        var items = adv.getAsJsonObject("criteria").getAsJsonObject("inv")
                .getAsJsonObject("conditions").getAsJsonArray("items");
        assertEquals(1, items.size(), path.getFileName() + " item count");
        String blob = items.get(0).toString();
        assertTrue(blob.contains(itemId), path.getFileName() + " expected " + itemId + " got " + blob);
    }

    private static void assertBossOr(Path dir, JsonObject en, String id, String entityId, String itemId,
                                     String descEntityToken, String descLootToken) throws IOException {
        JsonObject adv = readObject(dir.resolve(id + ".json"));
        JsonObject criteria = adv.getAsJsonObject("criteria");
        assertTrue(criteria.has("kill"), id + " kill");
        assertTrue(criteria.has("inv"), id + " inv");
        assertEquals("minecraft:player_killed_entity", criteria.getAsJsonObject("kill").get("trigger").getAsString());
        String entityBlob = criteria.getAsJsonObject("kill").toString();
        assertTrue(entityBlob.contains(entityId), id + " entity " + entityId + " in " + entityBlob);
        String invBlob = criteria.getAsJsonObject("inv").toString();
        assertTrue(invBlob.contains(itemId), id + " item " + itemId);
        assertTrue(adv.has("requirements") && adv.getAsJsonArray("requirements").size() == 1
                        && adv.getAsJsonArray("requirements").get(0).getAsJsonArray().size() >= 2,
                id + " OR requirements");
        String desc = en.get("advancement.theaurorian." + id + ".description").getAsString().toLowerCase();
        assertTrue(desc.contains(descEntityToken), id + " desc should mention " + descEntityToken + ": " + desc);
        assertTrue(desc.contains(descLootToken) || desc.contains("or"),
                id + " desc should mention loot/or path: " + desc);
    }

    /**
     * 1.19+ ItemPredicate uses {@code items:[id,...]}, not the pre-1.13 singular {@code item}.
     * A bare {@code item} key is ignored, so the predicate matches every stack and any pickup
     * unlocks every inventory_changed advancement.
     * <p>
     * 1.20.5+ display icons are ItemStacks ({@code id}), not the old {@code item} key.
     */
    @Test
    void inventoryChangedAdvancementsUseItemsArrayNotSingularItem() throws Exception {
        Path dir = MAIN.resolve("data/theaurorian/advancement");
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(p -> p.toString().endsWith(".json")).forEach(path -> {
                try {
                    JsonObject adv = readObject(path);
                    String file = path.getFileName().toString();
                    if (adv.has("display")) {
                        JsonObject icon = adv.getAsJsonObject("display").getAsJsonObject("icon");
                        assertTrue(icon.has("id"), file + " display.icon must use \"id\" (ItemStack), not \"item\"");
                        assertFalse(icon.has("item"), file + " display.icon still has obsolete \"item\" key");
                    }
                    if (!adv.has("criteria")) {
                        return;
                    }
                    assertNoSingularItemInItemPredicates(adv.getAsJsonObject("criteria"), file);
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
        Path structures = MAIN.resolve("data/theaurorian/structure");
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
        Path mf = MAIN.resolve("data/theaurorian/recipe/moonlight_forge");
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
