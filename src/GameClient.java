package io.github.dungeon_call;

import com.zenith.ServerLogger;
import com.zenith.network.MovementData;
import com.zenith.network.packet.Packet;
import com.zenith.network.packet.PacketType;
import com.zenith.network.packet.PlayerData;
import io.github.dungeon_call.entities.player.RemotePlayer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient {
    private UUID myPlayerId;
    private volatile boolean connected = false;

    private Socket clientSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    private final ServerLogger logger;
    private final Map<UUID, RemotePlayer> players = new ConcurrentHashMap<>();

    public GameClient(ServerLogger logger) {
        this.logger = logger;
    }

    public void connect() {
        try {
            clientSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);

            objectOutputStream =
                new ObjectOutputStream(clientSocket.getOutputStream());

            objectOutputStream.flush();

            objectInputStream =
                new ObjectInputStream(clientSocket.getInputStream());

            connected = true;

            logger.info("Conectado ao servidor!");

            createPlayer("Zenith");

            Thread listenerThread = new Thread(this::listen);
            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (IOException e) {
            logger.error("Não foi possível conectar ao servidor." + e);
        }
    }

    public void disconnect() {
        if (!connected) {
            return;
        }

        try {
            send(new Packet(PacketType.DISCONNECT, null));

            connected = false;
            clientSocket.close();

        } catch (IOException e) {
            logger.error("Erro ao desconectar." + e);
        }
    }

    public synchronized void send(Object packet) {
        try {
            logger.info("Enviando mensagem: " + packet);

            objectOutputStream.writeObject(packet);
            objectOutputStream.flush();

        } catch (IOException e) {
            logger.error("Erro ao enviar pacote." + e);
        }
    }

    private void listen() {
        try {
            while (connected) {

                Object message = objectInputStream.readObject();

                if (message instanceof Packet(PacketType type, Object data)) {

                    switch (type) {

                        case JOIN_ACCEPTED -> {
                            PlayerData playerData = (PlayerData) data;

                            myPlayerId = playerData.id();

                            players.put(
                                playerData.id(),
                                new RemotePlayer(playerData)
                            );
                        }

                        case PLAYER_JOINED -> {
                            PlayerData playerData = (PlayerData) data;

                            players.put(
                                playerData.id(),
                                new RemotePlayer(playerData)
                            );

                            logger.info(
                                "Player entrou: "
                                    + playerData.name()
                            );
                        }

                        case PLAYER_LEFT -> {
                            UUID playerId = (UUID) data;

                            players.remove(playerId);

                            logger.info(
                                "Jogador saiu: " + playerId
                            );
                        }

                        case BROADCAST -> {
                            logger.info((String) data);
                        }

                        case PLAYER_MOVED -> {

                            PlayerData playerData = (PlayerData) data;

                            RemotePlayer player =
                                players.get(playerData.id());

                            if (player != null) {
                                player.setPosition(
                                    playerData.x(),
                                    playerData.y()
                                );
                            }
                        }

                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {

            if (connected) {
                logger.error(
                    "Conexão com o servidor perdida." + e
                );
            }

            connected = false;
        }
    }

    public Map<UUID, RemotePlayer> getPlayers() {
        return players;
    }

    public void createPlayer(String name) {
        send(new Packet(PacketType.JOIN, name));
    }

    public RemotePlayer getMyPlayer() {
        if (myPlayerId == null) {
            return null;
        }

        return players.get(myPlayerId);
    }

    public void move(float dx, float dy) {

        send(
            new Packet(
                PacketType.MOVE,
                new MovementData(dx, dy)
            )
        );
    }
}
