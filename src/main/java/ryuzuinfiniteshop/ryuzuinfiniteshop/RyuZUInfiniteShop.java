package ryuzuinfiniteshop.ryuzuinfiniteshop;

import com.github.ryuzu.ryuzucommandsgenerator.RyuZUCommandsGenerator;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.commands.SpawnCommand;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.CancelAffectNPC;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.CancelItemMoveListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades.OpenShopListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades.TradeListener;
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
        ShopUtil.saveAllShops();
        ShopUtil.removeAllNPC();
    }

    public static RyuZUInfiniteShop getPlugin() {
        return plugin;
    }

    public static void registerEvents() {
        getPlugin().getServer().getPluginManager().registerEvents(new EditMainPageListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new EditTradePageListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new CancelItemMoveListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new OpenShopListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new TradeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new CancelAffectNPC(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeEquipmentListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeDisplayNameListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeIndividualSettingsListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeShopTypeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ConvartListener(), getPlugin());

        /*try {
            for(Class<?> clazz : new HashSet<>(ClassUtil.loadClasses("ryuzuinfiniteshop.ryuzuinfiniteshop", "listeners"))) {
                Object o;
                try {
                    o = clazz.newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    return;
                }
                if(o instanceof Listener) getPlugin().getServer().getPluginManager().registerEvents((Listener) o, getPlugin());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public static void registerCommands() {
        SpawnCommand.registerCommand();
    }
}
