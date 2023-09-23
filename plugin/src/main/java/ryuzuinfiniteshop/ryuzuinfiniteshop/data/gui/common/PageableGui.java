package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import lombok.Getter;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ModeGui;

@Getter
public abstract class PageableGui extends ModeGui {
    protected final int page;

    public PageableGui(int page) {
        this.page = page;
    }
}
