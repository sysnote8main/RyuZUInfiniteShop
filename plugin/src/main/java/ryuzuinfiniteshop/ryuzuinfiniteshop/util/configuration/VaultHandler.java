package ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;

import java.util.UUID;

public class VaultHandler {
    private static Economy economy;

    public static Economy getInstance() {
        if (economy == null || !economy.isEnabled()) setInstance();
        if (economy == null) throw new NullPointerException(LanguageKey.ERROR_INVALID_LOADED_VAULT.getMessage());

        return economy;
    }

    public static double getMoney(UUID p) {
        return economy.getBalance(Bukkit.getOfflinePlayer(p));
    }

    public static void takeMoney(UUID p, double amount) {
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(p), amount);
    }

    public static void giveMoney(UUID p, double amount) {
        economy.depositPlayer(Bukkit.getOfflinePlayer(p), amount);
    }

    public static boolean hasMoney(UUID p, double amount) {
        return economy.has(Bukkit.getOfflinePlayer(p), amount);
    }

    public static boolean isLoaded() {
        return economy != null;
    }

    public static void setInstance() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> rsp = RyuZUInfiniteShop.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;
        economy = rsp.getProvider();
    }
}
