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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

			if (RanksManager.getRank(rankName) != null) {
				s.sendMessage(ChatColor.RED + "A rank with that name does already exists!");
				return true;
			}
			Rank rank = RanksManager.addRank(rankName, prefix, new ArrayList<String>(), new ArrayList<String>());
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

			Rank rank = RanksManager.getRank(rankName);
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

			RanksManager.removeAllRanks();
			s.sendMessage(ChatColor.GREEN + "You deleted all ranks!");
		}

		else if (args[0].equalsIgnoreCase("list")) {
			if (!s.hasPermission("simpleranks.list")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (RanksManager.getRanks().isEmpty()) {
				s.sendMessage(ChatColor.RED + "There are no ranks at the moment!");
				return true;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.GOLD + "Ranks: " + ChatColor.RESET);
			for (Rank rank : RanksManager.getRanks()) {
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

			Rank rank = RanksManager.getRank(rankName);
			if (rank == null) {
				s.sendMessage(rankName + ChatColor.RED + " is not a rank!");
				return true;
			}

			UUID uuid;
			try {
				uuid = UUIDFetcher.getUUIDOf(pName);
			} catch (Exception e) {
				s.sendMessage(ChatColor.RED + "That player does not exist, or the mojang API is down!");
				return true;
			}

			if (rank.getMembers().contains(uuid.toString())) {
				s.sendMessage(ChatColor.RED + pName + " is already a member of that rank!");
				return true;
			}

			for (Rank otherRank : RanksManager.getRanks()) {
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

			Rank rank = RanksManager.getRank(rankName);
			if (rank == null) {
				s.sendMessage(rankName + ChatColor.RED + " is not a rank!");
				return true;
			}

			UUID uuid;
			try {
				uuid = UUIDFetcher.getUUIDOf(pName);
			} catch (Exception e) {
				s.sendMessage(ChatColor.RED + "That player does not exist, or the mojang API is down!");
				return true;
			}

			if (!rank.getMembers().contains(uuid.toString())) {
				s.sendMessage(ChatColor.RED + pName + " is not a member of that rank!");
				return true;
			}

			rank.removeMember(uuid, pName);

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

			Rank rank = RanksManager.getRank(rankName);
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

			Rank rank = RanksManager.getRank(rankName);
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

		else if (args[0].equalsIgnoreCase("setfriendlyfire")) {
			if (!s.hasPermission("simpleranks.setfriendlyfire")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr setfriendlyfire [rank] [true/false]");
				return true;
			}

			String booleanString = args[2];
			if (!isBoolean(booleanString)) {
				s.sendMessage(ChatColor.RED + booleanString + " is not equal to true or false!");
				return true;
			}
			String rankName = chatColor(args[1]);
			Rank rank = RanksManager.getRank(rankName);
			if (rank == null) {
				s.sendMessage(ChatColor.RED + rankName + " is not a rank!");
				return true;
			}

			rank.getTeam().setAllowFriendlyFire(Boolean.parseBoolean(booleanString));
			s.sendMessage(ChatColor.GREEN + "You set the friendlyfire of the rank " + ChatColor.RESET + rank.getName()
					+ ChatColor.GREEN + " equal to " + ChatColor.YELLOW + booleanString);
		}

		else if (args[0].equalsIgnoreCase("loadpermsfromfile")) {
			if (!s.hasPermission("simpleranks.loadpermsfromfile")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 3) {
				s.sendMessage(ChatColor.RED + "Usage: /sr loadpermsfromfile [rank] [file path]");
				return true;
			}

			String rankName = chatColor(args[1]);
			Rank rank = RanksManager.getRank(rankName);
			if (rank == null) {
				s.sendMessage(ChatColor.RED + rankName + " is not a rank!");
				return true;
			}

			String filePath = args[2];
			File file = new File(filePath);
			if (!file.exists()) {
				s.sendMessage(ChatColor.RED + "That file does not exist!");
				return true;
			}
			if (!file.isFile()) {
				s.sendMessage(ChatColor.RED + "That is a directory and not a file!");
				return true;
			}
			if (!file.canRead()) {
				s.sendMessage(ChatColor.RED + "That file couldn't be read!");
				return true;
			}

			try {
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				List<String> tempPerms = new ArrayList<>();
				for (String line; (line = br.readLine()) != null;) {
					if (rank.getPermissions().contains(line))
						continue;
					tempPerms.add(line);
				}
				rank.addPermission((String[]) tempPerms.toArray());
				in.close();
				br.close();
			} catch (IOException e) {
				s.sendMessage(ChatColor.RED
						+ "Something went wrong while loading the permissions from the file! See the console for more info!");
				e.printStackTrace();
				return true;
			}

			s.sendMessage(ChatColor.GREEN + "You added all permissions from the file to the rank " + ChatColor.RESET
					+ rank.getName() + ChatColor.GREEN + "! Now you can delete the file, it's not longer needed!");
		}

		else if (args[0].equalsIgnoreCase("permlist")) {
			if (!s.hasPermission("simpleranks.permlist")) {
				s.sendMessage(ChatColor.DARK_RED + "You don't have permission to do this!");
				return true;
			}
			if (args.length != 2) {
				s.sendMessage(ChatColor.RED + "Usage: /sr permlist [rank]");
				return true;
			}

			String rankName = chatColor(args[1]);
			Rank rank = RanksManager.getRank(rankName);
			if (rank == null) {
				s.sendMessage(ChatColor.RED + rankName + " is not a rank!");
				return true;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.GOLD + "\nThe permissions(" + rank.getPermissions().size() + ") of the rank "
					+ ChatColor.RESET + rank.getName() + ChatColor.GOLD + " are:");
			for (String perm : rank.getPermissions()) {
				sb.append(ChatColor.YELLOW + perm + "\n");
			}
			s.sendMessage(sb.toString());
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
		sb.append(ChatColor.GOLD + "/sr permlist [rank] " + line + " See all permissions of a rank!\n");
		sb.append(ChatColor.GOLD + "/sr addplayer [player] [rank] " + line + " Add a player to a rank!\n");
		sb.append(ChatColor.GOLD + "/sr removeplayer [player] [rank] " + line + " Remove a player from a rank!\n");
		sb.append(ChatColor.GOLD + "/sr addperm [perm] [rank] " + line + " Add a permssion to a rank!\n");
		sb.append(ChatColor.GOLD + "/sr removeperm [perm] [rank] " + line + " Remove a permission form a rank!\n");
		sb.append(ChatColor.GOLD + "/sr setfriendlyfire [rank] [true/false] " + line
				+ " Set friendly fire for a rank to true or false!\n");
		sb.append(ChatColor.GOLD + "/sr loadpermsfromfile [rank] [file path] " + line
				+ " Load permissions from a txt file into a rank!\n");
		sb.append(ChatColor.GOLD + "/sr help " + line + " Shows this message\n\n");

		s.sendMessage(sb.toString());
	}

	private String chatColor(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	private boolean isBoolean(String str) {
		if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false"))
			return false;
		return true;
	}

}
