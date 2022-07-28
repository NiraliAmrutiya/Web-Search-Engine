package ca.uwindsor.searchengine.util.entity;

// Ranking is entity class.
// We are not storing direct URL in in-memory database,
// but we are storing Ranking object in in-memory database
// This is simple class with two property URL and ranking
public class Ranking {
    private String url;
    private int ranking;

    public Ranking(String url, int ranking) {
        this.url = url;
        this.ranking = ranking;
    }

    public String getUrl() {
        return url;
    }

    public int getRanking() {
        return ranking;
    }
}
