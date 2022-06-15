package ryuzuinfiniteshop.ryuzuinfiniteshop.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.ShopTrade;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopGui {
    public List<ShopTrade> trades = new ArrayList<>();

    public abstract Inventory getInventory();

}
