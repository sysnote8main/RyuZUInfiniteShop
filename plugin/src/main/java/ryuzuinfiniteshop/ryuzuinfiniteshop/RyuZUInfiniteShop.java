package ryuzuinfiniteshop.ryuzuinfiniteshop;

import com.github.ryuzu.ryuzucommandsgenerator.RyuZUCommandsGenerator;
import de.tr7zw.nbtinjector.NBTInjector;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.command.CommandChain;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.trades.SearchTradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item.MythicItem;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller.CancelAffectNpc;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller.CancelItemMoveListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit.ShopListListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit.EditMainPageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit.EditTradePageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.trades.OpenShopListener;

import java.util.logging.Logger;

public final class RyuZUInfiniteShop extends JavaPlugin {
    @Getter
    private static RyuZUInfiniteShop plugin;
    private static Logger logger;
    public static final String prefixCommand = ChatColor.GOLD + "[SIS]";
    public static final String prefixPersistent = "RyuZU.Infinite.Shop.";
    public static final int VERSION = Integer.parseInt((Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".").substring(3).substring(0, (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".").substring(3).indexOf("_")));

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = getLogger();
        MythicInstanceProvider.setInstance();
        CommandChain.registerCommand();
        registerEvents();
        new RyuZUCommandsGenerator(this);
        ConfigurationSerialization.registerClass(MythicItem.class);
        if(VERSION < 14) NBTInjector.inject();
        FileUtil.loadAll();
    }

    @Override
    public void onDisable() {
        FileUtil.saveAllSync();
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
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeMythicMobTypeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeNpcDirecationListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeLockListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ChangeSearchableListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ConvartListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new RemoveShopListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new ReloadShopListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new SearchTradeListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new TeleportShopListener(), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new SchedulerListener(), getPlugin());
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
