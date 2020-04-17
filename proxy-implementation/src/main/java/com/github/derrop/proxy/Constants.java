package com.github.derrop.proxy;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public interface Constants {

    String MESSAGE_PREFIX = "§8┃ §6P§froxy §8× §7";

    ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    DirectoryStream.Filter<Path> JAR_FILE_FILTER = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path path) {
            return !Files.isDirectory(path) && path.toString().endsWith(".jar");
        }
    };

}
