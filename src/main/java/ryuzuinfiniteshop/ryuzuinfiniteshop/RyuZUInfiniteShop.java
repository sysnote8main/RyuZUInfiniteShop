package ryuzuinfiniteshop.ryuzuinfiniteshop;

import com.github.ryuzu.ryuzucommandsgenerator.RyuZUCommandsGenerator;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.commands.ListCommand;
import ryuzuinfiniteshop.ryuzuinfiniteshop.commands.SpawnCommand;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.DisplayConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin.MythicListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.canceller.CancelAffectNpc;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.canceller.CancelItemMoveListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin.OpenShopListListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.change.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.convert.ConvartListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.delete.RemoveShopListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.edit.EditMainPageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.edit.EditTradePageListener;
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
        DisplayConfig.loadDisplay();
        MythicListener.reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
        getPlugin().getServer().getPluginManager().registerEvents(new OpenShopListListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new TradeListener(), getPlugin());
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
//            System.out.println(listenerspath);
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

    public static void registerCommands() {
        SpawnCommand.registerCommand();
        ListCommand.registerCommand();
    }
}
