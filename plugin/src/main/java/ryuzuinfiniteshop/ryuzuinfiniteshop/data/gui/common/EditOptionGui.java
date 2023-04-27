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
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.ShopType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.OptionType;
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
    protected final int slot;
    protected final Inventory inventory;

    public EditOptionGui(ShopTrade trade, Shop shop, int page, int slot, Inventory inventory) {
        super(shop, page);
        this.trade = trade;
        this.option = trade.getOption();
        this.slot = slot;
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new OptionHolder(ShopMode.EDIT, shop, this), 9 * (VaultHandler.isLoaded() ? 4 : 3), ChatColor.DARK_BLUE + "取引オプションの編集");

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, trade.getTradeItems(shop.getShopType())[i]);
        }
        inv.setItem(shop.getShopType().equals(ShopType.SixtoTwo) ? 6 : 4, ItemUtil.getNamedItem(DisplayPanelConfig.getPanel(ShopTrade.TradeResult.Normal).getItemStack(), ChatColor.BLACK + ""));

        //取引上限
        List<String> limits = Arrays.asList("-64", "-32", "-8", "-1", "0", "+1", "+8", "+32", "+64");
        for (int i = 0; i < 9; i++) {
            ItemStack panel = ItemUtil.getNamedItem(ItemUtil.getColoredItem((i < 5 ? "RED" : "GREEN") + "_STAINED_GLASS_PANE"), (i < 5 ? ChatColor.RED : ChatColor.GREEN) + limits.get(i));
            panel = NBTUtil.setNMSTag(panel, "OptionType", "Limit");
            panel = NBTUtil.setNMSTag(panel, "OptionValue", limits.get(i).replace("+", ""));
            inv.setItem(i + 9, panel);
        }
        inv.setItem(13, NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.BARRIER, ChatColor.BLUE + "取引上限: " + ChatColor.YELLOW + option.getLimit(), ChatColor.YELLOW + "シフトクリック: チャットで値を入力"), "OptionType", "Limit"));

        //取引確率
        List<String> rates = Arrays.asList("-100", "-30", "-10", "-1", "0", "+1", "+30", "+50", "+100");
        for (int i = 0; i < 9; i++) {
            ItemStack panel = ItemUtil.getNamedItem(ItemUtil.getColoredItem((i < 5 ? "RED" : "GREEN") + "_STAINED_GLASS_PANE"), (i < 5 ? ChatColor.RED : ChatColor.GREEN) + rates.get(i));
            panel = NBTUtil.setNMSTag(panel, "OptionType", "Rate");
            panel = NBTUtil.setNMSTag(panel, "OptionValue", rates.get(i).replace("+", ""));
            inv.setItem(i + 9 * 2, panel);
        }
        inv.setItem(22, NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.DAMAGED_ANVIL, ChatColor.BLUE + "取引成功確率: " + ChatColor.YELLOW + option.getRate() + "%" + " " + (option.isHide() ? ChatColor.RED + "確率を隠す" : ChatColor.GREEN + "確率を表示する"), ChatColor.YELLOW + "クリック: 切り替え", ChatColor.YELLOW + "シフトクリック: チャットで値を入力") , "OptionType", "Rate"));

        //お金
        if (VaultHandler.isLoaded()) {
            List<String> money = Arrays.asList("-1,000,000", "-10,000", "-100", "-1", "0", "+1", "+100", "+10,000", "+1,000,000");
            for (int i = 0; i < 9; i++) {
                ItemStack panel = ItemUtil.getNamedItem(ItemUtil.getColoredItem((i < 5 ? "RED" : "GREEN") + "_STAINED_GLASS_PANE"), (i < 5 ? ChatColor.RED : ChatColor.GREEN) + money.get(i));
                panel = NBTUtil.setNMSTag(panel, "OptionType", "Money");
                panel = NBTUtil.setNMSTag(panel, "OptionValue", money.get(i).replace("+", "").replace(",", ""));
                inv.setItem(i + 9 * 3, panel);
            }
            inv.setItem(31, NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.GOLD_INGOT, ChatColor.BLUE + "お金: " + ChatColor.YELLOW + VaultHandler.getInstance().format(option.getMoney()) + " " + (option.isGive() ? ChatColor.GREEN + "受け取り" : ChatColor.RED + "支払い"), ChatColor.YELLOW + "クリック: 切り替え", ChatColor.YELLOW + "シフトクリック: チャットで値を入力"), "OptionType", "Money"));
        }

        return inv;
    }

    public ItemStack getOptionPanel(OptionType type) {
        switch (type) {
            case LIMIT:
                return NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.BARRIER, ChatColor.BLUE + "取引上限: " + ChatColor.YELLOW + option.getLimit(), ChatColor.YELLOW + "シフトクリック: チャットで値を入力"), "OptionType", "Limit");
            case RATE:
                return NBTUtil.setNMSTag(ItemUtil.getNamedItem(Material.DAMAGED_ANVIL, ChatColor.BLUE + "取引成功確率: " + ChatColor.YELLOW + option.getRate() + "% " + (option.isHide() ? ChatColor.RED + "確率を隠す" : ChatColor.GREEN + "確率を表示する"), ChatColor.YELLOW + "クリック: 切り替え", ChatColor.YELLOW + "シフトクリック: チャットで値を入力") , "OptionType", "Rate");
            case MONEY:
                return NBTUtil.setNMSTag(
                        ItemUtil.getNamedItem(Material.GOLD_INGOT, ChatColor.BLUE + "お金: " + ChatColor.YELLOW + VaultHandler.getInstance().format(option.getMoney()) + " " + (option.isGive() ? ChatColor.GREEN + "受け取り" : ChatColor.RED + "支払い"), ChatColor.YELLOW + "クリック: 切り替え", ChatColor.YELLOW + "シフトクリック: チャットで値を入力"), "OptionType", "Money");
        }
        return new ItemStack(Material.AIR);
    }
}
