package com.github.derrop.proxy.plugin;

import com.github.derrop.proxy.Constants;
import com.github.derrop.proxy.MCProxy;
import com.github.derrop.proxy.api.Proxy;
import com.github.derrop.proxy.api.plugin.PluginContainer;
import com.github.derrop.proxy.api.plugin.PluginManager;
import com.github.derrop.proxy.api.plugin.PluginState;
import com.github.derrop.proxy.api.plugin.annotation.Dependency;
import com.github.derrop.proxy.api.plugin.annotation.Inject;
import com.github.derrop.proxy.api.plugin.annotation.Plugin;
import com.github.derrop.proxy.api.plugin.exceptions.PluginMainClassNotDefinedException;
import com.github.derrop.proxy.api.service.ServiceRegistry;
import com.github.derrop.proxy.util.Duo;
import com.github.derrop.proxy.util.io.IOUtils;
import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class DefaultPluginManager implements PluginManager {

    private final List<PluginContainerEntry> pluginContainers = new CopyOnWriteArrayList<>();

    private final Collection<Path> toLoad = new CopyOnWriteArrayList<>();

    public DefaultPluginManager(Path pluginsDirectory) {
        this.pluginsDirectory = pluginsDirectory;
        IOUtils.createDirectories(pluginsDirectory);
    }

    private final Path pluginsDirectory;

    @Override
    public @NotNull Optional<PluginContainer> fromInstance(@NotNull Object instance) {
        if (instance instanceof PluginContainer) {
            return Optional.of((PluginContainer) instance);
        }

        return Optional.empty();
    }

    @Override
    public @NotNull Optional<PluginContainer> getPlugin(@NotNull String id) {
        for (PluginContainer pluginContainer : this.getPlugins()) {
            if (pluginContainer.getId().equals(id)) {
                return Optional.of(pluginContainer);
            }
        }

        return Optional.empty();
    }

    @Override
    public @NotNull Collection<PluginContainer> getPlugins() {
        return this.pluginContainers.stream().map(e -> e.getPluginContainer()).collect(Collectors.toList());
    }

    @Override
    public boolean isLoaded(@NotNull String id) {
        PluginContainer container = this.getPlugin(id).orElse(null);
        return container != null && (container.getState() == PluginState.LOADED || container.getState() == PluginState.ENABLED);
    }

    @Override
    public boolean isEnabled(@NotNull String id) {
        PluginContainer container = this.getPlugin(id).orElse(null);
        return container != null && container.getState() == PluginState.ENABLED;
    }

    @Override
    public void detectPlugins() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.pluginsDirectory, Constants.JAR_FILE_FILTER)) {
            for (Path path : stream) {
                if (this.isPluginDetectedByPath(path)) {
                    continue;
                }

                toLoad.add(path);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.handleLoaded();
    }

    @Override
    public void loadPlugins() {
        for (PluginContainerEntry pluginContainer : this.pluginContainers) {
            for (Method method : pluginContainer.getOnLoadMethod()) {
                try {
                    System.out.println("Staging load method \"" + method.getName() + "\" in class "
                            + pluginContainer.getPluginContainer().getMainClass().getName()
                            + " (plugin: " + pluginContainer.getPluginContainer().getId() + "/"
                            + pluginContainer.getPluginContainer().getVersion() + ")"
                    );
                    this.invokeMethod0(method, pluginContainer.getInstance(), pluginContainer.getPluginContainer());
                } catch (final Exception ex) {
                    System.err.println("Unexpected exception while calling load method " + method.getName()
                            + " in plugin " + pluginContainer.getPluginContainer().getId());
                }
            }
        }
    }

    @Override
    public void enablePlugins() {
        for (PluginContainerEntry pluginContainer : this.pluginContainers) {
            for (Method method : pluginContainer.getOnEnableMethod()) {
                try {
                    System.out.println("Staging enable method \"" + method.getName() + "\" in class "
                            + pluginContainer.getPluginContainer().getMainClass().getName()
                            + " (plugin: " + pluginContainer.getPluginContainer().getId() + "/"
                            + pluginContainer.getPluginContainer().getVersion() + ")"
                    );
                    this.invokeMethod0(method, pluginContainer.getInstance(), pluginContainer.getPluginContainer());
                } catch (final Exception ex) {
                    System.err.println("Unexpected exception while calling enable method " + method.getName()
                            + " in plugin " + pluginContainer.getPluginContainer().getId());
                }
            }
        }
    }

    @Override
    public void disablePlugins() {
        for (PluginContainerEntry pluginContainer : this.pluginContainers) {
            for (Method method : pluginContainer.getOnDisableMethod()) {
                try {
                    System.out.println("Staging disable method \"" + method.getName() + "\" in class "
                            + pluginContainer.getPluginContainer().getMainClass().getName()
                            + " (plugin: " + pluginContainer.getPluginContainer().getId() + "/"
                            + pluginContainer.getPluginContainer().getVersion() + ")"
                    );
                    this.invokeMethod0(method, pluginContainer.getInstance(), pluginContainer.getPluginContainer());
                } catch (final Exception ex) {
                    System.err.println("Unexpected exception while calling disable method " + method.getName()
                            + " in plugin " + pluginContainer.getPluginContainer().getId());
                }
            }
        }
    }

    private boolean isPluginDetectedByPath(@NotNull Path path) {
        String check = path.toAbsolutePath().toString();
        for (PluginContainer pluginContainer : this.getPlugins()) {
            if (pluginContainer.getPluginPath().toAbsolutePath().toString().equals(check)) {
                return true;
            }
        }

        return toLoad.stream().anyMatch(e -> e.toAbsolutePath().toString().equals(check));
    }

    private void handleLoaded() {
        for (Path path : this.toLoad) {
            this.toLoad.remove(path);

            try {
                URLClassLoader classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()});
                List<Duo<Class<?>, Plugin>> mainClassPossibilities = this.findMainClass(path, classLoader);
                if (mainClassPossibilities.size() != 1) {
                    throw new PluginMainClassNotDefinedException("Found " + mainClassPossibilities.size() + " main class targets in " + path.toString() + ". Expected: 1");
                }

                Duo<Class<?>, Plugin> mainClass = mainClassPossibilities.get(0);
                PluginContainer container = new DefaultPluginContainer(mainClass.getRight(), mainClass.getLeft(), classLoader, path);

                Object instance;
                try {
                    instance = mainClass.getLeft().getDeclaredConstructor().newInstance();
                } catch (final NoSuchMethodException ex) {
                    System.err.println("No args constructor in plugin " + container.getId() + " not present");
                    continue;
                } catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                    System.err.println("Unexpected exception while calling constructor of " + container.getId());
                    ex.printStackTrace();
                    continue;
                }

                Collection<Duo<PluginState, Method>> injectMethods = new ArrayList<>();
                for (Method declaredMethod : mainClass.getLeft().getDeclaredMethods()) {
                    Inject inject = declaredMethod.getAnnotation(Inject.class);
                    if (inject == null) {
                        continue;
                    }

                    injectMethods.add(new Duo<>(inject.state(), declaredMethod));
                }

                PluginContainerEntry entry = new PluginContainerEntry(
                        container,
                        instance,
                        injectMethods.stream().filter(e -> e.getLeft() == PluginState.LOADED).map(Duo::getRight).toArray(Method[]::new),
                        injectMethods.stream().filter(e -> e.getLeft() == PluginState.ENABLED).map(Duo::getRight).toArray(Method[]::new),
                        injectMethods.stream().filter(e -> e.getLeft() == PluginState.DISABLED).map(Duo::getRight).toArray(Method[]::new)
                );
                this.pluginContainers.add(entry);
            } catch (final IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        this.pluginContainers.sort(Comparator.comparing(e -> e.getPluginContainer().getId()));
        List<PluginContainer> sorted = this.sort(this.pluginContainers.stream().map(PluginContainerEntry::getPluginContainer).collect(Collectors.toList()));

        load:
        for (PluginContainer container : sorted) {
            for (Dependency dependency : container.getDependencies()) {
                if (dependency.optional()) {
                    continue;
                }

                PluginContainer depend = this.getPlugin(dependency.id()).orElse(null);
                if (depend == null) {
                    System.err.println("Unable to load plugin " + container.getId() + " because of missing dependency " + dependency.id());
                    this.pluginContainers.removeIf(e -> e.getPluginContainer().getId().equals(container.getId()));
                    continue load;
                }

                if (depend.getVersion() < dependency.minimumVersion()) {
                    System.err.println("Cannot load plugin " + container.getId() + " because of missing dependency with minimum version " + dependency.minimumVersion());
                    this.pluginContainers.removeIf(e -> e.getPluginContainer().getId().equals(container.getId()));
                    continue load;
                }
            }
        }
    }

    @NotNull
    private List<Duo<Class<?>, Plugin>> findMainClass(@NotNull Path plugin, @NotNull URLClassLoader classLoader) throws IOException, ClassNotFoundException {
        List<Duo<Class<?>, Plugin>> out = new ArrayList<>();
        try (JarInputStream jarInputStream = new JarInputStream(Files.newInputStream(plugin))) {
            JarEntry entry;
            while ((entry = jarInputStream.getNextJarEntry()) != null) {
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                String className = entry.getName().replace("/", ".");
                className = IOUtils.replaceLast(className, ".class", "");

                Class<?> clazz = classLoader.loadClass(className);
                Plugin annotation = clazz.getAnnotation(Plugin.class);
                if (annotation == null) {
                    continue;
                }

                out.add(new Duo<>(clazz, annotation));
            }
        }

        return out;
    }

    // See https://github.com/google/guava/wiki/GraphsExplained
    @NotNull
    private List<PluginContainer> sort(@NotNull List<PluginContainer> candidates) {
        MutableGraph<PluginContainer> graph = GraphBuilder
                .directed()
                .expectedNodeCount(candidates.size())
                .allowsSelfLoops(false)
                .build();
        Map<String, PluginContainer> candidateAsMap = Maps.uniqueIndex(candidates, PluginContainer::getId);

        for (PluginContainer candidate : candidates) {
            graph.addNode(candidate);

            for (Dependency dependency : candidate.getDependencies()) {
                PluginContainer container = candidateAsMap.get(dependency.id());
                if (container != null) {
                    graph.putEdge(candidate, container);
                }
            }
        }

        List<PluginContainer> sorted = new ArrayList<>();
        Map<PluginContainer, Integer> integerMap = new HashMap<>();

        for (PluginContainer node : graph.nodes()) {
            this.visitNode(graph, node, integerMap, sorted, new ArrayDeque<>());
        }

        return sorted;
    }

    private void visitNode(Graph<PluginContainer> graph, PluginContainer node, Map<PluginContainer, Integer> marks, List<PluginContainer> sorted,
                           Deque<PluginContainer> currentIteration) {
        Integer integer = marks.getOrDefault(node, 0);
        if (integer == 2) {
            return;
        } else if (integer == 1) {
            currentIteration.addLast(node);

            StringBuilder stringBuilder = new StringBuilder();
            for (PluginContainer description : currentIteration) {
                stringBuilder.append(description.getId()).append(": ");
            }

            stringBuilder.setLength(stringBuilder.length() - 4);
            throw new StackOverflowError("Dependency load injects itself or other injects other: " + stringBuilder.toString());
        }

        currentIteration.addLast(node);
        marks.put(node, 2);
        for (PluginContainer edge : graph.successors(node)) {
            this.visitNode(graph, edge, marks, sorted, currentIteration);
        }

        marks.put(node, 2);
        currentIteration.removeLast();
        sorted.add(node);
    }

    private void invokeMethod0(@NotNull Method method, @NotNull Object instance, @NotNull PluginContainer container) throws Exception {
        if (method.getParameters().length == 0) {
            method.invoke(instance);
            return;
        }

        Object[] parameters = new Object[method.getParameters().length];
        for (int i = 0; i < method.getParameters().length; i++) {
            Class<?> type = method.getParameters()[i].getType();
            if (Proxy.class.isAssignableFrom(type)) {
                parameters[i] = MCProxy.getInstance().getServiceRegistry().getProviderUnchecked(Proxy.class);
                continue;
            }

            if (ServiceRegistry.class.isAssignableFrom(type)) {
                parameters[i] = MCProxy.getInstance().getServiceRegistry();
                continue;
            }

            if (PluginContainer.class.isAssignableFrom(type)) {
                parameters[i] = container;
                continue;
            }

            throw new IllegalStateException("Expecting type plugin container, service registry or proxy not " + type.getName());
        }

        method.invoke(instance, parameters);
    }

    private static class PluginContainerEntry {

        public PluginContainerEntry(PluginContainer pluginContainer, Object instance, Method[] onLoadMethod, Method[] onEnableMethod, Method[] onDisableMethod) {
            this.pluginContainer = pluginContainer;
            this.instance = instance;
            this.onLoadMethod = onLoadMethod;
            this.onEnableMethod = onEnableMethod;
            this.onDisableMethod = onDisableMethod;
        }

        private final PluginContainer pluginContainer;

        private final Object instance;

        private final Method[] onLoadMethod;

        private final Method[] onEnableMethod;

        private final Method[] onDisableMethod;

        public PluginContainer getPluginContainer() {
            return pluginContainer;
        }

        public Object getInstance() {
            return instance;
        }

        public Method[] getOnLoadMethod() {
            return onLoadMethod;
        }

        public Method[] getOnEnableMethod() {
            return onEnableMethod;
        }

        public Method[] getOnDisableMethod() {
            return onDisableMethod;
        }
    }
}
