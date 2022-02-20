package dev.ratas.aggressiveanimals;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.config.CustomConfigHandler;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.config.messaging.Messages;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public class AggressiveAnimals extends JavaPlugin {
    private CustomConfigHandler config;
    private Messages messages;
    private Settings settings;
    private NPCHookManager npcHookManager;

    private void loadDataFromFile() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atLoad();
        try {
            config = new CustomConfigHandler(this, "config.yml");
        } catch (InvalidConfigurationException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues);
            return;
        }
        try {
            messages = new Messages(this);
        } catch (InvalidConfigurationException e) {
            issues.logIssue("INVALID MESSAGES", "Invalid messages - disabling", e);
            disableMe(issues);
            return;
        }
        settings = new Settings(this.config);
    }

    @Override
    public void onEnable() {
        loadDataFromFile();
        npcHookManager = new NPCHookManager();
    }

    @Override
    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public void reload() {
        ConfigLoadIssueResolver issues = ConfigLoadIssueResolver.atReload();
        try {
            config.reloadConfig();
        } catch (InvalidConfigurationException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues);
            return;
        }
        try {
            messages.reloadConfig();
        } catch (InvalidConfigurationException e) {
            issues.logIssue("INVALID CONFIGURATION", "Invalid configuration - disabling", e);
            disableMe(issues);
            return;
        }
    }

    public Messages getMessages() {
        return messages;
    }

    public Settings getSettings() {
        return settings;
    }

    public NPCHookManager getNPCHookManager() {
        return npcHookManager;
    }

    private void disableMe(ConfigLoadIssueResolver issues) {
        getLogger().severe(issues.asString());
        getServer().getPluginManager().disablePlugin(this);
    }

}
