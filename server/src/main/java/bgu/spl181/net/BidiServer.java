package bgu.spl181.net;

import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.Movie_Rental_Service_Users;
import bgu.spl181.net.srv.User;

public class BidiServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Movie_Rental_Service_Users service = new Movie_Rental_Service_Users();
		String s = "1=2";
		String[] a = s.split("=");
		for(String s1 : a) {
			System.out.print(s1 + ",");
		}
		System.out.println("   length is " + a.length);
		
		User adam = new User("AdamShindel2", "some type","Cola", "Israel",new ConcurrentHashMap<String,String>(),5);
		service.addUser(adam);
		service.changeBalanceUser("AdamShindel2", 6000);
		//service.changeBalanceUser(adam.userName, 1111);
	}
}
