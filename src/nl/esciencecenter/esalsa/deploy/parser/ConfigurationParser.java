package nl.esciencecenter.esalsa.deploy.parser;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.esciencecenter.esalsa.deploy.EnsembleDescription;

public abstract class ConfigurationParser {
	
	protected final Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);
	
	protected File source;
	
	protected ConfigurationParser(File source) {
		this.source = source;
	}

	protected void checkFile(File file, String name) throws FileNotFoundException { 
		
		if (!file.exists()) { 
			throw new FileNotFoundException(name + " file does not exist: " + file.getName());
		}
		
		if (!file.canRead() || !file.isFile()) { 
			throw new FileNotFoundException(name + " file not accesible: " + file.getName());
		}
	}

	protected void checkDirectory(File dir, String name) throws FileNotFoundException { 
		
		if (!dir.exists()) { 
			throw new FileNotFoundException(name + " directory does not exists: " + dir.getName());
		}
		
		if (!dir.canRead() || !dir.isDirectory()) { 
			throw new FileNotFoundException(name + " directory not accesible: " + dir.getName());
		}
	}

	
	public abstract EnsembleDescription parse() throws Exception;
}
