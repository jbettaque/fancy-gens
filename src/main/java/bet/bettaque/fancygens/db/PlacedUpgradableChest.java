package bet.bettaque.fancygens.db;


import bet.bettaque.fancygens.helpers.SerializationHelper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DatabaseTable(tableName = "placed_upgradable_chests")
public class PlacedUpgradableChest extends PlacedCustomBlock {
    @DatabaseField
    private int level;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, BigInteger> storedItems;

    public PlacedUpgradableChest() {
    }

    public PlacedUpgradableChest(Location location, UUID owner,int level) {
        super(location, owner);
        this.level = 1;
        this.storedItems = new HashMap<>();
    }
//    public HashMap<String, BigInteger> getStoredItems() {
//        return storedItems;
//    }

    public void addItem(ItemStack itemStack) {
        BigInteger amount = BigInteger.valueOf(itemStack.getAmount());
        itemStack.setAmount(1);
        String serial = SerializationHelper.itemStackToBase64(itemStack);
        if (storedItems.containsKey(serial)) {
            BigInteger newAmount =  storedItems.get(serial).add(amount);
            storedItems.put(serial, newAmount);

        } else {
            storedItems.put(serial, amount);
        }
    }


    public boolean removeItem(ItemStack itemStack) {
        String serial = SerializationHelper.itemStackToBase64(itemStack);

        if (storedItems.containsKey(serial)){
            storedItems.remove(serial);
            return true;
        }
        return false;
    }

    public boolean removeItem(ItemStack itemStack, BigInteger amount) {
        String serial = SerializationHelper.itemStackToBase64(itemStack);
        if (storedItems.get(serial).compareTo(BigInteger.valueOf(1))<0){
            return false;
        }

        if (storedItems.containsKey(serial)){
            BigInteger newAmount = storedItems.get(serial).subtract(amount);
            storedItems.put(serial,newAmount);
            if (newAmount.compareTo(BigInteger.ZERO)<1){
                storedItems.remove(serial);
            }


            return true;
        }
        return false;
    }


    public int getLevel() {
        return level;
    }

    public HashMap<ItemStack,BigInteger> getStoredItems(){
        HashMap<ItemStack,BigInteger> deserialized = new HashMap<>();
        for (Map.Entry<String, BigInteger> item : storedItems.entrySet()){
            deserialized.put(SerializationHelper.itemStackFromBase64(item.getKey()),item.getValue()) ;
        }
        return deserialized;
    }

}
