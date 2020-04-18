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
package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.chat.ClickEvent;
import com.github.derrop.proxy.api.chat.HoverEvent;
import com.github.derrop.proxy.api.chat.component.TextComponent;
import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.entity.player.Player;
import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;
import com.mojang.authlib.exceptions.AuthenticationException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class CommandAccount extends NonTabCompleteableCommandCallback {

    public CommandAccount() {
        super("proxy.command.help", null);
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            MCCredentials credentials = MCCredentials.parse(args[2]);
            NetworkAddress address = NetworkAddress.parse(args[1]);

            if (credentials == null || address == null) {
                sender.sendMessage("§cInvalid credentials or address");
                return CommandResult.BREAK;
            }

            if (MCProxy.getInstance().getClientByEmail(credentials.getEmail()).isPresent()) {
                sender.sendMessage("§cThat account is already registered");
                return CommandResult.BREAK;
            }

            try {
                boolean success = MCProxy.getInstance().createConnection(credentials, address).connect().get(5, TimeUnit.SECONDS);

                ServiceConnection client = MCProxy.getInstance().getOnlineClients().stream()
                        .filter(proxyClient -> proxyClient.getCredentials().equals(credentials))
                        .filter(proxyClient -> proxyClient.getServerAddress().equals(address))
                        .findFirst()
                        .orElse(null);
                if (client == null) {
                    sender.sendMessage("§cFailed, reason unknown. Look into the console for more information");
                    return CommandResult.BREAK;
                }

                sender.sendMessage(success ? ("§aSuccessfully connected as §e" + credentials.getEmail() + " §7(§e" + client.getName() + "#" + client.getName() + "§7) §ato §e" + address) : "§cFailed to connect to §e" + address);
                if (sender instanceof Player) {
                    TextComponent component = new TextComponent(Constants.MESSAGE_PREFIX + "§aClick to connect");
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/switch " + client.getName()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Switch to §e" + client.getName() + "#" + client.getUniqueId())));
                    sender.sendMessage(component);
                }
            } catch (ExecutionException | InterruptedException | TimeoutException exception) {
                exception.printStackTrace();
            } catch (AuthenticationException exception) {
                sender.sendMessage("§cInvalid credentials");
                System.out.println("Invalid credentials for " + credentials.getEmail());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("close")) {
            this.closeAll(sender, client -> (client.getCredentials().getEmail() != null && client.getCredentials().getEmail().equalsIgnoreCase(args[1])) ||
                    args[1].equalsIgnoreCase(client.getName()));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("closeAll")) {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            this.closeAll(sender, client -> client.getServerAddress().equals(address));
        } else {
            this.sendHelp(sender);
        }

        return CommandResult.END;
    }

    private void closeAll(CommandSender sender, Predicate<ServiceConnection> tester) {
        for (ServiceConnection client : MCProxy.getInstance().getOnlineClients()) {
            if (tester.test(client)) {
                sender.sendMessage("§7Closing client §e" + client.getName() + "#" + client.getUniqueId() + " §7(§e" + client.getCredentials().getEmail() + "§7)...");
                try {
                    client.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                sender.sendMessage("§aDone");
            }
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("acc add <address> <E-Mail:Password>");
        sender.sendMessage("acc close <E-Mail|Username>");
        sender.sendMessage("acc closeAll <address>");
    }
}
