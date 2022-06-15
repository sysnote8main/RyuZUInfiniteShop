package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;

public class ShopTrade {
    public ItemStack[] give = new ItemStack[4];
    public ItemStack[] take = new ItemStack[4];

    public ConfigurationSection getConfig() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("give", give);
        config.set("take", take);

        return config;
    }
}
