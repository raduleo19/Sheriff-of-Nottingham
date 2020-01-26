package com.players;

import com.common.Constants;

import java.util.ArrayList;
import java.util.List;

public final class GreedyPlayer extends BasePlayer {
  public GreedyPlayer(final int id) {
    super(id);
  }

  @Override
  public String toString() {
    return this.id + " " + Constants.GREEDY.toUpperCase() + " " + this.coins;
  }

  @Override
  public void createBag(final int round) {
    super.createBag(round);
    if (round % 2 == 0) {
      if (bag.size() < Constants.BAG_CAPACITY) {
        Integer maxIllegal = getMaxIllegal();
        if (maxIllegal != null && coins >= Constants.ILLEGAL_PENALTY) {
          bag.add(maxIllegal);
          hand.remove(maxIllegal);
        }
      }
    }
  }

  @Override
  public void inspect(final ArrayList<BasePlayer> players, final List<Integer> goods) {
    for (BasePlayer player : players) {
      if (player != this) {
        if (player.bribe > 0) {
          coins += player.bribe;
          player.bribe = 0;
          player.moveToStand();
        } else {
          inspect(player, goods);
        }
      }
    }
  }
}
