package com.tac.guns.graph;

import com.tac.guns.graph.math.LocalMatrix4f;
import com.tac.guns.graph.math.LocalVector3f;

public class ModelPart {
    private final String name;

    private final Mesh mesh;

    private LocalMatrix4f matrix4f;

    public ModelPart(Mesh mesh, String name) {
        this.mesh = mesh;
        matrix4f = LocalMatrix4f.createTranslateMatrix(0,0,0);
        this.name = name;
    }

    public ModelPart(Mesh mesh, String name, LocalMatrix4f matrix4f) {
        this.mesh = mesh;
        this.matrix4f = matrix4f;
        this.name = name;
    }

    public LocalMatrix4f getExtraMatrix(){
        return matrix4f;
    }

    public void setExtraMatrix(LocalMatrix4f matrix4f){
        this.matrix4f = matrix4f;
    }

    public void translate(LocalVector3f alpha){
        matrix4f.multiply(LocalMatrix4f.createTranslateMatrix(alpha.x(), alpha.y(), alpha.z()));
    }

    /**
     * By degree.
     * */
    public void rotate(LocalVector3f alpha){
        matrix4f.multiply(LocalVector3f.XP.rotationDegrees(alpha.x()));
        matrix4f.multiply(LocalVector3f.YP.rotationDegrees(alpha.y()));
        matrix4f.multiply(LocalVector3f.ZP.rotationDegrees(alpha.z()));
    }

    public Mesh getMesh() {
        return mesh;
    }

    public String getName() { return name; }

    public void cleanUp(){
        mesh.cleanUp();
    }
}