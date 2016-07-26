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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.VetBakSim.simpleranks.PermissionsManager;
import me.VetBakSim.simpleranks.Rank;
import me.VetBakSim.simpleranks.RanksManager;

public class PlayerListeners implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		Rank rank = RanksManager.getRank(p.getUniqueId());
		if (rank == null)
			return;
		if (!rank.getTeam().getEntries().contains(p.getName()))
			rank.getTeam().addEntry(p.getName());
		PermissionsManager.loadPermissions(p);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Rank rank = RanksManager.getRank(p.getUniqueId());

		if (e.getMessage().startsWith("!")) {
			e.setMessage(e.getMessage().substring(1));
			if (rank == null)
				return;
			e.getRecipients().removeAll(Bukkit.getOnlinePlayers());
			e.getRecipients().add(p);
			e.setFormat(ChatColor.DARK_AQUA + "[" + ChatColor.RESET + rank.getName() + ChatColor.DARK_AQUA + " Chat] "
					+ ChatColor.RESET + e.getFormat());
			for (String member : rank.getMembers()) {
				Player pl = Bukkit.getPlayer(UUID.fromString(member));
				if (pl != null)
					e.getRecipients().add(pl);
			}
		} else {
			if (rank != null)
				e.setFormat(rank.getPrefix() + " " + e.getFormat());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		PermissionsManager.getAttachments().remove(e.getPlayer().getUniqueId());
	}

}
