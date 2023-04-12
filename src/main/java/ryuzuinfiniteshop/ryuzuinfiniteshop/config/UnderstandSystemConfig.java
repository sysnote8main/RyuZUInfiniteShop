package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UnderstandSystemConfig {
    public static List<String> signedPlayers;

    public static void load() {
        File file = FileUtil.initializeFile("sign.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        signedPlayers = (List<String>) yaml.getList("signedPlayers", new ArrayList<>());
    }

    public static void save() {
        File file = FileUtil.initializeFile("sign.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("signedPlayers", signedPlayers);
        try {
            yaml.save(file);
        } catch (IOException e) {
            if(!Config.readOnlyIgnoreException) e.printStackTrace();
        }
    }
}
