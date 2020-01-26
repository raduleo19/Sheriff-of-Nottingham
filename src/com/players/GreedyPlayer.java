package com.tema1.players;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.ArrayList;

public final class GreedyPlayer extends BasePlayer {
    public GreedyPlayer(final int id) {
        super(id);
    }

    @Override
    public void print() {
        System.out.println(this.id + " " + Constants.GREEDY_MESSAGE + " " + this.score);
    }

    @Override
    public void createBag(final int round) {
        super.createBag(round);
        if (round % 2 == 0) {
            if (bag.size() < Constants.BAG_CAPACITY) {
                Integer best = null;
                for (Integer good : hand) {
                    if (GoodsFactory
                            .getInstance()
                            .getGoodsById(good)
                            .getType().equals(GoodsType.Illegal)) {
                        if (best == null) {
                            best = good;
                        } else if (GoodsFactory.getInstance().getGoodsById(good).getProfit()
                                > GoodsFactory.getInstance().getGoodsById(best).getProfit()) {
                            best = good;
                        }
                    }
                }

                if (best != null) {
                    bag.add(best);
                    hand.remove(best);
                }
            }
        }
    }

    @Override
    public void inspect(final ArrayList<BasePlayer> players, final ArrayList<Integer> goods) {
        for (BasePlayer player : players) {
            if (player != this) {
                if (player.bribe > 0) {
                    coins += player.bribe;
                    player.bribe = 0;
                    player.moveToStand();
                } else {
                    this.inspect(player, goods);
                }
            }
        }
    }
}
