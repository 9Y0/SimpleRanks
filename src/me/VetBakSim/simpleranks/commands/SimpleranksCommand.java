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
package me.VetBakSim.simpleranks.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.evilmidget38.UUIDFetcher;

import me.VetBakSim.simpleranks.Rank;
import me.VetBakSim.simpleranks.RanksManager;

public class SimpleranksCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sendHelp(s);
		}

		else if (args[0].equalsIgnoreCase("create")) {
			if (!s.hasPermission("simpleranks.create")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr create [rank] [prefix]");
				return true;
			}

			String rankName = chatColor(args[1]);
			String prefix = chatColor(args[2]);

			if (prefix.length() > 14) {
				s.sendMessage(ChatColor.RED + "That prefix is too long!");
				return true;
			}

			if (RanksManager.getInstance().getRank(rankName) != null) {
				s.sendMessage(ChatColor.RED + "A rank with that name does already exists!");
				return true;
			}
			Rank rank = RanksManager.getInstance().addRank(rankName, prefix, new ArrayList<String>(),
					new ArrayList<String>());
			s.sendMessage(ChatColor.GREEN + "You created rank " + ChatColor.RESET + rank.getName() + ChatColor.GREEN
					+ " with the prefix " + ChatColor.RESET + rank.getPrefix());
		}

		else if (args[0].equalsIgnoreCase("delete")) {
			if (!s.hasPermission("simpleranks.delete")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 2) {
				s.sendMessage(ChatColor.RED + "Usage: /sr delete [rank]");
				return true;
			}

			String rankName = chatColor(args[1]);

			Rank rank = RanksManager.getInstance().getRank(rankName);
			if (rank == null) {
				s.sendMessage(rankName + ChatColor.RED + " is not a rank!");
				return true;
			}

			rank.remove();
			s.sendMessage(ChatColor.GREEN + "You deleted the rank called " + ChatColor.RESET + rank.getName());
		}

		else if (args[0].equalsIgnoreCase("deleteall")) {
			if (!s.hasPermission("simpleranks.deleteall")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 1) {
				s.sendMessage(ChatColor.RED + "Usage: /sr deleteall");
				return true;
			}

			RanksManager.getInstance().removeAllRanks();
			s.sendMessage(ChatColor.GREEN + "You deleted all ranks!");
		}

		else if (args[0].equalsIgnoreCase("list")) {
			if (!s.hasPermission("simpleranks.list")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (RanksManager.getInstance().getRanks().isEmpty()) {
				s.sendMessage(ChatColor.RED + "There are no ranks at the moment!");
				return true;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.GOLD + "Ranks: " + ChatColor.RESET);
			for (Rank rank : RanksManager.getInstance().getRanks()) {
				sb.append(rank.getName() + ChatColor.RESET + ", ");
			}

			String ranks = sb.toString();
			ranks = ranks.substring(0, ranks.length() - 2);

			s.sendMessage(ranks.trim());
		}

		else if (args[0].equalsIgnoreCase("addplayer")) {
			if (!s.hasPermission("simpleranks.addplayer")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr addplayer [player] [rank]");
				return true;
			}

			String rankName = chatColor(args[2]);
			String pName = args[1];

			Rank rank = RanksManager.getInstance().getRank(rankName);
			if (rank == null) {
				s.sendMessage(rankName + ChatColor.RED + " is not a rank!");
				return true;
			}

			UUID uuid;
			try {
				uuid = UUIDFetcher.getUUIDOf(pName);
			} catch (Exception e) {
				s.sendMessage(ChatColor.RED + "That player does not exist");
				return true;
			}

			if (rank.getMembers().contains(uuid.toString())) {
				s.sendMessage(ChatColor.RED + pName + " is already a member of that rank!");
				return true;
			}

			for (Rank otherRank : RanksManager.getInstance().getRanks()) {
				if (otherRank.getMembers().contains(uuid.toString())) {
					s.sendMessage(ChatColor.RED + pName + " is already a member of the rank " + ChatColor.RESET
							+ otherRank.getName());
					return true;
				}
			}

			rank.addMember(uuid);
			s.sendMessage(ChatColor.GREEN + "You added " + ChatColor.DARK_GREEN + pName + ChatColor.GREEN
					+ " to the rank " + ChatColor.RESET + rank.getName());
		}

		else if (args[0].equalsIgnoreCase("removeplayer")) {
			if (!s.hasPermission("simpleranks.removeplayer")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr removeplayer [player] [rank]");
				return true;
			}

			String rankName = chatColor(args[2]);
			String pName = args[1];

			Rank rank = RanksManager.getInstance().getRank(rankName);
			if (rank == null) {
				s.sendMessage(rankName + ChatColor.RED + " is not a rank!");
				return true;
			}

			UUID uuid;
			try {
				uuid = UUIDFetcher.getUUIDOf(pName);
			} catch (Exception e) {
				s.sendMessage(ChatColor.RED + "That player does not exist");
				return true;
			}

			if (!rank.getMembers().contains(uuid.toString())) {
				s.sendMessage(ChatColor.RED + pName + " is not a member of that rank!");
				return true;
			}

			rank.removeMember(uuid);

			s.sendMessage(ChatColor.GREEN + "You removed " + ChatColor.DARK_GREEN + pName + ChatColor.GREEN
					+ " from the rank " + ChatColor.RESET + rank.getName());
		}

		else if (args[0].equalsIgnoreCase("addperm")) {
			if (!s.hasPermission("simpleranks.addperm")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr addperm [perm] [rank]");
				return true;
			}

			String rankName = chatColor(args[2]);
			String perm = args[1];

			Rank rank = RanksManager.getInstance().getRank(rankName);
			if (rank == null) {
				s.sendMessage(ChatColor.RED + rankName + " is not a rank!");
				return true;
			}

			if (rank.getPermissions().contains(perm)) {
				s.sendMessage(ChatColor.RED + "That rank already has the permission: " + perm);
				return true;
			}

			rank.addPermission(perm);
			s.sendMessage(ChatColor.GREEN + "You added the permission " + ChatColor.YELLOW + perm + ChatColor.GREEN
					+ " to the rank " + ChatColor.RESET + rank.getName());
		}

		else if (args[0].equalsIgnoreCase("removeperm")) {
			if (!s.hasPermission("simpleranks.removeperm")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr removeperm [perm] [rank]");
				return true;
			}

			String rankName = chatColor(args[2]);
			String perm = args[1];

			Rank rank = RanksManager.getInstance().getRank(rankName);
			if (rank == null) {
				s.sendMessage(ChatColor.RED + rankName + " is not a rank!");
				return true;
			}

			if (!rank.getPermissions().contains(perm)) {
				s.sendMessage(ChatColor.RED + "That rank does not have the permission: " + perm);
				return true;
			}

			rank.removePermission(perm);
			s.sendMessage(ChatColor.GREEN + "You removed the permission " + ChatColor.YELLOW + perm + ChatColor.GREEN
					+ " for the rank " + ChatColor.RESET + rank.getName());
		}

		else if (args[0].equalsIgnoreCase("help")) {
			sendHelp(s);
		}

		else {
			sendHelp(s);
		}
		return true;
	}

	private void sendHelp(CommandSender s) {
		StringBuilder sb = new StringBuilder();

		String line = ChatColor.GRAY + "-" + ChatColor.AQUA;

		sb.append("\n\n");
		sb.append(ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "-------------" + ChatColor.AQUA
				+ "SimpleRanks by VetBakSim" + ChatColor.GOLD + "" + ChatColor.STRIKETHROUGH + "-------------\n");
		sb.append(ChatColor.GOLD + "/sr create [rank] [prefix] " + line + " Create a rank!\n");
		sb.append(ChatColor.GOLD + "/sr delete [rank] " + line + " Delete a rank!\n");
		sb.append(ChatColor.GOLD + "/sr deleteall " + line + " Delete all ranks!\n");
		sb.append(ChatColor.GOLD + "/sr list " + line + " See a list of all ranks!\n");
		sb.append(ChatColor.GOLD + "/sr addplayer [player] [rank] " + line + " Add a player to a rank!\n");
		sb.append(ChatColor.GOLD + "/sr removeplayer [player] [rank] " + line + " Remove a player from a rank!\n");
		sb.append(ChatColor.GOLD + "/sr addperm [perm] [rank] " + line + " Add a permssion to a rank!\n");
		sb.append(ChatColor.GOLD + "/sr removeperm [perm] [rank] " + line + " Remove a permission form a rank!\n");
		sb.append(ChatColor.GOLD + "/sr help " + line + " Shows this message");

		s.sendMessage(sb.toString().trim());
	}

	private String chatColor(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

}
