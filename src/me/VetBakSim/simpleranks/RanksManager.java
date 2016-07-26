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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RanksManager {

	private static Set<Rank> ranks = new HashSet<>();

	public static Rank addRank(String name, String prefix, List<String> members, List<String> perms) {
		Rank rank = new Rank(name, prefix, members, perms);
		ranks.add(rank);

		for (String member : rank.getMembers()) {
			Player p = Bukkit.getPlayer(member);
			if (p != null) {
				PermissionsManager.loadPermissions(p);
			}
		}

		return rank;
	}

	public static void removeAllRanks() {
		Set<Rank> tempList = ranks;
		ranks = new HashSet<>();

		for (Rank rank : tempList) {
			rank.remove();
		}
	}

	public static Rank getRank(String name) {
		for (Rank rank : ranks) {
			if (ChatColor.stripColor(rank.getName()).equalsIgnoreCase(ChatColor.stripColor(name)))
				return rank;
		}
		return null;
	}

	public static Rank getRank(UUID uuid) {
		for (Rank rank : ranks) {
			if (rank.getMembers().contains(uuid.toString()))
				return rank;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void saveRanks() {
		FileConfiguration cfg = ConfigManager.getRanks().getConfig();

		for (Rank rank : ranks) {
			ConfigurationSection s = cfg.createSection(rank.getName());
			s.set("members", rank.getMembers());
			s.set("permissions", rank.getPermissions());
			s.set("prefix", rank.getPrefix());

			for (OfflinePlayer p : rank.getTeam().getPlayers()) {
				rank.getTeam().removePlayer(p);
			}
			rank.getTeam().unregister();
		}

		ConfigManager.getRanks().save();
	}

	public static void loadRanks() {
		FileConfiguration cfg = ConfigManager.getRanks().getConfig();

		for (String name : cfg.getKeys(false)) {
			ConfigurationSection s = cfg.getConfigurationSection(name);
			addRank(name, s.getString("prefix"), s.getStringList("members"), s.getStringList("permissions"));

			cfg.set(name, null);
		}

		ConfigManager.getRanks().save();
	}

	public static Set<Rank> getRanks() {
		return ranks;
	}

}
