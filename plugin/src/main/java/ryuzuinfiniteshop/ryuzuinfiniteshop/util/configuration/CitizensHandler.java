package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;

import java.util.UUID;

public class CitizensHandler {
    @Getter
    private static boolean loaded = false;

    public static void setInstance() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Citizens") == null) return;
        loaded = true;
    }
}
