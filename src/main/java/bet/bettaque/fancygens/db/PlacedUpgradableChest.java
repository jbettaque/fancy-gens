package bet.bettaque.fancygens.db;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;

@DatabaseTable(tableName = "placed_upgradable_chests")
public class PlacedUpgradableChest extends PlacedCustomBlock {
    @DatabaseField
    private int level;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<FancyItemStack, BigInteger> storedItems;

    public PlacedUpgradableChest() {
    }

    public HashMap<FancyItemStack, BigInteger> getStoredItems() {
        return storedItems;
    }

    public void addItem(ItemStack itemStack) {
        if (storedItems.containsKey(itemStack)) {
            storedItems.get(itemStack).add(BigInteger.valueOf(itemStack.getAmount()));
        } else {
            storedItems.put((FancyItemStack) itemStack, BigInteger.valueOf(itemStack.getAmount()));
        }
    }


    public boolean removeItem(ItemStack itemStack) {
        if (storedItems.containsKey(itemStack)){
            storedItems.remove(itemStack);
            return true;
        }
        return false;
    }

    public boolean removeItem(ItemStack itemStack, BigInteger amount) {
        if (storedItems.containsKey(itemStack)){
            storedItems.get(itemStack).subtract(amount);
            return true;
        }
        return false;
    }


    public int getLevel() {
        return level;
    }

    public PlacedUpgradableChest(Location location, UUID owner) {
        super(location, owner);
        this.level = 0;
        this.storedItems = new HashMap<>();
    }


}
