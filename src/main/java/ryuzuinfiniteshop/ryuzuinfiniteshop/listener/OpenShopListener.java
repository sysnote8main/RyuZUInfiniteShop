package ryuzuinfiniteshop.ryuzuinfiniteshop.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ShopUtil;

public class OpenShopListener implements Listener {

    //ショップを開く
    @EventHandler
    public void openShop(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        String id = PersistentUtil.getNMSStringTag(entity , "Shop");
        if(id == null) return;
        Shop shop = TradeListener.getShop(id);
        p.openInventory(shop.getPage(1).getInventory(ShopHolder.ShopMode.Trade));
        event.setCancelled(true);
    }

    //ショップのページ切り替え
    @EventHandler
    public void changePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        if (event.getClickedInventory() != null) return;
        if(!ShopUtil.isShopTradeInventory(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = TradeListener.getShop(shopholder.tags.get(0));

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (shop.getPage(shopholder.page - 1) == null) {
                fail = true;
            } else {
                p.openInventory(shop.getPage(shopholder.page - 1).getInventory(ShopHolder.ShopMode.Trade));
            }
        }
        if (type.isRightClick()) {
            if (shop.getPage(shopholder.page + 1) == null) {
                fail = true;
            } else {
                p.openInventory(shop.getPage(shopholder.page + 1).getInventory(ShopHolder.ShopMode.Trade));
            }
        }
        if (fail) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 2);
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
            setTradeStatus(p , shopholder , shop);
        }
        event.setCancelled(true);
    }

    //ショップのステータスの更新
    @EventHandler
    public void updateStatus(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        if (event.getClickedInventory() != null) return;
        if(!ShopUtil.isShopTradeInventory(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = TradeListener.getShop(shopholder.tags.get(0));

        setTradeStatus(p , shopholder , shop);
        event.setCancelled(true);
    }

    //それぞれの取引が可能か表示
    public void setTradeStatus(Player p, ShopHolder holder, Shop shop) {
        int page = holder.page;
        Inventory inv = p.getOpenInventory().getTopInventory();

        ItemStack status1 = ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "購入可能");
        ItemStack status2 = ItemUtil.getNamedItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "購入不可");
        ItemStack status3 = ItemUtil.getNamedItem(Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "インベントリに十分な空きがありません");

        int addslot = shop.getShopType().equals(Shop.ShopType.TwotoOne) ? 2 : 4;
        for (int i = 0; i < shop.getPage(page).getTrades().size(); i++) {
            int baseslot = shop.getShopType().equals(Shop.ShopType.TwotoOne) ?
                    (i / 2) * 9 + (i % 2 == 1 ? 5 : 0) :
                    i * 9;
            ShopTrade trade = shop.getPage(page).getTrade(baseslot);

            inv.setItem(baseslot + addslot, status1);
            if (!trade.hasEnoughSpace(p)) inv.setItem(baseslot + addslot, status3);
            if (!trade.affordTrade(p)) inv.setItem(baseslot + addslot, status2);
        }
    }
}
