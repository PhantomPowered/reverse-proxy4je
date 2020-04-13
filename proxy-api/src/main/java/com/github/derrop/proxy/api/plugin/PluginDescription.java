package com.github.derrop.proxy.api.plugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public class PluginDescription {

    private String name;
    private String version;
    private String[] authors;
    private String main;
    private String description;
    private transient Path path;
    private transient URLClassLoader classLoader;

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String[] getAuthors() {
        return this.authors;
    }

    public String getMain() {
        return this.main;
    }

    public String getDescription() {
        return this.description;
    }

    public Path getPath() {
        return this.path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public URLClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
