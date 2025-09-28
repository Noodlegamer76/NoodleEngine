package com.noodlegamer76.noodleengine.engine.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.noodleengine.client.glitf.rendering.RenderableModel;
import com.noodlegamer76.noodleengine.engine.GameObject;
import com.noodlegamer76.noodleengine.engine.network.GameObjectSerializers;
import com.noodlegamer76.noodleengine.engine.network.SyncedVar;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

import java.util.List;

public class MeshRenderer extends Component implements RenderableComponent {
    private final SyncedVar<ResourceLocation> modelLocation = new SyncedVar<>(this, null, GameObjectSerializers.RESOURCE_LOCATION);
    private RenderableModel model;

    public MeshRenderer() {
        super(InitComponents.MESH_RENDERER);
    }

    @Override
    public List<SyncedVar<?>> getSyncedData() {
        return List.of(
                modelLocation
        );
    }

    public RenderableModel getModel() {
        return model;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation.getValue();
    }

    public void setModelLocation(ResourceLocation modelLocation) {
        this.modelLocation.setValue(modelLocation);
    }

    @Override
    public void render(RenderLevelStageEvent event, PoseStack poseStack, float partialTicks) {
        if (modelLocation.getValue() == null || !(event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES)) {
            return;
        }

        if (model == null || modelLocation.getValue() == null) {
            model = new RenderableModel(modelLocation.getValue());
        }

        model.setActiveAnimation(0);

        int packedLight = getPackedLight();
        model.renderSingleModel(poseStack, partialTicks, packedLight);
    }

    public int getPackedLight() {
        Vector3f position = gameObject.getPosition().getValue();
        BlockPos blockPos = new BlockPos((int) position.x, (int) position.y, (int) position.z);

        int blockLight = gameObject.getLevel().getBrightness(LightLayer.BLOCK, blockPos);
        int skyLight = gameObject.getLevel().getBrightness(LightLayer.SKY, blockPos);

        return LightTexture.pack(blockLight, skyLight);
    }
}
