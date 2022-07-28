package ca.uwindsor.searchengine.store;

import ca.uwindsor.searchengine.util.entity.Ranking;
import ca.uwindsor.searchengine.util.RankingComparator;

import java.util.*;

public final class WebStore {

    private final HashMap<String, PriorityQueue<Ranking>> tokenDatabase;
    private final HashSet<String> urlDatabase;

    private static WebStore webStore;

    private WebStore() {
        tokenDatabase = new HashMap<>();
        urlDatabase = new HashSet<>();
    }

    public static WebStore getInstance(){
        if(webStore == null)
            webStore = new WebStore();

        return webStore;
    }

    public boolean addUrl(String url){
        if(urlDatabase.contains(url))
            return false;
        urlDatabase.add(url);
        return true;
    }

    public void addDomainTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 6));
        tokenDatabase.put(token, queue);
    }


    public void addUrlTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 5));
        tokenDatabase.put(token, queue);
    }


    public void addTitleTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 4));
        tokenDatabase.put(token, queue);
    }


    public void addMetaTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 3));
        tokenDatabase.put(token, queue);
    }


    public void addHeadingTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 2));
        tokenDatabase.put(token, queue);
    }

    public void addStringTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 1));
        tokenDatabase.put(token, queue);
    }

    public void addWordTokens(String token, String url){
        PriorityQueue<Ranking> queue = new PriorityQueue<>(new RankingComparator());

        if(tokenDatabase.containsKey(token)){
            queue = tokenDatabase.get(token);
        }

        queue.add(new Ranking(url, 0));
        tokenDatabase.put(token, queue);
    }


    public PriorityQueue<Ranking> searchTokens(String token){
        PriorityQueue<Ranking> queue = tokenDatabase.get(token);
        if(queue == null)
            return new PriorityQueue<>();

        return queue;
    }
}
