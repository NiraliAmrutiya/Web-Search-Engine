package ca.uwindsor.searchengine.util;

import ca.uwindsor.searchengine.util.entity.Ranking;

import java.util.Comparator;

// RankingComparator is Comparator class which can pass to Priority queue initialisation time
// This comparator make sure that all the items in priority queue stored in descending
// order of their ranking
public class RankingComparator implements Comparator<Ranking> {
    public int compare(Ranking rank1, Ranking rank2) {
        if (rank1.getRanking() < rank2.getRanking())
            return 1;
        else if (rank1.getRanking() > rank2.getRanking())
            return -1;
        return 0;
    }
}