package ryuzuinfiniteshop.ryuzuinfiniteshop;

import com.github.ryuzu.ryuzucommandsgenerator.RyuZUCommandsGenerator;
import de.tr7zw.nbtinjector.NBTInjector;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ryuzuinfiniteshop.ryuzuinfiniteshop.command.CommandChain;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.player.SearchTradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.MythicInstanceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item.MythicItem;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller.CancelAffectNpc;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.canceller.CancelItemMoveListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.player.ShopListListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit.EditMainPageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit.EditTradePageListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.player.OpenShopListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
        registerAllListeners();
        LanguageConfig.load();
        RyuZUCommandsGenerator.initialize(this, LanguageKey.COMMAND_ERROR_PERMISSION.getMessage());
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
    }

    public static void registerAllListeners() {
        Plugin plugin = RyuZUInfiniteShop.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        URL listenersUrl = plugin.getClass().getResource("listeners");

        try {
            URI listenersUri = listenersUrl.toURI();
            Path listenersPath = Paths.get(listenersUri);

            try (Stream<Path> paths = Files.walk(listenersPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".class"))
                        .forEach(classFile -> {
                            String className = extractClassName(listenersPath, classFile);
                            try {
                                Class<?> clazz = Class.forName(className);
                                if (Listener.class.isAssignableFrom(clazz)) {
                                    Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                                    pluginManager.registerEvents(listener, plugin);
                                }
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static String extractClassName(Path basePath, Path classFile) {
        String packageName = RyuZUInfiniteShop.getPlugin().getClass().getPackage().getName();
        String relativePath = basePath.relativize(classFile).toString();
        String className = relativePath.replace(File.separator, ".");
        className = className.substring(0, className.length() - ".class".length());
        return packageName + ".listeners." + className;
    }
}
