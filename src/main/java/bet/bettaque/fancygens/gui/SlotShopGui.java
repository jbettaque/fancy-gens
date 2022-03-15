package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.commands.UiCommands;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class SlotShopGui extends FancyGui {
    UiCommands uiCommands;
    ShopCommands shopCommands;
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public SlotShopGui(Player player, MainMenuGui previous, UiCommands uiCommands, ShopCommands shopCommands, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        super(TextHelper.parseFancyString("&#AB47BC-#42A5F5&**Buy Generator Slots**"), 9, player, previous);
        this.uiCommands = uiCommands;
        this.shopCommands = shopCommands;
        this.generatorPlayerDao = generatorPlayerDao;
        this.center = true;
    }

    @Override
    public void populate() {
        try {
            GeneratorPlayer generatorPlayer = this.generatorPlayerDao.queryForId(player.getUniqueId().toString());

            ItemStack slotsIcon = OraxenItems.getItemById("slots_icon").build();
            ItemUtils.setName(slotsIcon, TextHelper.parseFancyString("&#AB47BC-#42A5F5&Buy 10 generator slots"));
            if (this.contents.size() > 0){
                this.contents.set(0, slotsIcon);
            } else {
                this.contents.add(0,slotsIcon);
            }
            this.setLore(slotsIcon, Arrays.asList(TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatCurrency(shopCommands.calculateSlotPrice(generatorPlayer), player))));
            this.setCallback(slotsIcon, e -> {
                shopCommands.buySlots(generatorPlayer, player);
                this.populate();
            });
            super.populate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
