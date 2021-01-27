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
package com.github.phantompowered.proxy.account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.util.UUIDTypeAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachedUserAuthentication implements UserAuthentication {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer())
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer())
            .create();

    private final String sessionToken;
    private final String selectedProfileJson;
    private transient GameProfile selectedProfile;

    public CachedUserAuthentication(String sessionToken, GameProfile selectedProfile) {
        this.sessionToken = sessionToken;
        this.selectedProfileJson = GSON.toJson(selectedProfile);
        this.selectedProfile = selectedProfile;
    }

    public static CachedUserAuthentication fromAuthentication(UserAuthentication authentication) {
        if (authentication instanceof CachedUserAuthentication) {
            return (CachedUserAuthentication) authentication;
        }

        return new CachedUserAuthentication(authentication.getAuthenticatedToken(), authentication.getSelectedProfile());
    }

    @Override
    public boolean canLogIn() {
        return true;
    }

    @Override
    public void logIn() {
    }

    @Override
    public void logOut() {
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public boolean canPlayOnline() {
        return true;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        return new GameProfile[]{this.selectedProfile};
    }

    @Override
    public GameProfile getSelectedProfile() {
        if (this.selectedProfile == null) {
            this.selectedProfile = GSON.fromJson(this.selectedProfileJson, GameProfile.class);
        }
        return this.selectedProfile;
    }

    @Override
    public void selectGameProfile(GameProfile profile) {
    }

    @Override
    public void loadFromStorage(Map<String, Object> credentials) {
    }

    @Override
    public Map<String, Object> saveForStorage() {
        return new HashMap<>();
    }

    @Override
    public void setUsername(String username) {
    }

    @Override
    public void setPassword(String password) {
    }

    @Override
    public String getAuthenticatedToken() {
        return this.sessionToken;
    }

    @Override
    public String getUserID() {
        return null;
    }

    @Override
    public PropertyMap getUserProperties() {
        return null;
    }

    @Override
    public UserType getUserType() {
        return null;
    }
}
