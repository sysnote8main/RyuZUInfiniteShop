package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ModeGui;

@EqualsAndHashCode
@Getter
public class ModeHolder implements InventoryHolder {
    protected final ShopMode mode;
    protected final ModeGui gui;
    @Setter
    protected ModeHolder before;

    public ModeHolder(ShopMode mode, ModeGui gui) {
        this.mode = mode;
        this.gui = gui;
    }

    public ModeHolder(ShopMode mode, ModeGui gui, ModeHolder before) {
        this.mode = mode;
        this.gui = gui;
        this.before = before;
    }

    @Override
    public Inventory getInventory() {
        return gui.getInventory(mode);
    }
}
