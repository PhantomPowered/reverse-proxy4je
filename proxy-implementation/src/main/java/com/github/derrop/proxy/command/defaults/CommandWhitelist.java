package com.github.derrop.proxy.command.defaults;

import com.github.derrop.proxy.api.command.basic.NonTabCompleteableCommandCallback;
import com.github.derrop.proxy.api.command.exception.CommandExecutionException;
import com.github.derrop.proxy.api.command.result.CommandResult;
import com.github.derrop.proxy.api.command.sender.CommandSender;
import com.github.derrop.proxy.api.connection.Whitelist;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.api.util.player.PlayerId;
import com.github.derrop.proxy.api.util.player.PlayerIdRepository;
import com.mojang.util.UUIDTypeAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class CommandWhitelist extends NonTabCompleteableCommandCallback {

    private final ServiceRegistry registry;

    public CommandWhitelist(ServiceRegistry registry) {
        super("command.whitelist", null);
        this.registry = registry;
    }

    @Override
    public @NotNull CommandResult process(@NotNull CommandSender sender, @NotNull String[] args, @NotNull String fullLine) throws CommandExecutionException {
        Optional<Whitelist> optionalWhitelist = this.registry.getProvider(Whitelist.class);
        if (!optionalWhitelist.isPresent()) {
            sender.sendMessage("The provider for the whitelist has been removed by a plugin");
            return CommandResult.BREAK;
        }
        Whitelist whitelist = optionalWhitelist.get();

        PlayerIdRepository playerIdRepository = this.registry.getProviderUnchecked(PlayerIdRepository.class);


        if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {
            if (whitelist.isEnabled()) {
                sender.sendMessage("§cThe whitelist is already enabled");
                return CommandResult.BREAK;
            }
            whitelist.setEnabled(true);
            sender.sendMessage("§aThe whitelist has been successfully enabled");
            return CommandResult.SUCCESS;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("disable")) {
            if (!whitelist.isEnabled()) {
                sender.sendMessage("§cThe whitelist is not enabled");
                return CommandResult.BREAK;
            }
            whitelist.setEnabled(false);
            sender.sendMessage("§aThe whitelist has been successfully disabled");
            return CommandResult.SUCCESS;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

            Collection<UUID> entries = whitelist.getEntries();
            sender.sendMessage("The whitelist is " + (whitelist.isEnabled() ? "§aenabled" : "§cdisabled"));
            if (entries.isEmpty()) {
                sender.sendMessage("§cThe whitelist has no entries");
                return CommandResult.SUCCESS;
            }

            sender.sendMessage("§aEntries:");
            for (UUID entry : entries) {
                PlayerId id = playerIdRepository.getPlayerId(entry);
                sender.sendMessage("§7- " + id);
            }
            return CommandResult.SUCCESS;

        } else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {

            String input = args[1];
            PlayerId id;

            try {
                if (input.length() <= 16) {
                    id = playerIdRepository.getPlayerId(input);
                } else if (input.contains("-")) {
                    id = playerIdRepository.getPlayerId(UUID.fromString(input));
                } else {
                    id = playerIdRepository.getPlayerId(UUIDTypeAdapter.fromString(input));
                }
            } catch (IllegalArgumentException exception) {
                sender.sendMessage("§cThis UUID is not valid");
                return CommandResult.BREAK;
            }

            if (id == null) {
                sender.sendMessage("§cNo player with that name/UUID found");
                return CommandResult.BREAK;
            }

            boolean add = args[0].equalsIgnoreCase("add");

            if (add) {
                if (whitelist.isWhitelisted(id.getUniqueId())) {
                    sender.sendMessage("§cThis player is already whitelisted");
                    return CommandResult.BREAK;
                }

                whitelist.addEntry(id.getUniqueId());
                sender.sendMessage("§aThe entry " + id + " has been successfully added to the whitelist");
            } else {
                if (!whitelist.isWhitelisted(id.getUniqueId())) {
                    sender.sendMessage("§cThis player is not whitelisted");
                    return CommandResult.BREAK;
                }

                whitelist.removeEntry(id.getUniqueId());
                sender.sendMessage("§aThe entry " + id + " has been successfully removed from the whitelist");
            }

            return CommandResult.SUCCESS;

        } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {

            long size = whitelist.size();
            if (size == 0) {
                sender.sendMessage("§cThe whitelist is already empty");
                return CommandResult.BREAK;
            }

            whitelist.clear();
            sender.sendMessage("§aThe whitelist has been successfully cleared");

        } else {
            this.sendUsage(sender);
        }

        return CommandResult.END;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("whitelist enable");
        sender.sendMessage("whitelist disable");
        sender.sendMessage("whitelist list");
        sender.sendMessage("whitelist add <UUID|Name>");
        sender.sendMessage("whitelist remove <UUID|Name>");
        sender.sendMessage("whitelist clear");
    }

}
