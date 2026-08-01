package shiroroku.theaurorian.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ContentTestSupport {

    private ContentTestSupport() {
    }

    static Path projectRoot() {
        Path dir = Path.of("").toAbsolutePath();
        for (int i = 0; i < 6; i++) {
            if (Files.isRegularFile(dir.resolve("build.gradle")) && Files.isDirectory(dir.resolve("scripts"))) {
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

    static JsonObject readObject(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement el = JsonParser.parseReader(reader);
            if (!el.isJsonObject()) {
                throw new IOException(path + " not a JSON object");
            }
            return el.getAsJsonObject();
        }
    }

    static Map<String, JsonObject> loadLangs(Path main) throws IOException {
        Map<String, JsonObject> map = new HashMap<>();
        Path dir = main.resolve("assets/theaurorian/lang");
        for (String name : List.of("en_us", "zh_cn", "es_es")) {
            map.put(name, readObject(dir.resolve(name + ".json")));
        }
        return map;
    }

    static Set<String> jsonStems(Path primary, Path secondary, String relativeDir) throws IOException {
        Set<String> out = new HashSet<>();
        for (Path root : List.of(primary, secondary)) {
            Path dir = root.resolve(relativeDir);
            if (!Files.isDirectory(dir)) {
                continue;
            }
            try (Stream<Path> stream = Files.list(dir)) {
                stream.filter(p -> p.getFileName().toString().endsWith(".json"))
                        .map(p -> p.getFileName().toString().replace(".json", ""))
                        .forEach(out::add);
            }
        }
        return out;
    }

    static Set<String> parseBlockIds(Path javaRoot) throws IOException {
        String text = Files.readString(javaRoot.resolve("Registry/BlockRegistry.java"), StandardCharsets.UTF_8);
        Set<String> ids = new HashSet<>();
        Matcher m1 = Pattern.compile("regBlockItem\\w*\\(\\s*\\w+\\s*,\\s*\"([a-z0-9_]+)\"").matcher(text);
        while (m1.find()) {
            ids.add(m1.group(1));
        }
        Matcher m2 = Pattern.compile("(?:BLOCKS\\w*)\\.register\\(\"([a-z0-9_]+)\"").matcher(text);
        while (m2.find()) {
            ids.add(m2.group(1));
        }
        return ids;
    }

    static Set<String> parseItemIds(Path javaRoot) throws IOException {
        String text = Files.readString(javaRoot.resolve("Registry/ItemRegistry.java"), StandardCharsets.UTF_8);
        Matcher m = Pattern.compile("\\.register\\(\"([a-z0-9_]+)\"").matcher(text);
        Set<String> ids = new HashSet<>();
        while (m.find()) {
            ids.add(m.group(1));
        }
        return ids;
    }

    static List<String> parseEntityIds(Path javaRoot) throws IOException {
        String text = Files.readString(javaRoot.resolve("Registry/EntityRegistry.java"), StandardCharsets.UTF_8);
        Matcher m = Pattern.compile("ENTITIES\\.register\\(\"([a-z0-9_]+)\"").matcher(text);
        return m.results().map(r -> r.group(1)).collect(Collectors.toList());
    }
}
