package dev.mmunki.error422.server;

import dev.mmunki.error422.Error422Settings;
import dev.mmunki.error422.core.Anomaly;
import dev.mmunki.error422.core.Error422Text;
import dev.mmunki.error422.core.Session;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class ServerEffects {
    private static final Set<Block> REPLACEABLE = Set.of(
            Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT,
            Blocks.SAND, Blocks.GRAVEL, Blocks.COBBLESTONE, Blocks.OAK_LOG,
            Blocks.SANDSTONE, Blocks.DEEPSLATE, Blocks.ANDESITE, Blocks.DIORITE,
            Blocks.GRANITE, Blocks.TUFF, Blocks.CLAY, Blocks.SNOW_BLOCK);

    private static final List<Block> REPLACEMENTS = List.of(
            Blocks.GLASS, Blocks.REDSTONE_BLOCK, Blocks.COAL_BLOCK,
            Blocks.BOOKSHELF, Blocks.OBSIDIAN, Blocks.NETHERRACK,
            Blocks.SOUL_SAND, Blocks.CRYING_OBSIDIAN, Blocks.GLOWSTONE,
            Blocks.TINTED_GLASS, Blocks.BLACKSTONE, Blocks.END_STONE);

    private ServerEffects() {
    }

    public static void apply(ServerPlayer player, Session session, Anomaly anomaly, Error422Settings settings) {
        Random random = new Random(session.seed() ^ (session.ticks() * 31L) ^ anomaly.ordinal());
        switch (anomaly) {
            case CHAT_ERROR -> chatError(player, random);
            case GLITCH_SCREEN -> player.sendOverlayMessage(
                    Component.literal("ERR422 " + Error422Text.hex(random))
                            .withStyle(ChatFormatting.RED));
            case STATIC -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 0.45F, 0.45F);
            case LIGHTNING -> lightning(player, random, settings.allowDamage);
            case NAUSEA -> player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 180, 0, false, false));
            case PUSH -> player.push((random.nextDouble() - 0.5D) * 0.7D, 0.35D,
                    (random.nextDouble() - 0.5D) * 0.7D);
            case DARKNESS -> player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0, false, false));
            case TITLE_GLITCH -> title(player, "ERR422", Error422Text.corruption(random));
            case BLOCK_SWAP -> {
                if (settings.allowWorldChanges) {
                    swapBlocks(player, random);
                }
            }
            case INVENTORY_SCRAMBLE -> {
                if (settings.allowInventoryScramble) {
                    scrambleInventory(player, random);
                }
            }
            case FINALE -> finale(player, random, settings.allowDamage);
        }
    }

    private static void chatError(ServerPlayer player, Random random) {
        player.sendSystemMessage(Component.literal(Error422Text.chatLine(random))
                .withStyle(ChatFormatting.RED));
    }

    private static void lightning(ServerPlayer player, Random random, boolean damage) {
        ServerLevel level = player.level();
        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
        if (bolt == null) {
            return;
        }
        double angle = random.nextDouble() * Math.PI * 2.0D;
        double distance = 4.0D + random.nextDouble() * 10.0D;
        bolt.setPos(player.getX() + Math.cos(angle) * distance,
                player.getY(),
                player.getZ() + Math.sin(angle) * distance);
        bolt.setVisualOnly(!damage);
        bolt.setCause(player);
        level.addFreshEntity(bolt);
    }

    private static void title(ServerPlayer player, String title, String subtitle) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(4, 40, 10));
        player.connection.send(new ClientboundSetSubtitleTextPacket(
                Component.literal(subtitle).withStyle(ChatFormatting.DARK_RED)));
        player.connection.send(new ClientboundSetTitleTextPacket(
                Component.literal(title).withStyle(ChatFormatting.RED)));
    }

    private static void finale(ServerPlayer player, Random random, boolean damage) {
        title(player, "ERR422", "the game has taken control");
        player.sendSystemMessage(Component.literal("[ERROR422] the session is complete")
                .withStyle(ChatFormatting.DARK_RED));
        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 260, 1, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 260, 0, false, false));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WITHER_SPAWN, SoundSource.AMBIENT, 0.8F, 0.5F);
        lightning(player, random, damage);
        lightning(player, random, damage);
    }

    private static void swapBlocks(ServerPlayer player, Random random) {
        ServerLevel level = player.level();
        BlockPos origin = player.blockPosition();
        int swapped = 0;
        for (int attempt = 0; attempt < 28 && swapped < 10; attempt++) {
            BlockPos pos = origin.offset(
                    random.nextInt(17) - 8,
                    random.nextInt(7) - 3,
                    random.nextInt(17) - 8);
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (!REPLACEABLE.contains(level.getBlockState(pos).getBlock())) {
                continue;
            }
            Block replacement = REPLACEMENTS.get(random.nextInt(REPLACEMENTS.size()));
            level.setBlockAndUpdate(pos, replacement.defaultBlockState());
            swapped++;
        }
    }

    private static void scrambleInventory(ServerPlayer player, Random random) {
        Inventory inventory = player.getInventory();
        List<ItemStack> items = new ArrayList<>();
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                items.add(stack.copy());
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
        Collections.shuffle(items, random);
        for (int slot = 0; slot < items.size(); slot++) {
            inventory.setItem(slot, items.get(slot));
        }
    }
}
