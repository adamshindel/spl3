package bgu.spl181.net;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import bgu.spl181.net.api.LineMessageEncoderDecoder;
import bgu.spl181.net.api.MessageEncoderDecoder;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.MovieRentalServiceProtocol;
import bgu.spl181.net.srv.Movie;
import bgu.spl181.net.srv.Server;
import bgu.spl181.net.srv.SharedData;
import bgu.spl181.net.srv.User;

public class BidiServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*SharedData service = SharedData.getData();
		String s = "1=2";
		String[] a = s.split("=");
		for(String s1 : a) {
			System.out.print(s1 + ",");
		}
		System.out.println("   length is " + a.length);
		*/
		/*User john = new User("ravid","normal","sisma","isreal",new ConcurrentHashMap<>(),"0");
		//service.addUser(adam);
		Map<String,String> moviesToAdd = new ConcurrentHashMap<>();
		moviesToAdd.put("0", "movie1");
		moviesToAdd.put("1", "movie2");
		moviesToAdd.put("2", "movie3");
		// = moviesToAdd;
		//service.changeMoviesUser(john, moviesToAdd);*/
		//bgu.spl181.net.srv.SharedData a = SharedData.getData();
		//a.addUser(john);
		//Movie m = new Movie("9", "Moviee","99","7","999", new ArrayList<String>());
		//a.addMovie(m);
		Server.reactor(8,7777, ()->new MovieRentalServiceProtocol(),()->new LineMessageEncoderDecoder()).serve();
		//Server.threadPerClient(7777, ()->new MovieRentalServiceProtocol(),()->new LineMessageEncoderDecoder()).serve();
	}
}
