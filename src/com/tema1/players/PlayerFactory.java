package com.players;

import com.common.Constants;

public final class PlayerFactory {

  private static PlayerFactory instance = new PlayerFactory();

  public static PlayerFactory getInstance() {
    return instance;
  }

  private PlayerFactory() {

  }

  public BasePlayer getPlayer(final String playerType, final int id) {
    if (playerType.equals(Constants.BASE)) {
      return new BasePlayer(id);
    } else if (playerType.equals(Constants.BRIBE)) {
      return new BribePlayer(id);
    } else if (playerType.equals(Constants.GREEDY)) {
      return new GreedyPlayer(id);
    }
    return null;
  }
}
