/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl181.net.api.bidi;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author bennyl
 */
public interface ConnectionHandler<T> extends Closeable{
	/*
	 * The ConnectionHandler object maintains the state of the connection for the specific client which it serves 
	 * (for example, if the user performed "login", the ConnectionHandler object will remember this in its state).
	 */
    void send(T msg) ;

}
