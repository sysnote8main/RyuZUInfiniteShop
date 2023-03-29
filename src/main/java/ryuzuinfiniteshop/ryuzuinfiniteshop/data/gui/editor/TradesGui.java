package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor;

import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.PageableGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class TradesGui extends PageableGui {

    protected List<ShopTrade> trades = new ArrayList<>();

    public TradesGui(int page) {
        super(page);
    }
}
