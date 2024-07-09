package net.sweenus.simplyswords.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.sweenus.simplyswords.config.Config;
import net.sweenus.simplyswords.config.ConfigDefaultValues;
import net.sweenus.simplyswords.item.custom.MagiscytheSwordItem;
import net.sweenus.simplyswords.registry.EffectRegistry;
import net.sweenus.simplyswords.registry.SoundRegistry;
import net.sweenus.simplyswords.util.HelperMethods;

import java.util.List;
import java.util.Random;

public class MagistormEffect extends HighOrbitingEffect {
    public MagistormEffect(StatusEffectCategory statusEffectCategory, int color) {
        super (statusEffectCategory, color);
        setParticleType1(ParticleTypes.ENCHANT);
        setParticleType2(ParticleTypes.POOF);
        setParticleType3(ParticleTypes.CRIT);
        yOffset = 4f;
        width = 4;
    }
    @Override
    public void applyUpdateEffect(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) livingEntity.getWorld();
            double x = livingEntity.getX();
            double y = livingEntity.getY();
            double z = livingEntity.getZ();
            float damage = Config.getFloat("magistormDamage", "UniqueEffects", ConfigDefaultValues.magistormDamage);
            double radius = Config.getFloat("magistormRadius", "UniqueEffects", ConfigDefaultValues.magistormRadius);
            float duration = Config.getFloat("magistormDuration", "UniqueEffects", ConfigDefaultValues.magistormDuration);
            int frequency = Math.max(3, 10 - amplifier);

            float spellScalingModifier = Config.getFloat("magistormSpellScaling", "UniqueEffects", ConfigDefaultValues.magistormSpellScaling);
            if (HelperMethods.commonSpellAttributeScaling(spellScalingModifier, livingEntity, "arcane") > damage)
                damage = HelperMethods.commonSpellAttributeScaling(spellScalingModifier, livingEntity, "arcane");

            DamageSource damageSource =  livingEntity.getDamageSources().indirectMagic(livingEntity, livingEntity);
            if (livingEntity.age % frequency == 0 && livingEntity instanceof  PlayerEntity player) {
                Box box = new Box(x - radius, y - 1, z - radius, x + radius, y + 1, z + radius);
                List<Entity> nearbyEntities = world.getOtherEntities(livingEntity, box, EntityPredicates.VALID_LIVING_ENTITY);

                if (!nearbyEntities.isEmpty() && player.getMainHandStack().getItem() instanceof MagiscytheSwordItem) {
                    Entity randomEntity = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));
                    if (randomEntity instanceof LivingEntity target && HelperMethods.checkFriendlyFire(target, player)) {
                        if (target instanceof PlayerEntity)
                            damageSource = livingEntity.getDamageSources().playerAttack(player);
                        target.timeUntilRegen = 0;
                        HelperMethods.applyDamageWithoutKnockback(target, damageSource, damage);
                        target.timeUntilRegen = 0;
                        HelperMethods.spawnRainingParticles(world, ParticleTypes.ENCHANT, target, 20, yOffset);
                        HelperMethods.spawnRainingParticles(world, ParticleTypes.GLOW, target, 4, yOffset);
                        HelperMethods.spawnOrbitParticles(world, target.getPos(), ParticleTypes.GLOW, 0.5, 6);
                        player.getWorld().playSoundFromEntity(null, player, SoundRegistry.ELEMENTAL_BOW_HOLY_SHOOT_IMPACT_03.get(),
                                SoundCategory.PLAYERS, 0.1f, 1.0f + (player.getRandom().nextFloat()));

                        if (new Random().nextInt(100) < 5)
                            HelperMethods.incrementStatusEffect(livingEntity, EffectRegistry.MAGISTORM.get(), (int) duration, 1, 10);
                    }
                }
            }
        }

        super.applyUpdateEffect(livingEntity, amplifier);
    }



    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return super.canApplyUpdateEffect(duration, amplifier);
    }

}
