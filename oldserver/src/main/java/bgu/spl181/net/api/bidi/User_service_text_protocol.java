package bgu.spl181.net.api.bidi;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl181.net.srv.Movie;
import bgu.spl181.net.srv.SharedData;
import bgu.spl181.net.srv.SimpleSharedData;
import bgu.spl181.net.srv.SimpleUser;
import bgu.spl181.net.srv.User;

public class User_service_text_protocol implements BidiMessagingProtocol<String>{

	protected Connections<String> connections; 
	protected int connectionId;
	private SimpleUser currentUser = null;
	private boolean terminate;
	private SimpleSharedData data = SimpleSharedData.getData();
	@Override
	public void start(int connectionId, Connections<String> connections) {
		// TODO Auto-generated method stub
		this.connections = connections;
		this.connectionId = connectionId;
	}

	@Override
	public void process(String message) {
		// TODO Auto-generated method stub
		String[] parts = message.split(" ");
		switch (parts[0]) {
		case "LOGIN":
			if (parts.length != 3) {
				connections.send(connectionId, "ERROR login failed"); // syntax error
			break;
			}
			SimpleUser u = data.getUserByName(parts[1]);
			if ( u == null) {
				connections.send(connectionId, "ERROR login failed"); // doesn't exists
			break;
			}
			if (u.getPassword().compareTo(parts[2])!=0) {
				connections.send(connectionId, "ERROR login failed"); // wrong password
			break;
			}
			if (data.getLoggedByName(parts[1]) != null) {
				connections.send(connectionId, "ERROR login failed"); // already logged in
			break;
			}
			terminate = false;
			currentUser = u;
			data.addLoggedUser(currentUser, connectionId);
			connections.send(connectionId, "ACK login succeeded");
			break;
			
		case "SIGNOUT":
			if (parts.length != 1) {
				connections.send(connectionId, "ERROR signout failed"); // syntax error
			break;
			}
			if (currentUser == null) {
				connections.send(connectionId, "ERROR signout failed"); // isn't logged in
			break;
			}
			data.disconnectUser(connectionId);
			currentUser = null;
			connections.send(connectionId, "ACK signout succeeded");
			connections.disconnect(connectionId);
			terminate = true;
			break;
			
		}
	}
	@Override
	public boolean shouldTerminate() { 	
		return terminate;
	}
}
