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

public class PrestigeGui extends FancyGui {
    UiCommands uiCommands;
    ShopCommands shopCommands;
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public PrestigeGui(Player player, MainMenuGui previous, UiCommands uiCommands, ShopCommands shopCommands, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        super(TextHelper.parseFancyString("&#AB47BC-#42A5F5&**Prestige**"), 9, player, previous);
        this.uiCommands = uiCommands;
        this.shopCommands = shopCommands;
        this.generatorPlayerDao = generatorPlayerDao;
        this.center = true;
    }

    @Override
    public void populate() {
        try {
            GeneratorPlayer generatorPlayer = this.generatorPlayerDao.queryForId(player.getUniqueId().toString());

            ItemStack slotsIcon = OraxenItems.getItemById("prestige_icon").build();
            ItemUtils.setName(slotsIcon, TextHelper.parseFancyString("&#AB47BC-#42A5F5&Reset your points & gain prestige!"));
            if (this.contents.size() > 0){
                this.contents.set(0, slotsIcon);
            } else {
                this.contents.add(0,slotsIcon);
            }
            this.setLore(slotsIcon, Arrays.asList(
                    TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatCurrency(uiCommands.calculatePrestigePrice(generatorPlayer), player)),
                    TextHelper.parseFancyString("&gray&Points Required: " + TextHelper.formatPoints(uiCommands.calculatePrestigeRequirement(generatorPlayer), player))
            ));
            this.setCallback(slotsIcon, e -> {
                uiCommands.buyPrestige(generatorPlayer, player);
                this.populate();
            });
            super.populate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
