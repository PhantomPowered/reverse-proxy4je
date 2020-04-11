package de.derrop.minecraft.proxy.minecraft;

import de.derrop.minecraft.proxy.util.NetworkAddress;

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
