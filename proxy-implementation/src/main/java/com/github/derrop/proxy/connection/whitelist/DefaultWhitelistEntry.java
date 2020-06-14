package com.github.derrop.proxy.connection.whitelist;

import java.util.Collection;
import java.util.UUID;

public class DefaultWhitelistEntry {

    private boolean enabled;
    private Collection<UUID> whitelisted;

    public DefaultWhitelistEntry(boolean enabled, Collection<UUID> whitelisted) {
        this.enabled = enabled;
        this.whitelisted = whitelisted;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<UUID> getWhitelisted() {
        return this.whitelisted;
    }

    public void setWhitelisted(Collection<UUID> whitelisted) {
        this.whitelisted = whitelisted;
    }
}
