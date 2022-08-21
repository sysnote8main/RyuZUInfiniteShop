package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.AgeableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.PoweredableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.VillagerableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;

import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ConfirmRemoveShop extends ShopGui {

    public ConfirmRemoveShop(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9, JavaUtil.getOrDefault(getShop().getNPC().getCustomName() , "ショップ") + "を本当に削除しますか？");

        inv.setItem(0, ItemUtil.getNamedItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "キャンセル"));
        inv.setItem(8, ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "削除する"));

        return inv;
    }
}
