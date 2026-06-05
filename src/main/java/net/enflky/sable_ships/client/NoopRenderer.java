package net.enflky.sable_ships.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

public class NoopRenderer<T extends Entity> extends EntityRenderer<T> { // I tried making chair but failed i will make it at some point
    public NoopRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    }

    @Override
    public void render(T entity, float yaw, float partialTick,
                       PoseStack stack, MultiBufferSource buffers, int light) {
        // we shouldnt have this anymore but i dont want to delete it beacuse i didnt know that we have noop integrated to neoforge :d (get it from google no lies :\ some guy said CHEATER!!! :D)
    }
}