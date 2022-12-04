package com.tac.guns;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.graph.*;
import com.tac.guns.graph.math.LocalMatrix4f;
import com.tac.guns.graph.math.LocalVector3f;
import com.tac.guns.graph.util.Buffers;
import net.minecraft.client.Minecraft;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;

public enum TestRenderer {
    INSTANCE;

    boolean init = false;

    private static final float FOV = 70.0f;

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    float[] positions = new float[]{
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,

            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,

            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,

            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,

            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f,
    };
    float[] textCoords = new float[]{
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,

            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,

            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,

            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,

            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,
    };
    int[] indices = new int[]{
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7,};
    com.tac.guns.graph.Renderer renderer;
    com.tac.guns.graph.Mesh mesh;

    com.tac.guns.graph.ModelPart modelPart;
    com.tac.guns.Model model;
    AIScene scene;

    public void render(PoseStack poseStack){
        if(!init){
            try {

                renderer = new com.tac.guns.graph.Renderer();
                renderer.init();
                com.tac.guns.graph.Texture texture = new com.tac.guns.graph.Texture("assets/tac/textures/test.png");
                mesh = new com.tac.guns.graph.Mesh(positions, textCoords, indices, texture);
                modelPart = new com.tac.guns.graph.ModelPart(mesh, "test");
                model = new com.tac.guns.Model("test");
                model.putModelPart(modelPart);
                modelPart.setExtraMatrix(LocalMatrix4f.createTranslateMatrix(0F,0f,-3f));
                init = true;
                try(AIScene aiScene = Assimp.aiImportFileFromMemory(Buffers.loadResourceForJar("assets/tac/models/test.gltf"),aiProcess_Triangulate
                        | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights, (ByteBuffer) null)){
                    if(aiScene == null) return;
                    scene = aiScene;
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        modelPart.rotate(new LocalVector3f(0,0.5f,0f));
        LocalMatrix4f projectionMatrix = new LocalMatrix4f(RenderSystem.getProjectionMatrix());
        LocalMatrix4f worldMatrix =  LocalMatrix4f.createTranslateMatrix(0F,0f,0f);
        Minecraft.getInstance().getWindow().setTitle(scene.mName().dataString());
        glDisable(GL_CULL_FACE);
        renderer.render(projectionMatrix, worldMatrix, model);
    }
}
