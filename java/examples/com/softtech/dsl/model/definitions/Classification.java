package com.softtech.dsl.model.definitions;

import java.util.HashSet;

public class Classification {
	private String name;
	public String getName() {
		return name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	private String domain;
	public String getDomain() {
		return domain;
	}
	private HashSet<String> tags;
	public HashSet<String> getTags() {
		return tags;
	}

    public Classification(String name,String description, String domain, HashSet<String> tags)
    {
        this.name= name;
        this.description = description;
        this.domain = domain;
        this.tags = tags;
    }
}
