import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.stb.STBImage.*;

public class Test {
    public static void main(String[] args) {
        try(AIScene aiScene = Assimp.aiImportFile("C:\\Users\\魏宇强\\test.glb",aiProcess_Triangulate
                | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights)){
            if(aiScene == null) return;
            AINode root = aiScene.mRootNode();
            loadNode(root, aiScene);
            System.out.println();
            System.out.println(aiScene.mName().dataString());
            loadEmbeddedTexture(aiScene);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void loadEmbeddedTexture(AIScene scene){
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
                int width = w.get();
                int height = h.get();

                System.out.print(" " + width + "," + height);
            }
        }
        System.out.println();
    }

    static int depth = 0;

    protected static void loadNode(AINode node, AIScene scene){
        if(node == null) {
            return;
        }
        System.out.println();

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < depth; i++) stringBuilder.append(' ');
        stringBuilder.append(node.mName().dataString());
        System.out.print(stringBuilder);

        List<AIMesh> meshes = readMesh(node, scene);
        PointerBuffer materials = scene.mMaterials();
        if(meshes != null && materials != null){
            for(AIMesh mesh : meshes){

            }
        }

        PointerBuffer children = node.mChildren();
        int childrenNum = node.mNumChildren();
        depth++;
        for(int i = 0; i < childrenNum; i++){
            assert children != null;
            AINode child = AINode.create(children.get(i));
            loadNode(child, scene);
        }
        depth--;
    }

    protected static List<AIMesh> readMesh(AINode node, AIScene scene){
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
}
