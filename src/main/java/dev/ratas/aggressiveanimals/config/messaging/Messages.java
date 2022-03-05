package dev.ratas.aggressiveanimals.config.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.api.messaging.SDCMessage;
import dev.ratas.slimedogcore.api.messaging.context.SDCVoidContext;
import dev.ratas.slimedogcore.api.messaging.factory.SDCDoubleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.api.messaging.factory.SDCVoidContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.MessagesBase;
import dev.ratas.slimedogcore.impl.messaging.context.VoidContext;
import dev.ratas.slimedogcore.impl.messaging.factory.MsgUtil;

public class Messages extends MessagesBase {
    private static final String FILE_NAME = "messages.yml";
    private SDCVoidContextMessageFactory reloadMessage;
    private SDCVoidContextMessageFactory reloadFailMessage;
    private SDCVoidContextMessageFactory listHeaderMessage;
    private SDCDoubleContextMessageFactory<MobType, Boolean> listItemMessage;
    private SDCVoidContextMessageFactory enabledMessage;
    private SDCVoidContextMessageFactory disabledMessage;
    private SDCSingleContextMessageFactory<String> mobTypeNotFoundMessage;
    private SDCSingleContextMessageFactory<MobType> mobTypeNotDefined;
    private SDCSingleContextMessageFactory<MobTypeSettings> infoMessage;

    public Messages(SlimeDogPlugin plugin) throws InvalidConfigurationException {
        super(plugin.getCustomConfigManager().getConfig(FILE_NAME));
        loadMessages();
    }

    private void loadMessages() {
        this.reloadMessage = MsgUtil.voidContext(getRawMessage("reloaded-config", "Plugin was successfully reloaded"));
        this.reloadFailMessage = MsgUtil.voidContext(getRawMessage("problem-reloading-config",
                "There was an issue while reloading the config - check the console log"));
        this.listHeaderMessage = MsgUtil.voidContext(getRawMessage("list-header", "&8Configured mobs"));
        Function<Boolean, String> enabledStringGetter = b -> {
            SDCMessage<SDCVoidContext> m = (b ? enabledMessage : disabledMessage).getMessage(VoidContext.INSTANCE);
            return m.getFilled();
        };
        this.listItemMessage = MsgUtil.doubleContext("%mob-type%", t -> t.name(), "%status%", enabledStringGetter,
                getRawMessage("list-format", "&6%mob-type% &f- %status%"));
        this.enabledMessage = MsgUtil.voidContext(getRawMessage("enabled", "enabled"));
        this.disabledMessage = MsgUtil.voidContext(getRawMessage("disabled", "disabled"));
        this.mobTypeNotFoundMessage = MsgUtil.singleContext("%mob-type%", t -> t,
                getRawMessage("mob-type-not-found", "Mob type not found: %mob-type%"));
        this.mobTypeNotDefined = MsgUtil.singleContext("%mob-type%", mt -> mt.name(),
                getRawMessage("mob-type-not-defined", "No information defined for mon type: %mob-type%"));
        MsgUtil.MultipleToOneBuilder<MobTypeSettings> builder = new MsgUtil.MultipleToOneBuilder<>(
                getRawMessage("mob-type-info",
                        String.join("\n", "enabled: %enabled%", "always-aggressive: %always-aggressive%",
                                "speed-multiplier: %speed-multiplier%", "attack-damage: %attack-damage%",
                                "attack-damage-limit: %attack-damage-limit%", "attack-speed: %attack-speed%",
                                "attack-leap-height: %attack-leap-height%", "acquisition-range: %acquisition-range%",
                                "deacquisition-range: %deacquisition-range%",
                                "attacker-health-threshold: %attacker-health-threshold%", "age.adult: %age.adult%",
                                "age.baby: %age.baby%", "include-npcs: %include-npcs%",
                                "include-tamed-mobs: %include-tamed-mobs%",
                                "named-mobs-only: %named-mobs-only%", "override-targeting: %override-targeting%",
                                "group-aggression-range: %group-aggression-range%",
                                "player-movement.standing: %player-movement.standing%", //
                                "player-movement.sneaking: %player-movement.sneaking%",
                                "player-movement.walking: %player-movement.walking%",
                                "player-movement.sprinting: %player-movement.sprinting%",
                                "player-movement.looking: %player-movement.looking%",
                                "player-movement.sleeping: %player-movement.sleeping%",
                                "player-movement.gliding: %player-movement.gliding%",
                                "enabled-worlds: %enabled-worlds%",
                                "disabled-worlds: %disabled-worlds%")));
        builder.with("%enabled%", mts -> enabledStringGetter.apply(mts.enabled()));
        builder.with("%always-aggressive%", mts -> String.valueOf(mts.alwaysAggressive()));
        builder.with("%speed-multiplier%", mts -> formatDouble(mts.speedMultiplier()));
        builder.with("%attack-damage%", mts -> formatDouble(mts.attackSettings().damage()));
        builder.with("%attack-damage-limit%", mts -> formatDouble(mts.attackSettings().attackDamageLimit()));
        builder.with("%attack-speed%", mts -> formatDouble(mts.attackSettings().speed()));
        builder.with("%attack-leap-height%", mts -> formatDouble(mts.attackSettings().attackLeapHeight()));
        builder.with("%acquisition-range%", mts -> formatDouble(mts.acquisitionSettings().acquisitionRange()));
        builder.with("%deacquisition-range%", mts -> formatDouble(mts.acquisitionSettings().deacquisitionRange()));
        builder.with("%attacker-health-threshold%", mts -> formatDouble(mts.attackerHealthThreshold()));
        builder.with("%age.adult%", mts -> String.valueOf(mts.ageSettings().attackAsAdult()));
        builder.with("%age.baby%", mts -> String.valueOf(mts.ageSettings().attackAsBaby()));
        builder.with("%include-npcs%", mts -> String.valueOf(mts.miscSettings().includeNpcs()));
        builder.with("%include-tamed-mobs%", mts -> {
            if (!mts.entityType().isTameable() && mts.entityType() != MobType.fox) {
                return "N/A";
            }
            return String.valueOf(mts.miscSettings().includeTamed());
        });
        builder.with("%named-mobs-only%", mts -> String.valueOf(mts.miscSettings().targetAsNamedOnly()));
        builder.with("%override-targeting%", mts -> String.valueOf(mts.overrideTargets()));
        builder.with("%group-aggression-range%", mts -> formatDouble(mts.groupAgressionDistance()));
        builder.with("%player-movement.standing%", mts -> String.valueOf(mts.playerStateSettings().attackStanding()));
        builder.with("%player-movement.sneaking%", mts -> String.valueOf(mts.playerStateSettings().attackSneaking()));
        builder.with("%player-movement.walking%", mts -> String.valueOf(mts.playerStateSettings().attackWalking()));
        builder.with("%player-movement.sprinting%", mts -> String.valueOf(mts.playerStateSettings().attackSprinting()));
        builder.with("%player-movement.looking%", mts -> String.valueOf(mts.playerStateSettings().attackLooking()));
        builder.with("%player-movement.sleeping%", mts -> String.valueOf(mts.playerStateSettings().attackSleeping()));
        builder.with("%player-movement.gliding%", mts -> String.valueOf(mts.playerStateSettings().attackGliding()));
        builder.with("%enabled-worlds%", mts -> {
            List<String> enabled = sort(mts.worldSettings().enabledWorlds());
            if (enabled.isEmpty()) {
                return "all"; // TODO - configurable?
            }
            return String.join(", ", enabled);
        });
        builder.with("%disabled-worlds%", mts -> {
            List<String> enabled = sort(mts.worldSettings().disabledWorlds());
            if (enabled.isEmpty()) {
                return "none"; // TODO - configurable?
            }
            return String.join(", ", enabled);
        });
        infoMessage = builder.build();
    }

    public SDCVoidContextMessageFactory getReloadMessage() {
        return reloadMessage;
    }

    public SDCVoidContextMessageFactory getReloadFailedMessage() {
        return reloadFailMessage;
    }

    public SDCVoidContextMessageFactory getListHeaderMessage() {
        return listHeaderMessage;
    }

    public SDCDoubleContextMessageFactory<MobType, Boolean> getListItemMessage() {
        return listItemMessage;
    }

    public SDCVoidContextMessageFactory getEnabledMessage() {
        return enabledMessage;
    }

    public SDCVoidContextMessageFactory getDisabledMessage() {
        return disabledMessage;
    }

    public SDCSingleContextMessageFactory<String> getMobTypeNotFoundMessage() {
        return mobTypeNotFoundMessage;
    }

    public SDCSingleContextMessageFactory<MobType> getMobTypeNotDefined() {
        return mobTypeNotDefined;
    }

    public SDCSingleContextMessageFactory<MobTypeSettings> getInfoMessage() {
        return infoMessage;
    }

    public void reloadConfig() {
        super.reloadConfig();
        loadMessages();
    }

    public static String formatDouble(double val) {
        return String.format("%.2f", val);
    }

    public static List<String> sort(Collection<String> names) {
        List<String> list = new ArrayList<>(names);
        list.sort((s1, s2) -> s1.compareTo(s2));
        return list;
    }

}
