package bet.bettaque.fancygens;

import bet.bettaque.fancygens.db.PlacedGenerator;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.itemutils.ItemBuilder;

import java.sql.SQLException;

public class GeneratorHandler {
    Dao<PlacedGenerator, Integer> placedGeneratorDao;

    public GeneratorHandler(Dao<PlacedGenerator, Integer> placedGeneratorDao) {
        this.placedGeneratorDao = placedGeneratorDao;
    }

    public void generateResources(){
        try {
            for (PlacedGenerator placedGenerator : placedGeneratorDao.queryForAll()) {
                Location location = placedGenerator.getLocation();
                location.add(0, 1, 0);
                World world = location.getWorld();

                world.dropItem(location, new ItemBuilder(placedGenerator.getMaterial()));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }
}
