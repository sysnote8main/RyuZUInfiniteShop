package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.PersistentUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopSystem implements Listener {
    private static HashMap<String , Shop> shops = new HashMap<>();

    public static Shop getShop(String id) {
        return shops.get(id);
    }

    //ショップで購入する
    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null) return;
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder == null) return;
        ShopHolder shopholder = (ShopHolder) holder;
        if (!shopholder.mode.equals(ShopHolder.ShopMode.Trade)) return;
        Shop shop = ShopSystem.getShop(shopholder.tags.get(0));
        if (shop == null) return;
        ClickType type = event.getClick();

        //取引
        int times = 1;
        switch (type) {
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
                times = 10;
                break;
            case MIDDLE:
                times = 100;
                break;
        }
        shop.getPage(shopholder.page).getTrade(shopholder.page).trade(p, times);
    }
}
