/**
 Copyright (c) 6 jul. 2016 Simcha van Collem

 The MIT License (MIT)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package me.VetBakSim.simpleranks.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import me.VetBakSim.simpleranks.PermissionsManager;
import me.VetBakSim.simpleranks.Rank;
import me.VetBakSim.simpleranks.RanksManager;

public class PlayerListeners implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		Rank rank = RanksManager.getInstance().getRank(p.getUniqueId());
		if (rank == null)
			return;
		if (!rank.getTeam().getEntries().contains(p.getName()))
			rank.getTeam().addEntry(p.getName());
		PermissionsManager.getInstance().loadPermissions(p);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Rank rank = RanksManager.getInstance().getRank(p.getUniqueId());
		if (rank == null)
			return;
		e.setFormat(rank.getPrefix() + " " + e.getFormat());
	}

}
