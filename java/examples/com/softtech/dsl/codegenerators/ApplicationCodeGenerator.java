package com.softtech.dsl.codegenerators;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import com.softtech.dsl.model.definitions.Application;

public class ApplicationCodeGenerator {

	private Application app;
	private String rootPath;	

	public ApplicationCodeGenerator(Application app,String rootPath) {
		this.app=app;		
		this.rootPath = rootPath;
		
	}
	
	public ArrayList<MemoryFile> Generate() throws IOException{
        //String appName = app.getClassification().getName();                             
               
        ArrayList<MemoryFile> fileList=new ArrayList<>();
        
        ArrayList<MemoryFile> apiFiles=new ApiCodeGenerator(app,rootPath+ "\\api").Generate();
        
        fileList.addAll(apiFiles);
        
        return fileList;
    }

	private void deleteAllContent(Path appPath) throws IOException {
		
		if(Files.notExists(appPath)) {
			return;
		}
				
		Files.walkFileTree(appPath, new SimpleFileVisitor<Path>() {
			   @Override
			   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				   try {
					   Files.delete(file);
				   }catch(Exception ex) {
					   
				   }
			       
			       return FileVisitResult.CONTINUE;
			   }

			   @Override
			   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				   try {
					   Files.delete(dir);
				   }catch(Exception ex) {
					   
				   }
			       return FileVisitResult.CONTINUE;
			   }
			});
		
	}
}


/*
package com.softtech.dsl.codegenerators;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.softtech.dsl.model.definitions.Application;

public class ApplicationCodeGenerator {

	private Application app;
	private String rootDir;

	public ApplicationCodeGenerator(Application app,String rootDir) {
		this.app=app;
		this.rootDir=rootDir;		
		
	}
	
	public void Generate() throws IOException{
        String appName = app.getClassification().getName();
        Path appPath = Paths.get(rootDir +"\\" +appName);
        
        
        deleteAllContent(appPath);

        // create api root dir
        Path apiDir =Paths.get(appPath+"\\");            
        Files.createDirectories(apiDir);

        new ApiCodeGenerator(app, apiDir).Generate();
        
        
    }

	private void deleteAllContent(Path appPath) throws IOException {
		
		if(Files.notExists(appPath)) {
			return;
		}
				
		Files.walkFileTree(appPath, new SimpleFileVisitor<Path>() {
			   @Override
			   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				   try {
					   Files.delete(file);
				   }catch(Exception ex) {
					   
				   }
			       
			       return FileVisitResult.CONTINUE;
			   }

			   @Override
			   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				   try {
					   Files.delete(dir);
				   }catch(Exception ex) {
					   
				   }
			       return FileVisitResult.CONTINUE;
			   }
			});
		
	}
}

 */
