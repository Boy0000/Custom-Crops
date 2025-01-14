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

package net.momirealms.customcrops.api.object.action;

import net.momirealms.customcrops.api.object.ItemMode;
import net.momirealms.customcrops.api.object.world.SimpleLocation;
import net.momirealms.customcrops.util.AdventureUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record MessageActionImpl(String[] messages, double chance) implements Action {

    @Override
    public void doOn(@Nullable Player player, @Nullable SimpleLocation cropLoc, ItemMode itemMode) {
        if (player == null || Math.random() > chance) return;
        for (String message : messages) {
            AdventureUtils.playerMessage(player,
                    message.replace("{player}", player.getName())
                            .replace("{world}", player.getWorld().getName())
                            .replace("{x}", cropLoc == null ? "" : String.valueOf(cropLoc.getX()))
                            .replace("{y}", cropLoc == null ? "" : String.valueOf(cropLoc.getY()))
                            .replace("{z}", cropLoc == null ? "" : String.valueOf(cropLoc.getZ()))
            );
        }
    }
}