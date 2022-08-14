package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ShopHolder implements InventoryHolder {

    public enum ShopMode {Edit, Trade}

    public List<String> tags;

    public int page = 1;
    public ShopMode mode;

    public ShopHolder(ShopMode mode, String... tags) {
        this.tags = Arrays.asList(tags);
        this.mode = mode;
    }

    public ShopHolder(ShopMode mode, int page, String... tags) {
        this.tags = Arrays.asList(tags);
        this.mode = mode;
        this.page = page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
