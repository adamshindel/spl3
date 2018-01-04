package bgu.spl181.net.api.bidi;

public class User_service_text_protocol<String> implements BidiMessagingProtocol<String>{

	private Connections<String> connections; 
	private int conncetionId;
	@Override
	public void start(int connectionId, Connections<String> connections) {
		// TODO Auto-generated method stub
		this.connections = connections;
		this.conncetionId = connectionId;
	}

	@Override
	public void process(String message) {
		// TODO Auto-generated method stub
		// not sure if should proccess messages here??? -- > if(message == "REQUEST balance info");
	}

	@Override
	public boolean shouldTerminate() { 	
		return false;// what does this do?????
	}

}
