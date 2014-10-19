/*
 * This file is part of MoreMaterials, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 AlmuraDev <http://www.almuradev.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * This file is part of Sprout.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Sprout is licensed under the GNU General Public License.
 *
 * Sprout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sprout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package net.morematerials.manager;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class JobsWorker {
    
    public static void jobsBreak(Player sPlayer, Block block) {

        me.zford.jobs.Player player = me.zford.jobs.bukkit.BukkitUtil.wrapPlayer(sPlayer);
        if (!sPlayer.getGameMode().equals(GameMode.CREATIVE)) {
            if (me.zford.jobs.Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld())) {
                double multiplier = me.zford.jobs.config.ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
                me.zford.jobs.container.JobsPlayer jPlayer = me.zford.jobs.Jobs.getPlayerManager().getJobsPlayer(player.getName());
                me.zford.jobs.Jobs.action(jPlayer, new me.zford.jobs.bukkit.actions.BlockActionInfo(block,  me.zford.jobs.container.ActionType.BREAK), multiplier);
            }
        }
    }

    public static void jobsPlace(Player sPlayer, Block block) {
        me.zford.jobs.Player player = me.zford.jobs.bukkit.BukkitUtil.wrapPlayer(sPlayer);
        if (!sPlayer.getGameMode().equals(GameMode.CREATIVE)) {
            if (me.zford.jobs.Jobs.getPermissionHandler().hasWorldPermission(player, player.getLocation().getWorld())) {			
                double multiplier = me.zford.jobs.config.ConfigManager.getJobsConfiguration().getRestrictedMultiplier(player);
                me.zford.jobs.container.JobsPlayer jPlayer = me.zford.jobs.Jobs.getPlayerManager().getJobsPlayer(player.getName());
                me.zford.jobs.Jobs.action(jPlayer, new me.zford.jobs.bukkit.actions.BlockActionInfo(block, me.zford.jobs.container.ActionType.PLACE), multiplier);
            }
        }
    }
}
