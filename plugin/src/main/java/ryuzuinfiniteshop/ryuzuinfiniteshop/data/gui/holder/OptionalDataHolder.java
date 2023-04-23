package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.TradeOption;

@EqualsAndHashCode(callSuper = true)
@Getter
public class OptionalDataHolder extends ShopHolder {
    private final TradeOption data;

    public OptionalDataHolder(ShopMode mode, Shop shop, TradeOption data, ShopGui gui) {
        this(mode, shop, data, gui, null);
    }

    public OptionalDataHolder(ShopMode mode, Shop shop, TradeOption data, ShopGui gui, ModeHolder before) {
        super(mode, shop, gui, before);
        this.data = data;
    }

    @Override
    public Inventory getInventory() {
        return gui.getInventory(mode);
    }

    @Override
    public ShopGui getGui() {
        return (ShopGui) gui;
    }

    @Override
    public int getMaxPage() {
        return shop.getPageCount();
    }
}
