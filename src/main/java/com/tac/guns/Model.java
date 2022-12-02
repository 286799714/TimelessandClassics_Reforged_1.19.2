package com.tac.guns;

import com.tac.guns.graph.ModelPart;

import java.util.*;

public class Model {
    private final String id;

    private final Map<String , ModelPart> modelParts;

    public Model(String id, List<ModelPart> modelParts) {
        this.id = id;
        this.modelParts = new HashMap<>();
        modelParts.forEach(modelPart -> this.modelParts.put(modelPart.getName(), modelPart));
    }

    public Model(String id) {
        this.id = id;
        this.modelParts = new HashMap<>();
    }

    public void cleanup() {
        modelParts.values().forEach(ModelPart::cleanUp);
    }

    public String getId() {
        return id;
    }

    public Map<String, ModelPart> getModelParts() {
        return modelParts;
    }

    public void putModelPart(ModelPart modelPart){
        modelParts.put(modelPart.getName(), modelPart);
    }

    public void removeModelPart(String name){
        modelParts.remove(name);
    }
}