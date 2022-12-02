package com.tac.guns.graph;

/*
 * The following code is from project MCgLTF, using MIT license
 * url: https://github.com/ModularMods/MCglTF
 */
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.ShaderInstance;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Model {
    /**
     * ShaderMod attribute location for middle UV coordinates, used for parallax occlusion mapping.</br>
     * This may change in different Minecraft version.</br>
     * <a href="https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/shaders.txt">optifine/shaders.txt</a>
     */
    public static final int mc_midTexCoord = 12;

    /**
     * ShaderMod attribute location for Tangent.</br>
     * This may change in different Minecraft version.</br>
     * <a href="https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/shaders.txt">optifine/shaders.txt</a>
     */
    public static final int at_tangent = 13;

    /**
     * ShaderMod Texture index, this may change in different Minecraft version.</br>
     * <a href="https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/shaders.txt">optifine/shaders.txt</a>
     */
    public static final int COLOR_MAP_INDEX = GL13.GL_TEXTURE0;
    public static final int NORMAL_MAP_INDEX = GL13.GL_TEXTURE1;
    public static final int SPECULAR_MAP_INDEX = GL13.GL_TEXTURE3;

    public static int MODEL_VIEW_MATRIX;
    public static int MODEL_VIEW_MATRIX_INVERSE;
    public static int NORMAL_MATRIX;

    public static final int vaPosition = 0;
    public static final int vaColor = 1;
    public static final int vaUV0 = 2;
    public static final int vaUV1 = 3;
    public static final int vaUV2 = 4;
    public static final int vaNormal = 5;

    protected static final Runnable vanillaDefaultMaterialCommand = () -> {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, RenderSetting.INSTANCE.defaultColorMap);
        GL20.glVertexAttrib4f(vaColor, 1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_CULL_FACE);
    };

    protected static final Runnable shaderModDefaultMaterialCommand = () -> {
        GL13.glActiveTexture(COLOR_MAP_INDEX);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, RenderSetting.INSTANCE.defaultColorMap);
        GL13.glActiveTexture(NORMAL_MAP_INDEX);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, RenderSetting.INSTANCE.defaultNormalMap);
        GL13.glActiveTexture(SPECULAR_MAP_INDEX);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, RenderSetting.INSTANCE.defaultSpecularMap);
        GL20.glVertexAttrib4f(vaColor, 1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_CULL_FACE);
    };

    public static ShaderInstance CURRENT_SHADER_INSTANCE;
    public static Matrix4f CURRENT_POSE;
    public static Matrix3f CURRENT_NORMAL;
    public static Vector3f LIGHT0_DIRECTION;
    public static Vector3f LIGHT1_DIRECTION;

    protected static final int skinning_joint = 0;
    protected static final int skinning_weight = 1;
    protected static final int skinning_position = 2;
    protected static final int skinning_normal = 3;
    protected static final int skinning_tangent = 4;

    protected static final int skinning_out_position = 0;
    protected static final int skinning_out_normal = 1;
    protected static final int skinning_out_tangent = 2;

    protected static FloatBuffer uniformFloatBuffer = null;

    protected static final FloatBuffer BUF_FLOAT_9 = BufferUtils.createFloatBuffer(9);
    protected static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer(16);

    protected final Map<AINode, Pair<List<Runnable>, List<Runnable>>> rootNodeModelToCommands = new IdentityHashMap<>();

    private final AIScene scene;


    private final RenderedScene renderedScene;

    public Model(AIScene scene){
        this.scene = scene;
        renderedScene = new RenderedScene();
    }

    public void load(){
        loadNode(scene.mRootNode());
    }

    protected void loadNode(AINode node){
        if(node == null) return;

        Pair<List<Runnable>, List<Runnable>> commands = rootNodeModelToCommands.get(node);
        List<Runnable> vanillaRootRenderCommands;
        List<Runnable> shaderModRootRenderCommands;
        if(commands == null) {
            vanillaRootRenderCommands = new ArrayList<>();
            shaderModRootRenderCommands = new ArrayList<>();
            processNodeModel(node, vanillaRootRenderCommands, shaderModRootRenderCommands);
            rootNodeModelToCommands.put(node, Pair.of(vanillaRootRenderCommands, shaderModRootRenderCommands));
        }
        else {
            vanillaRootRenderCommands = commands.getLeft();
            shaderModRootRenderCommands = commands.getRight();
        }
        renderedScene.vanillaRenderCommands.addAll(vanillaRootRenderCommands);
        renderedScene.shaderModRenderCommands.addAll(shaderModRootRenderCommands);



        PointerBuffer children = node.mChildren();
        int childrenNum = node.mNumChildren();
        for(int i = 0; i < childrenNum; i++){
            assert children != null;
            try (AINode child = AINode.create(children.get(i))){
                loadNode(child);
            }
        }
    }

    protected void processNodeModel(AINode node, List<Runnable> vanillaRootRenderCommands, List<Runnable> shaderModRootRenderCommands){
        ArrayList<Runnable> vanillaNodeRenderCommands = new ArrayList<Runnable>();
        ArrayList<Runnable> shaderModNodeRenderCommands = new ArrayList<Runnable>();

    }

    public RenderedScene getRenderedScene() {
        return renderedScene;
    }
}
