package br.com.zenith.domain;

public class Player {
    private final String name;
    private Long lives;
    private Long score;

    public Player(String name) {
        this.name = name;
        this.lives = 3L;
        this.score = 0L;
    }

    public String getName() {
        return name;
    }


}
