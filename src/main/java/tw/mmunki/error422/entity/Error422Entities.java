package tw.mmunki.error422.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tw.mmunki.error422.Error422;

public final class Error422Entities {
    public static final EntityType<Error422Entity> ERROR422 = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Error422.id("error422"),
            EntityType.Builder.of(Error422Entity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Error422.id("error422"))));

    private Error422Entities() {
    }

    public static void register() {
        FabricDefaultAttributeRegistry.register(ERROR422, Error422Entity.createAttributes());
    }
}
