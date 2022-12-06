package com.tac.guns.graph.math;

import com.mojang.math.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

import java.nio.FloatBuffer;

public class LocalMatrix4f {
    private final Matrix4f matrix4f;

    public LocalMatrix4f(Matrix4f matrix4f){
        this.matrix4f = matrix4f == null ? new Matrix4f() : matrix4f;
    }

    public LocalMatrix4f(AIMatrix4x4 matrix4x4){
        matrix4f = new Matrix4f(new float[]{matrix4x4.a1(), matrix4x4.a2(), matrix4x4.a3(), matrix4x4.a4(),
                                            matrix4x4.b1(), matrix4x4.b2(), matrix4x4.b3(), matrix4x4.b4(),
                                            matrix4x4.c1(), matrix4x4.c2(), matrix4x4.c3(), matrix4x4.c4(),
                                            matrix4x4.d1(), matrix4x4.d2(), matrix4x4.d3(), matrix4x4.d4()});
    }

    public LocalMatrix4f(){
        matrix4f = new Matrix4f();
    }

    public void setIdentity(){
        matrix4f.setIdentity();
    }

    public Matrix4f getMatrix4f(){
        return matrix4f;
    }

    public void store(FloatBuffer buffer){
        matrix4f.store(buffer);
    }

    public LocalMatrix4f copy(){
        return new LocalMatrix4f(this.matrix4f.copy());
    }

    public void multiply(LocalMatrix4f matrix4f){
        this.matrix4f.multiply(matrix4f.matrix4f);
    }

    public void multiply(LocalQuaternion quaternion){
        this.matrix4f.multiply(quaternion.getQuaternion());
    }

    public static LocalMatrix4f createTranslateMatrix(float x, float y, float z){
        return new LocalMatrix4f(Matrix4f.createTranslateMatrix(x, y, z));
    }

    public static LocalMatrix4f createScaleMatrix(float x, float y, float z){
        return new LocalMatrix4f(Matrix4f.createScaleMatrix(x, y, z));
    }
}
