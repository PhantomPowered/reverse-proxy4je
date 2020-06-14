package com.github.derrop.proxy.plugins.gomme.match.messages.defaults.game;

import com.github.derrop.proxy.plugins.gomme.GommeGameMode;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ReflectiveGameMessageRegistrar extends SpecificGameMessageRegistrar {

    private final String packageName;

    public ReflectiveGameMessageRegistrar(String packageName, GommeGameMode gameMode, GameMessageRegistry registry, TeamRegistry teamRegistry) {
        super(gameMode, registry, teamRegistry);
        this.packageName = packageName;
    }

    @Override
    public void init() {
        try {
            for (ClassPath.ClassInfo classInfo : ClassPath.from(super.getClass().getClassLoader()).getTopLevelClasses(this.packageName)) {
                Class<?> clazz = classInfo.load();
                if (SingleGameMessageRegistrar.class.isAssignableFrom(clazz)) {
                    SingleGameMessageRegistrar registrar = (SingleGameMessageRegistrar) clazz.getDeclaredConstructor().newInstance();
                    registrar.register(this);
                }
            }
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }
}
