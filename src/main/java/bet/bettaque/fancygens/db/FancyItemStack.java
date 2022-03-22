package bet.bettaque.fancygens.db;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class FancyItemStack extends ItemStack implements Serializable {
    public ItemStack getItemStack(){
        return this;
    }
}
