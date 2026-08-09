package com.zenith;

import com.zenith.domain.Player;
import com.zenith.network.packet.Packet;
import com.zenith.network.packet.PacketType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private final ServerLogger logger;
    private static final int DEFAULT_PORT = 5000;
    private static final int MAX_CONNECTIONS = 4;
    private boolean running;
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final GameState state;

    public GameServer(ServerLogger logger, GameState gameState) {
        this.logger = logger;
        this.state = gameState;
    }

    public static void main(String[] args) {
        ServerLogger logger = new ServerLogger();
        GameState gameState = new GameState();
        GameServer server = new GameServer(logger, gameState);

        server.start();
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logError(String message) {
        logger.error(message);
    }

    public void broadcast(Packet packet, ClientHandler except) throws IOException {
        for (ClientHandler client : clients) {
            if (client != except) {
                client.sendPacket(packet);
            }
        }
    }

    public void start() {
        logger.info("Iniciando servidor...");

        try {
            serverSocket = new ServerSocket(DEFAULT_PORT, MAX_CONNECTIONS);
            running = true;

            logger.info("Servidor iniciado no endereço: " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());

            acceptConnections();

        } catch (IOException e) {
            logger.error("Não foi possível iniciar o servidor.");
            e.printStackTrace();
        }
    }

    private void acceptConnections() throws IOException {
        while (running) {
            Socket socket = serverSocket.accept();

            ClientHandler clientHandler = new ClientHandler(socket, this);
            clients.add(clientHandler);

            Thread thread = new Thread(clientHandler);
            thread.start();

            logger.info(
                    "Nova conexão recebida: " + socket.getInetAddress().getHostAddress()
            );
        }
    }

    public void disconnectClient(ClientHandler client) {
        clients.remove(client);

        Player player = client.getPlayer();

        if (player == null) {
            return;
        }

        removePlayer(player);

        try {
            broadcast(
                new Packet(
                    PacketType.PLAYER_LEFT,
                    player.getId()
                ),
                null
            );
        } catch (IOException e) {
            logError(
                "Erro ao informar saída do jogador: "
                    + player.getName()
            );
        }
    }

    public void addPlayer(Player player) {
        state.addPlayer(player);
    }

    public void removePlayer(Player player) {
        state.removePlayer(player);
    }

    public void playerJoined(ClientHandler client, Player player) {

        state.addPlayer(player);

        try {
            client.sendPacket(
                new Packet(
                    PacketType.JOIN_ACCEPTED,
                    player.toData()
                )
            );

            broadcast(
                new Packet(
                    PacketType.PLAYER_JOINED,
                    player.toData()
                ),
                client
            );

            for (Player existingPlayer : state.getPlayers()) {

                if (existingPlayer == player) {
                    continue;
                }

                client.sendPacket(
                    new Packet(
                        PacketType.PLAYER_JOINED,
                        existingPlayer.toData()
                    )
                );
            }

            logInfo("Jogador entrou: " + player.getName());

        } catch (IOException e) {
            logError(
                "Erro ao sincronizar jogador: "
                    + player.getName()
            );
        }
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Servidor encerrado.");
    }

    public void playerMoved(Player player) {

        try {
            broadcast(
                new Packet(
                    PacketType.PLAYER_MOVED,
                    player.toData()
                ),
                null
            );
        } catch (IOException e) {
            logError(
                "Erro ao sincronizar movimento de "
                    + player.getName()
            );
        }
    }
}
