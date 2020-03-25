package de.derrop.minecraft.proxy.logging;
/*
 * Created by Mc_Ruben on 08.02.2019
 */

import de.derrop.minecraft.proxy.logging.ConsoleColor;
import de.derrop.minecraft.proxy.logging.LogHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FileLoggerHandler implements LogHandler {

    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private long maxBytes, writtenBytes = 0;

    private String path;
    private String fileName;
    private OutputStream currentOutputStream;

    public FileLoggerHandler(String path, long maxBytesPerFile) {
        this.path = path;
        this.fileName = Paths.get(path).getFileName().toString();
        this.maxBytes = maxBytesPerFile;

        try {
            this.currentOutputStream = Files.newOutputStream(this.findNextFile(), StandardOpenOption.APPEND);

            this.appendFile(" \n START \n " + DEFAULT_DATE_FORMAT.format(new Date()) + " \n ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path findNextFile() {
        Path path = Paths.get(this.path);
        Path parent = path.getParent();
        if (parent == null)
            parent = Paths.get(".");
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.writtenBytes = 0;
            return path;
        }
        AtomicReference<Path> result = new AtomicReference<>();
        AtomicInteger count = new AtomicInteger();
        try {
            Files.walkFileTree(
                    parent,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (!file.getFileName().toString().startsWith(FileLoggerHandler.this.fileName)) {
                                return FileVisitResult.CONTINUE;
                            }

                            if (attrs.size() >= maxBytes) {
                                count.incrementAndGet();
                                return FileVisitResult.CONTINUE;
                            }

                            result.set(file);

                            return FileVisitResult.TERMINATE;
                        }
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path out = result.get();

        if (out == null) {
            out = Paths.get(this.path + "." + count);
        }

        try {
            if (!Files.exists(out)) {
                Files.createFile(out);
            }
            this.writtenBytes = Files.size(out);
        } catch (IOException e) {
            e.printStackTrace();
            this.writtenBytes = 0;
        }

        return out;
    }

    private void checkMaxBytes() {
        if (this.writtenBytes >= this.maxBytes) {
            try {
                if (this.currentOutputStream != null) {
                    this.currentOutputStream.close();
                }
                this.currentOutputStream = Files.newOutputStream(this.findNextFile(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleLine(String line, String s) {
        s = ConsoleColor.stripColor(s);
        this.appendFile(s);
    }

    @Override
    public void close() {
        this.appendFile(" \n END \n " + DEFAULT_DATE_FORMAT.format(new Date()) + " \n ");
        try {
            this.currentOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendFile(String s) {
        byte[] bytes = (s + "\n").getBytes(StandardCharsets.UTF_8);
        this.writtenBytes += bytes.length;
        try {
            this.currentOutputStream.write(bytes);
            this.currentOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.checkMaxBytes();
    }
}
