package bgu.spl181.net.srv;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleSharedData {

	//ein kesher le movie!!!!!!!!!!!!!!!!!!!!!!!!!!1
	private static SimpleSharedData instance;
	private ConcurrentLinkedQueue<SimpleUser> users = new ConcurrentLinkedQueue<>();
	private Map<Integer, SimpleUser> loggedUsers = new ConcurrentHashMap<>();
	
	protected SimpleSharedData() {
		
	}

	public static synchronized SimpleSharedData getData() {
		//synchronized (instance) {
			if (instance == null) {
				instance = new SimpleSharedData();
			}
		//}
		return instance;
	}
	
	public void addLoggedUser(SimpleUser toAdd, Integer connectionId) {
		loggedUsers.put(connectionId, toAdd);
	}
	
	public SimpleUser getUserByName(String name) {
		for (SimpleUser u : users) {
			if (u.getUserName().compareTo(name) == 0)
				return u;
		}
		return null;
	}

	public SimpleUser getLoggedByName(String name) {
		for (Entry<Integer, SimpleUser> entry : loggedUsers.entrySet()) {
			if (entry.getValue().getUserName().compareTo(name) == 0)
				return entry.getValue();
		}
		return null;
	}

	public SimpleUser getLoggedUser(Integer connectionId) {
		return loggedUsers.get(connectionId);
	}

	public void disconnectUser(Integer connectionId) {
		loggedUsers.remove(connectionId);
	}
	
	public void addUser(SimpleUser u) {
		users.add(u);
	}

	public ConcurrentLinkedQueue<SimpleUser> getSimpleUsers() {
		return users;
	}
}
