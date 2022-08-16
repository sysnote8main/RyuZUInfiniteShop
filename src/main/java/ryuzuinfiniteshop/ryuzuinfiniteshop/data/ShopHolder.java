package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ShopHolder implements InventoryHolder {

    public enum ShopMode {Edit, Trade}

    private final List<String> tags;

    private final int page;
    private final Shop shop;
    private final ShopMode mode;

    public ShopHolder(ShopMode mode, Shop shop, String... tags) {
        this.mode = mode;
        this.shop = shop;
        this.page = 1;
        this.tags = Arrays.asList(tags);
    }

    public ShopHolder(ShopMode mode, Shop shop, int page, String... tags) {
        this.mode = mode;
        this.shop = shop;
        this.page = page;
        this.tags = Arrays.asList(tags);
    }

    public List<String> getTags() {
        return tags;
    }

    public int getPage() {
        return page;
    }

    public Shop getShop() {
        return shop;
    }

    public ShopMode getShopMode() {
        return mode;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
