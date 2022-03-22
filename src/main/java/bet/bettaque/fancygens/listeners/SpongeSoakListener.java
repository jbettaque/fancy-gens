package bet.bettaque.fancygens.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class SpongeSoakListener implements Listener {
    Plugin plugin;

    public SpongeSoakListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpongeAbsorb (SpongeAbsorbEvent e ){
        Block block = e.getBlock();
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            NamespacedKey key = new NamespacedKey(plugin, "generator");
            if (container.get(key, PersistentDataType.INTEGER)!=null){
                // block is a generator
                e.setCancelled(true);
            }



    }

}
