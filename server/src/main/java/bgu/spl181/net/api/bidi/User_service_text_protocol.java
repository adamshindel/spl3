package bgu.spl181.net.api.bidi;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import bgu.spl181.net.srv.Movie;
import bgu.spl181.net.srv.SharedData;
import bgu.spl181.net.srv.User;

public class User_service_text_protocol implements BidiMessagingProtocol<String>{

	private Connections<String> connections; 
	private int connectionId;
	private User currentUser = null;
	private boolean terminate = false;
	private SharedData data = SharedData.getData();
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
			if (data.getUserByName(parts[1]) != null) {
				connections.send(connectionId, "ERROR registration failed"); // already exists
			break;
			}
			if (!parts[3].contains("=\"")|| parts[3].charAt(parts[3].length() -1) != '"') {
				connections.send(connectionId, "ERROR registration failed"); // data block syntax error
			break;
			}
			String country = parts[3].substring(parts[3].indexOf("=\"") + 1);// (\") allows to insert quote inside a string
			country = country.substring(0, country.length() -2); // cutting last quote
			data.addUser(new User(parts[1], "normal", parts[2], country, new ConcurrentHashMap<String,String>(), "0"));
			connections.send(connectionId, "ACK registration succeeded");
			break;
		
		case "LOGIN":
			if (parts.length != 3) {
				connections.send(connectionId, "ERROR login failed"); // syntax error
			break;
			}
			User u = data.getUserByName(parts[1]);
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
			data.disconnectUser(currentUser);
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
					connections.send(connectionId, "Ack balance " + currentUser.getBalance()); // syntax error
					break;
				}
				else if(parts[2].compareTo("Add")==0) {
					int amount=Integer.parseInt("parts[3]");
					int balance=Integer.parseInt(currentUser.getBalance())+1;
					currentUser.setBalance(balance+"") ;
					data.changeUser(currentUser,"balance",amount+"");
					connections.send(connectionId, "Ack balance " +currentUser.getBalance()+" added "+amount); // syntax error
					break;
				}
			}
			else if(parts[1].compareTo("info")==0){
				if(parts.length==2){
					String result="Ack info ";
					for (Movie movie: data.getMovies()){
						result=result+movie.getName()+" ";
					}
					connections.send(connectionId,result); // syntax error
					break;
				}
				else{
					Movie currentMovie=data.getMovieByName(parts[2]);
					if (currentMovie!=null){
						connections.send(connectionId, "Ack info " +currentMovie.getName()+" "+currentMovie.getTotalAmount()+" "+currentMovie.getPrice()+" "+currentMovie.getBannedCountries().toString()); // syntax error
						break;
					}
					else{
						connections.send(connectionId, "ERROR requset info failed"); // isn't logged in
						break;
					}
				}
			}
			else if(parts[1].compareTo("rent")==0){
				Movie currentMovie=data.getMovieByName(parts[2]);
				if (currentMovie!=null&&Integer.parseInt(currentUser.getBalance())>=Integer.parseInt(currentMovie.getPrice())
						&&Integer.parseInt(currentMovie.getAviailableAmount())>=1&&!currentUser.getMovies().containsValue(currentMovie)
						&&!currentMovie.getBannedCountries().contains(currentUser.getCountry())){
					int balance=Integer.parseInt(currentUser.getBalance())-Integer.parseInt(currentMovie.getPrice());
					currentUser.setBalance(balance+"");
					int temp=Integer.parseInt(currentMovie.getAviailableAmount())-1;
					currentMovie.setAvailableAmount(temp+"");
					data.changeMovie(currentMovie, "aviailableAmount",currentMovie.getAviailableAmount());
					data.changeUser(currentUser, "balance", currentUser.getBalance());
					currentUser.getMovies().put(currentMovie.getId(), currentMovie.getName());
					data.changeMoviesUser(currentUser);
					connections.send(connectionId, "Ack rent " +currentMovie.getName()+" success");
					connections.broadcast("BROADCAST movie "+currentMovie.getName()+" "+currentMovie.getAviailableAmount()+" "+currentMovie.getPrice());
					break;
				}
				else{
					connections.send(connectionId, "ERROR requset rent failed"); // isn't logged in
					break;
				}
			}
			else if(parts[1].compareTo("return")==0){
				Movie currentMovie=data.getMovieByName(parts[2]);
				if(currentMovie!=null&&currentUser.getMovies().containsValue(currentMovie)){
					currentUser.getMovies().remove(currentMovie);
					data.changeMoviesUser(currentUser);
					int amount=Integer.parseInt(currentMovie.getAviailableAmount())+1;
					currentMovie.setAvailableAmount(amount+"");
					data.changeMovie(currentMovie, "availableAmount", currentMovie.getAviailableAmount());
					connections.send(connectionId, "Ack return " +currentMovie.getName()+" success"); // syntax error
					connections.broadcast("BROADCAST movie "+currentMovie.getName()+" "+currentMovie.getAviailableAmount()+" "+currentMovie.getPrice());
				}
				else{
					connections.send(connectionId, "ERROR requset return failed"); // isn't logged in
					break;
				}
				
			}
			else if(parts[1].compareTo("addmovie")==0){
				int amount=Integer.parseInt("parts[3]");
		        int price =Integer.parseInt("parts[4]");
				if(currentUser.getType().compareTo("admin")==0&&!data.getMovies().contains(parts[2])&&price>0&&amount>0){
					int temp=Integer.parseInt(data.getHighestId())+1;
				    data.setHighestId(temp+ "");
				    ArrayList<String> bannedCountries=new ArrayList<>();
				    for(int i=5;i<parts.length;i++){
				    	bannedCountries.add(parts[i]);
				    }
					data.addMovie(new Movie(data.getHighestId(),parts[2],price+"",amount+"",amount+"",bannedCountries));		
					connections.send(connectionId, "Ack addmovie " +parts[2]+" success");
					connections.broadcast("BROADCAST movie "+parts[2]+" "+amount+" "+price);
					}
				else{
					connections.send(connectionId, "ERROR requset addmovie failed"); // isn't logged in
					break;
				}
			}
			else if(parts[1].compareTo("remmovie")==0){
				Movie currentMovie=data.getMovieByName(parts[2]);
				if(currentUser.getType().compareTo("admin")==0&&currentMovie!=null&&
						data.getMovies().contains(parts[2])&&currentMovie.getAviailableAmount()==currentMovie.getTotalAmount()){
					data.removeMovie(currentMovie);
					connections.send(connectionId, "Ack remmovie " +parts[2]+" success");
					connections.broadcast("BROADCAST movie "+parts[2]+" removed");
				}
				else{
					connections.send(connectionId, "ERROR requset remmovie failed"); // isn't logged in
					break;
				}
			}
			else if(parts[1].compareTo("changeprice")==0){
				Movie currentMovie=data.getMovieByName(parts[2]);
				int price=Integer.parseInt("parts[3]");
	            if(currentMovie!=null&&currentUser.getType().compareTo("admin")==0&&data.getMovies().contains(currentMovie)&&price>0){
	        	   currentMovie.setPrice(price+"");
	        	   data.changeMovie(currentMovie, "price",currentMovie.getPrice() );
	        	   connections.send(connectionId, "Ack changeprice " +parts[2]+" success");
				connections.broadcast("BROADCAST movie "+parts[2]+" "+currentMovie.getAviailableAmount()+" "+price);
			
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
