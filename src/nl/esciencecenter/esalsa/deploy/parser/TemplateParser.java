package nl.esciencecenter.esalsa.deploy.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import nl.esciencecenter.esalsa.deploy.ConfigurationTemplate;

public class TemplateParser {

	private final String sourceName;
	private final 	BufferedReader in;
	
	private ConfigurationTemplate template;
	private int lineNumber = 0;
	
	public TemplateParser(ConfigurationTemplate template, File file) throws FileNotFoundException {
		this.template = template;
		this.in = new BufferedReader(new FileReader(file));
		sourceName = file.getName();
	}
	
	public TemplateParser(ConfigurationTemplate template, String input) throws FileNotFoundException {
		this.template = template;
		sourceName = "user input";
		this.in = new BufferedReader(new StringReader(input)); 
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

		throw new ParseException(sourceName, lineNumber, "Illegal variable declaration in value: " + value);
	}
		
	private boolean readBlockField(ConfigurationTemplate.Block b) throws Exception { 
	
		String line = readLine(); 
		
		if (line == null) { 
			throw new ParseException(sourceName, lineNumber, "Block " + b.name + " terminated unexpectedly!");
		}

		if (line.startsWith("/")) { 
			return false;
		}
		
		int index = line.lastIndexOf("=");
		
		if (index <= 0 || index == line.length()-1) { 
			throw new ParseException(sourceName, lineNumber, "Expected key value pair!");
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
			throw new ParseException(sourceName, lineNumber, "Block does not start with &"); 
		}
		
		ConfigurationTemplate.Block b = template.addBlock(line);
		
		while (readBlockField(b));
					
		return true;
	}
	
	public ConfigurationTemplate parse() throws Exception { 
		while (readBlock());
		return template;
	}	
}
