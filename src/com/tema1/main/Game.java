package com.main;

import com.common.Constants;
import com.goods.GoodsFactory;
import com.goods.LegalGoods;
import com.players.BasePlayer;
import com.players.PlayerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Game {
  private int numRounds;
  private ArrayList<BasePlayer> players;
  private List<Integer> goods;

  public Game(final GameInput gameInput) {
    numRounds = gameInput.getRounds();
    goods = gameInput.getAssetIds();
    players = new ArrayList<>();

    int id = 0;
    for (String name : gameInput.getPlayerNames()) {
      players.add(PlayerFactory.getInstance().getPlayer(name, id++));
    }
  }

  public void run() {
    for (int round = 1; round <= numRounds; ++round) {
      for (BasePlayer sheriff : players) {
        for (BasePlayer merchant : players) {
          if (merchant != sheriff) {
            merchant.drawCards(goods);
            merchant.createBag(round);
            merchant.burnCards();
          }
        }
        sheriff.inspect(players, goods);
      }
    }
  }

  public void rewardIllegalBonuses() {
    for (BasePlayer player : players) {
      player.rewardIllegalBonus();
    }
  }

  public void calculateScore() {
    for (BasePlayer player : players) {
      player.calculateScore();
    }
  }

  /**
   * For every type of goods calculates 2 max and then rewards them by king and queen.
   */
  public void rewardKingAndQueenBonuses() {
    for (Integer good = 0; good < Constants.MAX_LEGAL_ID; ++good) {
      if (GoodsFactory.getInstance().getGoodsById(good) != null) {
        Integer king = null;
        Integer queen = null;
        for (int player = 0; player < players.size(); ++player) {
          if (king == null) {
            king = player;
          } else if (queen == null) {
            if (players.get(player).getGoodFreq(good) > players.get(king).getGoodFreq(good)) {
              queen = king;
              king = player;
            } else {
              queen = player;
            }
          } else if (players.get(player).getGoodFreq(good) > players.get(king).getGoodFreq(good)) {
            queen = king;
            king = player;
          } else if (players.get(player).getGoodFreq(good) > players.get(queen).getGoodFreq(good)) {
            queen = player;
          }
        }
        if (players.get(king).getGoodFreq(good) != 0) {
          players.get(king).addCoins(((LegalGoods) GoodsFactory.getInstance().getGoodsById(good)).getKingBonus());
          if (players.get(queen).getGoodFreq(good) != 0) {
            players.get(queen).addCoins(((LegalGoods) GoodsFactory.getInstance().getGoodsById(good)).getQueenBonus());
          }
        }
      }
    }
  }

  public void sortLeaderboard() {
    Collections.sort(players);
  }

  public void printLeaderboard() {
    for (BasePlayer player : players) {
      System.out.println(player.toString());
    }
  }
}
