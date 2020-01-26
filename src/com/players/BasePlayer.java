package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.goods.IllegalGoods;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.min;

public class BasePlayer implements Comparable<BasePlayer> {
  protected int id;
  protected int coins;
  protected int declaration;
  protected int bribe;
  protected int score;
  protected ArrayList<Integer> hand;
  protected ArrayList<Integer> bag;
  protected ArrayList<Integer> stand;
  //    protected HashMap<Integer,Integer> stand;

  public BasePlayer(final int id) {
    this.score = 0;
    this.id = id;
    this.coins = Constants.INIT_COINS;
    this.declaration = 0;



    this.bribe = 0;
    hand = new ArrayList<Integer>();
    bag = new ArrayList<Integer>();
    stand = new ArrayList<Integer>();
  }

  public final int compareTo(final BasePlayer other) {
    return other.score - this.score;
  }

  public void print() {
    System.out.println(id + " " + Constants.BASE_MESSAGE + " " + this.score);
  }

  public void createBag(final int round) {

//    System.out.println(bag);
    ArrayList<Integer> frequency =
        new ArrayList<Integer>(Collections.nCopies(Constants.MAX_GOODS_ID, 0));

    for (Integer good : hand) {
      frequency.set(good, frequency.get(good) + 1);
    }

    Integer mostFreq = null;
    for (int i = 0; i < frequency.size(); ++i) {
      if (frequency.get(i) > 0) {
        if (GoodsFactory.getInstance().getGoodsById(i).getType().equals(GoodsType.Legal)) {
          if (mostFreq == null) {
            mostFreq = i;
          } else if (frequency.get(mostFreq) < frequency.get(i)) {
            mostFreq = i;
          } else if (frequency.get(mostFreq) == frequency.get(i)) {
            if (GoodsFactory.getInstance().getGoodsById(mostFreq).getProfit()
                <= GoodsFactory.getInstance().getGoodsById(i).getProfit()) {
              mostFreq = i;
            }
          }
        }
      }
    }
    if (mostFreq != null) {
//      System.out.println(mostFreq);
      int freq = min(frequency.get(mostFreq), Constants.BAG_CAPACITY);
      for (int i = 0; i < freq; ++i) {
        bag.add(mostFreq);
        hand.remove(mostFreq);
      }
      declaration = mostFreq;
//      System.out.println(bag);

    } else {
      Integer best = hand.get(0);
      for (Integer good : hand) {
        if (GoodsFactory.getInstance().getGoodsById(good).getProfit()
            > GoodsFactory.getInstance().getGoodsById(best).getProfit()) {
          best = good;
        }
      }

      if (coins >= Constants.ILLEGAL_PENALTY) {
        bag.add(best);
        hand.remove(best);
      }

      declaration = Constants.APPLE_ID;
    }
  }

  public final void drawCards(final ArrayList<Integer> cards) {
    while (hand.size() < Constants.HAND_CAPACITY) {
      hand.add(cards.get(0));
      cards.remove(0);
    }
  }

  public final int getGoodFreq(final Integer goodId) {
    int freq = 0;
    for (Integer good : stand) {
      if (goodId.equals(good)) {
        freq++;
      }
    }
    return freq;
  }

  public void inspect(final ArrayList<BasePlayer> players, final ArrayList<Integer> cards) {
    for (BasePlayer player : players) {
      if (player != this) {
        this.inspect(player, cards);
      }
    }
  }

  public void inspect(final BasePlayer other, final ArrayList<Integer> cards) {
    other.coins += other.bribe;
    other.bribe = 0;
    if (coins < Constants.MIN_INSPECT_COINS) {
      other.moveToStand();
      return;
    }
    boolean honest = true;
    ArrayList<Integer> toDelete = new ArrayList<Integer>();
    for (Integer good : other.bag) {
      if (!good.equals(other.declaration)) {
        honest = false;
        this.coins += GoodsFactory.getInstance().getGoodsById(good).getPenalty();
        other.coins -= GoodsFactory.getInstance().getGoodsById(good).getPenalty();
        toDelete.add(good);
        cards.add(good);
      }
    }

    for (Integer good : toDelete) {
      other.bag.remove(good);
    }

    if (honest) {
      this.coins -=
          other.bag.size() * GoodsFactory.getInstance().getGoodsById(declaration).getPenalty();
      other.coins +=
          other.bag.size() * GoodsFactory.getInstance().getGoodsById(declaration).getPenalty();
    }

    other.moveToStand();
//    bag.clear();
  }

  public final void moveToStand() {
    for (Integer good : bag) {
      stand.add(good);
    }
    bag.clear();
    hand.clear();
  }

  public final void computeScore() {
    ArrayList<Integer> bonusBag = new ArrayList<Integer>();

    for (Integer good : stand) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType().equals(GoodsType.Illegal)) {
        Map<Goods, Integer> bonuses =
            ((IllegalGoods) GoodsFactory.getInstance().getGoodsById(good)).getIllegalBonus();
        Set<Map.Entry<Goods, Integer>> st = bonuses.entrySet();
        for (Map.Entry<Goods, Integer> bonus : st) {
          for (int i = 0; i < bonus.getValue(); ++i) {
            bonusBag.add(bonus.getKey().getId());
          }
        }
      }
    }

    for (Integer good : bonusBag) {
      stand.add(good);
    }

    int goodsValue = 0;
    for (Integer good : this.stand) {
      goodsValue += GoodsFactory.getInstance().getGoodsById(good).getProfit();
    }
        score = coins + goodsValue;
//    score = coins;
  }

  public final void addCoins(final int value) {
    score += value;
  }

  public void printStand() {
//    this.print();
    System.out.println(id + " " + coins);
//    System.out.println(stand);
  }
}
