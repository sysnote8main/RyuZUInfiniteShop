package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.change;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

import java.util.HashMap;
import java.util.UUID;

//ショップのNPCの名前を変更する
public class ChangeDisplayNameListener implements Listener {
    private static HashMap<UUID, Long> namingTime = new HashMap<>();
    private static HashMap<UUID, String> namingShop = new HashMap<>();

    @EventHandler
    public void changeDisplay(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if(!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 8) return;

        //チャット入力待機
        namingTime.put(p.getUniqueId(), System.currentTimeMillis());
        namingShop.put(p.getUniqueId(), shop.getID());
        p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "ページに設定する名前をチャットに入力してください");
        p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "20秒待つか'Cancel'と入力することでキャンセルことができます");
        p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "カラーコードを使う際は'&'か'{color:R,G,B}'を使用してください");

        //音を出す
        SoundUtil.playClickShopSound(p);

        //インベントリを閉じる
        p.closeInventory();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void changeDisplay(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (!namingTime.containsKey(p.getUniqueId())) return;
        if ((double) (System.currentTimeMillis() - namingTime.get(p.getUniqueId())) / 1000 > 20) return;
        if (event.getMessage().equalsIgnoreCase("Cancel"))  {
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "名前変更をキャンセルしました");
            SoundUtil.playClickShopSound(p);
        } else {
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.GREEN + "名前が設定されました");
            SoundUtil.playSuccessSound(p);
            Shop shop = ShopUtil.getShop(namingShop.get(p.getUniqueId()));
            shop.getNPC().setCustomName(event.getMessage());
        }
        namingTime.remove(p.getUniqueId());
        namingShop.remove(p.getUniqueId());
        event.setCancelled(true);
    }
}
