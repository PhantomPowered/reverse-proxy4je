package de.derrop.minecraft.proxy.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.derrop.minecraft.proxy.api.Proxy;
import de.derrop.minecraft.proxy.api.plugin.Plugin;
import de.derrop.minecraft.proxy.api.plugin.PluginDescription;
import de.derrop.minecraft.proxy.api.plugin.PluginManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultPluginManager implements PluginManager {

    private final Gson gson = new Gson();
    private final Proxy proxy;

    private Map<String, Plugin> enabledPlugins = new HashMap<>();
    private Collection<PluginDescription> loadedPlugins = new ArrayList<>();

    public DefaultPluginManager(Proxy proxy) {
        this.proxy = proxy;
    }

    private PluginDescription loadDescription(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return this.gson.fromJson(reader, PluginDescription.class);
        }
    }

    @Override
    public void loadPlugins(Path directory) {
        System.out.println("Loading plugins in " + directory + "...");

        try {
            Files.createDirectories(directory);

            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!file.getFileName().toString().endsWith(".jar")) {
                        return FileVisitResult.CONTINUE;
                    }

                    PluginDescription description = null;

                    try (InputStream inputStream = Files.newInputStream(file);
                         ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
                        ZipEntry entry;
                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            if (entry.getName().equals("plugin.json")) {
                                try {
                                    description = loadDescription(zipInputStream);
                                    break;
                                } catch (JsonSyntaxException exception) {
                                    throw new InvalidPluginDescriptionException("Invalid plugin.json in " + file);
                                }
                            }
                        }
                    }

                    if (description == null) {
                        throw new InvalidPluginDescriptionException("No plugin.json found in " + file);
                    }
                    if (description.getName() == null) {
                        throw new InvalidPluginDescriptionException("No name given in the plugin.json of " + file);
                    }
                    if (description.getVersion() == null) {
                        throw new InvalidPluginDescriptionException("No version given in the plugin.json of " + file);
                    }
                    if (description.getMain() == null) {
                        throw new InvalidPluginDescriptionException("No main given in the plugin.json of " + file);
                    }

                    description.setPath(file);



                    loadedPlugins.add(description);

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        System.out.println("Successfully loaded " + this.loadedPlugins.size() + " plugins out of " + directory);
    }

    @Override
    public void enablePlugins() {
        System.out.println("Enabling " + this.loadedPlugins.size() + " plugins...");
        for (PluginDescription description : this.loadedPlugins) {
            System.out.println("Enabling plugin " + description.getName() + " " + description.getVersion() + " by " + String.join(", ", description.getAuthors()) + "...");
            URLClassLoader classLoader = null;
            try {
                classLoader = new FinalizeURLClassLoader(description.getPath().toUri().toURL());

                description.setClassLoader(classLoader);

                Class<?> mainClass = classLoader.loadClass(description.getMain());

                if (!Plugin.class.isAssignableFrom(mainClass)) {
                    throw new InvalidPluginDescriptionException("Main class must be an instance of Plugin");
                }

                Plugin plugin = (Plugin) mainClass.getConstructor().newInstance();
                plugin.set(this.proxy, description);

                try {
                    plugin.onEnable();

                    this.enabledPlugins.put(description.getName(), plugin);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
                try {
                    classLoader.close();
                } catch (IOException exception1) {
                    exception.printStackTrace();
                }
            } catch (MalformedURLException | ReflectiveOperationException exception) {
                exception.printStackTrace();
            }
        }
        System.out.println("Successfully enabled " + this.enabledPlugins.size() + " plugins");
        this.loadedPlugins.clear();
    }

    @Override
    public void disablePlugins() {
        System.out.println("Disabling " + this.enabledPlugins.size() + " plugins...");
        for (Plugin plugin : this.enabledPlugins.values()) {
            System.out.println("Disabling plugin " + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion() + " by " + String.join(", ", plugin.getDescription().getAuthors()) + "...");
            try {
                plugin.onDisable();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        System.out.println("Successfully disabled " + this.enabledPlugins.size() + " plugins");
        this.enabledPlugins.clear();
    }

    @Override
    public Plugin getEnabledPlugin(String name) {
        return this.enabledPlugins.get(name);
    }

    @Override
    public Collection<Plugin> getEnabledPlugins() {
        return this.enabledPlugins.values();
    }

    @Override
    public boolean isPluginLoaded(String name) {
        return this.isPluginEnabled(name) || this.loadedPlugins.stream().anyMatch(description -> description.getName().equals(name));
    }

    @Override
    public boolean isPluginEnabled(String name) {
        return this.enabledPlugins.containsKey(name);
    }
}
