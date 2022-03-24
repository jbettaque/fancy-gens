package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.helpers.TextHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class GeneratorShopGui extends FancyGui{
    ShopCommands shopCommands;

    public GeneratorShopGui(Player player, MainMenuGui previous, ShopCommands shopCommands) {
        super(TextHelper.parseFancyString("&#2196F3-#E91E63&**Buy Generators**"), 45, player, previous);
        this.shopCommands = shopCommands;
    }

    @Override
    public void populate() {
        contents = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (Map.Entry<Integer, GenConfig> genConfig: GensConfig.gens.entrySet()) {
                ItemStack item = new ItemBuilder(genConfig.getValue().block)
                        .setName(TextHelper.parseFancyString("&#4DB6AC&" + genConfig.getValue().name))
                        .addLore(TextHelper.parseFancyString("&gray&Tier: &yellow&" + genConfig.getKey()))
                        .addLore(TextHelper.parseFancyString("&red&Boost: " + i));

                contents.add(item);
                int finalI = i;
                setCallback(item, e -> {
                    shopCommands.buyGenerator(genConfig.getValue(), player, 1, finalI);
                });
                setLore(item, Arrays.asList(
                        TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatCurrency(shopCommands.calculateGenPrice(genConfig.getValue(), i), player))
                ));
            }
        }



        super.populate();
    }
}
