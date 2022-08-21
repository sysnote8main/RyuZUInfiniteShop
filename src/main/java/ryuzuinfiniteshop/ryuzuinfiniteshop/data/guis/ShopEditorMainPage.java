package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.AgeableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.PoweredableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.VillagerableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;

import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ShopEditorMainPage extends ShopGui {
    public enum ShopSettings {Age, Power, Profession, Biome, Visible}

    private final HashMap<Integer, ShopSettings> SettingsMap = new HashMap<>();

    public ShopEditorMainPage(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9 * 6, JavaUtil.getOrDefault(getShop().getNPC().getCustomName(), "ショップ") + " エディター ページ" + getPage());

        //アイテムを設置
        setDisplay(inv);

        return inv;
    }

    public List<ShopTrade> getTrades() {
        return this.trades;
    }

    public ShopTradeGui getTradeGui(int slot) {
        if (slot < 0 || 17 < slot) return null;
        if (getShop().getTradePageCount() > slot + 1) return null;
        return getShop().getPage(slot + (getPage() - 1) * 18);
    }

    //エディターに装備を置く
    private void setEquipment(Inventory inv) {
        if (getShop().getNPC() instanceof LivingEntity) {
            for (Integer slot : EquipmentUtil.getEquipmentsSlot().keySet()) {
                inv.setItem(slot, getShop().getEquipmentDisplayItem(EquipmentUtil.getEquipmentSlot(slot)));
            }
        }
    }

    private void setTradesPage(Inventory inv) {
        int lastslot = getTradeLastSlotNumber();
        for (int i = 0; i <= lastslot; i++) {
            inv.setItem(i, ItemUtil.getNamedItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "ページ" + getTradePageNumber(i)));
        }
        int newslot = getTradeNewSlotNumber();
        if (newslot == 18)
            getShop().createEditorNewPage();
        else if (newslot != -1)
            inv.setItem(newslot, ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.YELLOW + "新規ページ"));
    }

    private void setDisplayName(Inventory inv) {
        inv.setItem(5 * 9 + 8, ItemUtil.getNamedItem(Material.NAME_TAG, ChatColor.GREEN + "名前を変更する"));
    }

    private void setShopType(Inventory inv) {
        String typename = "";
        switch (getShop().getShopType()) {
            case TwotoOne:
                typename = ChatColor.GREEN + "2対1";
                break;
            case FourtoFour:
                typename = ChatColor.GREEN + "4対4";
                break;
            case SixtoTwo:
                typename = ChatColor.GREEN + "6対2";
                break;
        }
        inv.setItem(5 * 9 + 7, ItemUtil.getNamedItem(Material.MAGENTA_GLAZED_TERRACOTTA, typename));
    }

    private void setNPCDirecation(Inventory inv) {
        inv.setItem(5 * 9 + 6, ItemUtil.getNamedItem(Material.ARROW, ChatColor.GREEN + "方向切り替え"));
    }

    private void setLock(Inventory inv) {
        ItemStack item = getShop().isLock() ?
                ItemUtil.getNamedEnchantedItem(Material.TRIPWIRE_HOOK, ChatColor.GREEN + "ロック") :
                ItemUtil.getNamedItem(Material.TRIPWIRE_HOOK, ChatColor.GREEN + "アンロック");
        inv.setItem(5 * 9 + 5, item);
    }

    private void setConvertTrades(Inventory inv) {
        inv.setItem(3 * 9 + 7, ItemUtil.getNamedItem(Material.EMERALD, ChatColor.GREEN + "トレード内容をアイテム化する"));
        inv.setItem(2 * 9 + 8, ItemUtil.getNamedEnchantedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GREEN + "トレードを読み込む"));
    }

    private void setConvertShop(Inventory inv) {
        inv.setItem(3 * 9 + 8, ItemUtil.getNamedItem(Material.DIAMOND, ChatColor.GREEN + "ショップをアイテム化する"));
    }

    private void setRemoveShop(Inventory inv) {
        inv.setItem(3 * 9 + 6, ItemUtil.getNamedItem(Material.BARRIER, ChatColor.RED + "ショップを削除する"));
    }

    public void setDisplay(Inventory inv) {
        setEquipment(inv);
        setTradesPage(inv);
        setDisplayName(inv);
        setShopType(inv);
        setNPCDirecation(inv);
        setConvertTrades(inv);
        setConvertShop(inv);
        setRemoveShop(inv);
        setSettings(inv);
    }

    public void setSettings(Inventory inv) {
        SettingsMap.clear();
        setAge(inv);
        setPower(inv);
        setProfession(inv);
        setBiome(inv);
        setVisible(inv);
    }

    private void setAge(Inventory inv) {
        if (!(getShop() instanceof AgeableShop)) return;
        ItemStack item = ((AgeableShop) getShop()).isAdult() ?
                ItemUtil.getNamedItem(Material.STONE, ChatColor.GREEN + "大人") :
                ItemUtil.getNamedItem(Material.STONE_BUTTON, ChatColor.GREEN + "子供");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.Age);
    }

    private void setPower(Inventory inv) {
        if (!(getShop() instanceof PoweredableShop)) return;
        ItemStack item = ((AgeableShop) getShop()).isAdult() ?
                ItemUtil.getNamedItem(Material.CREEPER_SPAWN_EGG, ChatColor.GREEN + "通常") :
                ItemUtil.getNamedEnchantedItem(Material.CREEPER_SPAWN_EGG, ChatColor.GREEN + "帯電");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.Power);
    }

    private void setProfession(Inventory inv) {
        if (!(getShop() instanceof VillagerableShop)) return;
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, ItemUtil.getNamedItem(((VillagerableShop) getShop()).getJobBlockMaterial(), ChatColor.GREEN + "ジョブチェンジ"));
        SettingsMap.put(slot, ShopSettings.Profession);
    }

    private void setBiome(Inventory inv) {
        if (!(getShop() instanceof VillagerableShop)) return;
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, ItemUtil.getNamedItem(((VillagerableShop) getShop()).getBiomeImageMaterial(), ChatColor.GREEN + "バイオームチェンジ"));
        SettingsMap.put(slot, ShopSettings.Biome);
    }

    private void setVisible(Inventory inv) {
        if (!(getShop().getNPC() instanceof LivingEntity)) return;
        ItemStack item = ((LivingEntity) getShop().getNPC()).isInvisible() ?
                ItemUtil.getNamedItem(Material.GLASS, ChatColor.GREEN + "透明") :
                ItemUtil.getNamedItem(Material.POLISHED_ANDESITE, ChatColor.GREEN + "不透明");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.Visible);
    }

    public int getTradePageRawNumber(int slot) {
        return slot + 1 + (getPage() - 1) * 18;
    }

    public int getTradePageNumber(int slot) {
        int page = getTradePageRawNumber(slot);
        return page <= getShop().getTradePageCount() ? page : 0;
    }

    public int getTradeLastSlotNumber() {
        return Math.min(17, getShop().getTradePageCount() - 1 - (getPage() - 1) * 18);
    }

    public int getTradeNewSlotNumber() {
        int last = getTradeLastSlotNumber();
        if (last == -1) return 0;
        if (getShop().isLimitPage(getTradePageNumber(last)))
            return last + 1;
        else
            return -1;
    }

    public HashMap<Integer, ShopSettings> getSettingsMap() {
        return SettingsMap;
    }
}
