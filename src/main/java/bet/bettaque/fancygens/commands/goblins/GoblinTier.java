package bet.bettaque.fancygens.commands.goblins;

import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum GoblinTier {
    COMMON("common"),
    RARE("rare"),
    EPIC("epic"),
    LEGENDARY("legendary"),
    ULTIMATE("ultimate");

    private String prefix;

    GoblinTier(String prefix) {
        this.prefix = prefix;
    }

    public String getItemId() {
        return "summoning_scroll_" + prefix;
    }

    public ItemStack getOraxenItem(){
        return OraxenItems.getItemById(this.getItemId()).build();
    }

    public String getDisplayName(){
        return getOraxenItem().getItemMeta().getDisplayName();
    }

    public static GoblinTier get(String prefix){
        return Arrays.stream(GoblinTier.values())
                .filter(tier -> tier.prefix.equals(prefix))
                .findFirst().orElse(GoblinTier.COMMON);
    }

    public double getMultiplier(){
        switch (this){
            case COMMON: return 1;
            case RARE: return 2.5d;
            case EPIC: return 4d;
            case LEGENDARY: return 7d;
            case ULTIMATE: return 12d;
        }
        return 1;
    }
}
