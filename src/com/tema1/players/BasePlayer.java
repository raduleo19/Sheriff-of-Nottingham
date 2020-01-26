package com.players;

import com.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import com.tema1.goods.IllegalGoods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.min;

public class BasePlayer implements Comparable<BasePlayer> {
  protected int id;
  protected int coins;
  protected int declaration;
  protected int bribe;
  protected ArrayList<Integer> hand;
  protected ArrayList<Integer> bag;
  protected ArrayList<Integer> standFreq;

  public BasePlayer(final int id) {
    this.id = id;
    this.coins = Constants.INIT_COINS;
    this.declaration = 0;
    this.bribe = 0;
    this.hand = new ArrayList<>();
    this.bag = new ArrayList<>();
    this.standFreq = new ArrayList<>(Collections.nCopies(Constants.MAX_GOODS_ID + 1, 0));
  }

  /**
   * @return Returns a string containing player Id, his strategy and his coins.
   */
  public String toString() {
    return id + " " + Constants.BASE.toUpperCase() + " " + this.coins;
  }

  /**
   * Selects goods according to strategy and then moves them in the bag.
   */
  public void createBag(final int round) {

    ArrayList<Integer> frequency =
        new ArrayList<Integer>(Collections.nCopies(Constants.MAX_GOODS_ID + 1, 0));

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
      int freq = min(frequency.get(mostFreq), Constants.BAG_CAPACITY);
      for (int i = 0; i < freq; ++i) {
        bag.add(mostFreq);
        hand.remove(mostFreq);
      }
      declaration = mostFreq;
    } else if (coins >= Constants.ILLEGAL_PENALTY) {
      Integer maxIllegal = getMaxIllegal();
      bag.add(maxIllegal);
      hand.remove(maxIllegal);
      declaration = Constants.APPLE_ID;
    }
  }

  /**
   * Inspects other players according to strategy.
   */
  public void inspect(final ArrayList<BasePlayer> players, final List<Integer> goods) {
    for (BasePlayer player : players) {
      if (player != this) {
        this.inspect(player, goods);
      }
    }
  }

  /**
   * Comparator for sorting by coins.
   * @param other
   * @return Returns positive value if other player is better and negative otherwise.
   */
  public final int compareTo(final BasePlayer other) {
    return other.coins - this.coins;
  }

  /**
   * Draw 10 cards from the deck.
   * @param goods List of goods' ids.
   */
  public final void drawCards(final List<Integer> goods) {
    while (hand.size() < Constants.HAND_CAPACITY) {
      hand.add(goods.get(0));
      goods.remove(0);
    }
  }

  /**
   * Discards unused cards from hand.
   */
  public final void burnCards() {
    hand.clear();
  }

  /**
   * @param good Takes id of a good
   * @return Returns number of occurences of good in the player's stand;
   */
  public final int getGoodFreq(final Integer good) {
    return standFreq.get(good);
  }

  /**
   * @return Returns most profitable illegal good.
   */
  protected final Integer getMaxIllegal() {
    Integer best = null;
    for (Integer good : hand) {
      if (GoodsFactory.getInstance().getGoodsById(good).getType().equals(GoodsType.Illegal)) {
        if (best == null) {
          best = good;
        } else if (GoodsFactory.getInstance().getGoodsById(good).getProfit()
            > GoodsFactory.getInstance().getGoodsById(best).getProfit()) {
          best = good;
        }
      }
    }
    return best;
  }

  /**
   * Inspects other player, and confiscate illegale ones.
   * @param other Takes other player.
   * @param goods Takes the deck.
   */
  public final void inspect(final BasePlayer other, final List<Integer> goods) {
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
        goods.add(good);
      }
    }

    for (Integer good : toDelete) {
      other.bag.remove(good);
    }

    if (honest) {
      int penalty = other.bag.size() * GoodsFactory.getInstance().getGoodsById(declaration).getPenalty();
      this.coins -= penalty;
      other.coins += penalty;
    }

    other.moveToStand();
  }

  /**
   * Moves the goods from the bag to the stand.
   */
  public final void moveToStand() {
    for (Integer good : bag) {
      standFreq.set(good, standFreq.get(good) + 1);
    }
    bag.clear();
  }

  /**
   * For each illegal good adds the bonus legal goods.
   */
  public final void rewardIllegalBonus() {
    for (Integer good = Constants.FIRST_ILLEGAL_ID; good <= Constants.MAX_GOODS_ID; ++good) {
      Set<Map.Entry<Goods, Integer>> bonuses =
          ((IllegalGoods) GoodsFactory.getInstance().getGoodsById(good))
              .getIllegalBonus()
              .entrySet();
      for (Map.Entry<Goods, Integer> bonus : bonuses) {
        Integer bonusId = bonus.getKey().getId();
        standFreq.set(bonusId, standFreq.get(bonusId) + bonus.getValue() * standFreq.get(good));
      }
    }
  }

  /**
   *  Adds the profit of each good to player's coins.
   */
  public final void calculateScore() {
    for (Integer good = 0; good <= Constants.MAX_GOODS_ID; ++good) {
      if (GoodsFactory.getInstance().getGoodsById(good) != null) {
        coins += GoodsFactory.getInstance().getGoodsById(good).getProfit() * standFreq.get(good);
      }
    }
  }

  public final void addCoins(final int value) {
    coins += value;
  }
}
