package nl.esciencecenter.esalsa.deploy.ui.swing;

public interface StoreCallback {
	void add(String item);
	void remove(String item);
	void clear();
}
