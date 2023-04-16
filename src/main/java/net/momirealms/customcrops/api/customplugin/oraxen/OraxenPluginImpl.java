package net.momirealms.customcrops.api.customplugin.oraxen;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import io.th0rgal.oraxen.utils.drops.Drop;
import net.momirealms.customcrops.api.customplugin.PlatformInterface;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OraxenPluginImpl implements PlatformInterface {

    @Override
    public boolean removeCustomBlock(Location location) {
        return OraxenBlocks.remove(location, null);
    }

    @Nullable
    @Override
    public String getCustomBlockID(Location location) {
        Mechanic mechanic = OraxenBlocks.getOraxenBlock(location);
        return mechanic == null ? null : mechanic.getItemID();
    }

    @Nullable
    @Override
    public ItemStack getItemStack(String id) {
        ItemBuilder itemBuilder = OraxenItems.getItemById(id);
        return itemBuilder == null ? null : itemBuilder.build();
    }

    @Nullable
    @Override
    public ItemFrame placeItemFrame(Location location, String id) {
        FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(id);
        Entity entity = mechanic.place(location, 0, Rotation.NONE, BlockFace.UP);
        if (entity instanceof ItemFrame itemFrame)
            return itemFrame;
        else {
            entity.remove();
        }
        return null;
    }

    @Nullable
    @Override
    public ItemDisplay placeItemDisplay(Location location, String id) {
        FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(id);
        Entity entity = mechanic.place(location);
        if (entity instanceof ItemDisplay itemDisplay)
            return itemDisplay;
        else {
            entity.remove();
        }
        return null;
    }

    @Override
    public void placeNoteBlock(Location location, String id) {
        NoteBlockMechanicFactory.setBlockModel(location.getBlock(), id);
    }

    @Override
    public void placeTripWire(Location location, String id) {
        StringBlockMechanicFactory.setBlockModel(location.getBlock(), id);
    }

    @NotNull
    @Override
    public String getBlockID(Block block) {
        Mechanic mechanic = OraxenBlocks.getOraxenBlock(block.getBlockData());
        return mechanic == null ? block.getType().name() : mechanic.getItemID();
    }

    @Override
    public boolean doesItemExist(String id) {
        return OraxenItems.getItemById(id) != null;
    }

    @Override
    public void dropBlockLoot(Block block) {
        BlockMechanic mechanic = BlockMechanicFactory.getBlockMechanic(block);
        if (mechanic == null) return;
        Drop drop = mechanic.getDrop();
        if (drop != null)
            drop.spawns(block.getLocation(), new ItemStack(Material.AIR));
    }

    @Override
    public boolean removeItemDisplay(Location location) {
        //TODO Not implemented
        return false;
    }

    @Override
    public void placeChorus(Location location, String id) {
        //TODO Not implemented
    }

    @Override
    public Location getItemFrameLocation(Location location) {
        return location.clone().add(0.5,0.03125,0.5);
    }

    @Nullable
    @Override
    public String getCustomItemAt(Location location) {
        String block = getBlockID(location.getBlock());
        if (!block.equals("AIR")) return block;

        ItemFrame itemFrame = getItemFrameAt(location);
        if (itemFrame != null) {
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(itemFrame);
            if (furnitureMechanic != null) {
                return furnitureMechanic.getItemID();
            }
        }

        return null;
    }

    @NotNull
    @Override
    public String getItemID(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            NBTCompound bukkitPublic = nbtItem.getCompound("PublicBukkitValues");
            if (bukkitPublic != null) {
                String id = bukkitPublic.getString("oraxen:id");
                if (!id.equals("")) return id;
            }
        }
        return itemStack.getType().name();
    }
}