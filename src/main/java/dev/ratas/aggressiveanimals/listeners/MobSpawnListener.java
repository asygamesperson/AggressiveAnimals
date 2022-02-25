package dev.ratas.aggressiveanimals.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.AggressivityReason;
import dev.ratas.aggressiveanimals.aggressive.AttackReason;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public class MobSpawnListener implements Listener {
    private final AggressiveAnimals plugin;
    private final AggressivityManager aggressivityManager;

    public MobSpawnListener(AggressiveAnimals plugin, AggressivityManager aggressivityManager) {
        this.plugin = plugin;
        this.aggressivityManager = aggressivityManager;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) entity;
        if (!aggressivityManager.isManaged(mob)) {
            return;
        }
        aggressivityManager.setAggressivityAttributes(mob, AggressivityReason.SPAWN);
        if (!aggressivityManager.shouldBeAggressiveAtSpawn(mob)) {
            return;
        }
        aggressivityManager.attemptAttacking(mob, null, AttackReason.AGGRESSIVE_AT_SPAWN);
    }

    private Player getDamagingPlayer(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Entity soruceEntity) {
                return getDamagingPlayer(soruceEntity);
            }
            // TODO try to identify shooter of redstone or something?
            return null;
        }
        return null;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity targetEntity = event.getEntity();
        if (!(targetEntity instanceof Mob)) {
            return; // currently only managing living entities
        }
        Mob target = (Mob) targetEntity;
        if (!aggressivityManager.isManaged(target)) {
            return;
        }
        Player damagingPlayer = getDamagingPlayer(event.getDamager());
        if (damagingPlayer == null) {
            return;
        }
        if (!aggressivityManager.shouldBeAggressiveOnAttack(target, damagingPlayer)) {
            return;
        }
        aggressivityManager.attemptAttacking(target, damagingPlayer, AttackReason.RETALIATE);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        aggressivityManager.untargetPlayer(player);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) damager;
        if (!aggressivityManager.isManaged(mob)) {
            return;
        }
        Player target = getDamagingPlayer(event.getEntity());
        if (target == null) {
            return;
        }
        MobTypeSettings settings = aggressivityManager.getMobTypeManager()
                .getEnabledSettings(MobType.fromBukkit(mob.getType()));
        if (!settings.shouldAttack(mob, target)) {
            plugin.debug("Removing target of " + mob + " : " + target);
            event.setCancelled(true);
            mob.setTarget(null);
        }
    }

}
