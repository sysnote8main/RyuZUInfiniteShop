package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopGui;
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
        return gui.getInventory(mode);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if(!(obj instanceof ShopHolder)) return false;
        ShopHolder holder = (ShopHolder) obj;
        if(!holder.getGui().equals(gui)) return false;
        if(!holder.getShopMode().equals(mode)) return false;
        if(!holder.getShop().equals(shop)) return false;
        if(!holder.getTags().equals(tags)) return false;
        return true;
    }
}
