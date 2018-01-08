package bgu.spl181.net.api.bidi;

import java.util.ArrayList;

import bgu.spl181.net.srv.Movie;
import bgu.spl181.net.srv.Movie_Rental_Service_Users;
import bgu.spl181.net.srv.Movies;
import bgu.spl181.net.srv.User;

public class User_service_text_protocol implements BidiMessagingProtocol<String>{

	private Connections<String> connections; 
	private int connectionId;
	private User currentUser = null;
	private boolean terminate = false;
	private Movie_Rental_Service_Users users = new Movie_Rental_Service_Users();
	private Movies movies=new Movies();
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
			if (parts[1].compareTo("balance")==0) {
				if (parts[2].compareTo("info")==0) {
					connections.send(connectionId, "Ack balance " + currentUser.balance); // syntax error
					break;
				}
				else if(parts[2].compareTo("Add")==0) {
					int amount=Integer.parseInt("parts[3]");
					users.changeBalanceUser(currentUser.userName,amount);
					connections.send(connectionId, "Ack balance " +currentUser.balance+" added "+amount); // syntax error
					break;
				}
			}
			else if(parts[1].compareTo("info")==0){
				if(parts.length==2){
					String result="Ack info ";
					for (Movie movie: movies.getMovies()){
						result=result+movie.name+" ";
					}
					connections.send(connectionId,result); // syntax error
					break;
				}
				else{
					Movie currentMovie=movies.getMovieByName(parts[2]);
					if (currentMovie!=null){
						connections.send(connectionId, "Ack info " +currentMovie.name+" "+currentMovie.totalAmount+" "+currentMovie.price+" "+currentMovie.bannedCountries.toString()); // syntax error
						break;
					}
					else{
						connections.send(connectionId, "ERROR requset info failed"); // isn't logged in
						break;
					}
				}
			}
			else if(parts[1].compareTo("rent")==0){
				Movie currentMovie=movies.getMovieByName(parts[2]);
				if (currentMovie!=null&&currentUser.balance>=currentMovie.price&&currentMovie.availableAmount>=1&&!currentUser.movie.contains(currentMovie)&&!currentMovie.bannedCountries.contains(currentUser.country)){
					currentUser.setBalance(currentUser.balance-currentMovie.price);
					currentMovie.setAvailableAmount(currentMovie.availableAmount-1);
					currentUser.movie.add(currentMovie);
					connections.send(connectionId, "Ack rent " +currentMovie.name+" success");
					connections.broadcast("BROADCAST movie "+currentMovie.name+" "+currentMovie.availableAmount+" "+currentMovie.price);
					break;
				}
				else{
					connections.send(connectionId, "ERROR requset rent failed"); // isn't logged in
					break;
				}
			}
			else if(parts[1].compareTo("return")==0){
				Movie currentMovie=movies.getMovieByName(parts[2]);
				if(currentMovie!=null&&currentUser.movie.contains(currentMovie)){
					currentUser.movie.remove(currentMovie);
					currentMovie.setAvailableAmount(currentMovie.availableAmount+1);
					connections.send(connectionId, "Ack return " +currentMovie.name+" success"); // syntax error
					connections.broadcast("BROADCAST movie "+currentMovie.name+" "+currentMovie.availableAmount+" "+currentMovie.price);
				}
				else{
					connections.send(connectionId, "ERROR requset return failed"); // isn't logged in
					break;
				}
				
			}
			else if(parts[1].compareTo("addmovie")==0){
				int amount=Integer.parseInt("parts[3]");
		        int price =Integer.parseInt("parts[4]");
				if(currentUser.type.compareTo("admin")==0&&!movies.getMovies().contains(parts[2])&&price>0&&amount>0){
					int temp=Integer.parseInt(movies.getHighestId())+1;
				    movies.setHighestId(temp+ "");
				    ArrayList<String> bannedCountries=new ArrayList<>();
				    for(int i=5;i<parts.length;i++){
				    	bannedCountries.add(parts[i]);
				    }
					movies.addMovie(new Movie(movies.getHighestId(),parts[2],price,amount,amount,bannedCountries));		
					connections.send(connectionId, "Ack addmovie " +parts[2]+" success");
					connections.broadcast("BROADCAST movie "+parts[2]+" "+amount+" "+price);
					}
				else{
					connections.send(connectionId, "ERROR requset addmovie failed"); // isn't logged in
					break;
				}
			}
			else if(parts[1].compareTo("remmovie")==0){
				Movie currentMovie=movies.getMovieByName(parts[2]);
				if(currentUser.type.compareTo("admin")==0&&currentMovie!=null&&movies.getMovies().contains(parts[2])&&currentMovie.availableAmount==currentMovie.totalAmount){
					movies.removeMovie(currentMovie);
					connections.send(connectionId, "Ack remmovie " +parts[2]+" success");
					connections.broadcast("BROADCAST movie "+parts[2]+" removed");
				}
				else{
					connections.send(connectionId, "ERROR requset remmovie failed"); // isn't logged in
					break;
				}
			}
			else if(parts[1].compareTo("changeprice")==0){
				Movie currentMovie=movies.getMovieByName(parts[2]);
				int price=Integer.parseInt("parts[3]");
	            if(currentMovie!=null&&currentUser.type.compareTo("admin")==0&&movies.getMovies().contains(currentMovie)&&price>0){
	        	   currentMovie.setPrice(price);
	        	   connections.send(connectionId, "Ack changeprice " +parts[2]+" success");
				connections.broadcast("BROADCAST movie "+parts[2]+" "+currentMovie.availableAmount+" "+price);
			
	           }
	            else{
	            	connections.send(connectionId, "ERROR requset changeprice failed"); // isn't logged in
					break;
	            }
			}
		}
	}
	@Override
	public boolean shouldTerminate() { 	
		return terminate;
	}
}
