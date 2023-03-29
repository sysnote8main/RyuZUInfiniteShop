package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.PageableGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.TradesGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

public class ShopListHolder extends PageableHolder {
    public ShopListHolder(ShopMode mode, ShopListGui gui) {
        super(mode, gui);
    }

    public ShopListHolder(ShopMode mode, ShopListGui gui, ModeHolder before) {
        super(mode, gui, before);
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
