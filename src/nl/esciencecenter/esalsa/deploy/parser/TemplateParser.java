package nl.esciencecenter.esalsa.deploy.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nl.esciencecenter.esalsa.deploy.Template;

public class TemplateParser {

	private final File file; 
	private final 	BufferedReader in;
	
	
	private Template template;
	private int lineNumber;
	
	public TemplateParser(File file) throws FileNotFoundException { 
		this.file = file;
		this.in = new BufferedReader(new FileReader(file)); 
	}
	
	private String readLine() throws IOException { 

		String line;
		
		do { 
			line = in.readLine();
		
			if (line == null) { 
				return null;
			}
		
			lineNumber++;
			line = line.trim();
		} while (line.length() == 0);
		
		return line;
	}

	private String findVariable(String value) throws Exception { 
	
		int start = value.indexOf("${");
		int end = value.indexOf("}");

		if (start < 0) { 
			return null;
		}
		
		if (start >= 0 && end > start+1) { 
			return value.substring(start+2, end);
		}

		throw new Exception("Parse error at: " + file.getName() + ":" + lineNumber + ": illegal variable declaration in value: " + value);
	}
	

	
	private boolean readBlockField(Template.Block b) throws Exception { 
	
		String line = readLine(); 
		
		if (line == null) { 
			throw new Exception("Parse error at: " + file.getName() + ":" + lineNumber + ": block " + b.name + " terminated unexpectedly!");
		}

		if (line.startsWith("/")) { 
			return false;
		}
		
		int index = line.lastIndexOf("=");
		
		if (index <= 0 || index == line.length()-1) { 
			throw new Exception("Parse error at: " + file.getName() + ":" + lineNumber + ": block " + b.name + " expected \"key = value\".");
		}

		String key = line.substring(0,  index).trim();
		String value = line.substring(index+1).trim();
		String variable = findVariable(value);
		
		//System.out.println("Adding field " + key + " = " + value + " (" + variable + ")");
		
		b.add(key, value, variable);
		
		template.addVariable(variable);
		
		return true;
	}
		
	
	private boolean readBlock() throws Exception { 
		
		String line = readLine(); 
		
		if (line == null) { 
			return false;
		}
		
		if (!line.startsWith("&")) { 
			throw new Exception("Parse error at: " + file.getName() + ":" + lineNumber + ": line does not start with &"); 
		}
		
		Template.Block b = template.addBlock(line);
		
		while (readBlockField(b));
					
		return true;
	}
	
	public Template parse() throws Exception { 
		
		template = new Template();
		
		while (readBlock());
		
		return template;
	}
	
}
