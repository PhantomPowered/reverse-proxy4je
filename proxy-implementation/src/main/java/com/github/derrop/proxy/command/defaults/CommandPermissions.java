/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
// TODO: replace this with new command

/*package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.command.Command;
import com.github.derrop.proxy.api.command.CommandSender;
import com.github.derrop.proxy.permission.PermissionEntity;

import java.util.ArrayList;
import java.util.UUID;

public class CommandPermissions extends Command {

    public CommandPermissions() {
        super("perms", "permissions", "perm", "permission");
        super.setPermission("command.permissions");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length < 1) {
            this.sendHelp(sender);
            return;
        }

        if (!args[0].equalsIgnoreCase("user")) {
            return;
        }

        UUID uniqueId = MCProxy.getInstance().getUUIDStorage().getUniqueId(args[1]);

        if (uniqueId == null) {
            sender.sendMessage("That user doesn't exist");
            return;
        }

        String name = MCProxy.getInstance().getUUIDStorage().getName(uniqueId);

        PermissionEntity entity = MCProxy.getInstance().getPermissionProvider().getEntity(uniqueId);

        if (entity == null) {
            entity = new PermissionEntity(uniqueId, new ArrayList<>());
        }


        if (args.length == 4 && args[2].equalsIgnoreCase("clear") && args[3].equalsIgnoreCase("permissions")) {
            entity.getPermissions().clear();
            MCProxy.getInstance().getPermissionProvider().updatePermissionEntity(entity);
            sender.sendMessage("The permissions of the user " + name + " have been successfully cleared!");
        } else if (args.length == 5 && args[2].equalsIgnoreCase("add") && args[3].equalsIgnoreCase("permission")) {
            String permission = args[4];
            if (entity.getPermissions().contains(permission.toLowerCase())) {
                sender.sendMessage("The user " + name + " already has this permission");
                return;
            }

            entity.getPermissions().add(permission.toLowerCase());

            MCProxy.getInstance().getPermissionProvider().updatePermissionEntity(entity);
            sender.sendMessage("The permission has been successfully added to the user " + name + "!");
        } else if (args.length == 5 && args[2].equalsIgnoreCase("remove") && args[3].equalsIgnoreCase("permission")) {
            String permission = args[4];
            if (!entity.getPermissions().contains(permission.toLowerCase())) {
                sender.sendMessage("The user " + name + " doesn't have this permission");
                return;
            }

            entity.getPermissions().remove(permission.toLowerCase());

            MCProxy.getInstance().getPermissionProvider().updatePermissionEntity(entity);
            sender.sendMessage("The permission has been successfully removed from the user " + name + "!");
        } else if (args.length == 2) {
            sender.sendMessage(name + "#" + uniqueId + ":");
            sender.sendMessage("Permissions:");
            for (String permission : entity.getPermissions()) {
                sender.sendMessage("- " + permission);
            }
        } else {
            this.sendHelp(sender);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("perms user <name> add permission <permission>");
        sender.sendMessage("perms user <name> remove permission <permission>");
        sender.sendMessage("perms user <name> clear permissions");
        sender.sendMessage("perms user <name>");
    }

}

 */
