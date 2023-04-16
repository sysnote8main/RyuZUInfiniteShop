package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import org.bukkit.Bukkit;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit.MythicListener;

public class MythicInstanceProvider {
    private static MythicListener instance;
    public static MythicListener getInstance() {
        if(instance == null) throw new NullPointerException(LanguageKey.ERROR_MYTHICMOBS_INVALID_LOADED.getMessage());
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
