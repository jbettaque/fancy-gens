package bet.bettaque.fancygens.listeners;

import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class UpgradableChestListener implements Listener {
    @EventHandler
    public void onUpgradableChestPlace(BlockPlaceEvent e){
       /* e.getItemInHand()*/
    }

    @EventHandler
    public void onUpgradableChestBrake(BlockBreakEvent e){

    }

    @EventHandler
    public void onUpgradableChestOpen(PlayerInteractEvent e){
//        if (e.getClickedBlock().getState() instanceof Chest)

    }
}
