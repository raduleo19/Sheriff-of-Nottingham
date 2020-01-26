package com.tema1.main;

import java.util.ArrayList;
import java.util.List;

import fileio.implementations.FileReader;

public final class GameInputLoader {
    private final String mInputPath;

    GameInputLoader(final String inputPath) {
        mInputPath = inputPath;
    }

    public GameInput load() {
        List<Integer> assetsIds = new ArrayList<Integer>();
        List<String> playerOrder = new ArrayList<String>();
        int rounds = 0;
        int noPlayers = 0;
        int noGoods = 0;

        try {
            FileReader fs = new FileReader(mInputPath);

            rounds = fs.nextInt();
            noPlayers = fs.nextInt();

            for (int i = 0; i < noPlayers; ++i) {
                playerOrder.add(fs.nextWord());
            }

            noGoods = fs.nextInt();

            for (int i = 0; i < noGoods; ++i) {
                assetsIds.add(fs.nextInt());
            }

            fs.close();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return new GameInput(rounds, assetsIds, playerOrder);
    }
}
