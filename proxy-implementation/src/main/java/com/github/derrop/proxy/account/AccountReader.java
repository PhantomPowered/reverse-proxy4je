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
package com.github.derrop.proxy.account;

import com.github.derrop.proxy.api.util.MCCredentials;
import com.github.derrop.proxy.api.util.NetworkAddress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.BiConsumer;

public class AccountReader {

    /**
     * Reads the accounts line by line in a given file in the following pattern:
     * E-Mail:Password->ServerAddress:ServerPort
     * <p>
     * the port is optional, if it isn't provided, 25565 is used
     *
     * @param path            the path to the file
     * @param accountConsumer the handler for accounts and servers
     * @throws IOException           if an I/O error occurred
     * @throws FileNotFoundException if the given file doesn't exist
     */
    public void readAccounts(Path path, BiConsumer<MCCredentials, NetworkAddress> accountConsumer) throws IOException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File for reading accounts doesn't exist");
        }

        boolean skip = false;
        for (String line : Files.readAllLines(path)) {
            if (line.equals("###")) {
                skip = !skip;
                continue;
            }

            if (skip) {
                continue;
            }

            line = line.trim();
            if (line.startsWith("#") || line.isEmpty()) {
                continue;
            }
            String[] base = line.split("->");
            if (base.length != 2) {
                System.err.println("Wrong pattern in the line " + line);
                continue;
            }

            MCCredentials credentials = MCCredentials.parse(base[0]);
            NetworkAddress address = NetworkAddress.parse(base[1]);

            if (credentials == null || address == null) {
                System.err.println("Wrong credentials/address pattern in line " + line);
                continue;
            }

            accountConsumer.accept(credentials, address);
        }
    }

    public void writeDefaults(Path path) {
        try {
            Files.write(path, Collections.singletonList("E-Mail:Password->ServerAddress:25565"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
