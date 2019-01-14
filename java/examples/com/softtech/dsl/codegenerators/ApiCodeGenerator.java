package com.softtech.dsl.codegenerators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.softtech.dsl.model.definitions.Application;
import com.softtech.dsl.model.definitions.Entity;

public class ApiCodeGenerator {

	private Application app;
	private String rootPath;

	public ApiCodeGenerator(Application app,String path) {
		this.app=app;		
		this.rootPath=path;
	}

	public ArrayList<MemoryFile> Generate() throws IOException {
		
		ArrayList<MemoryFile> apiFiles = new ArrayList<>();
		
		createDirectories(apiFiles);

        //copyDependencies(apiFiles);

        processTemplateFiles(apiFiles);
        
        return apiFiles;
		
	}

	private void processTemplateFiles(ArrayList<MemoryFile> apiFiles) throws IOException
    {       
        Path templatesDir = Paths.get("C:\\Users\\is96214\\source\\repos\\Softtech.Dsl\\HPaPaaS.CodeGenerator\\Files\\Api\\templates");
        
        for (Path filePath : Files.walk(templatesDir).filter(p -> p.toString().endsWith(".template")).collect(Collectors.toList())) {
        	String tmpFileName = filePath.toString().replace(".template","");
        	String targetFileName= tmpFileName.replace(templatesDir.toString(), rootPath);
        	
        	if (tmpFileName.contains("$featurename")) {
        		for(Entity childEntity : app.getEntities()) {
        			
        			String targetEntityFileName = targetFileName.replaceAll(Pattern.quote("$featurename"),childEntity.getClassification().getName());
        			
        			String content = new String(Files.readAllBytes(filePath));
        			content = content.replaceAll(Pattern.quote("$featurename"), childEntity.getClassification().getName());
        			content = content.replaceAll(Pattern.quote("$props"), buildProps(childEntity));
        			
        			if (content.contains("$jsonBody")) {
        				content = content.replaceAll(Pattern.quote("$jsonBody"), buildJsonBody(childEntity));
        			}
        			
        			if (content.contains("$formFields")) {
        				content = content.replaceAll(Pattern.quote("$formFields"), buildFormFields(childEntity));
        			}
        			
        			if (content.contains("$listTableHeaders")) {
        				content = content.replaceAll(Pattern.quote("$listTableHeaders"), buildListTableHeaders(childEntity));
        			}
        			
        			if (content.contains("$defaultFeatureValues")) {
        				content = content.replaceAll(Pattern.quote("$defaultFeatureValues"), buildDefaultFeatureValues(childEntity));
        			}
        			
        			
        			
        			
        			MemoryFile mf=new MemoryFile(targetEntityFileName, content.getBytes());
        			apiFiles.add(mf);
        		}
        	}else {
        		
        		boolean contentChanged=false;
        		String content = new String(Files.readAllBytes(filePath));
        		 if (tmpFileName.contains("$applicationname")) {
        			 targetFileName=targetFileName.replaceAll(Pattern.quote("$applicationname"),app.getClassification().getName());        			
        		 }        		
        		 
        		 if (content.contains("$applicationname")) {
        			 content = content.replaceAll(Pattern.quote("$applicationname"), app.getClassification().getName());
        			 contentChanged=true;
        		 }
        		
        		if (content.contains("$featurename")) {
        			ArrayList<String> featureLines=getFeatureLines(content);
        			
        			for (String featureLine : featureLines) {
        				StringBuilder featuresBuilder=new StringBuilder();
        				for (Entity childEntity : app.getEntities()) {
        					featuresBuilder.append(featureLine.replaceAll(Pattern.quote("$featurename"), childEntity.getClassification().getName())+"\r\n");
        				}
        				
        				content = content.replaceAll(Pattern.quote(featureLine), featuresBuilder.toString());
        				contentChanged=true;
        			}
        		}   
        		
        		if (content.contains("$menu")) {
    				content = content.replaceAll(Pattern.quote("$menu"), buildMenuLinks(app));
    				contentChanged=true;
        		}
        		
        		if (content.contains("$appImports")) {
    				content = content.replaceAll(Pattern.quote("$appImports"), buildAppImports(app));
    				contentChanged=true;
        		}
        		
        		if (content.contains("$appRoutes")) {
    				content = content.replaceAll(Pattern.quote("$appRoutes"), buildAppRoutes(app));
    				contentChanged=true;
        		}
        		
        		if (content.contains("$indexImports")) {
    				content = content.replaceAll(Pattern.quote("$indexImports"), buildIndexImports(app));
    				contentChanged=true;
        		}
        		
        		if (content.contains("$indexReducers")) {
    				content = content.replaceAll(Pattern.quote("$indexReducers"), buildIndexReducers(app));
    				contentChanged=true;
        		}
        		
        		if (content.contains("$initialStateReducers")) {
    				content = content.replaceAll(Pattern.quote("$initialStateReducers"), buildInitialStateReducers(app));
    				contentChanged=true;
        		}
        		
        		if (contentChanged) {
        			MemoryFile mf=new MemoryFile(targetFileName, content.getBytes());
        			apiFiles.add(mf);
        		}else {
        			MemoryFile mf=new MemoryFile(targetFileName, Files.readAllBytes(filePath));
        			apiFiles.add(mf);  
        		}        		        		        	
        	}
        }
    }      
	
	/*
	 customersReducer: { customers: [] },
    selectedCustomerReducer: { customer: undefined },
    
	 */
	
	private String buildInitialStateReducers(Application app) {
		StringBuilder initialStateReducersBuilder=new StringBuilder();

		
		for(Entity child: app.getEntities()) {
			String childName = child.getClassification().getName();
			initialStateReducersBuilder.append(childName.trim()+"sReducer: { "+childName.trim()+"s: [] }, \r\n");
			initialStateReducersBuilder.append("selected"+childName.trim()+"Reducer: { "+childName.trim()+": undefined }, \r\n");
			
		}
								
		return initialStateReducersBuilder.toString();
	}
	
	/*
	 customersReducer,
    selectedCustomerReducer,
	 */
	
	private String buildIndexReducers(Application app) {
		StringBuilder indexReducersBuilder=new StringBuilder();

		
		for(Entity child: app.getEntities()) {
			String childName = child.getClassification().getName();
			indexReducersBuilder.append(childName.trim()+"sReducer, \r\n");	
			indexReducersBuilder.append("selected"+childName.trim()+"Reducer, \r\n");
			
		}
								
		return indexReducersBuilder.toString();
	}
	
	/*
	 import customersReducer from './customersReducer';
import selectedCustomerReducer from './selectedCustomerReducer';
	 */
	
	private String buildIndexImports(Application app) {
		StringBuilder indexImportsBuilder=new StringBuilder();

		
		for(Entity child: app.getEntities()) {
			String childName = child.getClassification().getName();
			indexImportsBuilder.append("import "+childName.trim()+"sReducer from './"+childName.trim()+"sReducer'; \r\n");	
			indexImportsBuilder.append("import selected"+childName.trim()+"Reducer from './selected"+childName.trim()+"Reducer'; \r\n");
			
			
		}
								
		return indexImportsBuilder.toString();
	}
	
	/*
	 <Route path="/customers" component={CustomerListContainer} />
                        <Route exact path="/customer" component={AddOrEditCustomerContainer} />
                        <Route path="/customer/:_id" component={AddOrEditCustomerContainer} />
	 */
	private String buildAppRoutes(Application app) {
		StringBuilder appRoutesBuilder=new StringBuilder();

		
		for(Entity child: app.getEntities()) {
			String childName = child.getClassification().getName();
			appRoutesBuilder.append("<Route path=\"/"+childName.trim()+"s\" component={"+childName.trim()+"ListContainer} /> \r\n");	
			appRoutesBuilder.append("<Route exact path=\"/"+childName.trim()+"\" component={AddOrEdit"+childName.trim()+"Container} />\r\n");
			appRoutesBuilder.append("<Route path=\"/"+childName.trim()+"/:_id\" component={AddOrEdit"+childName.trim()+"Container} /> \r\n");
			
			
		}
								
		return appRoutesBuilder.toString();
	}
	
	
	/*
	 import CustomerListContainer from './customer/CustomerListContainer'; // eslint-disable-line import/no-named-as-default
import AddOrEditCustomerContainer from './customer/AddOrEditCustomerContainer'; // eslint-disable-line import/no-named-as-default
	 */
	
	private String buildAppImports(Application app) {
		StringBuilder appImportsBuilder=new StringBuilder();

		
		for(Entity child: app.getEntities()) {
			String childName = child.getClassification().getName().trim();
			appImportsBuilder.append("import "+childName+"ListContainer from './"+childName+"/"+childName+"ListContainer'; \r\n");	
			appImportsBuilder.append("import AddOrEdit"+childName+"Container from './"+childName+"/AddOrEdit"+childName+"Container'; \r\n");
			
		}
								
		return appImportsBuilder.toString();
	}
	
	/*
	 <NavLink className="nav-item nav-link" activeClassName="active" to="/customers" >Customers</NavLink>
	 */
	
	private String buildMenuLinks(Application app) {
		StringBuilder menuLinksBuilder=new StringBuilder();

		
		for(Entity child: app.getEntities()) {
			String childName = child.getClassification().getName().trim();
			menuLinksBuilder.append("<NavLink className=\"nav-item nav-link\" activeClassName=\"active\" to=\"/"+childName+"s\" >"+childName+"</NavLink> \r\n");	
			
		}
								
		return menuLinksBuilder.toString();
	}
	
	/*
	const customer = {
            _id: values._id,
            name: values.name,
            //watchHref: values.watchHref,
            // authorId: values.authorId,
            surname: values.surname,
            address: values.address
        };
	 */
	
	private String buildDefaultFeatureValues(Entity entity) {
		StringBuilder defaultFeatureValuesBuilder=new StringBuilder();

		String entityName = entity.getClassification().getName().trim();
		defaultFeatureValuesBuilder.append("const "+entityName+" = { \r\n");
		defaultFeatureValuesBuilder.append("_id: values._id, \r\n");	
		for(String childName : entity.getEntities().keySet()) {
			defaultFeatureValuesBuilder.append(childName.trim()+": values."+childName.trim()+", \r\n");	
			
		}
		
		defaultFeatureValuesBuilder.append("}; \r\n");	
					
		return defaultFeatureValuesBuilder.toString();
	}
	
	/*
	 <TableHeaderColumn 
                    dataField="name"
                    dataFormat={titleFormatter} 
                    dataSort={true}
                    caretRender={getCaret}
                    filter={{type: 'TextFilter', delay: 0 }}
                    columnTitle>
                    Name
                </TableHeaderColumn>
	 */
	
	private String buildListTableHeaders(Entity entity) {
		StringBuilder listTableHeadersBuilder=new StringBuilder();

		String entityName = entity.getClassification().getName().trim();
		for(String childName : entity.getEntities().keySet()) {
			listTableHeadersBuilder.append("<TableHeaderColumn \r\n");
			listTableHeadersBuilder.append("dataField =\""+childName.trim()+"\" \r\n");
			listTableHeadersBuilder.append("dataFormat={titleFormatter} \r\n");
			listTableHeadersBuilder.append("dataSort={true} \r\n");
			listTableHeadersBuilder.append("caretRender={getCaret} \r\n");
			listTableHeadersBuilder.append("filter={{type: 'TextFilter', delay: 0 }} \r\n");
			listTableHeadersBuilder.append("columnTitle> \r\n");
			listTableHeadersBuilder.append(childName.trim()+"\r\n");
			listTableHeadersBuilder.append("</TableHeaderColumn> \r\n");
		}
					
		return listTableHeadersBuilder.toString();
	}
	
	private String buildFormFields(Entity entity) {
		StringBuilder formFieldsBuilder=new StringBuilder();

		String entityName = entity.getClassification().getName().trim();
		for(String childName : entity.getEntities().keySet()) {
			formFieldsBuilder.append("<Field\r\n");
			formFieldsBuilder.append("type=\"text\"\r\n");
			formFieldsBuilder.append("name=\""+childName.trim()+"\"\r\n");
			formFieldsBuilder.append("label=\""+childName.trim()+"\"\r\n");
			formFieldsBuilder.append("placeholder=\""+entityName.trim()+" "+childName.trim()+"\"\r\n");
			formFieldsBuilder.append("component={FieldInput}\r\n");
			formFieldsBuilder.append("/>\r\n");
		}
					
		return formFieldsBuilder.toString();
	}

	/*
	  <Field
                type="text"
                name="name"
                label="Name"
                placeholder="$featurename Name"
                component={FieldInput}
            />
	 */
	
	
	/*
	 body: JSON.stringify({
                            "name": $featurename.name,
                            "surname": $featurename.surname,
                            "address": $featurename.address
                        })
	 */
	
	private String buildJsonBody(Entity entity) {
		StringBuilder jsonBodyBuilder=new StringBuilder();
		
		jsonBodyBuilder.append("body: JSON.stringify({\r\n");
		
		String entityName = entity.getClassification().getName();
		for(String childName : entity.getEntities().keySet()) {
			jsonBodyBuilder.append("\""+childName.trim()+"\": "+entityName.trim()+"."+childName.trim()+",\r\n");
		}
		
		jsonBodyBuilder.append("})\r\n");
		
		return jsonBodyBuilder.toString();
	}
    private String buildProps(Entity entity)
    {
        StringBuilder propBuilder = new StringBuilder();

        for(String childName : entity.getEntities().keySet())
        {
        	Entity child = entity.getEntities().get(childName);        
            propBuilder.append(childName.trim()+":{type:"+child.getClassification().getName().trim()+"},\r\n");
        }

        return propBuilder.toString();

    }

    private ArrayList<String> getFeatureLines(String contentStr)
    {
    	ArrayList<String> featureLines = new ArrayList<String>();
    	String lines[] = contentStr.split("\\r?\\n");
        for(String line : lines)
        {
            if (line.contains("$featurename"))
            {
                featureLines.add(line);
            }
        }

        return featureLines;
    }

	private void copyDependencies(ArrayList<MemoryFile> apiFiles) throws IOException {
		
        //copy dependencies
        Path nodeModulesSource = Paths.get("C:\\Users\\is96214\\source\\repos\\HPaPaaS\\HPaPaaS.CodeGenerator\\Files\\Api\\node_modules");       
        String nodeModulesTargetDir = rootPath+"\\node_modules";
      
        List<Path> allFiles = Files.walk(nodeModulesSource)   
        		.filter(p -> p.toFile().isDirectory() == false)
                .collect(Collectors.toList());
        
        
        for(Path fullFile : allFiles)
        {
        	
            byte[] content = Files.readAllBytes(fullFile);
            String targetFileName =fullFile.toString().replace(nodeModulesSource.toString(),nodeModulesTargetDir);
            
            apiFiles.add(new MemoryFile(targetFileName,content));            
        }
	}

	private void createDirectories(ArrayList<MemoryFile> apiFiles) {
		
		/*
		Files.createDirectories(Paths.get(apiDir + "\\api\\controllers"));
		Files.createDirectories(Paths.get(apiDir + "\\api\\models"));
		Files.createDirectories(Paths.get(apiDir + "\\api\\routes"));
		*/        		
	}

}


/*
package com.softtech.dsl.codegenerators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.softtech.dsl.model.definitions.Application;
import com.softtech.dsl.model.definitions.Entity;

public class ApiCodeGenerator {

	private Application app;
	private Path apiDir;

	public ApiCodeGenerator(Application app, Path apiDir) {
		this.app=app;
		this.apiDir=apiDir;
	}

	public void Generate() throws IOException {
		
		createDirectories();

        copyDependencies();

        processTemplateFiles();
		
	}

	private void processTemplateFiles() throws IOException
    {       
        Path templatesDir = Paths.get("C:\\Users\\is96214\\source\\repos\\Softtech.Dsl\\HPaPaaS.CodeGenerator\\Files\\Api\\templates");
        
        for (Path filePath : Files.walk(templatesDir).filter(p -> p.toString().endsWith(".template")).collect(Collectors.toList())) {
        	String tmpFileName = filePath.toString().replace(".template","");
        	String targetFileName= tmpFileName.replace(templatesDir.toString(), apiDir.toString());
        	
        	if (tmpFileName.contains("$featurename")) {
        		for(Entity childEntity : app.getEntities()) {
        			
        			String targetEntityFileName = targetFileName.replaceAll(Pattern.quote("$featurename"),childEntity.getClassification().getName());
        			
        			String content = new String(Files.readAllBytes(filePath));
        			content = content.replaceAll(Pattern.quote("$featurename"), childEntity.getClassification().getName());
        			content = content.replaceAll(Pattern.quote("$props"), buildProps(childEntity));
        			
        			Files.write(Paths.get(targetEntityFileName), content.getBytes());
        		}
        	}else {
        		targetFileName=targetFileName.replaceAll(Pattern.quote("$applicationname"),app.getClassification().getName());
        		String content = new String(Files.readAllBytes(filePath));        		        		
        		content = content.replaceAll(Pattern.quote("$applicationname"), app.getClassification().getName());
        		
        		if (content.contains("$featurename")) {
        			ArrayList<String> featureLines=getFeatureLines(content);
        			StringBuilder featuresBuilder=new StringBuilder();
        			for (String featureLine : featureLines) {
        				for (Entity childEntity : app.getEntities()) {
        					featuresBuilder.append(featureLine.replaceAll(Pattern.quote("$featurename"), childEntity.getClassification().getName())+"\r\n");
        				}
        				
        				content = content.replaceAll(Pattern.quote(featureLine), featuresBuilder.toString());
        			}
        		}
        		
        		Files.write(Paths.get(targetFileName), content.getBytes());
        	}
        }
    }                   

    private String buildProps(Entity entity)
    {
        StringBuilder propBuilder = new StringBuilder();

        for(String childName : entity.getEntities().keySet())
        {
        	Entity child = entity.getEntities().get(childName);        
            propBuilder.append(childName+":{type:"+child.getClassification().getName()+"},\r\n");
        }

        return propBuilder.toString();

    }

    private ArrayList<String> getFeatureLines(String contentStr)
    {
    	ArrayList<String> featureLines = new ArrayList<String>();
    	String lines[] = contentStr.split("\\r?\\n");
        for(String line : lines)
        {
            if (line.contains("$featurename"))
            {
                featureLines.add(line);
            }
        }

        return featureLines;
    }

	private void copyDependencies() throws IOException {
		
        //copy dependencies
        Path nodeModulesSource = Paths.get("C:\\Users\\is96214\\source\\repos\\HPaPaaS\\HPaPaaS.CodeGenerator\\Files\\Api\\node_modules");
        Path  nodeModulesTarget = Paths.get(apiDir.toString() + "\\node_modules");

        List<Path> allFiles = Files.walk(nodeModulesSource)            
                .collect(Collectors.toList());
        
        
        for(Path fullFile : allFiles)
        {
            Path targetFile = Paths.get(fullFile.toString().replace(nodeModulesSource.toString(),nodeModulesTarget.toString()));
            Path targetDir = Paths.get(targetFile.toString().substring(0, targetFile.toString().lastIndexOf("\\")));
            if (Files.notExists(targetDir))
            {
            	Files.createDirectory(targetDir);
            }
            
            Files.copy(fullFile, targetFile);
        }
	}

	private void createDirectories() throws IOException {
		Files.createDirectories(Paths.get(apiDir + "\\api\\controllers"));
		Files.createDirectories(Paths.get(apiDir + "\\api\\models"));
		Files.createDirectories(Paths.get(apiDir + "\\api\\routes"));        		
	}

}
*/