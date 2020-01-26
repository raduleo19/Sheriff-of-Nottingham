package com.tema1.main;

public final class Main {

    private Main() {

    }

    public static void main(final String[] args) {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0]);
        GameInput gameInput = gameInputLoader.load();
        Game game = new Game(gameInput);
        game.run();
        game.rewardBestPlayers();
        game.printLeaderboard();
    }
}
