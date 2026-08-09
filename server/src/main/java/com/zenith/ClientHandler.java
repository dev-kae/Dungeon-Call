package com.zenith;

import com.zenith.domain.Player;
import com.zenith.network.MovementData;
import com.zenith.network.packet.Packet;
import com.zenith.network.packet.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Player player;
    private boolean connected;

    private Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    private final GameServer server;

    public ClientHandler(Socket socket, GameServer server) throws IOException {
        this.socket = socket;
        this.server = server;

        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socket.getInputStream());
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

                            server.playerJoined(this, player);
                        }
                        case DISCONNECT -> {
                            if (player != null) {
                                server.logInfo(
                                        "Player %s desconectou-se.".formatted(player.getName())
                                );

                                server.broadcast(
                                        new Packet(
                                                PacketType.BROADCAST,
                                                "%s desconectou-se.".formatted(player.getName())
                                        ),
                                        this
                                );
                            }

                            this.connected = false;
                        }
                        case MOVE -> {

                            if (player == null) {
                                break;
                            }

                            MovementData movement = (MovementData) data;

                            float speed = 200f;

                            float newX =
                                player.getX() + movement.dx() * speed * 0.016f;

                            float newY =
                                player.getY() + movement.dy() * speed * 0.016f;

                            player.setPosition(newX, newY);

                            server.playerMoved(player);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            connected = false;

        } finally {
            server.disconnectClient(this);
        }
    }

    public void sendPacket(Packet packet) throws IOException {
        output.writeObject(packet);
        output.flush();
    }
}
