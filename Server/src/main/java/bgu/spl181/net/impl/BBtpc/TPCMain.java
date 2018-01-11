package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.bidi.MovieRentalServiceProtocol;
import bgu.spl181.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl181.net.srv.Server;

public class TPCMain {
	public static void main(String[] args) {
		Server.reactor(8, 7777, () -> new MovieRentalServiceProtocol(), () -> new LineMessageEncoderDecoder()).serve();
	}
}
