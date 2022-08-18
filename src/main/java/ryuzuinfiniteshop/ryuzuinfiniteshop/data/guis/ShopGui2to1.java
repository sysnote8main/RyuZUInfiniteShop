package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

import java.util.ArrayList;
import java.util.List;

public class ShopGui2to1 extends ShopTradeGui {

    private static final List<Integer> displayslot = new ArrayList<>();

    static {
        for (int i = 0; i < 6; i++) {
            displayslot.add(i * 9 + 2);
            displayslot.add(i * 9 + 4);
            displayslot.add(i * 9 + 7);
        }
    }

    public ShopGui2to1(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), getPage(), ShopGui.class.getName()), 9 * 6, "ショップ ページ" + getPage());

        ItemStack filler = ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "");

        for (int i = 0; i < 6; i++) {
            inv.setItem(i * 9 + 2, filler);
            inv.setItem(i * 9 + 4, filler);
            inv.setItem(i * 9 + 7, filler);
        }

        for (int i = 0; i < getTrades().size(); i++) {
            ShopTrade trade = getTrades().get(i);
            int slot = (i / 2) * 9 + (i % 2 == 1 ? 5 : 0);
            for (int k = 0; k < trade.take.length; k++) {
                inv.setItem(slot + k, trade.take[k]);
            }
            inv.setItem(slot + 3, trade.give[0]);
        }

        return inv;
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode, Player p) {
        Inventory inv = getInventory(mode);
        if (mode.equals(ShopHolder.ShopMode.Trade)) setTradeStatus(p, inv);
        return inv;
    }

    @Override
    public ShopTrade getTradeFromSlot(int slot) {
        int mod9 = slot % 9;
        if (mod9 == 4) return null;
        int quootient9 = slot / 9;
        int front = mod9 < 4 ? 0 : 1;
        return getTrade(quootient9 * 2 + front + 1);
    }

    @Override
    public boolean isDisplayItem(int slot) {
        return displayslot.contains(slot);
    }
}
