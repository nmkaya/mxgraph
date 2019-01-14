package com.mxgraph.examples.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.softtech.dsl.codegenerators.ApplicationCodeGenerator;
import com.softtech.dsl.codegenerators.MemoryFile;
import com.softtech.dsl.model.definitions.Application;

/**
 * Downloading Multiple Files As Zip
 * 
 * @author JavaDigest
 */
public class DownloadManager  {

	public void DownloadApiFiles(OutputStream out,Application app)
			throws ServletException, IOException {
		 
		String rootPath=app.getClassification().getName();
		
        ZipOutputStream zipOutputStream = new ZipOutputStream(out);
        
        ArrayList<MemoryFile> memoryFiles=new ApplicationCodeGenerator(app,rootPath).Generate();
        try {
            for (MemoryFile memoryFile : memoryFiles) {
                ZipEntry zipEntry = new ZipEntry(memoryFile.getFileName());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(memoryFile.getContent());
                zipOutputStream.closeEntry();
            }
        } finally {
            zipOutputStream.close();
        }        
		
	}
}