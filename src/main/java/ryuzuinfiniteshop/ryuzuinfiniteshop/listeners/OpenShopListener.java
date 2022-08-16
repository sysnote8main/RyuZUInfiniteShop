package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners;

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
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;

public class OpenShopListener implements Listener {

    //ショップを開く
    @EventHandler
    public void openShop(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        if (p.isSneaking()) return;
        String id = PersistentUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        Shop shop = TradeListener.getShop(id);
        if (!shop.isAvailableShop(p)) return;

        Inventory inv = shop.getPage(1).getInventory(ShopHolder.ShopMode.Trade);
        p.openInventory(inv);
        setTradeStatus(p, (ShopHolder) inv.getHolder(), shop);
        event.setCancelled(true);
    }

    //ショップのページ切り替え
    @EventHandler
    public void changePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopTradeGui)) return;
        if (!ShopUtil.isTradeMode(event)) return;
        if (event.getClickedInventory() != null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();

        //ページ切り替え
        boolean fail = false;
        if (type.isLeftClick()) {
            if (shop.getPage(shopholder.getPage() - 1) == null)
                fail = true;
            else
                p.openInventory(shop.getPage(shopholder.getPage() - 1).getInventory(ShopHolder.ShopMode.Trade));
        }
        if (type.isRightClick()) {
            if (shop.getPage(shopholder.getPage() + 1) == null)
                fail = true;
            else
                p.openInventory(shop.getPage(shopholder.getPage() + 1).getInventory(ShopHolder.ShopMode.Trade));
        }
        if (fail) {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
            setTradeStatus(p, shopholder, shop);
        }

        //GUI操作処理
        ShopUtil.playClickEffect(event);
    }

    //ショップのステータスの更新
    @EventHandler
    public void updateStatus(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopTradeGui)) return;
        if (!ShopUtil.isTradeMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();

        //ショップのステータスを更新
        setTradeStatus(p, shopholder, shop);

        //イベントをキャンセル
        event.setCancelled(true);
    }

    //それぞれの取引が可能か表示
    public void setTradeStatus(Player p, ShopHolder holder, Shop shop) {
        int page = holder.getPage();
        Inventory inv = p.getOpenInventory().getTopInventory();
        ShopGui gui = ShopUtil.getShopGui(inv);

        ItemStack status1 = ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "購入可能");
        ItemStack status2 = ItemUtil.getNamedItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "アイテムが足りません");
        ItemStack status3 = ItemUtil.getNamedItem(Material.YELLOW_STAINED_GLASS_PANE, ChatColor.YELLOW + "インベントリに十分な空きがありません");

        int addslot = shop.getShopType().equals(Shop.ShopType.TwotoOne) ? 2 : 4;
        for (int i = 0; i < shop.getPage(page).getTrades().size(); i++) {
            int baseslot = shop.getShopType().equals(Shop.ShopType.TwotoOne) ?
                    (i / 2) * 9 + (i % 2 == 1 ? 5 : 0) :
                    i * 9;
            int tradenumber = ((ShopTradeGui) gui).getTradeNumber(baseslot);
            ShopTrade trade = shop.getPage(page).getTrade(tradenumber);
            ShopTrade.Result result = trade.getResult(p);
            switch (result){
                case Lack:
                    inv.setItem(baseslot + addslot, status2);
                    break;
                case Full:
                    inv.setItem(baseslot + addslot, status3);
                    break;
                case Success:
                    inv.setItem(baseslot + addslot, status1);
                    break;
            }
        }
    }
}
