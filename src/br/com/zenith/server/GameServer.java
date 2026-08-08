package br.com.zenith.server;

import br.com.zenith.network.packet.Packet;
import br.com.zenith.shared.ServerLogger;

import java.io.*;
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

    public GameServer(ServerLogger logger) {
        this.logger = logger;
    }

    public static void main(String[] args) {
        ServerLogger logger = new ServerLogger();
        GameServer server = new GameServer(logger);

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

        IO.println("Servidor encerrado.");
    }
}