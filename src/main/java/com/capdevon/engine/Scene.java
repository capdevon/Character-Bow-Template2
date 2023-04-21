package com.capdevon.engine;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.state.AppState;

/**
 *
 * @author capdevon
 */
public class Scene {

    protected final String name;
    protected final List<Class<? extends AppState>> systemPrefabs;

    public Scene(String name) {
        this.name = name;
        systemPrefabs = new ArrayList<>();
    }

    public void addSystemPrefab(Class<? extends AppState> clazz) {
        systemPrefabs.add(clazz);
    }

    public String getName() {
        return name;
    }

}
