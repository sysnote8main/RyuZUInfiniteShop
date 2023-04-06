package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Getter
public class ShopListHolder extends PageableHolder {
    protected final LinkedHashMap<String, Shop> shops;

    public ShopListHolder(ShopMode mode, ShopListGui gui, LinkedHashMap<String, Shop> shops) {
        super(mode, gui);
        this.shops = shops;
    }

    public ShopListHolder(ShopMode mode, ShopListGui gui, LinkedHashMap<String, Shop> shops, ModeHolder before) {
        super(mode, gui, before);
        this.shops = shops;
    }

    @Override
    public int getMaxPage() {
        return (int) Math.ceil((double) ShopUtil.getShops().size() / 54);
    }

    @Override
    public ShopListGui getGui() {
        return (ShopListGui) super.getGui();
    }
}
