package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item.DisplayPanel;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DisplayPanelConfig {
    private static final HashMap<ShopTrade.TradeResult, Material> defaultResultMaterial = new HashMap<ShopTrade.TradeResult, Material>() {{
        put(ShopTrade.TradeResult.Success, Material.GREEN_STAINED_GLASS_PANE);
        put(ShopTrade.TradeResult.NotAfford, Material.RED_STAINED_GLASS_PANE);
        put(ShopTrade.TradeResult.Limited, Material.RED_STAINED_GLASS_PANE);
        put(ShopTrade.TradeResult.Full, Material.YELLOW_STAINED_GLASS_PANE);
        put(ShopTrade.TradeResult.Normal, Material.WHITE_STAINED_GLASS_PANE);
        put(ShopTrade.TradeResult.Locked, Material.ORANGE_STAINED_GLASS_PANE);
    }};
    private static HashMap<ShopTrade.TradeResult, DisplayPanel> panels = new HashMap<>();

    public static DisplayPanel getPanel(ShopTrade.TradeResult result) {
        return panels.get(result);
    }
    public static void load() {
        File file = FileUtil.initializeFile("panel.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        panels = new HashMap<ShopTrade.TradeResult, DisplayPanel>() {{
            for(ShopTrade.TradeResult result : defaultResultMaterial.keySet()) {
                put(result,
                    new DisplayPanel(
                            result,
                            Material.valueOf(yaml.getString(getResultConfig(result) + ".Material", defaultResultMaterial.get(result).name())),
                            yaml.getInt(getResultConfig(result) + ".CustomModelData", -1)
                    )
                );
            }
        }};
    }

    private static String getResultConfig(ShopTrade.TradeResult result) {
        return "Display.Panel." + result.name();
    }
}
