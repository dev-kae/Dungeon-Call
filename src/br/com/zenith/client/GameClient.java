package br.com.zenith.client;

import br.com.zenith.network.packet.Packet;
import br.com.zenith.network.packet.PacketType;
import br.com.zenith.shared.ServerLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {
    private final ServerLogger logger;
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    public boolean connected = false;

    public GameClient(ServerLogger logger) {
        this.logger = logger;
    }

    public static void main(String[] args) {
        ServerLogger logger = new ServerLogger();
        new GameClient(logger).connect();
    }

    public void connect() {
        try {
            clientSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutputStream.flush();

            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            connected = true;

            logger.info("conectado com sucesso, digite o nome do jogador: ");
            String playerName = IO.readln();
            createPlayer(playerName);
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            send(new Packet(PacketType.DISCONNECT, null));
            connected = false;
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createPlayer(String name) {
        send(new Packet(PacketType.JOIN, name));
    }


    public void send(Object packet) {
        try {
            logger.info("Enviando mensagem para o handler. " + packet);
            objectOutputStream.writeObject(packet);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() {
        try {
            while (connected) {
                Object message = objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
