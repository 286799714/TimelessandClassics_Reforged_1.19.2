package com.tac.guns.graph.math;

import com.mojang.math.Quaternion;

public class LocalQuaternion {
    private final Quaternion quaternion;

    public LocalQuaternion(Quaternion quaternion){
        this.quaternion = quaternion;
    }

    public Quaternion getQuaternion(){
        return quaternion;
    }
}
