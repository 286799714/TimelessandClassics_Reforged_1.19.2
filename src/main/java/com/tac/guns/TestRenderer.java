package com.tac.guns;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.graph.*;
import com.tac.guns.graph.math.LocalMatrix4f;
import com.tac.guns.graph.math.LocalVector3f;
import net.minecraft.client.Minecraft;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public enum TestRenderer {
    INSTANCE;

    boolean init = false;

    private final List<Float> vertices = new ArrayList<>();
    private final List<Float> uv = new ArrayList<>();
    private final List<Integer> ind = new ArrayList<>();

    Renderer renderer;
    Mesh mesh;

    ModelPart modelPart;
    Model model;
    AIScene scene;

    public void render(PoseStack poseStack){
        if(!init){
            try {
                renderer = new Renderer();
                renderer.init();
                Texture texture = new Texture("C:\\test.png");
                try(AIScene aiScene = Assimp.aiImportFile("C:\\Users\\魏宇强\\test.glb",aiProcess_Triangulate
                        | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights)){
                    if(aiScene == null) return;
                    scene = aiScene;
                    loadNode(scene.mRootNode());
                    mesh = new Mesh(vertices, uv, ind, texture);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
                modelPart = new ModelPart(mesh, "test");
                model = new Model("test");
                model.putModelPart(modelPart);
                modelPart.setExtraMatrix(LocalMatrix4f.createTranslateMatrix(0F,0f,-6f));
                init = true;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        modelPart.rotate(new LocalVector3f(0,0.5f,0f));
        LocalMatrix4f projectionMatrix = new LocalMatrix4f(RenderSystem.getProjectionMatrix());
        LocalMatrix4f worldMatrix =  LocalMatrix4f.createTranslateMatrix(0F,0f,0f);
        Minecraft.getInstance().getWindow().setTitle(scene.mName().dataString());
        renderer.render(projectionMatrix, worldMatrix, model);
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

        AIVector3D.Buffer buffer = mesh.mTextureCoords(0);
        if(buffer != null) while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            uv.add(textCoord.x());
            uv.add(1 - textCoord.y());
        }

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
