package br.com.zenith.server;

import br.com.zenith.domain.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState {

    private final List<Player> players = new CopyOnWriteArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }
}
