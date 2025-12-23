package dev.necro.essentials.dependencies;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages dependency downloading and loading for NecroEssentials
 */
public class DependencyManager {

    /**
     * -- GETTER --
     *  Gets the cache directory
     *
     * @return the cache directory path
     */
    @Getter
    private final Path cacheDirectory;
    private final Logger logger;
    private final Set<Dependency> loaded;
    private final ClassLoader pluginClassLoader;

    public DependencyManager(Logger logger) {
        this.logger = logger;
        this.cacheDirectory = setupCacheDirectory();
        this.loaded = EnumSet.noneOf(Dependency.class);
        this.pluginClassLoader = getClass().getClassLoader();
    }

    /**
     * Loads all required dependencies
     *
     * @param dependencies the dependencies to load
     */
    public void loadDependencies(Dependency... dependencies) {
        List<Path> downloadedFiles = new ArrayList<>();
        List<String> failedDependencies = new ArrayList<>();

        for (Dependency dependency : dependencies) {
            if (loaded.contains(dependency)) {
                continue;
            }

            Path dependencyFile = cacheDirectory.resolve(dependency.getFileName());
            logger.info("Loading dependency: " + dependency.getFileName());

            try {
                DependencyRepository.download(dependency, dependencyFile);
                downloadedFiles.add(dependencyFile);
                loaded.add(dependency);
            } catch (IOException e) {
                logger.warning("Failed to load dependency " + dependency.getFileName() + ": " + e.getMessage());
                failedDependencies.add(dependency.getFileName());
                // Continue loading other dependencies instead of throwing
            }
        }

        // Load all downloaded files into the plugin's classloader
        if (!downloadedFiles.isEmpty()) {
            try {
                loadFilesIntoClasspath(downloadedFiles);
                logger.info("Successfully loaded " + downloadedFiles.size() + " dependencies");
            } catch (Exception e) {
                logger.severe("Failed to inject dependencies into classloader!");
                e.printStackTrace();
            }
        }

        if (!failedDependencies.isEmpty()) {
            logger.warning("Failed to load " + failedDependencies.size() + " dependencies:");
            for (String failed : failedDependencies) {
                logger.warning("  - " + failed);
            }
        }
    }

    /**
     * Loads standard cloud framework dependencies
     */
    public void loadCloudDependencies() {
        loadDependencies(
                Dependency.CLOUD_CORE,
                Dependency.CLOUD_ANNOTATIONS,
                Dependency.CLOUD_PAPER,
                Dependency.CLOUD_BUKKIT,
                Dependency.CLOUD_BRIGADIER,
                Dependency.CLOUD_SERVICES,
                Dependency.CLOUD_TASKS,
                Dependency.CLOUD_MINECRAFT_EXTRAS
        );
    }

    /**
     * Loads adventure API dependencies
     */
    public void loadAdventureDependencies() {
        // Check if already loaded
        if (isClassLoaded("net.kyori.adventure.text.Component")) {
            logger.info("Adventure API already present, skipping download");
            loadDependencies(
                    Dependency.ADVENTURE_PLATFORM_API,
                    Dependency.ADVENTURE_PLATFORM_BUKKIT,
                    Dependency.ADVENTURE_PLATFORM_FACET,
                    Dependency.GEANTYREF
            );
            return;
        }

        loadDependencies(
                Dependency.ADVENTURE_API,
                Dependency.ADVENTURE_KEY,
                Dependency.ADVENTURE_PLATFORM_API,
                Dependency.ADVENTURE_PLATFORM_BUKKIT,
                Dependency.ADVENTURE_PLATFORM_FACET,
                Dependency.GEANTYREF,
                Dependency.EXAMINATION_API
        );
    }

    /**
     * Loads JAR files into the plugin's classloader using multiple approaches
     *
     * @param files the JAR files to load
     */
    private void loadFilesIntoClasspath(List<Path> files) throws Exception {
        // Try Bukkit's PluginClassLoader first (works on most Spigot/Paper versions)
        if (tryBukkitPluginClassLoader(files)) {
            logger.info("Injected " + files.size() + " JARs using PluginClassLoader");
            return;
        }

        // Fallback: Try URLClassLoader reflection (older Java versions)
        if (tryURLClassLoader(files)) {
            logger.info("Injected " + files.size() + " JARs using URLClassLoader");
            return;
        }

        // If both fail, throw an exception
        throw new RuntimeException("Unable to inject dependencies - no compatible classloader method found");
    }

    /**
     * Try to inject using Bukkit's PluginClassLoader
     */
    private boolean tryBukkitPluginClassLoader(List<Path> files) {
        try {
            Class<?> pluginClassLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");

            if (!pluginClassLoaderClass.isInstance(pluginClassLoader)) {
                return false;
            }

            Method addURLMethod = pluginClassLoaderClass.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);

            for (Path file : files) {
                URL url = file.toUri().toURL();
                addURLMethod.invoke(pluginClassLoader, url);
                logger.fine("Injected: " + file.getFileName());
            }

            return true;
        } catch (Exception e) {
            logger.fine("PluginClassLoader injection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Try to inject using URLClassLoader (fallback for older Java)
     */
    private boolean tryURLClassLoader(List<Path> files) {
        try {
            if (!(pluginClassLoader instanceof URLClassLoader)) {
                return false;
            }

            URLClassLoader urlClassLoader = (URLClassLoader) pluginClassLoader;
            Method addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);

            for (Path file : files) {
                URL url = file.toUri().toURL();
                addURLMethod.invoke(urlClassLoader, url);
                logger.fine("Injected: " + file.getFileName());
            }

            return true;
        } catch (Exception e) {
            logger.fine("URLClassLoader injection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a class is available
     *
     * @param className the class name to check
     * @return true if the class is loaded
     */
    private boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Sets up the cache directory for dependencies
     *
     * @return the cache directory path
     */
    private Path setupCacheDirectory() {
        Path cacheDir = new File("plugins/NecroEssentials/libs").toPath();

        try {
            if (!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
                logger.info("Created dependency cache directory");
            }
        } catch (IOException e) {
            logger.severe("Failed to create libs directory: " + e.getMessage());
        }

        return cacheDir;
    }

    /**
     * Gets all loaded dependencies
     *
     * @return set of loaded dependencies
     */
    public Set<Dependency> getLoadedDependencies() {
        return EnumSet.copyOf(loaded);
    }

    /**
     * Closes resources (no-op now since we're not using isolated classloader)
     */
    public void shutdown() {
        // No cleanup needed when injecting into plugin classloader
        logger.info("Dependency manager shutdown complete");
    }
}
