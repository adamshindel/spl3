package bgu.spl181.net.api.bidi;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.Movie;
import bgu.spl181.net.srv.SharedData;
import bgu.spl181.net.srv.User;

public class MovieRentalServiceProtocol extends User_service_text_protocol {
	//private Connections<String> connections;
	//private int connectionId;
	private User currentUser = null;
	private boolean terminate;
	private SharedData data = SharedData.getData();

	@Override
	public void start(int connectionId, Connections<String> connections) {
		super.start(connectionId, connections);
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
			if (!parts[3].contains("=\"") || parts[3].charAt(parts[3].length() - 1) != '"') {
				connections.send(connectionId, "ERROR registration failed"); // data block syntax error
				break;
			}
			String country = parts[3].substring(parts[3].indexOf("=\"") + 1);// (\") allows to insert quote inside a
																				// string
			country = country.substring(1, country.length() - 1); // cutting last quote
			data.addUser(new User(parts[1], "normal", parts[2], country, new ConcurrentHashMap<String, String>(), "0"));
			connections.send(connectionId, "ACK registration succeeded");
			break;

		case "LOGIN":
			if (parts.length != 3) {
				connections.send(connectionId, "ERROR login failed"); // syntax error
				break;
			}
			User u = data.getUserByName(parts[1]);
			if (u == null) {
				connections.send(connectionId, "ERROR login failed"); // doesn't exists
				break;
			}
			if (u.getPassword().compareTo(parts[2]) != 0) {
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
			connections.send(connectionId, "ACK signout succeeded");
			currentUser = null;
			connections.disconnect(connectionId);
			terminate = true;
			break;

		case "REQUEST":
			if (currentUser == null) {
				connections.send(connectionId, "ERROR request " + parts[1] + " failed"); // syntax error
				break;
			}
			if (parts[1].compareTo("balance") == 0) {
				if (parts[2].compareTo("info") == 0) {
					connections.send(connectionId, "Ack balance " + currentUser.getBalance()); // syntax error
					break;
				} else if (parts[2].compareTo("add") == 0) {
					int amount = Integer.parseInt(parts[3]);
					int balance = Integer.parseInt(currentUser.getBalance()) + amount;
					currentUser.setBalance(balance + "");
					data.changeUser(currentUser, "balance", balance + "");
					connections.send(connectionId, "Ack balance " + currentUser.getBalance() + " added " + amount); // syntax																						// error
					break;
				}
			} else if (parts[1].compareTo("info") == 0) {
				if (parts.length == 2) {
					String result = "Ack info ";
					for (Movie movie : data.getMovies()) {
						result = result +" \""+ movie.getName() + "\"";
					}
					connections.send(connectionId, result); // syntax error
					break;
				} else {
					String temp="";
					for(int i=2;i<parts.length;i++) {
						if(i!=parts.length-1) {
						temp=temp+parts[i]+" ";
						}
						else {
							temp=temp+parts[i];
						}
					}
					temp=temp.substring(1,temp.length()-1);
					Movie currentMovie = data.getMovieByName(temp);
					if (currentMovie != null) {
						String banned="";
						for(String str :currentMovie.getBannedCountries()) {
							banned=banned+" \""+str+"\"";
						}
						
						connections.send(connectionId,
								"Ack info \"" + currentMovie.getName() + "\" " + currentMovie.getAviailableAmount()+ " "
										+ currentMovie.getPrice() +  banned); // syntax																						// error
						break;
					} else {
						connections.send(connectionId, "ERROR requset info failed"); // isn't logged in
						break;
					}
				}
			} else if (parts[1].compareTo("rent") == 0) {
				String temp="";
				for(int i=2;i<parts.length;i++) {
					if(i!=parts.length-1) {
					temp=temp+parts[i]+" ";
					}
					else {
						temp=temp+parts[i];
					}
				}
				temp=temp.substring(1,temp.length()-1);
				Movie currentMovie = data.getMovieByName(temp);
				if (currentMovie != null
						&& Integer.parseInt(currentUser.getBalance()) >= Integer.parseInt(currentMovie.getPrice())
						&& Integer.parseInt(currentMovie.getAviailableAmount()) >= 1
						&& !currentUser.getMovies().containsValue(currentMovie)
						&& !currentMovie.getBannedCountries().contains(currentUser.getCountry())) {
					int balance = Integer.parseInt(currentUser.getBalance())
							- Integer.parseInt(currentMovie.getPrice());
					currentUser.setBalance(balance + "");
					int temp1 = Integer.parseInt(currentMovie.getAviailableAmount()) - 1;
					currentMovie.setAvailableAmount(temp1 + "");
					data.changeMovie(currentMovie, "aviailableAmount", currentMovie.getAviailableAmount());
					data.changeUser(currentUser, "balance", currentUser.getBalance());
					currentUser.getMovies().put(currentMovie.getId(), currentMovie.getName());
					data.changeMoviesUser(currentUser);
					connections.send(connectionId, "Ack rent \"" + currentMovie.getName() + "\" success");
					connections.broadcast("BROADCAST movie \"" + currentMovie.getName() + "\" "
							+ currentMovie.getAviailableAmount() + " " + currentMovie.getPrice());
					break;
				} else {
					connections.send(connectionId, "ERROR requset rent failed"); // isn't logged in
					break;
				}
			} else if (parts[1].compareTo("return") == 0) {
				String temp="";
				for(int i=2;i<parts.length;i++) {
					if(i!=parts.length-1) {
					temp=temp+parts[i]+" ";
					}
					else {
						temp=temp+parts[i];
					}
				}
				temp=temp.substring(1,temp.length()-1);
				Movie currentMovie = data.getMovieByName(temp);
				if (currentMovie != null && currentUser.getMovies().containsKey(currentMovie.getId())) {
					currentUser.getMovies().remove(currentMovie);
					data.changeMoviesUser(currentUser);
					int amount = Integer.parseInt(currentMovie.getAviailableAmount()) + 1;
					currentMovie.setAvailableAmount(amount + "");
					data.changeMovie(currentMovie, "availableAmount", currentMovie.getAviailableAmount());
					connections.send(connectionId, "Ack return \"" + currentMovie.getName() + "\" success"); // syntax error
					connections.broadcast("BROADCAST movie \"" + currentMovie.getName() + "\" "
							+ currentMovie.getAviailableAmount() + " " + currentMovie.getPrice());
				} else {
					connections.send(connectionId, "ERROR requset return failed"); // isn't logged in
					break;
				}

			} else if (parts[1].compareTo("addmovie") == 0) {
				//int amount = Integer.parseInt();
				//int price = Integer.parseInt());
				
				int first = message.indexOf("\"");
				int second = message.indexOf("\"", first+1);
				int third = message.indexOf("\"", second+1);
				String tempName=message.substring(first+1, second);
				String lastPart = message.substring(second+1,message.length());
				//if
				String[] addMovieParts = lastPart.split("\"");
				String [] parms = addMovieParts[0].split(" ");
				String price = "";
				String amount = "";
				for(int i = 0; i<parms.length && (price.compareTo("")==0|| amount.compareTo("")==0);i++) {
					if(amount.compareTo("")==0 && parms[i].compareTo("")!=0) {
						amount = parms[i];
					}
					else if(price.compareTo("")==0 && parms[i].compareTo("")!=0) {
						price = parms[i];
					}
				}
				ArrayList<String> bannedCountries = new ArrayList<>();
				for(int i=1; i< addMovieParts.length; i++) {
					if(addMovieParts[i].replaceAll(" ", "").compareTo("")!= 0) {
						bannedCountries.add(addMovieParts[i]);
					}
				}
				/*for(int i=2; i<parts.length-3; i++) {
					tempName += parts[i] + " ";
				}
				tempName = parts[parts.length-3];
				tempName = tempName.substring(1, tempName.length()-1);*/
				if (currentUser.getType().compareTo("admin") == 0 && !data.getMovies().contains(tempName) && Integer.parseInt(price) > 0
						&& Integer.parseInt(amount) > 0) {
					int temp = Integer.parseInt(data.getHighestId()) + 1;
					data.setHighestId(temp + "");
					//ArrayList<String> bannedCountries = new ArrayList<>();
					//for (int i = 5; i < parts.length; i++) {
					//	bannedCountries.add(parts[i]);
				//	}
					data.addMovie(new Movie(data.getHighestId(), tempName, amount + "", price + "", amount + "",
							bannedCountries));
					connections.send(connectionId, "Ack addmovie \"" + tempName + "\" success");
					connections.broadcast("BROADCAST movie \"" + tempName + "\" " + amount + " " + price);
				} else {
					connections.send(connectionId, "ERROR requset addmovie failed"); // isn't logged in
					break;
				}
			} else if (parts[1].compareTo("remmovie") == 0) {
				String temp="";
				for(int i=2;i<parts.length;i++) {
					if(i!=parts.length-1) {
					temp=temp+parts[i]+" ";
					}
					else {
						temp=temp+parts[i];
					}
				}
				temp=temp.substring(1,temp.length()-1);
				Movie currentMovie = data.getMovieByName(temp);
				if (currentUser.getType().compareTo("admin") == 0 && currentMovie != null
						&& data.getMovies().contains(currentMovie)
						&& currentMovie.getAviailableAmount().compareTo(currentMovie.getTotalAmount())==0) {
					data.removeMovie(currentMovie);
					connections.send(connectionId, "Ack remmovie \"" + temp + "\" success");
					connections.broadcast("BROADCAST movie \"" + temp + "\" removed");
				} else {
					connections.send(connectionId, "ERROR requset remmovie failed"); // isn't logged in
					break;
				}
			} else if (parts[1].compareTo("changeprice") == 0) {
				int first = message.indexOf("\"");
				int second = message.indexOf("\"", first+1);
				if(first == -1) {
					first =  message.indexOf("“");
					second = message.indexOf("”", first+1);
				}
				String tempName=message.substring(first+1, second);
				String price = message.substring(second+1,message.length()).replace(" ", "");
				
				
				Movie currentMovie = data.getMovieByName(tempName);
				if (currentMovie != null && currentUser.getType().compareTo("admin") == 0
						&& data.getMovies().contains(currentMovie) &&  Integer.parseInt(price) > 0) {
					currentMovie.setPrice(price);
					data.changeMovie(currentMovie, "price", currentMovie.getPrice());
					connections.send(connectionId, "Ack changeprice \"" +tempName+ "\" success");
					connections.broadcast(
							"BROADCAST movie \"" + tempName+ "\" " + currentMovie.getAviailableAmount() + " " + price);

				} else {
					connections.send(connectionId, "ERROR requset changeprice failed"); // isn't logged in
					break;
				}
			}
		}
	}

	@Override
	public boolean shouldTerminate() {
		// TODO Auto-generated method stub
		return terminate;
	}

}
