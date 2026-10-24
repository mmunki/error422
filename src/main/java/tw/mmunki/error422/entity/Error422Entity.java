package tw.mmunki.error422.entity;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

import java.util.UUID;

public final class Error422Entity extends Monster {
    private static final int MAX_LIFE_TICKS = 20 * 16;

    private UUID owner;
    private int life;
    private int movementCooldown = 20;

    public Error422Entity(EntityType<? extends Error422Entity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        setPersistenceRequired();
        setCustomName(Component.literal("ERROR422"));
        setCustomNameVisible(true);
        setNoAi(true);
        setNoGravity(true);
        setInvulnerable(true);
        noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 422.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.ATTACK_DAMAGE, 0.0D);
    }

    public void bindTo(UUID owner) {
        this.owner = owner;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }
        life++;
        ServerLevel serverLevel = (ServerLevel) level();
        ServerPlayer player = owner == null ? null
                : serverLevel.getServer().getPlayerList().getPlayer(owner);
        if (player == null || !player.isAlive() || life > MAX_LIFE_TICKS) {
            vanish(serverLevel);
            return;
        }
        if (life == 1) {
            serverLevel.playSound(null, getX(), getY(), getZ(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.4F, 0.45F);
            serverLevel.sendParticles(ParticleTypes.SOUL, getX(), getY() + 1.0D, getZ(),
                    40, 0.7D, 1.3D, 0.7D, 0.02D);
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, false, false));
        }
        if (--movementCooldown <= 0) {
            movementCooldown = 35 + getRandom().nextInt(45);
            moveNear(player);
            serverLevel.playSound(null, getX(), getY(), getZ(),
                    getRandom().nextBoolean() ? SoundEvents.WARDEN_HEARTBEAT : SoundEvents.ENDERMAN_STARE,
                    SoundSource.HOSTILE, 1.1F, 0.4F + getRandom().nextFloat() * 0.35F);
        }
        if (life % 5 == 0) {
            lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition());
        }
        if (life % 30 == 0) {
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 1.0D, getZ(),
                    18, 0.5D, 1.0D, 0.5D, 0.01D);
        }
    }

    private void moveNear(ServerPlayer player) {
        double angle = getRandom().nextDouble() * Math.PI * 2.0D;
        double distance = 3.5D + getRandom().nextDouble() * 7.0D;
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        double y = player.getY() + (getRandom().nextDouble() - 0.5D) * 2.0D;
        teleportTo(x, y, z);
        float yaw = (float) Math.toDegrees(Math.atan2(player.getZ() - z, player.getX() - x)) - 90.0F;
        setYRot(yaw);
        setYHeadRot(yaw);
        setYBodyRot(yaw);
    }

    private void vanish(ServerLevel serverLevel) {
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, getX(), getY() + 1.0D, getZ(),
                70, 0.6D, 1.2D, 0.6D, 0.12D);
        serverLevel.playSound(null, getX(), getY(), getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.2F, 0.4F);
        discard();
    }
}
