package nl.esciencecenter.esalsa.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkableMap<V extends MarkableObject> {

	private final HashMap<String, V> map = new HashMap<String, V>();	
	private final Object lock;
	private final String contents;
	
	public MarkableMap(Object lock, String contents) { 
		this.lock = lock;
		this.contents = contents;
	}
		
	public void add(V value) throws Exception { 
	
		synchronized (lock) {
			if (map.containsKey(value.ID)) { 
				throw new Exception(contents + " " + value.ID + " already exists!");
			}

			map.put(value.ID, value);
		}
	}
	
	public void remove(String key) throws Exception { 
	
		synchronized (lock) {
			V temp = map.get(key);
	
			if (temp == null) { 
				throw new Exception(contents + " " + key + " not found!");
			}
	
			if (temp.isInUse()) { 
				throw new Exception(contents + " " + key + " cannot be deleted: it is currently in use!");
			}
	
			map.remove(key);
		}
	}

	public V get(String key) throws Exception {

		synchronized (lock) {
			V temp = map.get(key);
			
			if (temp == null) { 
				throw new Exception(contents + " " + key + " not found!");
			}
	
			return temp;
		}
	}	
	
	public List<String> getKeys() { 
		ArrayList<String> result = new ArrayList<String>(map.size());
		result.addAll(map.keySet());		
		return result;
	}
}
