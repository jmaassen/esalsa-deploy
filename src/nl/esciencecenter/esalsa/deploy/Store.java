package nl.esciencecenter.esalsa.deploy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;

public class Store<T extends StoreableObject> { 
	
	private final String name;
	private StoredMap<String, T> map;
	
	public Store(Database db, EntryBinding<String> keyBinding, EntryBinding<T> dataBinding, String name) { 
		this.name = name;
		map = new StoredMap<String, T>(db, keyBinding, dataBinding, true);
	}
	
	public void add(T value) throws Exception {
	
		if (value == null) { 
    		throw new IllegalArgumentException(name + ": cannot store null object!");
    	}
    	
    	String ID = value.ID;
    	
    	if (ID == null || ID.length() == 0) { 
    		throw new IllegalArgumentException(name + ": cannot store object without ID!");
    	}
    	
    	if (map.containsKey(ID)) { 
			throw new Exception(name + ": " + ID + " already exists in database!");
		}

		map.put(value.ID, value);	    	    	
	}
	
	public void update(T value) throws Exception {
		
		if (value == null) { 
    		throw new IllegalArgumentException(name + ": cannot store null object!");
    	}
    	
    	String ID = value.ID;
    	
    	if (ID == null || ID.length() == 0) { 
    		throw new IllegalArgumentException(name + ": cannot store object without ID!");
    	}
    	
    	if (!map.containsKey(ID)) { 
			throw new Exception(name + ": " + ID + " does not exists in database -- cannot update it!");
		}

		map.put(value.ID, value);	    	    	
	}

	public T get(String ID) throws Exception {
		
		if (ID == null || ID.length() == 0) { 
    		throw new IllegalArgumentException(name + ": cannot retrieve object without ID!");
    	}
    	
    	if (!map.containsKey(ID)) { 
			throw new Exception(name + ": cannot retrieve object with ID " + ID + " (ID not found)!");
		}

    	return map.get(ID);	    	    	
	}
	
	public void remove(String ID) throws Exception {
    	
    	if (ID == null || ID.length() == 0) { 
    		throw new IllegalArgumentException(name + ": cannot remove object without ID!");
    	}
    	
    	if (!map.containsKey(ID)) { 
			throw new Exception(name + ": cannot delete object with ID " + ID + " (ID not found)!");
		}

		map.remove(ID);
    }
    
	public List<String> getKeys() {    
    	Set<String> tmp = map.keySet();
		ArrayList<String> result = new ArrayList<String>(tmp.size());
		result.addAll(tmp);		
		return result;    	
    }
}
