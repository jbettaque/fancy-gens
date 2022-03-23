package bet.bettaque.fancygens.services;

import bet.bettaque.fancygens.helpers.TextHelper;
import org.bukkit.entity.Player;

import java.util.Arrays;

public enum FancyResource {
    COINS,
    POINTS,
    GEMS,
    SLOTS,
    MULTIPLIER,
    PRESTIGE,
    NULL;

    public String formatValue(double value, Player player){
        switch (this){
            case COINS: return TextHelper.formatCurrency(value, player);
            case POINTS: return TextHelper.formatPoints(value, player);
            case GEMS: return TextHelper.formatGems(value, player);
            case SLOTS: return TextHelper.formatSlots(value, player);
            case MULTIPLIER: return TextHelper.formatMultiplier(value, true, player);
            case PRESTIGE: return TextHelper.formatPrestige(value, player);
        }
        return "";
    }

    public static FancyResource get(String value){
        return Arrays.stream(FancyResource.values())
                .filter(resource -> resource.name().equals(value))
                .findFirst().orElse(FancyResource.NULL);
    }

}
