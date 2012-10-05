package nl.esciencecenter.esalsa.deploy;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConfigurationTemplate extends StoreableObject implements Serializable {

	private static final long serialVersionUID = -7572136097006754510L;

	public class Field implements Serializable { 
		
		private static final long serialVersionUID = -2805282476737841430L;

		public final String key;
		public final String value;
		public final String variableName;

		public Field(String key, String value, String variableName) {
			this.key = key;
			this.value = value;
			this.variableName = variableName;
		}
		
		public String toString() { 
			return key + " = " + value;
		}
	}
	
	public class Block implements Serializable { 

		private static final long serialVersionUID = -4816608510918107495L;

		public final String name;
		
		private final List<Field> fields;
		
		public Block(String name) { 
			this.name = name;
			fields = new LinkedList<Field>();
		}
		
		public void add(String key, String value, String variableName) {
			fields.add(new Field(key, value, variableName));
		}
		
		public String toString() { 
			StringBuilder sb = new StringBuilder(name + "\n");
			
			for (Field f : fields) {
				sb.append("   ");
				sb.append(f.toString());
				sb.append("\n");
			}
			
			sb.append("/\n");
			
			return sb.toString();
		}
		
	}
	
	private LinkedList<ConfigurationTemplate.Block> blocks = new LinkedList<ConfigurationTemplate.Block>();
	
	private HashSet<String> variables = new HashSet<String>();
	
	public ConfigurationTemplate(String ID, String comment) {
		super(ID, comment);
	}
	
	public ConfigurationTemplate.Block addBlock(String name) { 
		ConfigurationTemplate.Block b = new ConfigurationTemplate.Block(name);
		blocks.add(b);
		return b;
	}
	
	public String [] getVariables() { 
		return variables.toArray(new String [variables.size()]);
	}
	
	public void addVariable(String value) { 
		if (value != null) { 
			variables.add(value);
		}
	}

	private String setVariable(String text, String variableName, Map<String,String> variables) throws Exception { 
		
		//System.out.println("SET VARIABLE " + text + " -> " + variableName);
		
		String value = variables.get(variableName);
		
		if (value == null) { 
			throw new Exception("Failed to expand variable to a value: " + variableName);
		}
		
		String toReplace = "${" + variableName + "}";
		
		int index = text.indexOf(toReplace);
		
		if (index < 0) {
			throw new Exception("Failed to find variable " + variableName + " text: " + text);
		}
		
		return text.substring(0, index) + value + text.substring(index + toReplace.length());
	}
	
	private void generate(Field field, Map<String,String> variables, StringBuilder sb) throws Exception {	

		sb.append("   " + field.key + " = ");
		
		if (field.variableName == null) { 
			sb.append(field.value);
		} else { 
			sb.append(setVariable(field.value, field.variableName, variables));
		}
	
		sb.append("\n");
	}
	
	private void generate(Block block, Map<String,String> variables, StringBuilder sb) throws Exception {	
		
		sb.append(block.name + "\n");
		
		for (Field f : block.fields) { 
			generate(f, variables, sb);
		}
		
		sb.append("/\n");		
	}
	
	public String generate(Map<String,String> variables) throws Exception {	

		StringBuilder sb = new StringBuilder();
		
		for (Block block : blocks) { 
			generate(block, variables, sb);
			sb.append("\n");
		}
		
		return sb.toString();
	}	
	
	public String toString() { 
		
		StringBuilder sb = new StringBuilder();
		
		for (Block block : blocks) {
			sb.append(block.toString());
			sb.append("\n");
		}
		
		return sb.toString();
		
	}
	
}
