package nl.demiannieuwenhuis.panzer.game.net;

import com.badlogic.gdx.Gdx;
import lombok.Getter;
import nl.demiannieuwenhuis.panzer.game.io.BattleMap;
import nl.demiannieuwenhuis.panzer.game.net.client.Client;
import nl.demiannieuwenhuis.panzer.game.net.server.BattleServer;
import nl.demiannieuwenhuis.panzer.game.net.server.GameRoomControlServer;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameRoom {
    private static int UID = 0;
    public final String code;
    public static final int MAX_PLAYERS = 8;

    public final String mapFileName;

    private @Getter Set<Client> connectedClients;

    private final GameRoomControlServer gameRoomControlServer;
    private @Getter final BattleServer battleServer;

    public GameRoom(BattleMap battleMap) {
        int port = 4445;
        this.code = hashPort(port);
        this.mapFileName = (battleMap != null ? battleMap.fileName : "default_level.map");
        this.connectedClients = new HashSet<>();
        try {
            gameRoomControlServer = new GameRoomControlServer(port, this);
            battleServer = new BattleServer(port, this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gdx.app.log("GameRoom", "Created Room with code: " + code);


    }

//    public List<TankUpdate> getTankUpdates

    public void startControlServer() {
        gameRoomControlServer.start();
    }

    public void startBattleServer() {
        battleServer.start();
    }


    public void incrementClientsLastActiveTimes(long nanos) {
        for (Client client : connectedClients) {
            client.addInactivity(nanos);
        }
    }

    public void disconnectInactiveClients() {
        connectedClients = connectedClients.stream()
            .filter(client -> client.getLastActive() <= 500_000_000)
            .collect(Collectors.toSet());
    }

    public void addClientConnection(SocketAddress socketAddress) {
        UID++;
        connectedClients.add(new Client(UID, socketAddress));
    }

    public Optional<Client> findConnectedClient(SocketAddress socketAddress) {
        return connectedClients.stream()
            .filter(client -> client.getSocketAddress().equals(socketAddress))
            .findAny();
    }

    Optional<Client> findConnectedClient(int id) {
        return connectedClients.stream().filter(client -> client.getId() == id).findAny();
    }

    private String hashPort(int port) {
        return "ABCD";
    }

    public static int unhashRoomCode(String code) {
        return 4445;
    }
}
