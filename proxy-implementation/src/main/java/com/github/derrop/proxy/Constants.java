package com.github.derrop.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public interface Constants {

    String MESSAGE_PREFIX = "§8┃ §6P§froxy §8× §7";

    ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

}
