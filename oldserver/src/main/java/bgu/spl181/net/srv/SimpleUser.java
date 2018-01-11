package bgu.spl181.net.srv;

public class SimpleUser {

	protected String userName;
	protected String password;
	
	public SimpleUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
	public String getUserName() {
		return this.userName;
	}
	public String getPassword() {
		return this.password;
	}
	
}
