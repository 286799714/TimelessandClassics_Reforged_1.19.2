package com.tac.guns.graph.math;

import com.mojang.math.Matrix4f;

import java.nio.FloatBuffer;

public class LocalMatrix4f {
    private final Matrix4f matrix4f;

    public LocalMatrix4f(Matrix4f matrix4f){
        this.matrix4f = matrix4f == null ? new Matrix4f() : matrix4f;
    }

    public LocalMatrix4f(){
        matrix4f = new Matrix4f();
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
