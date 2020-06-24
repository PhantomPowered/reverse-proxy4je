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
package com.github.derrop.proxy.plugins.gomme.clan;

import com.github.derrop.proxy.api.connection.ServiceConnection;
import com.github.derrop.proxy.api.database.DatabaseProvidedStorage;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.plugins.gomme.clan.parser.ClanId;
import com.github.derrop.proxy.plugins.gomme.clan.parser.ClanParser;

import java.util.concurrent.TimeUnit;

public class ClanInfoProvider extends DatabaseProvidedStorage<ClanInfo> {

    private static final long VALID_MILLIS = TimeUnit.HOURS.toMillis(12);

    private final ClanParser parser;

    public ClanInfoProvider(ServiceRegistry registry, ClanParser parser) {
        super(registry, "gomme_clan_info", ClanInfo.class);
        this.parser = parser;
    }

    public ClanInfo getStoredClan(String name) {
        return super.get(name);
    }

    public ClanInfo getActualClan(ServiceConnection connection, String name) {
        ClanInfo info = this.getStoredClan(name);
        if (info != null && info.getTimestamp() + VALID_MILLIS >= System.currentTimeMillis()) {
            return info;
        }
        ClanId id = ClanId.forName(name);
        if (this.parser.hasPendingRequest(connection, id)) {
            return null;
        }
        ClanInfo newInfo = this.parser.requestClanInfo(connection, id).getUninterruptedly(15, TimeUnit.SECONDS);
        if (newInfo == null) {
            return null;
        }

        if (info != null) {
            newInfo.getTags().addAll(info.getTags());
        }

        if (info != null) {
            super.update(newInfo.getName(), newInfo);
        } else {
            super.insert(newInfo.getName(), newInfo);
        }

        return newInfo;
    }

    public ClanInfo getClanByShortcut(String shortcut) {
        return super.getAll().stream().filter(clanInfo -> clanInfo.getShortcut().equals(shortcut)).findFirst().orElse(null);
    }

}
