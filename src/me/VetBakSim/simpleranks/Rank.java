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
package me.VetBakSim.simpleranks;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class Rank {

	private String name;
	private String prefix;
	private List<String> members;
	private List<String> perms;
	private Team team;

	public Rank(String name, String prefix, List<String> members, List<String> perms) {
		this.name = name.endsWith("§r") ? name : name + ChatColor.RESET;
		this.prefix = prefix.endsWith("§r") ? prefix : prefix + ChatColor.RESET;
		this.members = members;
		this.perms = perms;

		if (Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(name) == null)
			this.team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(name);
		else
			this.team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(name);

		this.team.setPrefix(this.prefix + " ");
	}

	public void addMember(UUID uuid) {
		members.add(uuid.toString());
		Player p = Bukkit.getServer().getPlayer(uuid);
		if (p != null) {
			team.addEntry(p.getName());
			PermissionsManager.getInstance().loadPermissions(p);
		}
	}

	public void removeMember(UUID uuid) {
		members.remove(uuid.toString());
		Player p = Bukkit.getServer().getPlayer(uuid);
		if (p != null) {
			team.removeEntry(p.getName());
			PermissionsManager.getInstance().loadPermissions(p);
		}
	}

	public void addPermission(String perm) {
		perms.add(perm);
		for (String uuidString : members) {
			Player p = Bukkit.getServer().getPlayer(UUID.fromString(uuidString));
			if (p != null) {
				PermissionsManager.getInstance().loadPermissions(p);
			}
		}
	}

	public void removePermission(String perm) {
		perms.remove(perm);
		for (String uuidString : members) {
			Player p = Bukkit.getServer().getPlayer(UUID.fromString(uuidString));
			if (p != null) {
				System.out.println("player found");
				PermissionsManager.getInstance().loadPermissions(p);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void remove() {
		for (OfflinePlayer p : team.getPlayers()) {
			team.removePlayer(p);
		}
		team.unregister();
		RanksManager.getInstance().getRanks().remove(this);
	}

	public String getName() {
		return name;
	}

	public String getNameWithoutColors() {
		return ChatColor.stripColor(name);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPrefixWithoutColors() {
		return ChatColor.stripColor(prefix);
	}

	public List<String> getPermissions() {
		return perms;
	}

	public List<String> getMembers() {
		return members;
	}

	public Team getTeam() {
		return team;
	}

}