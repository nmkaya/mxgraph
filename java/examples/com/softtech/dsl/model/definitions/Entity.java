package com.softtech.dsl.model.definitions;

import java.util.HashMap;

public class Entity {

	public Entity(Classification classification, HashMap<String,Entity> entities)
    {
        this.classification = classification;
        this.entities = entities;
    }

    private Classification classification;
    public Classification getClassification() {
    	return classification;
    }
    
    private HashMap<String, Entity> entities;
    public HashMap<String,Entity> getEntities(){
    	return entities;
    }
}
