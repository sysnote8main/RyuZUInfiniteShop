package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import com.github.ryuzu.sis.api.IMythicHandler;
import com.github.ryuzu.sis.v16newer.MythicHandlerV5_2_0;
import com.github.ryuzu.sis.v16older.MythicHandlerV4_12_0;
import org.bukkit.Bukkit;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

public class MythicInstanceProvider {
    private static IMythicHandler instance;

    public static IMythicHandler getInstance() {
        if (instance == null) throw new NullPointerException(LanguageKey.ERROR_MYTHICMOBS_INVALID_LOADED.getMessage());
        return instance;
    }

    public static boolean isLoaded() {
        return instance != null;
    }

    public static void setInstance() {
        if (Bukkit.getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            if (RyuZUInfiniteShop.VERSION < 16)
                instance = new MythicHandlerV4_12_0(RyuZUInfiniteShop.getPlugin() , ShopUtil::reloadAllShopTradeInventory);
            else
                instance = new MythicHandlerV5_2_0();
        }
    }
}
