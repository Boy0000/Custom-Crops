package net.momirealms.customcrops.customplugin.geary;

import com.mineinabyss.blocky.api.BlockyFurnitures;
import com.mineinabyss.blocky.api.events.block.BlockyBlockBreakEvent;
import com.mineinabyss.blocky.api.events.block.BlockyBlockPlaceEvent;
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureBreakEvent;
import com.mineinabyss.blocky.api.events.furniture.BlockyFurnitureInteractEvent;
import com.mineinabyss.blocky.api.events.furniture.BlockyFurniturePlaceEvent;
import com.mineinabyss.blocky.helpers.NoteBlockHelpersKt;
import com.mineinabyss.blocky.helpers.TripWireHelpersKt;
import com.mineinabyss.geary.papermc.tracking.blocks.BlockTrackingKt;
import com.mineinabyss.geary.papermc.tracking.entities.EntityTrackingKt;
import net.momirealms.customcrops.customplugin.Handler;
import net.momirealms.customcrops.customplugin.PlatformManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;

public class GearyHandler extends Handler {
    public GearyHandler(PlatformManager platformManager) {
        super(platformManager);
    }

    @EventHandler
    public void onPlaceFurniture(BlockyFurniturePlaceEvent event) {
        platformManager.onPlaceFurniture(event.getPlayer(), event.getBaseEntity().getLocation(), BlockyFurnitures.INSTANCE.getPrefabKey(event.getBaseEntity()).getFull(), event);
    }

    @EventHandler
    public void onBreakFurniture(BlockyFurnitureBreakEvent event) {
        ItemDisplay entity = event.getBaseEntity();
        platformManager.onBreakFurniture(event.getPlayer(), entity, BlockyFurnitures.INSTANCE.getPrefabKey(entity).getFull(), event);
    }

    @EventHandler
    public void onInteractFurniture(BlockyFurnitureInteractEvent event) {
        ItemDisplay entity = event.getBaseEntity();
        platformManager.onInteractFurniture(event.getPlayer(), entity, BlockyFurnitures.INSTANCE.getPrefabKey(entity).getFull(), event);
    }

    @EventHandler
    public void onPlaceCustomBlock(BlockyBlockPlaceEvent event) {
        platformManager.onPlaceCustomBlock(event.getPlayer(), event.getBlock().getLocation(), BlockTrackingKt.getGearyBlocks().getBlock2Prefab().get(event.getBlock().getBlockData()).getFull(), event);
    }

    @EventHandler
    public void onBreakCustomBlock(BlockyBlockBreakEvent event) {
        platformManager.onBreakCustomBlock(event.getPlayer(), event.getBlock().getLocation(), BlockTrackingKt.getGearyBlocks().getBlock2Prefab().get(event.getBlock().getBlockData()).getFull(), event);
    }
}
