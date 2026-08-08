package br.com.zenith.server;

import br.com.zenith.domain.Player;
import br.com.zenith.network.packet.Packet;
import br.com.zenith.network.packet.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler implements Runnable {
    private final UUID uuid = UUID.randomUUID();
    private final GameServer server;
    private Player player;
    private Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private boolean connected;

    public ClientHandler(Socket socket, GameServer server) throws IOException {
        this.socket = socket;
        this.server = server;

        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socket.getInputStream());
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void run() {
        connected = true;
        try {
            while (connected) {
                Object message = input.readObject();

                if (message instanceof Packet(PacketType type, Object data)) {
                    switch (type) {
                        case JOIN -> {
                            String name = (String) data;

                            player = new Player(name);
                            server.logInfo("Jogador criado: " + player.getName());
                            server.broadcast(new Packet(PacketType.BROADCAST, "%s conectou-se.".formatted(name)), this);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            connected = false;
        }
    }

    //Envia uma mensagem do server para o player.
    public void sendPacket(Packet packet) throws IOException {
        output.writeObject(packet);
        output.flush();
    }


}
