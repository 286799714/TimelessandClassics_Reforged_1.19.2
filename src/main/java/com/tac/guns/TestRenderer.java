package com.tac.guns;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.graph.*;
import com.tac.guns.graph.math.LocalMatrix4f;
import com.tac.guns.graph.math.LocalVector3f;
import com.tac.guns.graph.util.Buffers;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
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

    int i = 0;

    public void render(PoseStack poseStack){
        if(!init){
            try {
                renderer = new com.tac.guns.graph.Renderer();
                renderer.init();
                ResourceLocation modelResource = new ResourceLocation("tac", "models/test2.glb");
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
                    modelPart.setExtraMatrix(LocalMatrix4f.createTranslateMatrix(0F,0f,-6f));
                }
                init = true;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        for(ModelPart modelPart : model.getModelParts().values()) {
            modelPart.rotate(new LocalVector3f(0,0.5f,0f));
        }
        LocalMatrix4f projectionMatrix = new LocalMatrix4f(RenderSystem.getProjectionMatrix());
        LocalMatrix4f worldMatrix =  LocalMatrix4f.createTranslateMatrix(0F,0f,0f);
        Minecraft.getInstance().getWindow().setTitle(scene.mName().dataString());
        glDisable(GL_CULL_FACE);
        renderer.render(projectionMatrix, worldMatrix, model);
        i=0;
    }

    public void loadMesh(AIMesh mesh){
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
        i++;
        model.putModelPart(modelPart);
    }

    public void loadNode(AINode node){
        if(node == null) {
            return;
        }
        List<AIMesh> meshes = readMesh(node);
        assert meshes != null;
        for(AIMesh mesh : meshes){
            loadMesh(mesh);
        }

        PointerBuffer children = node.mChildren();
        int childrenNum = node.mNumChildren();
        for(int i = 0; i < childrenNum; i++){
            assert children != null;
            AINode child = AINode.create(children.get(i));
            loadNode(child);
        }
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

    public void loadEmbeddedTexture(){
        PointerBuffer textures = scene.mTextures();
        for(int i = 0; i < scene.mNumTextures(); i++){
            try (MemoryStack stack = MemoryStack.stackPush()) {
                AITexture texture = AITexture.create(textures.get(i));
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                ByteBuffer buf = stbi_load_from_memory(texture.pcDataCompressed(), w, h, channels, 4);
                if (buf == null) {
                    throw new RuntimeException("name's embedded textures not loaded: " + stbi_failure_reason());//todo
                }
                //todo with buf
            }
        }
    }
}
