package dev.necro.essentials;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.necro.essentials.commands.main.NecroEssentialsCommand;
import dev.necro.essentials.config.ConfigFile;
import dev.necro.essentials.dependencies.DependencyManager;
import dev.necro.essentials.dependencies.deprecated.DependencyHelper;
import dev.necro.essentials.listeners.trolls.TrollListeners;
import dev.necro.essentials.listeners.*;
import dev.necro.essentials.managers.ConfirmationManager;
import dev.necro.essentials.managers.InvseeManager;
import dev.necro.essentials.managers.MainConfigManager;
import dev.necro.essentials.managers.SlotsManager;
import dev.necro.essentials.managers.whitelist.WhitelistManager;
import dev.necro.essentials.utils.Utils;
import dev.necro.essentials.versionsupport.VersionSupport;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class NecroEssentials extends JavaPlugin {

    @Getter
    private static NecroEssentials instance;

    @Setter
    private ConfigFile mainConfig, slotsConfig, whitelistConfig;

    private NecroEssentialsCommand mainCommand;
    @Getter
    private DependencyManager dependencyManager;
    private VersionSupport versionSupport;

    private InvseeManager invseeManager;
    private MainConfigManager mainConfigManager;
    private WhitelistManager whitelistManager;
    private SlotsManager slotsManager;

    private ConfirmationManager confirmationManager;

    @Override
    public void onEnable() {
        long millis = System.currentTimeMillis();

        instance = this;
        // Deprecated dependency loading
        //this.loadDependencies();
        //this.loadRetardedDependencies();

        // Initialize and load dependencies
        this.dependencyManager = new DependencyManager(this.getLogger());

        this.getLogger().info("Loading Cloud Framework dependencies...");
        this.dependencyManager.loadCloudDependencies();

        this.getLogger().info("Loading Adventure API dependencies...");
        this.dependencyManager.loadAdventureDependencies();

        this.loadVersionSupport();

        this.loadConfigs();
        this.mainConfigManager = new MainConfigManager(this);
        this.slotsManager = new SlotsManager(this);
        this.whitelistManager = new WhitelistManager(this);

        this.confirmationManager = new ConfirmationManager(this);

        this.mainCommand = new NecroEssentialsCommand(this);
        this.invseeManager = new InvseeManager(this);

        this.registerListeners(
                new ChatListener(this),
                new ConnectionListeners(this),
                new DamageListeners(),
                new DeathListeners(this)
        );
        new TrollListeners(this);
        new InvseeListeners(this);

        this.sendStuffToConsoleLmao();
        this.getLogger().info("NecroEssentials v" + this.getDescription().getVersion() + " loaded in " + (System.currentTimeMillis() - millis) + "ms!");
    }

    @Override
    public void onDisable() {
        // Cleanup is handled by classloader garbage collection
        this.getLogger().info("NecroEssentials disabled");
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(it -> Bukkit.getPluginManager().registerEvents(it, this));
    }

    public void loadConfigs() {
        this.mainConfig = new ConfigFile(this, "config.yml");
        this.slotsConfig = new ConfigFile(this, "slots.yml");
        this.whitelistConfig = new ConfigFile(this, "whitelist.yml");
    }

    /**
     * downloads and/or loads dependencies
     */
    private void loadDependencies() {
        this.getLogger().info("Loading and injecting dependencies...");
        Map<String, String> dependencyMap = new HashMap<>();

        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = NecroEssentials.class.getClassLoader().getResourceAsStream("dependencies.json");

            assert stream != null;
            reader = new InputStreamReader(stream);

            JsonParser parser = new JsonParser();
            JsonArray dependencies = parser.parse(reader).getAsJsonArray();
            if (dependencies.size() == 0) {
                return;
            }

            for (JsonElement element : dependencies) {
                JsonObject dependency = element.getAsJsonObject();
                if (!dependency.get("name").getAsString().contains("adventure-api")) {
                    dependencyMap.put(
                            dependency.get("name").getAsString(),
                            dependency.get("url").getAsString()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        DependencyHelper helper = new DependencyHelper(NecroEssentials.class.getClassLoader());
        File dir = new File("plugins/NecroEssentials/libs");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            helper.download(dependencyMap, dir.toPath());
            helper.loadDir(dir.toPath(), false);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * downloads and/or loads retarded dependencies
     */
    private void loadRetardedDependencies() {
        try {
            Class.forName("net.kyori.adventure.sound.Sound");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        this.getLogger().info("Loading and injecting retarded dependencies...");
        Map<String, String> dependencyMap = new HashMap<>();

        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = NecroEssentials.class.getClassLoader().getResourceAsStream("dependencies-retarded.json");

            assert stream != null;
            reader = new InputStreamReader(stream);

            JsonParser parser = new JsonParser();
            JsonArray dependencies = parser.parse(reader).getAsJsonArray();
            if (dependencies.size() == 0) {
                return;
            }

            for (JsonElement element : dependencies) {
                JsonObject dependency = element.getAsJsonObject();
                if (dependency.get("name").getAsString().contains("adventure-api")) {
                    dependencyMap.put(
                            dependency.get("name").getAsString(),
                            dependency.get("url").getAsString()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        DependencyHelper helper = new DependencyHelper(NecroEssentials.class.getClassLoader());
        File dir = new File("plugins/NecroEssentials/libs");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            helper.download(dependencyMap, dir.toPath());
            helper.loadDir(dir.toPath(), true);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * loads the appropriate {@link VersionSupport}
     */
    private void loadVersionSupport() {
        this.getLogger().info("Loading version support...");

        String version = Utils.getNmsVersion();
        this.getLogger().info("Detected server version: " + version);
        try {
            Class<?> support;
            switch (version) {
                case "v1_8_R3": {
                    support = Class.forName("dev.necro.essentials.versionsupport.v1_8_R3.v1_8_R3");
                    this.getLogger().info("Loaded version support v1_8_R3");
                    break;
                }
                case "v1_9_R1":
                case "v1_9_R2":
                case "v1_10_R1":
                case "v1_11_R1":
                case "v1_12_R1": {
                    support = Class.forName("dev.necro.essentials.versionsupport.v1_12_R1.v1_12_R1");
                    this.getLogger().info("Loaded version support v1_12_R1");
                    break;
                }
                case "v1_13_R1": {
                    support = Class.forName("dev.necro.essentials.versionsupport.v1_13_R1.v1_13_R1");
                    this.getLogger().info("Loaded version support v1_13_R1");
                    break;
                }
                case "v1_14_R1":
                case "v1_15_R1":
                case "v1_16_R1": {
                    support = Class.forName("dev.necro.essentials.versionsupport.v1_16_R1.v1_16_R1");
                    this.getLogger().info("Loaded version support v1_16_R1");
                    break;
                }
                case "v1_17_R1": {
                    support = Class.forName("dev.necro.essentials.versionsupport.v1_16_R1.v1_16_R1");
//                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_17_R1.v1_17_R1");
                    this.getLogger().info("Loaded version support v1_17_R1");
                    break;
                }
                case "v1_18_R1":
                case "v1_18_R2": {
                    support = Class.forName("dev.necro.essentials.versionsupport.v1_16_R1.v1_16_R1");
//                    support = Class.forName("id.luckynetwork.dev.lyrams.lej.versionsupport.v1_18_R2.v1_18_R2");
                    this.getLogger().info("Loaded version support v1_18_R2");
                    break;
                }
                default: {
                    this.getLogger().severe("Unsupported server version!");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }

            versionSupport = (VersionSupport) support.getConstructor(Class.forName("org.bukkit.plugin.Plugin")).newInstance(this);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            this.getLogger().severe("Unsupported server version!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Im bored...
     */
    private void sendStuffToConsoleLmao() {
        String iyh =
                "\n" +
                        "§b  _   _                     _____                    _   _       _                     \n" +
                        "§b | \\ | | ___  ___ _ __ ___ | ____|___ ___  ___ _ __ | |_(_) __ _| |___                \n" +
                        "§b |  \\| |/ _ \\/ __| '__/ _ \\|  _| / __/ __|/ _ \\ '_ \\| __| |/ _` | / __|           \n" +
                        "§b | |\\  |  __/ (__| | | (_) | |___\\__ \\__ \\  __/ | | | |_| | (_| | \\__ \\          \n" +
                        "§b |_| \\_|\\___|\\___|_|  \\___/|_____|___/___/\\___|_| |_|\\__|_|\\__,_|_|___/         \n" +
                        "                                                                                         \n" +
                        "                §aNecroEssentials §ev" + this.getDescription().getVersion() + " §eby §b" + Joiner.on(", ").join(this.getDescription().getAuthors()) + "\n" +
                        " ";

        for (String s : iyh.split("\n")) {
            Bukkit.getConsoleSender().sendMessage(s);
        }
    }
}
