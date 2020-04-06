package de.derrop.minecraft.proxy.minecraft;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Arrays;

@EqualsAndHashCode
public class MCCredentials {

    private String username;
    private String email;
    private String password;

    public MCCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public MCCredentials(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOffline() {
        return this.username != null;
    }

    @Override
    public String toString() {
        return this.isOffline() ? "Offline:" + this.username : "Online:" + this.email;
    }

    public static MCCredentials parse(String line) {
        String[] split = line.split(":");
        if (split.length == 1) { // Only the Name -> OfflineMode
            return new MCCredentials(split[0]);
        }
        if (split.length == 3) { // Name:E-Mail:Password
            split = Arrays.copyOfRange(split, 1, split.length);
        }
        if (split.length != 2) { // E-Mail:Password
            return null;
        }
        if (!split[0].contains("@")) {
            return null;
        }
        return new MCCredentials(split[0], split[1]);
    }

}
