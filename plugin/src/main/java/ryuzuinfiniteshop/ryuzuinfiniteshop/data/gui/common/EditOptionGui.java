package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.DisplayPanelConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.OptionHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.TradeOption;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.VaultHandler;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;

import java.util.*;

@Getter
public class EditOptionGui extends ShopGui {
    protected final ShopTrade trade;
    protected final TradeOption option;

    public EditOptionGui(ShopTrade trade, TradeOption option, Shop shop, int page) {
        super(shop, page);
        this.trade = trade;
        this.option = option;
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new OptionHolder(ShopMode.EDIT, shop, option, this), 9 * (VaultHandler.isLoaded() ? 4 : 3), ChatColor.DARK_BLUE + "取引オプションの編集");

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, trade.getTradeItems(shop.getShopType(), ShopMode.EDIT)[i]);
        }
        inv.setItem(5, ItemUtil.getNamedItem(DisplayPanelConfig.getPanel(ShopTrade.TradeResult.Normal).getItemStack(), ChatColor.BLACK + ""));

        //取引上限
        List<String> limits = Arrays.asList("-64", "-32", "-8", "-1", "0", "+1", "+8", "+32", "+64");
        for (int i = 0; i < 9; i++) {
            ItemStack panel = ItemUtil.getNamedItem(ItemUtil.getColoredItem((i < 5 ? "RED" : "GREEN") + "_STAINED_GLASS_PANE"), (i < 5 ? ChatColor.RED : ChatColor.GREEN) + limits.get(i));
            panel = NBTUtil.setNMSTag(panel, "OptionType", "Limit");
            panel = NBTUtil.setNMSTag(panel, "OptionValue", limits.get(i).replace("+", ""));
            inv.setItem(i + 9, panel);
        }
        inv.setItem(13, NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.BARRIER, ChatColor.BLUE + "取引上限", ChatColor.YELLOW + "シフトクリック: チャットで値を入力"), "OptionType", "Limit"));

        //取引確率
        List<String> rates = Arrays.asList("-100", "-30", "-10", "-1", "0", "+1", "+30", "+50", "+100");
        for (int i = 0; i < 9; i++) {
            ItemStack panel = ItemUtil.getNamedItem(ItemUtil.getColoredItem((i < 5 ? "RED" : "GREEN") + "_STAINED_GLASS_PANE"), (i < 5 ? ChatColor.RED : ChatColor.GREEN) + rates.get(i));
            panel = NBTUtil.setNMSTag(panel, "OptionType", "Rate");
            panel = NBTUtil.setNMSTag(panel, "OptionValue", rates.get(i).replace("+", ""));
            inv.setItem(i + 9 * 2, panel);
        }
        inv.setItem(22, NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.DAMAGED_ANVIL, ChatColor.BLUE + "取引成功確率: " + ChatColor.YELLOW + option.getRate(), ChatColor.YELLOW + "シフトクリック: チャットで値を入力") , "OptionType", "Rate"));

        //お金
        if (VaultHandler.isLoaded()) {
            List<String> money = Arrays.asList("-1,000,000", "-10,000", "-100", "-1", "0", "+1", "+100", "+10,000", "+1,000,000");
            for (int i = 0; i < 9; i++) {
                ItemStack panel = ItemUtil.getNamedItem(ItemUtil.getColoredItem((i < 5 ? "RED" : "GREEN") + "_STAINED_GLASS_PANE"), (i < 5 ? ChatColor.RED : ChatColor.GREEN) + money.get(i));
                panel = NBTUtil.setNMSTag(panel, "OptionType", "Money");
                panel = NBTUtil.setNMSTag(panel, "OptionValue", money.get(i).replace("+", ""));
                inv.setItem(i + 9 * 3, panel);
            }
            inv.setItem(31, NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.GOLD_INGOT, ChatColor.BLUE + "お金: " + ChatColor.YELLOW + option.getMoney(), ChatColor.YELLOW + "クリック: " + (option.isGive() ? "与えるお金" : "取るお金"), ChatColor.YELLOW + "シフトクリック: チャットで値を入力"), "OptionType", "Money"));
        }

        return inv;
    }
}
