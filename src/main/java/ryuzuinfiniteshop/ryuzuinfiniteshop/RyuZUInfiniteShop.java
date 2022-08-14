package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.gui.ShopGui;

import java.util.List;

public final class RyuZUInfiniteShop extends JavaPlugin {
    private static RyuZUInfiniteShop plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        registerEvents();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static RyuZUInfiniteShop getPlugin() {
        return plugin;
    }

    public static void registerEvents() {
        getPlugin().getServer().getPluginManager().registerEvents(new ShopSystem(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ShopOpen(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ShopEdit(), getPlugin());
    }
}
