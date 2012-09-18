package nl.esciencecenter.esalsa.util;

//import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.esciencecenter.esalsa.deploy.ResourceDescription;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.io.File;
import org.gridlab.gat.io.FileInterface;
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.Job.JobState;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.SoftwareDescription;
import org.gridlab.gat.security.PasswordSecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
//	private static int tempFileCounter = 0;
	
	/**
     * convert a list of Strings to a single comma separated String
     * 
     * @param list
     *            the input list
     * @return a comma separated version of the list
     */
    public static String strings2CSS(String[] list) {
        if (list == null) {
            return null;
        }

        if (list.length == 0) {
            return "";
        }
        String result = "";
        for (String object : list) {
            result = result + object.toString() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }
    
	private static Preferences getPreferences(ResourceDescription resource, String prefix) {
		
		if (resource == null) { 
			return null;
		}

		Preferences pref = null;
		
		if (resource.gateway != null) { 

			pref = addPreference(pref, "ssh.gateway.uri", resource.gateway.URI);

			if (resource.gateway.adaptors != null) { 
				pref = addPreference(pref, prefix + ".adaptor.name", strings2CSS(resource.gateway.adaptors));
			}

		} else { 

			if (resource.adaptors != null) {
				pref = addPreference(pref, prefix + ".adaptor.name", strings2CSS(resource.adaptors));
			}
		}
		
		return pref;
	}
	
	private static GATContext getContext(ResourceDescription resource) {
		
		if (resource == null) { 
			return null;
		}
		
		GATContext context = null;

		if (resource.gateway != null) { 
			if (resource.gateway.username != null) { 
				// TODO: add various flavours! + different context for gateway ?
				PasswordSecurityContext sc = new PasswordSecurityContext(resource.gateway.username, resource.gateway.userkey);
				context = new GATContext();
				context.addSecurityContext(sc);
			}

		} else if (resource.username != null) { 
			// TODO: add various flavours!
			PasswordSecurityContext sc = new PasswordSecurityContext(resource.username, resource.userkey);
			context = new GATContext();
			context.addSecurityContext(sc);
		}
		
		return context;
	} 	
	
	/*
    private static FileInterface getFileInterface(FileDescription fd) throws GATObjectCreationException { 
		
    	GATContext context = getContext(fd.file, fd.gateway);
		Preferences pref = getPreferences(fd.file, fd.gateway, "file");

		FileInterface tmp = null;
		
    	if (logger.isDebugEnabled()) { 
    		logger.debug("Attempt to access file " + fd + " using context: " + context + " and preferences: " + pref);
    	}
    	
		if (context == null && pref == null) {
			tmp = GAT.createFile(fd.file.URI).getFileInterface();
		} else if (context == null && pref != null) { 
			tmp = GAT.createFile(pref, fd.file.URI).getFileInterface();
		} else if (context != null && pref == null) { 
			tmp = GAT.createFile(context, fd.file.URI).getFileInterface();
		} else {
			tmp = GAT.createFile(context, pref, fd.file.URI).getFileInterface();
		}
		
		return tmp;
	}
	 */
	
	private static ResourceDescription findResourceDescription(java.net.URI uri) { 
		return null;
	}
	
	private static FileInterface getFileInterface(java.net.URI uri) throws Exception { 

		if (!uri.isAbsolute() || uri.isOpaque()) {
			throw new Exception("Cannot handle URI: " + uri + " (absolute=" +  uri.isAbsolute() + ", opaque=" + uri.isOpaque() + ")");
		} 
		
		org.gridlab.gat.URI tmpURI = new org.gridlab.gat.URI(uri.getScheme(), uri.getAuthority(), File.separator + uri.getPath(), uri.getQuery(), uri.getRawFragment());
		
		String scheme = uri.getScheme();
		String auth = uri.getAuthority();
			
		ResourceDescription resource = findResourceDescription(new java.net.URI(scheme, auth, "", null, null));
			
    	GATContext context = getContext(resource);
		Preferences pref = getPreferences(resource, "file");

		FileInterface tmp = null;
		
    	if (logger.isDebugEnabled()) { 
    		logger.debug("Attempt to access file " + uri + " using context: " + context + " and preferences: " + pref);
    	}
    	
		if (context == null && pref == null) {
			tmp = GAT.createFile(tmpURI).getFileInterface();
		} else if (context == null && pref != null) { 
			tmp = GAT.createFile(pref, tmpURI).getFileInterface();
		} else if (context != null && pref == null) { 
			tmp = GAT.createFile(context, tmpURI).getFileInterface();
		} else {
			tmp = GAT.createFile(context, pref, tmpURI).getFileInterface();
		}
		
		return tmp;
	}

	public static boolean accessibleFile(java.net.URI file) {
    	
    	FileInterface tmp = null;
    	
    	try { 
    		tmp = getFileInterface(file);
    	} catch (Exception e) {
    		logger.warn("Failed to check if " + file + " is accessible!", e);
    		return false;
    	}

    	try {
			return tmp.exists() && tmp.canRead() && tmp.isFile();
		} catch (GATInvocationException e) {
    		logger.warn("Failed to check if " + file + " is accessible! " + e);
			return false;
		}
	}

	public static boolean accessibleDirectory(java.net.URI dir) { 

		FileInterface tmp = null;
		
		try { 
			tmp = getFileInterface(dir);
		} catch (Exception e) {
    		logger.warn("Failed to check if " + dir + " is accessible!", e);
    		return false;
    	}

    	try {
			return tmp.exists() && tmp.canRead() && tmp.isDirectory();
		} catch (GATInvocationException e) {
    		logger.warn("Failed to check if " + dir + " is accessible! " + e);
			return false;
		}	
	}
	
	public static boolean exists(java.net.URI file) throws Exception {

		try { 
			FileInterface tmp = getFileInterface(file);
			return tmp.exists();
		} catch (Exception e) {
    		throw new Exception("Cannot check if " + file + " exists! ", e);
		}	
	}

	
/*	
    public static boolean accessibleFile(FileDescription fd) {
    	
    	FileInterface tmp = null;
    	
    	try { 
    		tmp = getFileInterface(fd);
    	} catch (GATObjectCreationException e) {
    		logger.warn("Failed to check if " + fd + " is accessible!", e);
    		return false;
    	}

    	try {
			return tmp.exists() && tmp.canRead() && tmp.isFile();
		} catch (GATInvocationException e) {
    		logger.warn("Failed to check if " + fd + " is accessible! " + e);
			return false;
		}
	}

	public static boolean accessibleDirectory(FileDescription fd) { 

		FileInterface tmp = null;
		
		try { 
			tmp = getFileInterface(fd);
		} catch (GATObjectCreationException e) {
    		logger.warn("Failed to check if " + fd + " is accessible!", e);
    		return false;
    	}

    	try {
			return tmp.exists() && tmp.canRead() && tmp.isDirectory();
		} catch (GATInvocationException e) {
    		logger.warn("Failed to check if " + fd + " is accessible! " + e);
			return false;
		}	
	}
	
	public static boolean exists(FileDescription fd) throws Exception {

		try { 
			FileInterface tmp = getFileInterface(fd);
			return tmp.exists();
		} catch (Exception e) {
    		throw new Exception("Cannot check if " + fd + " exists! ", e);
		}	
	}	
*/	
	
	
	public static FileDescription getSubFile(FileDescription parent, String filename) {
		String URI = parent.file.URI + java.io.File.separator + filename;
		ResourceDescription r = new ResourceDescription(URI, parent.file);  
		return new FileDescription(r, parent.gateway);
	}
	
	public static String stripScheme(String URI) throws Exception { 
		
		int index = URI.indexOf("://");
		
		if (index < 0) { 
			throw new Exception("Failed to remove scheme from URI: " + URI);
		}
		
		return URI.substring(index + 3);		
	}

	/*
	public static String striphost(String URI) throws Exception { 
		
		int index = URI.indexOf("/");
		
		if (index < 0) { 
			throw new Exception("Failed to remove host from URI: " + URI);
		}
		
		return URI.substring(index + 1);		
	}*/
	
	public static String getFileName(java.net.URI file) throws Exception { 
		
		String path = file.getPath();
		
		if (path == null) { 
			throw new Exception("Failed to find file name in URI: " + file);
		}
		
		int index = path.lastIndexOf(java.io.File.separator); 
		
		if (index < 0) { 
			throw new Exception("Failed to find file name in URI: " + file);
		}
		
		return path.substring(index + 1);
	}

	public static String getParentDir(java.net.URI file) throws Exception { 

		String path = file.getPath();
		
		if (path == null) { 
			throw new Exception("Failed to find parent directory in URI: " + file);
		}
		
		int index = path.lastIndexOf(java.io.File.separator); 
		
		if (index < 0) { 
			throw new Exception("Failed to find file parent in URI: " + file);
		}
		
		return path.substring(0, index);
	}
	
/*	
	public static String getPath(FileDescription fd) throws Exception { 		
		return striphost(stripScheme(fd.file.URI));
	}
	
	public static String getPath(String URI) throws Exception { 		
		return striphost(stripScheme(URI));
	}
*/
	
/*	
	public static boolean createDir(FileDescription fd) throws Exception {
		
		FileInterface tmp = getFileInterface(fd);
		
		if (tmp == null) { 
			throw new Exception("Failed to access remote filesystem: " + fd);
		}
		
		return tmp.mkdirs();
	}
	*/
	
	public static boolean createDir(java.net.URI dir) throws Exception {
		
		FileInterface tmp = getFileInterface(dir);
		
		if (tmp == null) { 
			throw new Exception("Failed to access remote filesystem: " + dir);
		}
		
		return tmp.mkdirs();
	}
	
/*	
	public static void copy(FileDescription remoteFrom, FileDescription remoteTo) throws Exception {
	
		FileInterface tmp = getFileInterface(remoteFrom);
		
		if (tmp == null) { 
			throw new Exception("Failed to access remote source file: " + remoteFrom.file.URI);
		}
		
		tmp.copy(new URI(remoteTo.file.URI));
	}
*/
	public static void copy(java.net.URI from, java.net.URI to) throws Exception {

		FileInterface tmp = getFileInterface(from);
		
		if (tmp == null) { 
			throw new Exception("Failed to access remote source file: " + from);
		}
		
		tmp.copy(new org.gridlab.gat.URI(to));
	}
	
	/*
	public static void createTransferList(FileDescription from, FileDescription to, List<FileTransferDescription> result) throws Exception {

		if (!accessibleDirectory(from)) { 
			throw new IOException("Directory not accesible: " + from.file.URI); 
		}

		if (!accessibleDirectory(to)) { 
			throw new IOException("Directory not accesible: " + to.file.URI); 
		}

		FileInterface tmp = getFileInterface(from);
		
		if (tmp == null) { 
			throw new IOException("Failed to access remote source file: " + from.file.URI);
		}
		
		java.io.File [] files = tmp.listFiles();
		
		for (java.io.File f : files) {
			
			if (f.isFile() && f.canRead()) { 
				String name = f.getName();
				FileDescription src = getSubFile(from, name);
				FileDescription dst = getSubFile(to, name);
				result.add(new FileTransferDescription(src, dst));
			}
		}
	}
	*/

	public static void createTransferList(java.net.URI from, java.net.URI to, List<FileTransferDescription> result) throws Exception {

		if (!accessibleDirectory(from)) { 
			throw new IOException("Directory not accesible: " + from); 
		}

		if (!accessibleDirectory(to)) { 
			throw new IOException("Directory not accesible: " + to); 
		}

		FileInterface tmp = getFileInterface(from);
		
		if (tmp == null) { 
			throw new IOException("Failed to access remote source file: " + from);
		}
		
		java.io.File [] files = tmp.listFiles();
		
		for (java.io.File f : files) {
			
			if (f.isFile() && f.canRead()) { 
				String name = f.getName();				
				java.net.URI src = new java.net.URI(from.getScheme(), from.getAuthority(), from.getPath() + "\\" + name, from.getQuery(), from.getFragment());
				java.net.URI dst = new java.net.URI(to.getScheme(), to.getAuthority(), to.getPath() + "\\" + name, to.getQuery(), to.getFragment());
				result.add(new FileTransferDescription(src, dst));
			}
		}
	}

	
	private static Preferences addPreference(Preferences pref, String key, String value) { 
		
		if (pref == null) { 
			pref = new Preferences();
		}
		
		pref.put(key, value);
		
		return pref;		
	}
	
	/*
	private static ResourceBroker getResourceBroker(ResourceDescription resource, ResourceDescription gateway) throws GATObjectCreationException, URISyntaxException { 

		GATContext context = getContext(resource, gateway);
		Preferences pref = getPreferences(resource, gateway, "resourcebroker");

		if (context == null && pref == null) {
			return GAT.createResourceBroker(new org.gridlab.gat.URI(resource.URI));
		} else if (context == null && pref != null) { 
			return GAT.createResourceBroker(pref, new org.gridlab.gat.URI(resource.URI));
		} else if (context != null && pref == null) { 
			return GAT.createResourceBroker(context, new org.gridlab.gat.URI(resource.URI));
		} else {
			return GAT.createResourceBroker(context, pref, new org.gridlab.gat.URI(resource.URI));
		}
	}
	 */
	
	private static ResourceBroker getResourceBroker(java.net.URI uri) throws Exception { 

		if (!uri.isAbsolute() || uri.isOpaque()) {
			throw new Exception("Cannot handle URI: " + uri + " (absolute=" +  uri.isAbsolute() + ", opaque=" + uri.isOpaque() + ")");
		} 
		
		String scheme = uri.getScheme();
		String auth = uri.getAuthority();
			
		ResourceDescription resource = findResourceDescription(new java.net.URI(scheme, auth, "", null, null));
			
    	GATContext context = getContext(resource);
		Preferences pref = getPreferences(resource, "resourcebroker");
		
		if (context == null && pref == null) {
			return GAT.createResourceBroker(new org.gridlab.gat.URI(uri));
		} else if (context == null && pref != null) { 
			return GAT.createResourceBroker(pref, new org.gridlab.gat.URI(uri));
		} else if (context != null && pref == null) { 
			return GAT.createResourceBroker(context, new org.gridlab.gat.URI(uri));
		} else {
			return GAT.createResourceBroker(context, pref, new org.gridlab.gat.URI(uri));
		}
	}

	public static StringBuffer readOutput(java.io.File file, StringBuffer output) throws IOException {
		
		if (output == null) { 
			output = new StringBuffer();
		}
		
		if (file.exists() && file.isFile() && file.canRead()) { 
			BufferedReader in = new BufferedReader(new FileReader(file));
		
			String line = in.readLine();
			
			while (line != null) { 
				output.append(line);
				line = in.readLine();
			}
			
			in.close();
		} else { 
			throw new IOException("Cannot access file: " + file);
		}
		
		return output;
	}
/*	
	private static synchronized String getTempFileName() {
		return "tmp." + tempFileCounter++;
	}
*/
	public static String [] combine(String [] args1, String [] args2) { 
		
		if (args1 == null || args1.length == 0) { 
			if (args2 == null) { 
				return null;
			}
		
			return args2.clone();
		}
	
		if (args2 == null) { 
			return args1.clone();
		}
		
		String [] result = new String[args1.length + args2.length];
		
		System.arraycopy(args1, 0, result, 0, args1.length);
		System.arraycopy(args2, 0, result, args1.length, args2.length);
		
		return result;
	}
	
	public static int runRemoteScript(java.net.URI host, 
			String remoteDirectory, String script, String [] arguments,
			java.io.File stdout, java.io.File stderr, Logger logger, String prefix) {
	
		// FIXME: decent logging!
		int exit = -1;
		
		ResourceBroker broker = null;
		JobDescription description = null;
		Job job = null;
	
		File tmpout = null;
		File tmperr = null;
				
		try {
			tmpout = GAT.createFile(stdout.getAbsolutePath());
			tmperr = GAT.createFile(stderr.getAbsolutePath());
		} catch (GATObjectCreationException e) {
			logger.error(prefix + " Failed to create tempfiles for stdout/stderr of " + host + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}
	
		try { 
			broker = getResourceBroker(host);
		} catch (Exception e) {			
			logger.error(prefix + " Failed to create resource broker for " + host, e);
			return -1;
		}
		
		String [] args = combine(new String [] { script }, arguments);
		
		try { 
			SoftwareDescription software = new SoftwareDescription();
			
			software.setExecutable("/bin/bash");
			software.setArguments(args);
			
			software.setStdout(tmpout);
			software.setStderr(tmperr);
				
			software.addAttribute(SoftwareDescription.DIRECTORY, remoteDirectory);
			software.addAttribute(SoftwareDescription.HOST_COUNT, 1);
			software.addAttribute(SoftwareDescription.SANDBOX_DELETE, "false");
	
			description = new JobDescription(software);
		} catch (Exception e) {
			logger.error(prefix + " Failed to create job description for " + host + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}

		try { 
			job = broker.submitJob(description);
		} catch (Exception e) {
			logger.error(prefix + " Failed to submit job " + host + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}

		logger.info(prefix + " Successfully submitted job " + host + "//" + remoteDirectory + "/" + script);
		
		JobState state = job.getState();
		
		while (state != JobState.STOPPED && state != JobState.SUBMISSION_ERROR) { 
			try { 
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignored as always...
			}
			
			state = job.getState();			
		}
		
		try {
			exit = job.getExitStatus();
		} catch (GATInvocationException e) {
			logger.error(prefix + " Failed to retrieve exit status for job " + host + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}
		
		logger.info(prefix + " Job " + host + "//" + remoteDirectory + "/" + script + " terminated succesfully!");
		
		return exit;
	}

	
/*	
	public static int runRemoteScript(ResourceDescription host, ResourceDescription gateway,
			String remoteDirectory, String script, String [] arguments,
			java.io.File stdout, java.io.File stderr, Logger logger, String prefix) {
	
		// FIXME: decent logging!
		int exit = -1;
		
		ResourceBroker broker = null;
		JobDescription description = null;
		Job job = null;
	
//		String tmpFile = getTempFileName();
		
		File tmpout = null;
		File tmperr = null;
				
		try {
			tmpout = GAT.createFile(stdout.getAbsolutePath());
			tmperr = GAT.createFile(stderr.getAbsolutePath());
		} catch (GATObjectCreationException e) {
			logger.error(prefix + " Failed to create tempfiles for stdout/stderr of " + host.URI + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}
		
	
		try { 
			broker = getResourceBroker(host, gateway);
		} catch (Exception e) {			
			logger.error(prefix + " Failed to create resource broker for " + host.URI, e);
			return -1;
		}
		
		String [] args = combine(new String [] { script }, arguments);
		
		try { 
			SoftwareDescription software = new SoftwareDescription();
			
			software.setExecutable("/bin/bash");
			software.setArguments(args);
			
			software.setStdout(tmpout);
			software.setStderr(tmperr);
				
			software.addAttribute(SoftwareDescription.DIRECTORY, remoteDirectory);
			software.addAttribute(SoftwareDescription.HOST_COUNT, 1);
			software.addAttribute(SoftwareDescription.SANDBOX_DELETE, "false");
	
			description = new JobDescription(software);
		} catch (Exception e) {
			logger.error(prefix + " Failed to create job description for " + host.URI + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}

		try { 
			job = broker.submitJob(description);
		} catch (Exception e) {
			logger.error(prefix + " Failed to submit job " + host.URI + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}

		logger.info(prefix + " Successfully submitted job " + host.URI + "//" + remoteDirectory + "/" + script);
		
		JobState state = job.getState();
		
		while (state != JobState.STOPPED && state != JobState.SUBMISSION_ERROR) { 
			try { 
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignored as always...
			}
			
			state = job.getState();			
		}
		
		try {
			exit = job.getExitStatus();
		} catch (GATInvocationException e) {
			logger.error(prefix + " Failed to retrieve exit status for job " + host.URI + "//" + remoteDirectory + "/" + script, e);
			return -1;
		}
		
		logger.info(prefix + " Job " + host.URI + "//" + remoteDirectory + "/" + script + " terminated succesfully!");
		
		return exit;
	}
	*/
	/*
	public static FileDescription createFileDescription(ResourceDescription fileServer, String path, ResourceDescription gateway) { 
		String completeURI = fileServer.URI + File.separator + path;
		ResourceDescription tmp = new ResourceDescription(completeURI, fileServer.username, fileServer.userkey, fileServer.adaptors);
		return new FileDescription(tmp, gateway);
	}
	 */	
}
