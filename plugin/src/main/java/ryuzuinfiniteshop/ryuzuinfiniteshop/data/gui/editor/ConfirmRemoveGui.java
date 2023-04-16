package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

//ショップエディターのメインページ
public class ConfirmRemoveGui extends ShopGui {

    public ConfirmRemoveGui(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9, ChatColor.DARK_BLUE + LanguageKey.INVENTORY_SHOP_DELETE.getMessage() + getShop().getDisplayNameOrElseShop());

        inv.setItem(0, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + LanguageKey.ITEM_EDITOR_BUTTON_CANCEL.getMessage()));
        inv.setItem(8, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + LanguageKey.ITEM_EDITOR_BUTTON_DELETE.getMessage()));

        return inv;
    }


}
