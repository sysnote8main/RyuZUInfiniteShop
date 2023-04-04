package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

//ショップのNPCの装備を変更する
public class TeleportShopListener implements Listener {
    @EventHandler
    public void teleportShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (slot != 2 * 9 + 8) return;

        Bukkit.getScheduler().runTaskLater(RyuZUInfiniteShop.getPlugin(), () -> ShopUtil.forceClose(p), 1L);
        p.teleport(holder.getShop().getLocation());
        SoundUtil.playSuccessSound(p);
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + holder.getShop().getDisplayNameOrElseShop() + ChatColor.GREEN + "にテレポートしました");
    }
}
