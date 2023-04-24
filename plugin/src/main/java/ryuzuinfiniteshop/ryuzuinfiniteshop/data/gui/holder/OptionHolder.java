package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.EditOptionGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.TradeOption;

@EqualsAndHashCode(callSuper = true)
@Getter
public class OptionHolder extends ShopHolder {
    private final TradeOption data;

    public OptionHolder(ShopMode mode, Shop shop, TradeOption data, EditOptionGui gui) {
        this(mode, shop, data, gui, null);
    }

    public OptionHolder(ShopMode mode, Shop shop, TradeOption data, EditOptionGui gui, ModeHolder before) {
        super(mode, shop, gui, before);
        this.data = data;
    }

    @Override
    public Inventory getInventory() {
        return gui.getInventory(mode);
    }

    @Override
    public EditOptionGui getGui() {
        return (EditOptionGui) gui;
    }

    @Override
    public int getMaxPage() {
        return shop.getPageCount();
    }
}
