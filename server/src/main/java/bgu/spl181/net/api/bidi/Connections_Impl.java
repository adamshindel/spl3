package bgu.spl181.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * This interface should map a unique ID for each active client ----------> to
 * do this don't forget connected to the server. The implementation of
 * Connections is part of the server pattern and not part of the protocol
 * 
 * @param <T>
 */
public class Connections_Impl<T> implements Connections<T> {

	private AtomicInteger idCounter = new AtomicInteger(0);
	private Map<Integer, ConnectionHandler<T>> activeUsers = new ConcurrentHashMap<>();
	// will hold all connected users

	@Override
	/**
	 * send's Confirmation message??
	 */
	public boolean send(int connectionId, Object msg) {
		if (activeUsers.containsKey(connectionId)) {
			activeUsers.get(connectionId).send((T) msg);
			return true;
		}
		return false;// what should be here??
	}

	@Override
	/**
	 * send's a message to all activeUsers (Or is it Connected only???)
	 */
	public void broadcast(Object msg) {
		// TODO Auto-generated method stub
		for (Map.Entry<Integer, ConnectionHandler<T>> entry : activeUsers.entrySet()) {
			entry.getValue().send((T) msg);
		}
	}

	@Override
	/**
	 * remove the users from connected users storage
	 */
	public void disconnect(int connectionId) {
		// TODO Auto-generated method stub
		activeUsers.remove(connectionId);
	}

	public synchronized Integer connect(ConnectionHandler<T> toAdd) {
		this.activeUsers.put(idCounter.get(), toAdd);
		int result =idCounter.get();
		 idCounter.incrementAndGet();
		return result;

	}

}
