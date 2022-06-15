package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ShopHolder implements InventoryHolder {
    public List<String> tags;
    public enum ShopType{Main,twotoone,fourtofour}
    public ShopType type;

    public ShopHolder(ShopType type , String... tags) {
        this.tags = Arrays.asList(tags);
        this.type = type;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
