package tw.mmunki.error422.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import tw.mmunki.error422.Error422;
import tw.mmunki.error422.entity.Error422Entity;

public final class Error422EntityRenderer extends MobRenderer<Error422Entity, HumanoidRenderState,
        HumanoidModel<HumanoidRenderState>> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            Error422.id("error422_entity"), "main");

    public Error422EntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(LAYER)), 0.8F);
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return Error422.id("textures/entity/error422.png");
    }
}
