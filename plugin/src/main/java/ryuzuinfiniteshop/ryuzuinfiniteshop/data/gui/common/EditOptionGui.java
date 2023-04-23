package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.OptionalDataHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.TradeOption;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.VaultHandler;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

public class EditOptionGui extends ShopGui {
    protected final TradeOption data;

    public EditOptionGui(TradeOption data, Shop shop, int page) {
        super(shop, page);
        this.data = data;
    }

    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new OptionalDataHolder(ShopMode.EDIT, shop, data, this), 9 * (VaultHandler.isLoaded() ? 4 : 3), ChatColor.DARK_BLUE + "取引オプションの編集");

        //取引上限
        inv.setItem(9, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-64"));
        inv.setItem(10, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-32"));
        inv.setItem(11, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-8"));
        inv.setItem(12, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-1"));
        inv.setItem(13, ItemUtil.getNamedItem(Material.BARRIER, ChatColor.BLUE + "取引上限", ChatColor.YELLOW + "シフトクリック: チャットで値を入力"));
        inv.setItem(14, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+1"));
        inv.setItem(15, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+8"));
        inv.setItem(16, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+32"));
        inv.setItem(17, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+64"));

        //取引確率
        inv.setItem(18, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-100"));
        inv.setItem(19, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-30"));
        inv.setItem(20, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-10"));
        inv.setItem(21, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-1"));
        inv.setItem(22, ItemUtil.getNamedItem(Material.DAMAGED_ANVIL, ChatColor.BLUE + "取引成功確率: " + ChatColor.YELLOW + data.getLimit(), ChatColor.YELLOW + "シフトクリック: チャットで値を入力"));
        inv.setItem(23, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+1"));
        inv.setItem(24, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+30"));
        inv.setItem(25, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+50"));
        inv.setItem(26, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+100"));

        //お金
        if (VaultHandler.isLoaded()) {
            inv.setItem(27, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-1,000,000", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(28, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-10,000", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(29, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-100", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(30, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), ChatColor.RED + "-1", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(31, ItemUtil.getNamedItem(Material.DAMAGED_ANVIL, ChatColor.BLUE + "お金: " + ChatColor.YELLOW + data.getValue(), ChatColor.YELLOW + "クリック: " + (data.isGive() ? "与えるお金" : "取るお金"), ChatColor.YELLOW + "シフトクリック: チャットで値を入力"));
            inv.setItem(32, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+1", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(33, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+100", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(34, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+10,000", ChatColor.RED + "シフトクリック: 10倍"));
            inv.setItem(35, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), ChatColor.GREEN + "+1,000,000", ChatColor.RED + "シフトクリック: 10倍"));
        }

        return inv;
    }
}
