package ryuzuinfiniteshop.ryuzuinfiniteshop;

import com.github.ryuzu.ryuzucommandsgenerator.RyuZUCommandsGenerator;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.commands.CommandChain;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.MythicItem;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.configurations.DisplayConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin.MythicListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.canceller.CancelAffectNpc;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.canceller.CancelItemMoveListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin.ShopListListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.change.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.convert.ConvartListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.delete.RemoveShopListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.edit.EditMainPageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.edit.EditTradePageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.trades.OpenShopListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.TradeUtil;

public final class RyuZUInfiniteShop extends JavaPlugin {
    private static RyuZUInfiniteShop plugin;
    public final static String prefixCommand = ChatColor.GOLD + "[RyuZUInfiniteShop]";
    public final static String prefixPersistent = ChatColor.GOLD + "RyuZU.Infinite.Shop.";

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        registerEvents();
        CommandChain.registerCommand();
        new RyuZUCommandsGenerator(this);
        ConfigurationSerialization.registerClass(MythicItem.class);
        MythicListener.reload();
        ShopUtil.removeAllNPC();
        ShopUtil.loadAllShops();
        TradeUtil.loadTradeLimits();
        DisplayConfig.loadDisplay();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        TradeUtil.saveTradeLimits();
        ShopUtil.saveAllShops();
        ShopUtil.removeAllNPC();
        DisplayConfig.saveDisplay();
    }

    public static RyuZUInfiniteShop getPlugin() {
        return plugin;
    }

    public static void registerEvents() {
        getPlugin().getServer().getPluginManager().registerEvents(new EditMainPageListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new EditTradePageListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new OpenShopListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ShopListListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new CancelAffectNpc(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new CancelItemMoveListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeEquipmentListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeDisplayNameListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeNpcTypeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeIndividualSettingsListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeShopTypeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeNpcDirecationListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeLockListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ConvartListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new RemoveShopListener(), getPlugin());
//        try {
//            String listenerspath = RyuZUInfiniteShop.getPlugin().getClass().getResource("listeners").getFile()
//                    .replaceFirst("file:/" , "")
//                    .replace("\\" , "/")
//                    .replace("/RyuZUInfiniteShop-1.0.0.jar!/ryuzuinfiniteshop/ryuzuinfiniteshop/listeners" , "");
//            HashSet<Class<? extends Listener>> classes = Files.walk(Paths.get(listenerspath) , 5)
//                    .filter(path -> path.toString().endsWith(".class"))
//                    .map(path -> {
//                        try {
//                            return Class.forName(path.toString().replace(".class", ""));
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    })
//                    .filter(clazz -> clazz != null && Listener.class.isAssignableFrom(clazz))
//                    .map(clazz -> (Class<? extends Listener>) clazz)
//                    .collect(HashSet::new, HashSet::add, HashSet::addAll);
//            for (Class<?> clazz : classes) {
//                Object o;
//                try {
//                    o = clazz.newInstance();
//                } catch (IllegalAccessException | InstantiationException e) {
//                    return;
//                }
//                if (o instanceof Listener)
//                    getPlugin().getServer().getPluginManager().registerEvents((Listener) o, getPlugin());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
