package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.commands.MineCommands;
import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.config.MineConfig;
import bet.bettaque.fancygens.helpers.TextHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class MinesGui extends FancyGui{
    MineCommands mineCommands;

    public MinesGui(Player player, MainMenuGui previous, MineCommands mineCommands) {
        super(TextHelper.parseFancyString("&#2196F3-#E91E63&**Mines**"), 45, player, previous);
        this.mineCommands = mineCommands;
    }

    @Override
    public void populate() {
        for (MineConfig mine: GensConfig.mines.stream().sorted(Comparator.comparingInt(MineConfig::getPrestigeRequirement)).collect(Collectors.toList())) {
            ItemStack item = new ItemBuilder(mine.getIcon()).setName(TextHelper.parseFancyString("&#64B5F6-#80CBC4&" + mine.getName()));
            contents.add(item);
            setCallback(item, e -> {
                mineCommands.tpToMines(player, mine);
            });
            setLore(item, Arrays.asList(TextHelper.parseFancyString("&gray&Requirement: " + TextHelper.formatPrestige(mine.getPrestigeRequirement(), player))));
        }


        super.populate();
    }
}
