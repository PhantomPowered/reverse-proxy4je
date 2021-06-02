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
package com.github.phantompowered.proxy.command.defaults;

import com.github.phantompowered.proxy.api.APIUtil;
import com.github.phantompowered.proxy.api.command.CommandMap;
import com.github.phantompowered.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.phantompowered.proxy.api.command.exception.CommandExecutionException;
import com.github.phantompowered.proxy.api.command.result.CommandResult;
import com.github.phantompowered.proxy.api.command.sender.CommandSender;
import com.github.phantompowered.proxy.api.connection.ServiceConnectResult;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.connection.ServiceConnector;
import com.github.phantompowered.proxy.api.network.NetworkAddress;
import com.github.phantompowered.proxy.api.player.Player;
import com.github.phantompowered.proxy.api.service.ServiceRegistry;
import com.github.phantompowered.proxy.api.session.MCServiceCredentials;
import com.github.phantompowered.proxy.auth.TheAlteningAuthentication;
import com.github.phantompowered.proxy.auth.service.AlteningServiceType;
import com.github.phantompowered.proxy.storage.MCServiceCredentialsStorage;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class CommandAccount extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;
    private final TheAlteningAuthentication authentication = TheAlteningAuthentication.mojang();

    public CommandAccount(ServiceRegistry registry) {
        super("proxy.command.help", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        ServiceConnector connector = this.registry.getProviderUnchecked(ServiceConnector.class);
        MCServiceCredentialsStorage storage = this.registry.getProviderUnchecked(MCServiceCredentialsStorage.class);

        if ((args.length == 4 || args.length == 5) && args[0].equalsIgnoreCase("add")) {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            final boolean exportable = Boolean.parseBoolean(args[args.length - 1]);

            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            if (storage.get(args[2]) != null) {
                sender.sendMessage("§cThat account is already registered");
                return CommandResult.BREAK;
            }

            sender.sendMessage("§aImporting account " + args[2] + "...");

            String password = args.length == 5 ? args[3] : null;

            MCServiceCredentials credentials = password == null
                    ? MCServiceCredentials.offline(args[2], address.asString(), exportable)
                    : MCServiceCredentials.online(args[2], password, address.asString(), exportable);

            storage.insert(args[2], credentials);
            this.connect(connector, credentials, address, sender);

            sender.sendMessage("§aSuccessfully imported account " + args[2]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("close")) {

            this.closeAll(connector, sender, this.emailOrName(args[1]));

        } else if (args.length == 2 && args[0].equalsIgnoreCase("start")) {

            for (MCServiceCredentials credentials : storage.getAll()) {
                if ((credentials.getEmail() != null && credentials.getEmail().equalsIgnoreCase(args[1]))
                        || (credentials.getUsername() != null && credentials.getUsername().equalsIgnoreCase(args[1]))) {
                    sender.sendMessage("§7Starting client §e" + credentials + "§7...");
                    if (credentials.getDefaultServer() == null) {
                        sender.sendMessage("§cFailed! No default server specified for this client");
                        continue;
                    }
                    this.connect(connector, credentials, NetworkAddress.parse(credentials.getDefaultServer()), sender);
                    sender.sendMessage("§aDone");
                }
            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("closeAll")) {
            NetworkAddress address = NetworkAddress.parse(args[1]);
            if (address == null) {
                sender.sendMessage("§cInvalid address");
                return CommandResult.BREAK;
            }

            this.closeAll(connector, sender, client -> client.getServerAddress().equals(address));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            storage.delete(args[1]);
            connector.getClientByEmail(args[1]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting online client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });
            connector.getClientByName(args[1]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting offline client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });
            sender.sendMessage("§cDeleted the account " + args[1]);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("export")) {
            sender.sendMessage("§aString account export...");

            try {
                String exportFile = "account-export-" + System.nanoTime() + ".txt";
                Path path = Paths.get(exportFile);
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }

                for (MCServiceCredentials credentials : storage.getAll()) {
                    if (credentials.isExportable()) {
                        sender.sendMessage("Exporting account " + credentials.getEmail() + "@" + credentials.getDefaultServer() + "...");
                        Files.write(
                                path,
                                (credentials.getEmail() + ":" + credentials.getPassword() + "->" + credentials.getDefaultServer()).getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.APPEND
                        );
                    }
                }

                sender.sendMessage("Export results saved to " + exportFile);
            } catch (IOException exception) {
                sender.sendMessage("§cUnable to save accounts");
                exception.printStackTrace();
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            Path path = Paths.get(args[1]);
            if (Files.notExists(path)) {
                sender.sendMessage("§cThe file does not exists");
                return CommandResult.BREAK;
            }

            try {
                List<String> strings = Files.readAllLines(path, StandardCharsets.UTF_8);

                int lines = 0;
                for (String string : strings) {
                    ++lines;

                    String[] credentialsSplit = string.split(":");
                    if (credentialsSplit.length != 2) {
                        sender.sendMessage("§cInvalid line " + lines);
                        continue;
                    }

                    String[] hostSplit = credentialsSplit[1].split("->");
                    if (hostSplit.length != 2) {
                        sender.sendMessage("§cInvalid line " + lines);
                        continue;
                    }

                    String email = credentialsSplit[0];
                    String password = hostSplit[0];
                    String defaultServer = hostSplit[1];

                    if (storage.get(email) != null) {
                        sender.sendMessage("§cThat account with the email " + email + " is already registered");
                        continue;
                    }

                    NetworkAddress address = NetworkAddress.parse(defaultServer);
                    if (address == null) {
                        sender.sendMessage("§cUnable to parse network address from string: \"" + defaultServer + "\"");
                        continue;
                    }

                    MCServiceCredentials credentials = MCServiceCredentials.online(email, password, defaultServer, true);
                    storage.insert(email, credentials);

                    sender.sendMessage("§aImported " + credentials.getEmail() + " successfully, trying to connect");
                    this.connect(connector, credentials, address, sender);
                }
            } catch (IOException exception) {
                sender.sendMessage("Unable to import file");
                exception.printStackTrace();
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("setpassword")) {
            MCServiceCredentials credentials = storage.get(args[0]);
            if (credentials == null) {
                sender.sendMessage("§cThe account " + args[0] + " is unknown");
                return CommandResult.BREAK;
            }

            if (credentials.isOffline()) {
                return CommandResult.BREAK;
            }

            if (credentials.getPassword().equals(args[2])) {
                sender.sendMessage("§cThe old and the new password are identical");
                return CommandResult.BREAK;
            }

            credentials.setPassword(args[2]);
            storage.update(args[0], credentials);

            connector.getClientByEmail(args[0]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting online client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });

            sender.sendMessage("Trying to connect to the default server using the new credentials...");
            this.connect(connector, credentials, NetworkAddress.parse(credentials.getDefaultServer()), sender);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("setdefaultserver")) {
            MCServiceCredentials credentials = storage.get(args[0]);
            if (credentials == null) {
                sender.sendMessage("§cThe account " + args[0] + " is unknown");
                return CommandResult.BREAK;
            }

            if (credentials.getDefaultServer().equals(args[2])) {
                sender.sendMessage("§cThe old and the new server ip are identical");
                return CommandResult.BREAK;
            }

            NetworkAddress address = NetworkAddress.parse(args[2]);
            if (address == null) {
                sender.sendMessage("§cUnable to parse the new default server ip");
                return CommandResult.BREAK;
            }

            credentials.setDefaultServer(args[2]);
            storage.update(args[0], credentials);
        } else if (args.length == 3 && args[1].equalsIgnoreCase("setemail")) {
            MCServiceCredentials credentials = storage.get(args[0]);
            if (credentials == null) {
                sender.sendMessage("§cThe account " + args[0] + " is unknown");
                return CommandResult.BREAK;
            }

            if (credentials.getEmail().equals(args[2])) {
                sender.sendMessage("§cThe old and the new email is identical");
                return CommandResult.BREAK;
            }

            credentials.setEmail(args[2]);
            storage.delete(args[0]);
            storage.insert(args[2], credentials);

            connector.getClientByEmail(args[0]).ifPresent(client -> {
                sender.sendMessage("§cDisconnecting online client...");
                client.close();
                sender.sendMessage("§cDisconnected client");
            });

            sender.sendMessage("Trying to connect to the default server using the new credentials...");
            this.connect(connector, credentials, NetworkAddress.parse(credentials.getDefaultServer()), sender);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (MCServiceCredentials credentials : storage.getAll()) {
                sender.sendMessage(" -> " + credentials.getEmail() + " / " + credentials.getDefaultServer()
                        + " (" + (credentials.isExportable() ? "exportable" : "not exportable") + ")");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            Collection<MCServiceCredentials> all = storage.getAll();
            for (MCServiceCredentials credentials : all) {
                storage.delete(credentials.getEmail());
            }
            sender.sendMessage("Cleared " + all.size() + " credentials");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("mojang")) {
            this.authentication.updateService(AlteningServiceType.MOJANG);
            sender.sendMessage("Switched the login servers to mojang.");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("thealtening")) {
            this.authentication.updateService(AlteningServiceType.THEALTENING);
            sender.sendMessage("Switched the login servers to TheAltening.");
        } else {
            this.sendHelp(sender);
        }

        return CommandResult.END;
    }

    private void closeAll(ServiceConnector connector, CommandSender sender, Predicate<ServiceConnection> tester) {
        for (ServiceConnection client : connector.getOnlineClients()) {
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

    private Predicate<ServiceConnection> emailOrName(String input) {
        return client -> (client.getCredentials().getEmail() != null && client.getCredentials().getEmail().equalsIgnoreCase(input)) || input.equalsIgnoreCase(client.getName());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("acc mojang/thealtening");
        sender.sendMessage("acc add <default-address> <E-Mail|OfflineUsername> [Password] <exportable>");
        sender.sendMessage("acc <email> setEmail <new-email>");
        sender.sendMessage("acc <email> setPassword <password>");
        sender.sendMessage("acc <email> setDefaultServer <default-server>");
        sender.sendMessage("acc start <E-Mail|Username>");
        sender.sendMessage("acc close <E-Mail|Username>");
        sender.sendMessage("acc delete <e-mail>");
        sender.sendMessage("acc closeAll <address>");
        sender.sendMessage("acc export");
        sender.sendMessage("acc import <file-path>");
        sender.sendMessage("acc clear");
        sender.sendMessage("acc list");
    }

    private void connect(ServiceConnector connector, MCServiceCredentials credentials, NetworkAddress address, CommandSender sender) {
        try {
            ServiceConnectResult result = connector.createConnection(credentials, address).connect().get(5, TimeUnit.SECONDS);

            ServiceConnection client = connector.getOnlineClients().stream()
                    .filter(proxyClient -> proxyClient.getCredentials().equals(credentials))
                    .filter(proxyClient -> proxyClient.getServerAddress().equals(address))
                    .findFirst()
                    .orElse(null);
            if (client == null) {
                sender.sendMessage("§cFailed, reason unknown. Look into the console for more information");
                return;
            }

            sender.sendMessage(result.isSuccess() ? ("§aSuccessfully connected as §e" + (credentials.getEmail() != null ? credentials.getEmail() : credentials.getUsername()) + " §7(§e" + client.getName() + "#" + client.getName() + "§7) §ato §e" + address) : "§cFailed to connect to §e" + address);
            if (sender instanceof Player) {
                TextComponent component = Component.text(APIUtil.MESSAGE_PREFIX + "§aClick to connect");
                component.clickEvent(ClickEvent.runCommand(CommandMap.INGAME_PREFIX + "switch " + client.getName()));
                component.hoverEvent(HoverEvent.showText(Component.text("§7Switch to §e" + client.getName() + "#" + client.getUniqueId())));
                sender.sendMessage(component);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException exception) {
            exception.printStackTrace();
        } catch (AuthenticationException exception) {
            sender.sendMessage("§cInvalid credentials: " + exception.getMessage());
            System.out.println("Invalid credentials for " + credentials.getEmail());
        }
    }
}
