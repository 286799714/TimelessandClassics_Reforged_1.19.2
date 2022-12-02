package com.tac.guns.graph.math;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public class LocalVector3f {
    private final Vector3f vector3f;

    public static LocalVector3f XN = new LocalVector3f(new Vector3f(-1.0F, 0.0F, 0.0F));
    public static LocalVector3f XP = new LocalVector3f(new Vector3f(1.0F, 0.0F, 0.0F));
    public static LocalVector3f YN = new LocalVector3f(new Vector3f(0.0F, -1.0F, 0.0F));
    public static LocalVector3f YP = new LocalVector3f(new Vector3f(0.0F, 1.0F, 0.0F));
    public static LocalVector3f ZN = new LocalVector3f(new Vector3f(0.0F, 0.0F, -1.0F));
    public static LocalVector3f ZP = new LocalVector3f(new Vector3f(0.0F, 0.0F, 1.0F));
    public static LocalVector3f ZERO = new LocalVector3f(new Vector3f(0.0F, 0.0F, 0.0F));

    public LocalVector3f(Vector3f vector3f){
        this.vector3f = vector3f;
    }

    public LocalVector3f(float x, float y, float z){
        vector3f = new Vector3f(x, y, z);
    }

    public void setX(float value){
        vector3f.setX(value);
    }

    public void setY(float value){
        vector3f.setY(value);
    }

    public void setZ(float value){
        vector3f.setZ(value);
    }

    public float x(){
        return vector3f.x();
    }

    public float y(){
        return vector3f.y();
    }

    public float z(){
        return vector3f.z();
    }

    public Vector3f getVector3f(){
        return vector3f;
    }

    public LocalQuaternion rotationDegrees(float degree){
        return new LocalQuaternion(new Quaternion(this.vector3f, degree, true));
    }
}
