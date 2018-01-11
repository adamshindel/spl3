#include "../include/connectionHandler.h"
#include <boost/thread/thread.hpp>
#include <iostream>


void readServerMessagesTherad(ConnectionHandler &connectionHandler){
    while (1) {
        std::string line;
        connectionHandler.getLine(line);
        std::cout<< line << std::endl;
        if (line=="ACK signout succeeded\n"){
            break;
        }
    }
}

void readUserCommandThread(ConnectionHandler &connectionHandler){
    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        connectionHandler.sendLine(line);
    }
}

int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    boost::thread handleUserRequest(readUserCommandThread, boost::ref(connectionHandler));
    boost::thread handleCommunicateWithServer(readServerMessagesTherad, boost::ref(connectionHandler));
    handleCommunicateWithServer.join();
    handleUserRequest.interrupt();
    connectionHandler.close();
    return 0;
}