package com.tac.guns.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {

    private final int vaoId;

    private final int vboId;

    private final int idxVboId;

    private final int texVboId;

    private final Texture texture;

    private final int vertexCount;



    public Mesh(List<Float> positions, List<Float> textCoords, List<Integer> indices, Texture texture) {
        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        this.texture = texture;
        try {
            vertexCount = indices.size();
            verticesBuffer = memAllocFloat(positions.size());
            for(float position : positions){
                verticesBuffer.put(position);
            }
            verticesBuffer.flip();
            indicesBuffer = MemoryUtil.memAllocInt(indices.size());
            for(int indice : indices){
                indicesBuffer.put(indice);
            }
            indicesBuffer.flip();
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.size());
            for(float coord : textCoords){
                textCoordsBuffer.put(coord);
            }
            textCoordsBuffer.flip();

            int currentVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            int currentVBO = GL11.glGetInteger(GL_ARRAY_BUFFER_BINDING);
            int currentEVBO = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

            idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            texVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, texVboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            GL30.glBindVertexArray(currentVAO);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, currentVBO);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, currentEVBO);
        } finally {
            if (verticesBuffer  != null) {
                memFree(verticesBuffer);
                memFree(indicesBuffer);
                memFree(textCoordsBuffer);
            }
        }
    }

    public void render() {
        int currentVAO = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        glBindVertexArray(getVaoId());
        {
            glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
            boolean currentDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
            boolean currentBlend = GL11.glGetBoolean(GL11.GL_BLEND);
            GL20.glVertexAttrib4f(1, 1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            glActiveTexture(GL_TEXTURE0);
            glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
            if(!currentDepthTest) GL11.glDisable(GL11.GL_DEPTH_TEST);
            if(!currentBlend) GL11.glDisable(GL11.GL_BLEND);
        }
        glBindVertexArray(currentVAO);
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVboId() {
        return vboId;
    }

    public int getIdxVboId() {
        return idxVboId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanUp(){
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(vboId);
        glDeleteBuffers(idxVboId);
        glDeleteBuffers(texVboId);
        texture.cleanup();
    }
}