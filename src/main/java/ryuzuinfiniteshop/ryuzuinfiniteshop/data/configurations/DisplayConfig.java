package ryuzuinfiniteshop.ryuzuinfiniteshop.data.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.configuration.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DisplayConfig {
    enum DisplayType {Success, NotAfford, Full, NONE}
    private static HashMap<DisplayType, Material> DisplayTypeMaterial = new HashMap<DisplayType, Material>() {{
        put(DisplayType.Success, Material.GREEN_STAINED_GLASS_PANE);
        put(DisplayType.NotAfford, Material.RED_STAINED_GLASS_PANE);
        put(DisplayType.Full, Material.YELLOW_STAINED_GLASS_PANE);
        put(DisplayType.NONE, Material.BLACK_STAINED_GLASS_PANE);
    }};
    private static HashMap<DisplayType,Integer> DisplayCustomModel = new HashMap<DisplayType, Integer>() {{
        put(DisplayType.Success, 0);
        put(DisplayType.NotAfford, 0);
        put(DisplayType.Full, 0);
        put(DisplayType.NONE, 0);
    }};

    public static Material getMaterial(DisplayType type) {
        return DisplayTypeMaterial.get(type);
    }

    public static int getCustomModel(DisplayType type) {
        return DisplayCustomModel.get(type);
    }

    public static void setMaterial(DisplayType type, Material material) {
        DisplayTypeMaterial.put(type, material);
    }

    public static void setCustomModel(DisplayType type, int customModel) {
        DisplayCustomModel.put(type, customModel);
    }

    public static void loadDisplay() {
        File file = FileUtil.initializeFile("config.yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        for(DisplayType type : DisplayType.values()) {
            if(config.contains("Display." + type.name() + ".Material")) {
                setMaterial(type, Material.valueOf(config.getString("Display." + type.name() + ".Material")));
            }
            if(config.contains("Display." + type.name() + ".CustomModel")) {
                setCustomModel(type, config.getInt("Display." + type.name() + ".CustomModel"));
            }
        }
    }

    public static void saveDisplay() {
        File file = FileUtil.initializeFile("config.yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        for(DisplayType type : DisplayType.values()) {
            config.set("Display." + type.name() + ".Material", getMaterial(type).toString());
            config.set("Display." + type.name() + ".CustomModel", getCustomModel(type));
        }
        try{
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
