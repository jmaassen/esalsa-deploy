package nl.esciencecenter.esalsa.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class EventLogger {

	private static EventLogger eventLogger;
	
	private final BufferedWriter writer;
	private final LinkedList<Event> events = new LinkedList<Event>();
	public final boolean restart;
	
	private EventLogger(File file) throws IOException { 
		
		if (file.exists() && file.canRead() && file.isFile()) { 
			restart = true;
		
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Read states!
			String line = reader.readLine();
			
			while (line != null) { 
				
				try { 
					Event tmp = Event.parse(line);
					events.add(tmp);
				} catch (Exception e) {
					System.out.println("ERROR: failed to parse event " + line);
					e.printStackTrace();
				}
				
				line = reader.readLine();
			}

			this.writer = new BufferedWriter(new FileWriter(file, true));
		} else { 
			restart = false;
			this.writer = new BufferedWriter(new FileWriter(file, false));
		}
	}
	
	public synchronized void log(Event e) { 
		events.addLast(e);
		
		try { 
			writer.write(e.toString());
			writer.write("\n");
			writer.flush();
		} catch (Exception ex) {
			System.err.println("Failed to write log! " + ex);
			ex.printStackTrace(System.err);
		}
	} 
	
	public synchronized LinkedList<Event> findAll(String tag, String ID, String state, String message) { 
		
		LinkedList<Event> result = new LinkedList<Event>();
		
		for (Event e : events) { 
			if (e.match(tag, ID, state, message)) { 
				result.addLast(e);
			}
		}
		
		return result;
	}

	public synchronized boolean exists(String tag, String ID, String state, String message) { 
		return (findOne(tag, ID, state, message) != null);
	}
		
	public synchronized Event findOne(String tag, String ID, String state, String message) { 
		
		for (Event e : events) { 
			if (e.match(tag, ID, state, message)) { 
				return e;
			}
		}
		
		return null;
	}
	
	public void log(String tag, String ID, String state, String message) {
		log(new Event(tag, ID, state, message));
	}
	
	public static EventLogger get() {
		return eventLogger;
	}

	public static EventLogger get(File file) throws Exception {
		if (eventLogger != null) { 
			throw new Exception("EventLogger already created!");
		}
		
		eventLogger = new EventLogger(file);
		
		return eventLogger;
	}
}
