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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class PermissionsManager {

	private static PermissionsManager instance = new PermissionsManager();

	private Map<UUID, PermissionAttachment> attachments;

	public void loadPermissions(Player p) {
		PermissionAttachment attachment;
		if (attachments.containsKey(p.getUniqueId()))
			attachment = attachments.get(p.getUniqueId());
		else {
			attachment = p.addAttachment(Main.getInstance());
			attachments.put(p.getUniqueId(), attachment);
		}

		for (String perm : attachment.getPermissions().keySet()) {
			attachment.setPermission(perm, false);
		}

		Rank rank = RanksManager.getInstance().getRank(p.getUniqueId());
		if (rank == null)
			return;
		for (String perm : rank.getPermissions()) {
			attachment.setPermission(perm, true);
		}
	}

	private PermissionsManager() {
		attachments = new HashMap<>();
	}

	public Map<UUID, PermissionAttachment> getAttachments() {
		return attachments;
	}

	public static PermissionsManager getInstance() {
		return instance;
	}

}
