package com.tema1.main;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.LegalGoods;
import com.tema1.players.BribePlayer;
import com.tema1.players.GreedyPlayer;
import com.tema1.players.BasePlayer;

import java.util.ArrayList;
import java.util.Collections;

public final class Game {
  private int numRounds;
  private ArrayList<BasePlayer> players;
  private ArrayList<Integer> cards;

  public Game(final GameInput gameInput) {
    players = new ArrayList<BasePlayer>();

    int id = 0;
    for (String name : gameInput.getPlayerNames()) {
      if (name.equals(Constants.BASE)) {
        players.add(new BasePlayer(id++));
      } else if (name.equals(Constants.BRIBE)) {
        players.add(new BribePlayer(id++));
      } else if (name.equals(Constants.GREEDY)) {
        players.add(new GreedyPlayer(id++));
      }
    }

    numRounds = gameInput.getRounds();
    cards = (ArrayList<Integer>) gameInput.getAssetIds();
  }

  public void run() {
    for (int round = 1; round <= numRounds; ++round) {
      for (BasePlayer sheriff : players) {
        for (BasePlayer merchant : players) {
          if (merchant != sheriff) {
            merchant.drawCards(cards);
            merchant.createBag(round);
          }
        }
        sheriff.inspect(players, cards);
      }
    }
    computeScore();
  }

  public void computeScore() {
    for (BasePlayer player : players) {
      player.computeScore();
    }
  }

  public void rewardBestPlayers() {
    for (int good = 0; good < Constants.MAX_LEGAL_ID; ++good) {
      if (GoodsFactory.getInstance().getGoodsById(good) != null) {
        int best = 0;
        for (int player = 1; player < players.size(); ++player) {
          if (players.get(player).getGoodFreq(good) > players.get(best).getGoodFreq(good)) {
            best = player;
          }
        }
        if (players.get(best).getGoodFreq(good) != 0) {
          players.get(best).addCoins(((LegalGoods) GoodsFactory.getInstance().getGoodsById(good)).getKingBonus());
        }
        int best2 = 0;
        if (best == best2) {
          best2++;
        }
        for (int player = 0; player < players.size(); ++player) {
          if (player != best && players.get(player).getGoodFreq(good) > players.get(best2).getGoodFreq(good)) {
            best2 = player;
          }
        }
        if (players.get(best2).getGoodFreq(good) != 0) {
          players.get(best2).addCoins(((LegalGoods) GoodsFactory.getInstance().getGoodsById(good)).getQueenBonus());
        }
      }
    }
  }

  public void printLeaderboard() {
    Collections.sort(players);
    for (BasePlayer player : players) {
      player.print();
    }
  }
}
