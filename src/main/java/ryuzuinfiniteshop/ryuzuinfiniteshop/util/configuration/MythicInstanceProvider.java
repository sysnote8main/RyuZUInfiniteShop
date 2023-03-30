package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import org.bukkit.Bukkit;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.admin.MythicListener;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class MythicInstanceProvider {
    private static final String errorMessage = "MythicMobs is not loaded. If you want to use MythicMobs, please install MythicMobs.";
    private static MythicListener instance;
    public static MythicListener getInstance() {
        if(instance == null) throw new NullPointerException(errorMessage);
        return instance;
    }

    public static boolean isLoaded() {
        return instance != null;
    }

    public static void setInstance() {
        if(Bukkit.getServer().getPluginManager().getPlugin("MythicMobs") != null)
            instance = new MythicListener();
    }
}
