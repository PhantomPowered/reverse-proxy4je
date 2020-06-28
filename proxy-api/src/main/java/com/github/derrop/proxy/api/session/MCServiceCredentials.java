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
package com.github.derrop.proxy.api.session;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class MCServiceCredentials {

    private String username;
    private String email;
    private byte[] password;
    private String defaultServer;
    private boolean exportable;

    public MCServiceCredentials(String username, String email, byte[] password, String defaultServer, boolean exportable) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.defaultServer = defaultServer;
        this.exportable = exportable;
    }

    public static MCServiceCredentials online(String email, String password, String defaultServer, boolean exportable) {
        byte[] passwordBytes = Base64.getEncoder().encode(password.getBytes(StandardCharsets.UTF_8));
        return new MCServiceCredentials(null, email, passwordBytes, defaultServer, exportable);
    }

    public static MCServiceCredentials offline(String username, String defaultServer, boolean exportable) {
        return new MCServiceCredentials(username, null, null, defaultServer, exportable);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return new String(Base64.getDecoder().decode(this.password), StandardCharsets.UTF_8);
    }

    public String getDefaultServer() {
        return defaultServer;
    }

    public boolean isExportable() {
        return exportable;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = Base64.getEncoder().encode(password.getBytes(StandardCharsets.UTF_8));
    }

    public void setDefaultServer(String defaultServer) {
        this.defaultServer = defaultServer;
    }

    public boolean isOffline() {
        return this.username != null;
    }

    @Override
    public String toString() {
        return this.isOffline() ? "Offline:" + this.username : "Online:" + this.email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MCServiceCredentials)) return false;
        MCServiceCredentials that = (MCServiceCredentials) o;
        return isExportable() == that.isExportable() &&
                getUsername().equals(that.getUsername()) &&
                Objects.equals(getEmail(), that.getEmail()) &&
                Objects.equals(getPassword(), that.getPassword()) &&
                getDefaultServer().equals(that.getDefaultServer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getEmail(), getDefaultServer(), isExportable(), getPassword());
    }
}
