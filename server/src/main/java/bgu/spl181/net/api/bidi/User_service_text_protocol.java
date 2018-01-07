package bgu.spl181.net.api.bidi;

import java.util.ArrayList;

import bgu.spl181.net.srv.Movie_Rental_Service_Users;
import bgu.spl181.net.srv.User;

public class User_service_text_protocol implements BidiMessagingProtocol<String>{

	private Connections<String> connections; 
	private int connectionId;
	private User currentUser = null;
	private boolean terminate = false;
	private Movie_Rental_Service_Users users = new Movie_Rental_Service_Users();
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
		case "REGISTER":
			if (parts.length != 4) {
				connections.send(connectionId, "ERROR registration failed"); // syntax error
			break;
			}
			if (users.getUserByName(parts[1]) != null) {
				connections.send(connectionId, "ERROR registration failed"); // already exists
			break;
			}
			if (!parts[3].contains("=\"")|| parts[3].charAt(parts[3].length() -1) != '"') {
				connections.send(connectionId, "ERROR registration failed"); // data block syntax error
			break;
			}
			String country = parts[3].substring(parts[3].indexOf("=\"") + 1);// (\") allows to insert quote inside a string
			country = country.substring(0, country.length() -2); // cutting last quote
			users.addUser(new User(parts[1], "normal", parts[2], country, new ArrayList<>(), 0));
			connections.send(connectionId, "ACK registration succeeded");
			break;
		
		case "LOGIN":
			if (parts.length != 3) {
				connections.send(connectionId, "ERROR login failed"); // syntax error
			break;
			}
			User u = users.getUserByName(parts[1]);
			if ( u == null) {
				connections.send(connectionId, "ERROR login failed"); // doesn't exists
			break;
			}
			if (u.password != parts[2]) {
				connections.send(connectionId, "ERROR login failed"); // wrong password
			break;
			}
			if (users.getLoggedByName(parts[1]) != null) {
				connections.send(connectionId, "ERROR login failed"); // already logged in
			break;
			}
			currentUser = u;
			users.addLoggedUser(currentUser);
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
			users.disconnectUser(currentUser);
			connections.disconnect(connectionId);
			currentUser = null;
			connections.send(connectionId, "ACK signout succeeded");
			break;
			
		case "REQUEST":
			if(currentUser == null) {
				connections.send(connectionId, "ERROR request "+parts[1]+" failed"); // syntax error
				break;
			}
			if (parts[1] == "balance") {
				if (parts[2] == "info") {
					connections.send(connectionId, "Ack balance " + currentUser.balance); // syntax error
					break;
				}
				else if(parts[2] == "Add") {
					
				}
			}
		}
	}
	@Override
	public boolean shouldTerminate() { 	
		return terminate;
	}
}
