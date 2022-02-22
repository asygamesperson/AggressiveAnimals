package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.LivingEntity;

import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

// #   ignore-npcs: true                  Ignore NPCs created by Citizens, EliteMobs, InfernalMobs, and Shopkeepers
// #   named-mobs-only: false             Should mobs attack only if they are named?

public record MobMiscSettings(boolean ignoreNpcs, boolean targetAsNamedOnly) {

    public boolean shouldBeAggressive(NPCHookManager npcHooks, LivingEntity mob, LivingEntity target) {
        if (ignoreNpcs && npcHooks.isNPC(target)) {
            return false;
        }
        if (targetAsNamedOnly && mob.getCustomName() == null) {
            return false;
        }
        return true;
    }

}
