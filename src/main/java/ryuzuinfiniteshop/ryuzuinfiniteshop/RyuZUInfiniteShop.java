package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.Editor.EditorListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.OpenListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.TradeListener;

public final class RyuZUInfiniteShop extends JavaPlugin {
    private static RyuZUInfiniteShop plugin;
    public final static String prefix = ChatColor.GOLD + "[RyuZUInfiniteShop]";

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
        getPlugin().getServer().getPluginManager().registerEvents(new TradeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new OpenListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new EditorListener(), getPlugin());
    }
}
