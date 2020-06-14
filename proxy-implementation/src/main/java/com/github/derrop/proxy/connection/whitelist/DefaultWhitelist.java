package com.github.derrop.proxy.connection.whitelist;

import com.github.derrop.proxy.api.connection.Whitelist;
import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class DefaultWhitelist extends DatabaseProvidedStorage<DefaultWhitelistEntry> implements Whitelist {

    private static final String KEY = "WhitelistKey";

    public DefaultWhitelist(ServiceRegistry registry) {
        super(registry, "default_proxy_whitelist", DefaultWhitelistEntry.class);
    }

    private DefaultWhitelistEntry getEntry() {
        DefaultWhitelistEntry entry = super.get(KEY);
        if (entry == null) {
            entry = new DefaultWhitelistEntry(false, new ArrayList<>());
            super.insert(KEY, entry);
        }
        return entry;
    }

    private void updateEntry(DefaultWhitelistEntry entry) {
        super.update(KEY, entry);
    }

    @Override
    public boolean isEnabled() {
        return this.getEntry().isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        DefaultWhitelistEntry entry = this.getEntry();
        if (entry.isEnabled() != enabled) {
            entry.setEnabled(enabled);
            this.updateEntry(entry);
        }
    }

    @Override
    public boolean isWhitelisted(@NotNull UUID uniqueId) {
        return this.getEntries().contains(uniqueId);
    }

    @Override
    public void addEntry(@NotNull UUID uniqueId) {
        DefaultWhitelistEntry entry = this.getEntry();
        if (!entry.getWhitelisted().contains(uniqueId)) {
            entry.getWhitelisted().add(uniqueId);
            this.updateEntry(entry);
        }
    }

    @Override
    public void removeEntry(@NotNull UUID uniqueId) {
        DefaultWhitelistEntry entry = this.getEntry();
        if (entry.getWhitelisted().contains(uniqueId)) {
            entry.getWhitelisted().remove(uniqueId);
            this.updateEntry(entry);
        }
    }

    @Override
    public @NotNull Collection<UUID> getEntries() {
        return this.getEntry().getWhitelisted();
    }

    @Override
    public void clear() {
        DefaultWhitelistEntry entry = this.getEntry();
        if (!entry.getWhitelisted().isEmpty()) {
            entry.getWhitelisted().clear();
            this.updateEntry(entry);
        }
    }

    @Override
    public long size() {
        return this.getEntries().size();
    }
}
