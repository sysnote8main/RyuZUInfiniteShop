package ryuzuinfiniteshop.ryuzuinfiniteshop.configs;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.configuration.FileUtil;

import java.io.File;
import java.io.IOException;

public class DisplayPanel {
    public static int noafford = -1;
    public static int nospace = -1;
    public static int valid = -1;
    public static void loadConfig() throws IOException, InvalidConfigurationException {
        File file = FileUtil.initializeFile("config.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(file);
        noafford =  yaml.getInt("Display.Panel.NoAfford");
        yaml.getInt("Display.Panel.NoSpace");
        yaml.getInt("Display.Panel.Valid");
    }
}
