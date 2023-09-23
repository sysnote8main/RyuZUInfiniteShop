package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.PageableGui;

public abstract class PageableHolder extends ModeHolder {
    public PageableHolder(ShopMode mode, PageableGui gui) {
        super(mode, gui);
    }

    public PageableHolder(ShopMode mode, PageableGui gui, ModeHolder before) {
        super(mode, gui, before);
    }

    public abstract int getMaxPage();

    @Override
    public PageableGui getGui() {
        return (PageableGui) super.getGui();
    }
}
