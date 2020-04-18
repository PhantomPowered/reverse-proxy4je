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
package com.github.derrop.proxy.api.util;

import lombok.EqualsAndHashCode;

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
