package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

import java.util.Arrays;
import java.util.List;

public class ShopHolder implements InventoryHolder {

    public enum ShopMode {Edit, Trade}

    private final List<String> tags;

    private final Shop shop;
    private final ShopMode mode;
    private final ShopGui gui;

    public ShopHolder(ShopMode mode, Shop shop, ShopGui gui, String... tags) {
        this.mode = mode;
        this.shop = shop;
        this.gui = gui;
        this.tags = Arrays.asList(tags);
    }

    public List<String> getTags() {
        return tags;
    }

    public ShopGui getGui() {
        return gui;
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
