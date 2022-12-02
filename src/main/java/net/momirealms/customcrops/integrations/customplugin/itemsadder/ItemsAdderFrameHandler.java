/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.integrations.customplugin.itemsadder;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import net.momirealms.customcrops.api.crop.Crop;
import net.momirealms.customcrops.config.BasicItemConfig;
import net.momirealms.customcrops.config.MainConfig;
import net.momirealms.customcrops.config.SoundConfig;
import net.momirealms.customcrops.config.SprinklerConfig;
import net.momirealms.customcrops.integrations.AntiGrief;
import net.momirealms.customcrops.managers.CropManager;
import net.momirealms.customcrops.objects.Sprinkler;
import net.momirealms.customcrops.utils.AdventureUtil;
import net.momirealms.customcrops.utils.FurnitureUtil;
import net.momirealms.customcrops.utils.MiscUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderFrameHandler extends ItemsAdderHandler {

    public ItemsAdderFrameHandler(CropManager cropManager) {
        super(cropManager);
    }

    public void onInteractFurniture(FurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final String namespacedID = event.getNamespacedID();
        if (namespacedID == null) return;

        final Player player = event.getPlayer();
        final Entity entity = event.getBukkitEntity();
        final Location location = entity.getLocation();;

        if (!canProceedAction(player, location)) return;

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedID);
        if (sprinkler != null) {
            if (!AntiGrief.testPlace(player, entity.getLocation())) return;
            super.onInteractSprinkler(entity.getLocation(), player, player.getInventory().getItemInMainHand(), sprinkler);
            return;
        }

        if (!namespacedID.contains("_stage_")) return;
        if (!namespacedID.equals(BasicItemConfig.deadCrop)) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (isRipe(namespacedID)) {
                if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand.getType() != Material.AIR)) {
                    if (!AntiGrief.testBreak(player, entity.getLocation())) return;
                    if (!canProceedAction(player, entity.getLocation())) return;
                    customInterface.removeFurniture(entity);
                    if (entity.isValid()) entity.remove();
                    this.onInteractRipeCrop(location, namespacedID, player);
                    return;
                }
            }
            else if (MainConfig.enableBoneMeal && itemInHand.getType() == Material.BONE_MEAL) {
                if (!AntiGrief.testPlace(player, location)) return;
                if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                if (Math.random() < MainConfig.boneMealChance) {
                    entity.getWorld().spawnParticle(MainConfig.boneMealSuccess, location.clone().add(0,0.5, 0),3,0.2,0.2,0.2);
                    if (SoundConfig.boneMeal.isEnable()) {
                        AdventureUtil.playerSound(
                                player,
                                SoundConfig.boneMeal.getSource(),
                                SoundConfig.boneMeal.getKey(),
                                1,1
                        );
                    }
                    customInterface.removeFurniture(entity);
                    customInterface.placeFurniture(location, customInterface.getNextStage(namespacedID));
                }
                return;
            }
        }
        super.tryMisc(player, player.getInventory().getItemInMainHand(), MiscUtils.getItemFrameBlockLocation(location.clone().subtract(0,1,0)));
    }

    public void onBreakFurniture(FurnitureBreakEvent event) {
        if (event.isCancelled()) return;

        final String namespacedId = event.getNamespacedID();
        if (namespacedId == null) return;

        final Location location = event.getBukkitEntity().getLocation();
        final Player player = event.getPlayer();

        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(namespacedId);
        if (sprinkler != null) {
            super.onBreakSprinkler(location);
            return;
        }

        if (MainConfig.enableCrow && namespacedId.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrow(event.getBukkitEntity().getLocation());
            return;
        }

        if (namespacedId.contains("_stage_")) {
            if (namespacedId.equals(BasicItemConfig.deadCrop)) return;
            if (!isRipe(namespacedId)) super.onBreakUnripeCrop(location);
            else super.onBreakRipeCrop(location, namespacedId, player, false);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        super.onPlayerInteract(event);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        CustomBlock cb = CustomBlock.byAlreadyPlaced(block);
        if (cb == null) return;

        final String blockID = cb.getNamespacedID();

        if (!blockID.equals(BasicItemConfig.wetPot) && !blockID.equals(BasicItemConfig.dryPot)) return;
        Location seedLoc = block.getLocation().clone().add(0,1,0);
        if (!canProceedAction(player, seedLoc)) return;

        ItemStack itemInHand = event.getItem();
        Location potLoc = block.getLocation();
        if (super.tryMisc(player, itemInHand, potLoc)) return;
        if (event.getBlockFace() != BlockFace.UP) return;

        CustomStack customStack = CustomStack.byItemStack(itemInHand);
        if (customStack != null) {
            String id = customStack.getId();
            if (id.endsWith("_seeds")) {
                String cropName = customStack.getId().substring(0, customStack.getId().length() - 6);
                plantSeed(seedLoc, cropName, player, itemInHand);
            }
        }
        else if (MainConfig.enableConvert) {
            String cropName = MainConfig.vanilla2Crops.get(itemInHand.getType());
            if (cropName == null) return;
            plantSeed(seedLoc, cropName, player, itemInHand);
        }
    }

    @Override
    public void onBreakBlock(CustomBlockBreakEvent event) {
        if (event.isCancelled()) return;

        final String namespacedId = event.getNamespacedID();
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (!AntiGrief.testBreak(player, location)) return;
        if (!canProceedAction(player, location)) return;

        if (namespacedId.equals(BasicItemConfig.dryPot) || namespacedId.equals(BasicItemConfig.wetPot)) {
            super.onBreakPot(location);
            ItemFrame itemFrame = FurnitureUtil.getItemFrame(customInterface.getFrameCropLocation(location.clone().add(0,1,0)));
            if (itemFrame == null) return;
            CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(itemFrame);
            if (customFurniture == null) return;
            String seedID = customFurniture.getNamespacedID();
            if (seedID.contains("_stage_")) {
                CustomFurniture.remove(itemFrame, false);
                if (itemFrame.isValid()) itemFrame.remove();
                if (seedID.equals(BasicItemConfig.deadCrop)) return;
                if (!isRipe(seedID)) super.onBreakUnripeCrop(location.clone().add(0,1,0));
                else super.onBreakRipeCrop(location.clone().add(0,1,0), seedID, player, true);
            }
        }
    }

    private void onInteractRipeCrop(Location location, String id, Player player) {
        Crop crop = customInterface.getCropFromID(id);
        if (crop == null) return;
        if (super.onInteractRipeCrop(location, crop, player)) return;
        if (crop.getReturnStage() != null) {
            CustomFurniture customFurniture = CustomFurniture.spawn(crop.getReturnStage(), location.getBlock());
            if (crop.canRotate() && customFurniture instanceof ItemFrame itemFrame) itemFrame.setRotation(FurnitureUtil.getRandomRotation());
        }
    }
}
