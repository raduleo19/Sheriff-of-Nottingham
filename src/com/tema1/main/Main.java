package com.main;

public final class Main {

  private Main() {

  }

    public static void main(final String[] args) {
      GameInputLoader gameInputLoader = new GameInputLoader(args[0]);
      GameInput gameInput = gameInputLoader.load();
      Game game = new Game(gameInput);
      game.run();
      game.rewardIllegalBonuses();
      game.calculateScore();
      game.rewardKingAndQueenBonuses();
      game.sortLeaderboard();
      game.printLeaderboard();
    }
}
