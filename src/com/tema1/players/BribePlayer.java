package com.players;

import com.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BribePlayer extends BasePlayer {
  public BribePlayer(final int id) {
    super(id);
  }

  @Override
  public String toString() {
    return id + " " + Constants.BRIBE.toUpperCase() + " " + this.coins;
  }

  public void createBag(final int round) {
    ArrayList<Integer> illegals = new ArrayList<Integer>();
    ArrayList<Integer> legals = new ArrayList<Integer>();

    for (Integer good : hand) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType().equals(GoodsType.Illegal)) {
        illegals.add(good);
      } else {
        legals.add(good);
      }
    }

    Comparator<Integer> comparator =
            (good1, good2) -> {
              int res =
                  GoodsFactory.getInstance().getGoodsById(good2).getProfit()
                      - GoodsFactory.getInstance().getGoodsById(good1).getProfit();
              if (res == 0) {
                return good2 - good1;
              } else {
                return res;
              }
            };

    illegals.sort(comparator);
    legals.sort(comparator);



    if (coins <= Constants.TWO_GOODS_BRIBE || illegals.size() == 0) {
      super.createBag(round);
    } else {
      ArrayList<Integer> tempBag = new ArrayList<>();
      int maxIllegal = (coins - 1) / Constants.ILLEGAL_PENALTY;
      maxIllegal = Math.min(illegals.size(), maxIllegal);
      maxIllegal = Math.min(maxIllegal, Constants.BAG_CAPACITY);
      if (coins > Constants.MORE_GOODS_BRIBE && maxIllegal > 2) {
        for (int i = 0; i < maxIllegal; ++i) {
          tempBag.add(illegals.get(0));
          illegals.remove(0);
        }
        bribe = Constants.MORE_GOODS_BRIBE;
        declaration = 0;
      } else {
        maxIllegal = Math.min(maxIllegal, 2);
        for (int i = 0; i < maxIllegal; ++i) {
          tempBag.add(illegals.get(0));
          illegals.remove(0);
        }
        bribe = Constants.TWO_GOODS_BRIBE;
        declaration = 0;
      }
      int maxLegal = (coins - maxIllegal * Constants.ILLEGAL_PENALTY - 1) / Constants.LEGAL_PENALTY;
      while (tempBag.size() < Constants.BAG_CAPACITY && maxLegal > 0 && legals.size() > 0) {
        tempBag.add(legals.get(0));
        legals.remove(0);
        maxLegal--;
      }
      coins -= bribe;

      // Keep original order
      for (Integer good : hand) {
        if (tempBag.contains(good)) {
          bag.add(good);
          tempBag.remove(good);
        }
      }
    }
  }

  @Override
  public void inspect(final ArrayList<BasePlayer> players, final List<Integer> goods) {
    int left = (players.size() + id - 1) % players.size();
    int right = (id + 1) % players.size();

    if (left == right) {
      this.inspect(players.get(left), goods);
    } else {
      this.inspect(players.get(left), goods);
      this.inspect(players.get(right), goods);
    }

    for (int i = 0; i < players.size(); ++i) {
      if (i != id && i != left && i != right) {
        coins += players.get(i).bribe;
        players.get(i).bribe = 0;
        players.get(i).moveToStand();
      }
    }
  }
}
