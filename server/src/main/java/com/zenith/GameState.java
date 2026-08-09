package com.zenith;

import com.zenith.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class GameState {
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
