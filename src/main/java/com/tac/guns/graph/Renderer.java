package com.tac.guns.graph;

import com.tac.guns.Model;
import com.tac.guns.graph.math.LocalMatrix4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Renderer {
    private ShaderProgram shaderProgram;

    public void init() throws Exception {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(RenderSetting.INSTANCE.vertexProgram, GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(RenderSetting.INSTANCE.fragmentProgram, GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("worldMatrix");
        shaderProgram.createUniform("texture_sampler");
    }

    public void render(LocalMatrix4f projectionMatrix, LocalMatrix4f worldMatrix, Model model){
        shaderProgram.bind(GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
        shaderProgram.setMatrixUniform("projectionMatrix", projectionMatrix);
        shaderProgram.setIntegerUniform("texture_sampler", 0);
        for(ModelPart modelPart : model.getModelParts().values()) {
            LocalMatrix4f worldMatrix1 = worldMatrix.copy();
            worldMatrix1.multiply(modelPart.getExtraMatrix());
            shaderProgram.setMatrixUniform("worldMatrix", worldMatrix1);
            modelPart.getMesh().render();
        }
        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
