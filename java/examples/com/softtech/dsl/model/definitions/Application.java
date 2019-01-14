package com.softtech.dsl.model.definitions;

import java.util.List;

public class Application {
	 public Application(Classification classification,List<Entity> entities) 
     {
         this.classification = classification;
         this.entities = entities;
     }

     private Classification classification;
     public Classification getClassification() {
    	 return classification;
     }
     private List<Entity> entities;
     public List<Entity> getEntities(){
    	 return entities;
     }
}
