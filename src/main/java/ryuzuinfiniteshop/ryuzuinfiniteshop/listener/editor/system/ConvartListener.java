package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LogUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.util.HashMap;
import java.util.Map;

//ショップのNPCの装備を変更する
public class ConvartListener implements Listener {
    @EventHandler
    public void convert(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.Edit)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 6 && slot != 5 * 9 + 7) return;

        //コンバート
        ItemStack item = slot == 5 * 9 + 6 ? shop.convertTradesToItemStack() : shop.convertShopToItemStack();
        if (ItemUtil.ableGive(p.getInventory(), item)) {
            p.getInventory().addItem(item);
            SoundUtil.playSuccessSound(p);
        } else
            SoundUtil.playFailSound(p);
    }

    //トレードを読み込む
    @EventHandler
    public void loadTrades(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.Edit)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Shop shop = holder.getShop();
        ItemStack item = event.getCursor();
        String tag = NBTUtil.getNMSStringTag(item, "ShopType");
        int slot = event.getSlot();

        if (slot != 5 * 9 + 8) return;
        if (tag == null) {
            SoundUtil.playFailSound(p);
            return;
        }
        if (!(shop.getShopType().equals(Shop.ShopType.TwotoOne) || Shop.ShopType.valueOf(tag).equals(shop.getShopType()))) {
            SoundUtil.playFailSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "ショップタイプが違います");
            return;
        }

        //トレードを読み込む
        if ((type.isRightClick() || type.isLeftClick()) && !type.isShiftClick()) {
            boolean duplication = shop.loadTrades(item, p);

            //音を出す
            if(duplication) {
                SoundUtil.playCautionSound(p);
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "重複している取引がありました");
            } else
                SoundUtil.playSuccessSound(p);

            //インベントリを更新する
            holder.getGui().reloadInventory(event.getClickedInventory());
        }
    }

    //ショップを読み込む
    @EventHandler
    public void loadShop(PlayerInteractEvent event) {
        //ショップ召喚用アイテムなのかチェック
        if (event.getHand() == null) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        //必要なデータを取得
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if (!p.hasPermission("sis.op")) return;
        if (!p.isSneaking()) return;
        String shopData = NBTUtil.getNMSStringTag(item, "ShopData");
        if (ItemUtil.isAir(item) || shopData == null) return;
        if (block == null) return;
        if(FileUtil.isSaveBlock(p)) return;

        //ショップを読み込む
        Shop shop = ShopUtil.reloadShop(block.getLocation().add(0, 1, 0), shopData, TradeUtil.convertTradesToList(item));
        shop.respawnNPC();

        //音を出し、メッセージを送信
        SoundUtil.playSuccessSound(p);
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "圧縮宝石を元に" + shop.getDisplayNameOrElseShop() + "を作成しました");
        LogUtil.log(LogUtil.LogType.CREATESHOP, p.getName(), shop.getID());
    }

    //ショップをマージする
    @EventHandler
    public void mergeShop(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player p = event.getPlayer();
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!p.hasPermission("sis.op")) return;
        if (p.isSneaking()) return;
        String id = NBTUtil.getNMSStringTag(entity, "Shop");
        if (id == null) return;
        Shop shop = ShopUtil.getShop(id);
        if (shop.isEditting()) return;
        if(FileUtil.isSaveBlock(p)) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        String shopData = NBTUtil.getNMSStringTag(item, "ShopData");
        if (ItemUtil.isAir(item) || shopData == null) return;

        String tag = NBTUtil.getNMSStringTag(item, "ShopType");
        if (!(shop.getShopType().equals(Shop.ShopType.TwotoOne) || Shop.ShopType.valueOf(tag).equals(shop.getShopType()))) {
            SoundUtil.playFailSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "ショップタイプが違います");
            return;
        }

        HashMap<String, String> data = ShopUtil.mergeShop(item, shop, p);
        String displayName = shop.getDisplayNameOrElseShop();
        item = NBTUtil.setNMSTag(item, data);
        p.getInventory().setItemInMainHand(item);
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + displayName + ChatColor.GREEN + "の取引を宝石のショップにマージしました");
        SoundUtil.playSuccessSound(p);
    }
}
