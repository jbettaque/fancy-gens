package bet.bettaque.fancygens.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "placed_autosell_chests")
public class PlacedAutosellChest extends PlacedCustomBlock {

    @DatabaseField
    private double multiplier;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<Double> lastSells = new ArrayList<>();

    public PlacedAutosellChest() {
    }

    public PlacedAutosellChest(Location location, UUID owner, double multiplier) {
        super(location, owner);
        this.multiplier = multiplier;
    }

    public void addSell(double price){
        if (this.lastSells == null) this.lastSells = new ArrayList<>();
        this.lastSells.add(0,price);
        if (lastSells.size() > 20) {
            lastSells.remove(lastSells.size() -1);
        }
    }

    public double getLastSellsAvrg(){
        double total = 0;
        if (this.lastSells != null){
            for (double sell: this.lastSells){
                total += sell;
            }
        } else {
            lastSells = new ArrayList<>();
        }


        return Math.round(total / lastSells.size());
    }

    public ArrayList<Double> getLastSells() {
        return lastSells;
    }

    public void incrementMultiplier(double amount){
        this.multiplier += amount;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
