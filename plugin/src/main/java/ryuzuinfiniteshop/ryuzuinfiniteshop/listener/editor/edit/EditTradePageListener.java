package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.edit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common.EditOptionGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.OptionHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.OptionType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.TradeOption;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system.SchedulerListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.player.SearchTradeListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LogUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

public class EditTradePageListener implements Listener {
    //ショップのラインナップを変更
    @EventHandler
    public void onEdit(InventoryCloseEvent event) {
        //インベントリがショップなのかチェック
        Inventory inv = event.getInventory();
        ShopHolder holder = ShopUtil.getShopHolder(inv);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;

        //必要なデータを取得
        Player p = (Player) event.getPlayer();
        Shop shop = holder.getShop();

        if (FileUtil.isSaveBlock(p)) return;

        //取引を上書きし、取引として成立しないものは削除する
        boolean warn = shop.checkTrades(inv);
        if (warn)
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_TRADE_DUPLICATE.getMessage());
    }

    //トレードをアイテム化する
    @EventHandler
    public void convertTradeOne(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if (!((ShopTradeGui) holder.getGui()).isConvertSlot(slot)) return;

        Player p = (Player) event.getWhoClicked();
        ItemStack item = holder.getShop().convertTrade(event.getClickedInventory(), slot);
        //トレードをアイテム化する
        if (!event.isShiftClick()) return;
        if (ItemUtil.isAir(item)) {
            SoundUtil.playFailSound(p);
            return;
        }
        if (ItemUtil.ableGive(p.getInventory(), item)) {
            p.getInventory().addItem(item);
            SoundUtil.playSuccessSound(p);
        } else
            SoundUtil.playFailSound(p);
    }

    //ショップの取引編集ページを開く
    @EventHandler
    public void openTradePage(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopEditorGui editormainpage = (ShopEditorGui) holder.getGui();
        Shop shop = holder.getShop();
        int slot = event.getSlot();

        int lastslot = editormainpage.getTradeLastSlotNumber();
        int newslot = editormainpage.getTradeNewSlotNumber();
        int page = editormainpage.getTradePageRawNumber(slot);

        //存在するページなのかチェック
        if (slot > lastslot && slot != newslot) return;

        //取引編集ページを開く
        if (slot == newslot) {
            shop.createTradeNewPage();
            p.openInventory(shop.getPage(page).getInventory(holder.getMode(), holder));
        } else
            p.openInventory(shop.getPage(editormainpage.getTradePageNumber(slot)).getInventory(holder.getMode(), holder));

        //音を出す
        SoundUtil.playClickShopSound(p);
    }

    //取引を行う
    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!holder.getMode().equals(ShopMode.TRADE)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        int slot = event.getSlot();
        ShopTradeGui gui = ((ShopTradeGui) holder.getGui());
        ShopTrade trade = gui.getTradeFromSlot(slot);

        if (trade == null) return;
        ShopTrade.TradeResult result = trade.getResult(p, gui.getShop());
        if (!result.equals(ShopTrade.TradeResult.Success)) {
            if (gui.getConvertSlot().contains(slot)) ShopTrade.playResultEffect(p, result);
            if (result.equals(ShopTrade.TradeResult.NotEnoughItems)) SearchTradeListener.search(event);
            return;
        }

        //取引
        int times = 1;
        switch (type) {
            case SHIFT_RIGHT:
            case SHIFT_LEFT:
                times = 8;
                break;
            case MIDDLE:
                times = 64;
                break;
        }
        int resultTime = trade.trade(p, times);
        LogUtil.log(p.getUniqueId().toString(), holder.getShop().getID(), trade, resultTime);
    }

    //取引オプションを設定する
    @EventHandler
    public void openTradeOptionInventory(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopTradeGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if (!((ShopTradeGui) holder.getGui()).isConvertSlot(slot)) return;

        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        ShopTrade trade = shop.getTrade(event.getClickedInventory(), slot);
        //トレードをアイテム化する
        if (event.isShiftClick()) return;
        if (trade == null)
            SoundUtil.playFailSound(p);
        else {
            p.openInventory(new EditOptionGui(trade, new TradeOption(), shop, holder.getGui().getPage(), slot).getInventory(ShopMode.EDIT, holder));
            SoundUtil.playClickShopSound(p);
        }
    }

    @EventHandler
    public void editOption(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof EditOptionGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;
        int slot = event.getSlot();
        if (slot / 9 == 0) return;

        Player p = (Player) event.getWhoClicked();
        OptionType type = OptionType.valueOf(NBTUtil.getNMSStringTag(event.getCurrentItem(), "OptionType").toUpperCase());
        EditOptionGui gui = ((EditOptionGui) holder.getGui());
        TradeOption option = gui.getOption();
        if (slot == 5 && event.isShiftClick()) {
            SchedulerListener.setSchedulers(p, "ignore", event.getClickedInventory(), (message) -> {
                //成功時の処理
                try {
                    switch (type) {
                        case LIMIT:
                            option.setLimit(Math.max(Integer.parseInt(message), 0));
                            break;
                        case RATE:
                            option.setRate(Math.max(Integer.parseInt(message), 0));
                            break;
                        case MONEY:
                            option.setMoney(Math.max(Double.parseDouble(message), 0));
                            break;
                    }
                    SoundUtil.playSuccessSound(p);
                } catch (IllegalArgumentException e) {
                    p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "正しい数値を入力してください");
                    SoundUtil.playFailSound(p);
                }
                p.openInventory(event.getClickedInventory());
                event.getClickedInventory().setItem((slot / 9) * 9 + 4, gui.getOptionPanel(type));
            });
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "適用する正の数値を入力してください");
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + LanguageKey.MESSAGE_ENTER_CANCEL.getMessage());
            SoundUtil.playClickShopSound(p);
        } else if (slot == 5) {
            if (type.equals(OptionType.MONEY)) {
                option.setGive(!option.isGive());
                SoundUtil.playClickShopSound(p);
                event.getClickedInventory().setItem((slot / 9) * 9 + 4, gui.getOptionPanel(type));
            }
        } else {
            int value = Integer.parseInt(NBTUtil.getNMSStringTag(event.getCurrentItem(), "OptionValue"));
            switch (type) {
                case LIMIT:
                    option.setLimit(Math.max(option.getLimit() + value, 0));
                    break;
                case RATE:
                    option.setRate(Math.min(Math.max(option.getRate() + value, 0), 100));
                    break;
                case MONEY:
                    option.setMoney(Math.max(option.getMoney() + value, 0));
                    break;
            }
            SoundUtil.playClickShopSound(p);
            event.getClickedInventory().setItem((slot / 9) * 9 + 4, gui.getOptionPanel(type));
        }
    }

    @EventHandler
    public void completeEdittingOption(InventoryCloseEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event.getInventory());
        if (holder == null) return;
        if (!(holder.getGui() instanceof EditOptionGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        Player p = (Player) event.getPlayer();
        OptionHolder optionHolder = (OptionHolder) holder;
        Inventory inv = optionHolder.getBefore().getInventory();
        optionHolder.getGui().getTrade()
    }
}
