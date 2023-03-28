package ryuzuinfiniteshop.ryuzuinfiniteshop.configs;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.DisplayPanel;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.configuration.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LanguageConfig {
    private static final HashMap<String, String> defaultTexts = new HashMap<String, String>() {{
        put("Prefix", "&b[&fRyuzuInfiniteShop&b]&f");
        put("NoPermission", "&cYou don't have permission to use this command.");
        put("NoPlayer", "&cYou must be a player to use this command.");
        put("NoShop", "&cThere is no shop here.");
        put("NoTrade", "&cThere is no trade here.");
        put("NoTradeResult", "&cThere is no trade result here.");
    }};
    private static HashMap<String, String> texts = new HashMap<>();

    public static void load() {
        File file = FileUtil.initializeFile("language.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        defaultTexts.forEach((key, value) -> texts.put(key, yaml.getString(key, value)));
    }

    public static void save() {
        File file = FileUtil.initializeFile("language.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        texts.forEach(yaml::set);
        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
