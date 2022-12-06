package com.tac.guns.graph;

import com.tac.guns.graph.util.Buffers;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private int textureId;

    public Texture(int width, int height, ByteBuffer buf) {
        generateTexture(width, height, buf);
    }

    public Texture(ResourceLocation resourceLocation){
        ByteBuffer imageBuffer;
        try {
            imageBuffer = Buffers.getByteBufferFromResource(resourceLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer buf = stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (buf == null) {
                throw new RuntimeException("Image file [" + resourceLocation.getPath() + "] not loaded: " + stbi_failure_reason());
            }

            int width = w.get();
            int height = h.get();

            generateTexture(width, height, buf);

            stbi_image_free(buf);
        }
    }

    public Texture(AIScene scene){
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

                generateTexture(width, height, buf);

                stbi_image_free(buf);
            }
        }
    }


    public void cleanup() {
        glDeleteTextures(textureId);
    }

    public int getTextureId() {
        return textureId;
    }

    private void generateTexture(int width, int height, ByteBuffer buf) {
        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }
}
