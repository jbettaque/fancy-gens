package bet.bettaque.fancygens.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

@DatabaseTable(tableName = "placed_generators")
public class PlacedGenerator {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double x;

    @DatabaseField
    private double y;

    @DatabaseField
    private double z;

    @DatabaseField
    private String material;

    @DatabaseField
    private String world;

    public PlacedGenerator() {
    }

    public PlacedGenerator(Location location, String material) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.material = material;
        this.world = location.getWorld().getName();
    }

    public Material getMaterial() {
        return Material.valueOf(material);
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, 0, 0);
    }

}
