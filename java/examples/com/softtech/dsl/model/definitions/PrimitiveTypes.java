package com.softtech.dsl.model.definitions;

import java.util.Arrays;
import java.util.HashSet;

public class PrimitiveTypes {
	
	 public static Classification StringTypeClassification = new Classification("String", "Character Array", "Primitives", new HashSet<String> (Arrays.asList("string", "String", "Text" )));
     public static Entity StringTypeEntity = new Entity(StringTypeClassification,null);

     public static Classification Int32TypeClassification = new Classification("int", "Integer Number", "Primitives", new HashSet<String> (Arrays.asList( "int", "Integer", "Number" )));
     public static Entity Int32TypeEntity = new Entity(Int32TypeClassification, null);

     public static Classification DateTimeTypeClassification = new Classification("DateTime", "Date", "Primitives", new HashSet<String> (Arrays.asList( "DateTime", "Date","Time" )));
     public static Entity DateTimeTypeEntity = new Entity(DateTimeTypeClassification, null);
}
