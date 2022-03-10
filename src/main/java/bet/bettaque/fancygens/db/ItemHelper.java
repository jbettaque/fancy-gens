package bet.bettaque.fancygens.db;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import redempt.redlib.itemutils.ItemUtils;

public class ItemHelper {

    public static void damageAndCheckRemove(ItemStack item, int amount, Player player){
        ItemUtils.damage(item, 1);

        org.bukkit.inventory.meta.Damageable damageable = ((Damageable) item.getItemMeta());
        if (damageable.getDamage() >= item.getType().getMaxDurability()){
            ItemUtils.remove(player.getInventory(), item, amount);
        }
    }
}
