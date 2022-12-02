package com.tac.guns.graph;

import java.util.ArrayList;
import java.util.List;

public enum RenderData {
    INSTANCE;

    public final List<Runnable> renderDataList = new ArrayList<Runnable>();
}
