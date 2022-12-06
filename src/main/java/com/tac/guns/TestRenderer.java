package com.tac.guns;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.tac.guns.graph.*;
import com.tac.guns.graph.math.LocalMatrix4f;
import com.tac.guns.graph.math.LocalQuaternion;
import com.tac.guns.graph.math.LocalVector3f;
import com.tac.guns.graph.util.Buffers;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.units.qual.A;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public enum TestRenderer {
    INSTANCE;

    boolean init = false;

    private final List<Float> vertices = new ArrayList<>();
    private final List<Float> uv = new ArrayList<>();
    private final List<Integer> ind = new ArrayList<>();
    private String textName = "";

    Renderer renderer;
    Mesh mesh;

    com.tac.guns.graph.ModelPart modelPart;
    com.tac.guns.Model model;
    AIScene scene;
    PoseStack poseStack = new PoseStack();

    int i = 0;

    public void render(PoseStack poseStack){
        if(!init){
            try {
                renderer = new com.tac.guns.graph.Renderer();
                renderer.init();
                //test1是正常的模型
                //test2是多材质模型
                //test3是复杂的多分层多材质模型
                //test4是复杂的多材质模型（模型无分层）
                ResourceLocation modelResource = new ResourceLocation("tac", "models/test3.glb");
                String[] spilt = modelResource.getPath().split("\\.");
                String hint = spilt[spilt.length - 1];
                try( AIScene aiScene = Assimp.aiImportFileFromMemory(
                        Buffers.getByteBufferFromResource(modelResource),
                        aiProcess_Triangulate | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights,
                        new StringBuffer(hint)) ){
                    if(aiScene == null) return;
                    scene = aiScene;
                    model = new Model("test");
                    loadNode(scene.mRootNode());
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
                for(ModelPart modelPart : model.getModelParts().values()) {
                    //modelPart.translate(new LocalVector3f(0F,-1.5f,-3f));
                }
                init = true;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        for(ModelPart modelPart : model.getModelParts().values()) {
            //modelPart.rotate(new LocalVector3f(0,0.5f,0f));
        }
        LocalMatrix4f projectionMatrix = new LocalMatrix4f(RenderSystem.getProjectionMatrix());
        LocalMatrix4f worldMatrix =  LocalMatrix4f.createTranslateMatrix(0F,-1f,-2.5f);
        Minecraft.getInstance().getWindow().setTitle(scene.mName().dataString());
        glDisable(GL_CULL_FACE);
        renderer.render(projectionMatrix, worldMatrix, model);
        i=0;
    }

    public void loadMesh(AIMesh mesh, AINode node){
        AIVector3D.Buffer vertices = mesh.mVertices();
        int numVertices = mesh.mNumVertices();
        for(int i = 0; i < numVertices; i++){
            AIVector3D ver = vertices.get(i);
            this.vertices.add(ver.x());
            this.vertices.add(ver.y());
            this.vertices.add(ver.z());
        }

        AIVector3D.Buffer texcoords = mesh.mTextureCoords(0);
        if(texcoords != null) while (texcoords.remaining() > 0) {
            AIVector3D textCoord = texcoords.get();
            uv.add(textCoord.x());
            uv.add(1 - textCoord.y());
        }


        PointerBuffer aiMaterials = scene.mMaterials();
        AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(mesh.mMaterialIndex()));
        AIString texName = AIString.calloc();//材质所使用的纹理名字
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, texName, (IntBuffer) null, null, null, null, null, null);

        AIFace.Buffer faces = mesh.mFaces();
        int numFaces = mesh.mNumFaces();
        for(int i = 0; i < numFaces; i++){
            AIFace face = faces.get(i);
            IntBuffer indices = face.mIndices();
            int numIndices = face.mNumIndices();
            for(int j = 0; j < numIndices; j++){
                this.ind.add(indices.get(j));
            }
        }
        com.tac.guns.graph.Texture texture = new com.tac.guns.graph.Texture(scene,texName);

        this.mesh = new Mesh(this.vertices, uv, ind, texture);
        this.vertices.clear();
        this.uv.clear();
        this.ind.clear();
        textName = "";

        modelPart = new ModelPart(this.mesh, "test" + i);
        modelPart.setExtraMatrix(new LocalMatrix4f(poseStack.last().pose()));
        i++;
        model.putModelPart(modelPart);
    }

    public void loadNode(AINode node){
        if(node == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPoseMatrix(new LocalMatrix4f(node.mTransformation()).getMatrix4f());
        List<AIMesh> meshes = readMesh(node);
        if (meshes != null) {
            //assert meshes != null;
            for (AIMesh mesh : meshes) {
                loadMesh(mesh, node);
            }
        }
        PointerBuffer children = node.mChildren();
        int childrenNum = node.mNumChildren();
        for(int i = 0; i < childrenNum; i++){
            assert children != null;
            AINode child = AINode.create(children.get(i));
            loadNode(child);
        }
        poseStack.popPose();
    }

    public List<AIMesh> readMesh(AINode node){
        IntBuffer meshesIndexes = node.mMeshes();
        PointerBuffer meshes = scene.mMeshes();
        if(meshesIndexes == null || meshes == null) return null;
        int numMesh = node.mNumMeshes();
        List<AIMesh> meshList = new ArrayList<>();
        for(int i = 0; i < numMesh; i++){
            int meshIndex = meshesIndexes.get(i);
            meshList.add(AIMesh.create(meshes.get(meshIndex)));
        }
        return meshList;
    }

    public static LocalMatrix4f computeGlobalTransform(AINode node) {
        AINode currentNode = node;
        LocalMatrix4f matrix = new LocalMatrix4f();
        matrix.setIdentity();
        while (currentNode != null){
            matrix.multiply(new LocalMatrix4f(currentNode.mTransformation()));
            currentNode = currentNode.mParent();
        }
        return matrix;
    }
}
