package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;
import java.util.Comparator;

public final class BribePlayer extends BasePlayer {
  public BribePlayer(final int id) {
    super(id);
  }

  @Override
  public void print() {
    System.out.println(id + " " + "BRIBED" + " " + this.score);
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
        new Comparator<Integer>() {
          @Override
          public int compare(final Integer good1, final Integer good2) {
            int res =
                GoodsFactory.getInstance().getGoodsById(good2).getProfit()
                    - GoodsFactory.getInstance().getGoodsById(good1).getProfit();
            if (res == 0) {
              return good2 - good1;
            } else {
              return res;
            }
          }
        };

    illegals.sort(comparator);
    legals.sort(comparator);

    if (coins <= Constants.TWO_GOODS_BRIBE || illegals.size() == 0) {
      super.createBag(round);
    } else {
      int maxIllegal = (coins - 1) / Constants.ILLEGAL_PENALTY;
      maxIllegal = Math.min(illegals.size(), maxIllegal);
      maxIllegal = Math.min(maxIllegal, Constants.BAG_CAPACITY);
      if (coins > Constants.MORE_GOODS_BRIBE && maxIllegal > 2) {
        for (int i = 0; i < maxIllegal; ++i) {
          bag.add(illegals.get(0));
          illegals.remove(0);
        }
        bribe = Constants.MORE_GOODS_BRIBE;
        declaration = 0;
      } else {
        maxIllegal = Math.min(maxIllegal, 2);
        for (int i = 0; i < maxIllegal; ++i) {
          bag.add(illegals.get(0));
          illegals.remove(0);
        }
        bribe = Constants.TWO_GOODS_BRIBE;
        declaration = 0;
      }
      int maxLegal = (coins - maxIllegal * Constants.ILLEGAL_PENALTY - 1) / Constants.LEGAL_PENALTY;
      while (bag.size() < Constants.BAG_CAPACITY && maxLegal > 0 && legals.size() > 0) {
        bag.add(legals.get(0));
        legals.remove(0);
        maxLegal--;
      }
      coins -= bribe;
    }
  }

  @Override
  public void inspect(final ArrayList<BasePlayer> players, final ArrayList<Integer> cards) {
    int left = (players.size() + id - 1) % players.size();
    int right = (id + 1) % players.size();

    if (left == right) {
      this.inspect(players.get(left), cards);
    } else {
      this.inspect(players.get(left), cards);
      this.inspect(players.get(right), cards);
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
