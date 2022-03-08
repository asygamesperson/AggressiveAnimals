package dev.ratas.aggressiveanimals.aggressive.settings;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ratas.aggressiveanimals.aggressive.settings.DefaultConfigTest.MockResourceProvider;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Setting;
import dev.ratas.slimedogcore.impl.config.CustomYamlConfig;

public class MobTypeSettingSettingsTest {
    private File configFile;
    private CustomYamlConfig config;

    @BeforeEach
    public void setup() {
        configFile = DefaultConfigTest.getFrom("src/test/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), configFile);
    }

    @Test
    public void test_mobTypeSettingsHasAllSettings() {
        Builder builder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        Assertions.assertEquals(25, allSettings.size());
    }

    @Test
    public void test_mobTypeSettingsHasNoDuplicateSettings() {
        Builder builder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        for (int i = 0; i < allSettings.size() - 1; i++) {
            for (int j = i + 1; j < allSettings.size(); j++) {
                Setting<?> s1 = allSettings.get(i);
                Setting<?> s2 = allSettings.get(j);
                Assertions.assertNotSame(s1, s2);
                Assertions.assertNotEquals(s1, s2);
            }
        }
    }

    @Test
    public void test_mobTypeSettingsTameableHasAllSettings() {
        Builder builder = new Builder(config.getConfig().getConfigurationSection("mobs.ocelot"));
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        Assertions.assertEquals(26, allSettings.size()); // has tamability one
    }

    @Test
    public void test_mobTypeSettingsTameableHasNoDuplicateSettings() {
        Builder builder = new Builder(config.getConfig().getConfigurationSection("mobs.ocelot"));
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        for (int i = 0; i < allSettings.size() - 1; i++) {
            for (int j = i + 1; j < allSettings.size(); j++) {
                Setting<?> s1 = allSettings.get(i);
                Setting<?> s2 = allSettings.get(j);
                Assertions.assertNotSame(s1, s2);
                Assertions.assertNotEquals(s1, s2);
            }
        }
    }

}
