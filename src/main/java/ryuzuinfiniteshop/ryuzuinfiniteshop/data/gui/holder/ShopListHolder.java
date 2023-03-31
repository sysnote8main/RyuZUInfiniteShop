package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.ShopListGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

@Getter
public class ShopListHolder extends PageableHolder {
    protected final String name;

    public ShopListHolder(ShopMode mode, ShopListGui gui, String name) {
        super(mode, gui);
        this.name = name;
    }

    public ShopListHolder(ShopMode mode, ShopListGui gui, String name, ModeHolder before) {
        super(mode, gui, before);
        this.name = name;
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
