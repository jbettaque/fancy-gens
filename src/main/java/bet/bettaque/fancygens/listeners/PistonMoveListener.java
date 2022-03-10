package bet.bettaque.fancygens.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class PistonMoveListener implements Listener {
    Plugin plugin;

    public  PistonMoveListener(Plugin plugin) {
        this.plugin=plugin;
    }
    @EventHandler
    public  void onPistonPush(BlockPistonExtendEvent e){
        for (Block block : e.getBlocks()){
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            NamespacedKey key = new NamespacedKey(plugin, "generator");
            if (container.get(key, PersistentDataType.INTEGER)!=null){
                // block is a generator
                e.setCancelled(true);
            }
        }
    }
 @EventHandler
    public  void onPistonRetraction(BlockPistonRetractEvent e   ){
        if(!e.isSticky()) return;

        for (Block block : e.getBlocks()){
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            NamespacedKey key = new NamespacedKey(plugin, "generator");
            if (container.get(key, PersistentDataType.INTEGER)!=null){
                // block is a generator

                e.setCancelled(true);
            }
        }
    }


}
