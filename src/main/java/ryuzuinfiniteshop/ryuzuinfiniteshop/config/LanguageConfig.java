package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import com.google.common.collect.Sets;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LanguageConfig {
    public static String getLanguage() {
        return "Japanese";
    }


    private static final HashMap<LanguageKey, String> texts = new HashMap<>();

    public static String getText(LanguageKey key) {
        return texts.get(key);
    }

    public static void load() {
        File file = FileUtil.initializeFile(Config.language.toLowerCase() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        Arrays.stream(LanguageKey.values()).forEach(key -> texts.put(key, yaml.getString(key.getConfigKey(), key.getLanguage(Config.language))));
    }

    public static void save() {
        Set<String> languages = Sets.newHashSet("japanese", "english", Config.language);
        for (String language : languages) {
            File file = FileUtil.initializeFile(language + ".yml");
            YamlConfiguration yaml = new YamlConfiguration();
            try {
                yaml.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }

            Arrays.stream(LanguageKey.values()).forEach(key -> {
                if(!yaml.contains(key.getConfigKey())) yaml.set(key.getConfigKey(), key.getLanguage(language));
            });

            try {
                yaml.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
