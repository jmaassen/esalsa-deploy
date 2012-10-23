package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.util.HashMap;
import java.util.List;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

public class RemoteStore<T extends StoreableObject> {
	
	private HashMap<String, T> map;	
	private StoreCallback callback;
	
	private SimpleStub stub;
	private int stubKey;
	
	public RemoteStore(SimpleStub stub, int stubKey) throws Exception {
		this.stub = stub;
		this.stubKey = stubKey;
		
		map = new HashMap<String, T>();
		
		List<String> keys = stub.list(stubKey);
		
		for (String s : keys) {
			map.put(s, null);
		}
	}

	public void addCallBack(StoreCallback callback) { 
		this.callback = callback;
		
		for (String s : map.keySet()) { 
			callback.add(s);
		}		
	}
	
	public boolean contains(String ID) {
		return map.containsKey(ID);
	}
	
	public boolean delete(String ID) throws Exception {
		
		if (map.containsKey(ID)) { 

			stub.remove(stubKey, ID);
			
			if (callback != null) { 
				callback.remove(ID);
			}
			
			return true;
		}
		
		return false;
	}
	
	public String [] getKeys() { 
		return map.keySet().toArray(new String[map.size()]);
	} 

	public boolean add(T e) throws Exception { 
	
		if (e == null) { 
			return false;
		}
		
		if (map.containsKey(e.getID())) { 
			return false;
		}

		// TODO: check if still consistent here!
		stub.add(stubKey, e);		
		map.put(e.getID(), e);
		
		if (callback != null) { 
			callback.add(e.getID());
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public T get(String ID) throws Exception { 
		
		if (ID == null) { 
			return null;
		}
		
		T elt = map.get(ID);
		
		if (elt == null) { 	
			// TODO: check if still consistent here!
			elt = (T) stub.get(stubKey, ID);			
			map.put(ID, elt);
		}
		
		return elt;
	}

	public void refresh() throws Exception {
		
		if (callback != null) { 
			callback.clear();
		}
		
		map.clear();
		
		List<String> keys = stub.list(stubKey);
		
		for (String s : keys) {
			map.put(s, null);
			
			if (callback != null) { 
				callback.add(s);
			}
		}
	}	
}
	