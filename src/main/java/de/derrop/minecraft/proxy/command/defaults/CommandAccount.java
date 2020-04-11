package de.derrop.minecraft.proxy.command.defaults;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import de.derrop.minecraft.proxy.Constants;
import de.derrop.minecraft.proxy.MCProxy;
import de.derrop.minecraft.proxy.command.Command;
import de.derrop.minecraft.proxy.command.CommandMap;
import de.derrop.minecraft.proxy.command.CommandSender;
import de.derrop.minecraft.proxy.connection.ConnectedProxyClient;
import de.derrop.minecraft.proxy.minecraft.MCCredentials;
import de.derrop.minecraft.proxy.util.NetworkAddress;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.connection.ProxiedPlayer;

import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class CommandAccount extends Command {

    public CommandAccount() {
        super("account", "acc");
        super.setPermission("command.account");
    }

    @Override
    public void execute(CommandSender sender, String input, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            MCCredentials credentials = MCCredentials.parse(args[2]);
            NetworkAddress address = NetworkAddress.parse(args[1]);

            if (credentials == null || address == null) {
                sender.sendMessage("§cInvalid credentials or address");
                return;
            }

            if (MCProxy.getInstance().getClientByEmail(credentials.getEmail()).isPresent()) {
                sender.sendMessage("§cThat account is already registered");
                return;
            }

            try {
                boolean success = MCProxy.getInstance().startClient(address, credentials);

                ConnectedProxyClient client = MCProxy.getInstance().getOnlineClients().stream()
                        .filter(proxyClient -> proxyClient.getCredentials().equals(credentials))
                        .filter(proxyClient -> proxyClient.getAddress().equals(address))
                        .findFirst()
                        .orElse(null);

                if (client == null) {
                    sender.sendMessage("§cFailed, reason unknown. Look into the console for more information");
                    return;
                }
                sender.sendMessage(success ? ("§aSuccessfully connected as §e" + credentials.getEmail() + " §7(§e" + client.getAccountName() + "#" + client.getAccountUUID() + "§7) §ato §e" + address) : "§cFailed to connect to §e" + address);
                if (sender instanceof ProxiedPlayer) {
                    TextComponent component = new TextComponent(Constants.MESSAGE_PREFIX + "§aClick to connect");
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandMap.PREFIX + "switch " + client.getAccountName()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Switch to §e" + client.getAccountName() + "#" + client.getAccountUUID())));
                    ((ProxiedPlayer) sender).sendMessage(ChatMessageType.CHAT, component);
                }
            } catch (ExecutionException | InterruptedException exception) {
                exception.printStackTrace();
            } catch (AuthenticationException exception) {
                sender.sendMessage("§cInvalid credentials");
                System.out.println("Invalid credentials for " + credentials.getEmail());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("close")) {
            this.closeAll(sender, client -> (client.getCredentials().getEmail() != null && client.getCredentials().getEmail().equalsIgnoreCase(args[1])) ||
                    client.getAccountName().equalsIgnoreCase(args[1]));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("closeAll")) {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return;
            }

            this.closeAll(sender, client -> client.getAddress().equals(address));
        } else {
            this.sendHelp(sender);
        }
    }

    private void closeAll(CommandSender sender, Predicate<ConnectedProxyClient> tester) {
        for (ConnectedProxyClient client : MCProxy.getInstance().getOnlineClients()) {
            if (tester.test(client)) {
                sender.sendMessage("§7Closing client §e" + client.getAccountName() + "#" + client.getAccountUUID() + " §7(§e" + client.getCredentials().getEmail() + "§7)...");
                client.disconnect();
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
