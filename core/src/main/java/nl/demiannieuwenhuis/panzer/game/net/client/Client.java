package nl.demiannieuwenhuis.panzer.game.net.client;

import lombok.Getter;
import lombok.Setter;

import java.net.SocketAddress;

public class Client {
    private final @Getter int id;
    private final @Getter SocketAddress socketAddress;
    private @Getter long lastActive;
    private @Getter @Setter long maxSequenceNumber = Long.MIN_VALUE;
    public Client(int id, SocketAddress socketAddress) {
        this.id = id;
        this.socketAddress = socketAddress;
        lastActive = 0;
    }

    public void addInactivity (long nanos) {
        lastActive += nanos;
    }

    public void touch() {
        lastActive = 0;
    }
}
