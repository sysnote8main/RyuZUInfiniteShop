package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode
@Getter
public class ShopHolder implements InventoryHolder {

    public enum ShopMode {Edit, Trade}

    private final List<String> tags;
    private final Shop shop;
    private final ShopMode mode;
    private final ShopGui gui;
    @Setter
    private ShopGui before;

    public ShopHolder(ShopMode mode, Shop shop, ShopGui gui, String... tags) {
        this.mode = mode;
        this.shop = shop;
        this.gui = gui;
        this.tags = Arrays.asList(tags);
    }

    public ShopHolder(ShopMode mode, Shop shop, ShopGui gui, ShopGui before, String... tags) {
        this.mode = mode;
        this.shop = shop;
        this.gui = gui;
        this.before = before;
        this.tags = Arrays.asList(tags);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return gui.getInventory(mode);
    }
}
