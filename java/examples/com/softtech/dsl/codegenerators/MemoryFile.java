package com.softtech.dsl.codegenerators;

public class MemoryFile {
    public MemoryFile(String fileName,byte[] content) {
		
    	this.fileName = fileName;
    	this.content=content;
	}
	private String fileName;
	public String getFileName() {
		return fileName;
	}
    private  byte[] content;
    public byte[] getContent() {
    	return content;
    }
}
