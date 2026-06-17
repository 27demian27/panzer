package nl.demiannieuwenhuis.panzer.game.net;

import lombok.Getter;

import java.net.InetAddress;
import java.net.SocketAddress;

public class Client {
    private final @Getter int id;
    private final @Getter SocketAddress socketAddress;
    private @Getter long lastActive;
    long maxSequenceNumber;
    public Client(int id, SocketAddress socketAddress) {
        this.id = id;
        this.socketAddress = socketAddress;
        lastActive = 0;
        maxSequenceNumber = 0;
    }

    public void addInactivity (long nanos) {
        lastActive += nanos;
    }

    public void touch() {
        lastActive = 0;
    }
}
