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
