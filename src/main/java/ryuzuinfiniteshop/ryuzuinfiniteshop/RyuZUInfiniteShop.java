package ryuzuinfiniteshop.ryuzuinfiniteshop;

import com.github.ryuzu.ryuzucommandsgenerator.RyuZUCommandsGenerator;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.commands.SpawnCommand;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.CancelItemMoveListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.Editor.ChangeDisplayNameListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.Editor.ChangeEquipmentListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.Editor.EditTradePageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.Editor.EditorListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.OpenShopListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.TradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

public final class RyuZUInfiniteShop extends JavaPlugin {
    private static RyuZUInfiniteShop plugin;
    public final static String prefix = ChatColor.GOLD + "[RyuZUInfiniteShop]";

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        registerEvents();
        registerCommands();
        new RyuZUCommandsGenerator(this);
        ShopUtil.loadAllShops();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ShopUtil.removeAllNPC();
    }

    public static RyuZUInfiniteShop getPlugin() {
        return plugin;
    }

    public static void registerEvents() {
        getPlugin().getServer().getPluginManager().registerEvents(new TradeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new OpenShopListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new EditorListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new EditTradePageListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeDisplayNameListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeEquipmentListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new CancelItemMoveListener(), getPlugin());
    }

    public static void registerCommands() {
        SpawnCommand.registerCommand();
    }
}
