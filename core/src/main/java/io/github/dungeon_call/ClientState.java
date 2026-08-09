package io.github.dungeon_call;

import com.zenith.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class ClientState {

    private final List<Player> players = new ArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }
}
