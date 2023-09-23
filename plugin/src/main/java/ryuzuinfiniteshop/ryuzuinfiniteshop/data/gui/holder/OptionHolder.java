package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.EditOptionGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;

@EqualsAndHashCode(callSuper = true)
@Getter
public class OptionHolder extends ShopHolder {

    public OptionHolder(ShopMode mode, Shop shop, EditOptionGui gui) {
        this(mode, shop, gui, null);
    }

    public OptionHolder(ShopMode mode, Shop shop, EditOptionGui gui, ModeHolder before) {
        super(mode, shop, gui, before);
    }

    @Override
    public EditOptionGui getGui() {
        return (EditOptionGui) gui;
    }
}
