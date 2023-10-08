package net.momirealms.customcrops.customplugin.geary;

import com.mineinabyss.blocky.api.BlockyBlocks;
import com.mineinabyss.blocky.api.BlockyFurnitures;
import com.mineinabyss.blocky.components.features.BlockyDrops;
import com.mineinabyss.blocky.helpers.GenericHelpers;
import com.mineinabyss.blocky.helpers.GenericHelpersKt;
import com.mineinabyss.blocky.helpers.NoteBlockHelpersKt;
import com.mineinabyss.geary.helpers.EntityHelpersKt;
import com.mineinabyss.geary.papermc.datastore.DataStoreKt;
import com.mineinabyss.geary.papermc.datastore.namespacedkey.GearySerializersExtensionsKt;
import com.mineinabyss.geary.papermc.tracking.blocks.BlockTrackingKt;
import com.mineinabyss.geary.papermc.tracking.items.ItemTrackingKt;
import com.mineinabyss.geary.prefabs.PrefabKey;
import net.momirealms.customcrops.customplugin.PlatformInterface;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GearyPluginImpl implements PlatformInterface {
    @Override
    public boolean removeCustomBlock(Location location) {
        return BlockyBlocks.INSTANCE.removeBlockyBlock(location);
    }

    @Nullable
    @Override
    public String getCustomBlockID(Location location) {
        PrefabKey prefabKey = BlockTrackingKt.getGearyBlocks().getBlock2Prefab().get(location.getBlock().getBlockData());
        return prefabKey != null ? prefabKey.getFull() : null;
    }

    @Nullable
    @Override
    public ItemStack getItemStack(String id) {
        PrefabKey prefabKey = PrefabKey.Companion.ofOrNull(id);
        if (prefabKey == null) return null;
        return ItemTrackingKt.getGearyItems().createItem(prefabKey, null);
    }

    @Nullable
    @Override
    public ItemFrame placeItemFrame(Location location, String id) {
        return null;
    }

    @Nullable
    @Override
    public ItemDisplay placeItemDisplay(Location location, String id) {
        PrefabKey prefabKey = PrefabKey.Companion.ofOrNull(id);
        if (prefabKey == null) return null;
        return BlockyFurnitures.INSTANCE.placeFurniture(prefabKey, location);
    }

    @Override
    public void placeNoteBlock(Location location, String id) {
        PrefabKey prefabKey = PrefabKey.Companion.ofOrNull(id);
        if (prefabKey == null) return;
        BlockyBlocks.INSTANCE.placeBlockyBlock(location, prefabKey);
    }

    @Override
    public void placeTripWire(Location location, String id) {
        PrefabKey prefabKey = PrefabKey.Companion.ofOrNull(id);
        if (prefabKey == null) return;
        BlockyBlocks.INSTANCE.placeBlockyBlock(location, prefabKey);
    }

    @Override
    public void placeChorus(Location location, String id) {
    }

    @NotNull
    @Override
    public String getBlockID(Block block) {
        PrefabKey prefabKey = BlockTrackingKt.getGearyBlocks().getBlock2Prefab().get(block.getBlockData());
        return prefabKey != null ? prefabKey.getFull() : "";
    }

    @Override
    public boolean doesItemExist(String id) {
        PrefabKey prefabKey = PrefabKey.Companion.ofOrNull(id);
        if (prefabKey == null) return false;
        return ItemTrackingKt.getGearyItems().getPrefabs().getKeys().contains(prefabKey);
    }

    @Override
    public void dropBlockLoot(Block block) {
        PrefabKey prefabKey = BlockTrackingKt.getGearyBlocks().getBlock2Prefab().get(block.getBlockData());
        if (prefabKey == null) return;
        for (Object component : DataStoreKt.decodeComponents(GenericHelpersKt.getPersistentDataContainer(block)).getPersistingComponents()) {
            if (component instanceof BlockyDrops drops) {
                for (BlockyDrops.BlockyDrop drop : drops.getDrops()) {
                    if (drop.getItem() == null) continue;
                    ItemStack itemStack = drop.getItem().toItemStackOrNull(new ItemStack(Material.AIR));
                    if (itemStack == null) continue;
                    block.getLocation().getWorld().dropItemNaturally(block.getLocation(), itemStack);
                }
            }
        }
    }

    @NotNull
    @Override
    public String getItemStackID(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return "";
        PrefabKey prefabKey = DataStoreKt.decodePrefabs(itemMeta.getPersistentDataContainer()).stream().findFirst().orElse(PrefabKey.Companion.ofOrNull(""));
        return prefabKey != null ? prefabKey.getFull() : "";
    }

    @Nullable
    @Override
    public String getItemDisplayID(ItemDisplay itemDisplay) {
        PrefabKey prefabKey = BlockyFurnitures.INSTANCE.getPrefabKey(itemDisplay);
        return prefabKey != null ? prefabKey.getFull() : null;
    }

    @Nullable
    @Override
    public String getItemFrameID(ItemFrame itemFrame) {
        return null;
    }
}
