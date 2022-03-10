package bet.bettaque.fancygens.db;

import com.j256.ormlite.field.DatabaseField;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class PlacedCustomBlock {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private double x;

    @DatabaseField
    private double y;

    @DatabaseField
    private double z;

    @DatabaseField
    private String world;

    @DatabaseField
    private UUID owner;

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, 0, 0);
    }

    public PlacedCustomBlock(Location location, UUID owner) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getName();
        this.owner = owner;
    }

    public PlacedCustomBlock() {
    }
}
