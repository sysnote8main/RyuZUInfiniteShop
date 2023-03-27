package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.change;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;

import java.util.HashMap;
import java.util.UUID;

//ショップのNPCの名前を変更する
public class ChangeNpcTypeListener implements Listener {
    private static HashMap<UUID, Long> changingTime = new HashMap<>();
    private static HashMap<UUID, String> changingShop = new HashMap<>();

    @EventHandler
    public void changeEntityType(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 4) return;

        //チャット入力待機
        changingTime.put(p.getUniqueId(), System.currentTimeMillis());
        changingShop.put(p.getUniqueId(), shop.getID());
        p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "エンティティIDを入力してください");
        p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "20秒待つか'Cancel'と入力することでキャンセルことができます");

        //音を出す
        SoundUtil.playClickShopSound(p);

        //インベントリを閉じる
        p.closeInventory();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void changeEntityType(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (!changingTime.containsKey(p.getUniqueId())) return;
        if ((double) (System.currentTimeMillis() - changingTime.get(p.getUniqueId())) / 1000 > 20) return;
        if (event.getMessage().equalsIgnoreCase("Cancel")) {
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "エンティティタイプ変更をキャンセルしました");
            SoundUtil.playClickShopSound(p);
        } else {
            //同期させてNPCを再構築する
            Shop shop = ShopUtil.getShop(changingShop.get(p.getUniqueId()));
            Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), () -> {
                p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "エンティティタイプを変更しました");
                SoundUtil.playSuccessSound(p);
                try {
                    ShopUtil.createShop(shop.getLocation(), shop.convertShopToString(), EntityType.valueOf(event.getMessage().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.RED + "有効なエンティティタイプを入力して下さい");
                }
            }, 1L);
        }
        changingTime.remove(p.getUniqueId());
        changingShop.remove(p.getUniqueId());
        event.setCancelled(true);
    }
}
