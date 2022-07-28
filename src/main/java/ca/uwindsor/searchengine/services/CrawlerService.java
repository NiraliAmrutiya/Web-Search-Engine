package ca.uwindsor.searchengine.services;

import ca.uwindsor.searchengine.util.entity.Ranking;
import ca.uwindsor.searchengine.store.WebStore;

import java.util.*;

public class CrawlerService {
    private final WebStore store;
    private final WebpageRenderService webpageRenderService;

    public CrawlerService() {
        this.store = WebStore.getInstance();
        this.webpageRenderService = new WebpageRenderService();
    }

    // This method accepts user entered search string,
    // sanitize search string and
    // take tokens out of search string
    // get all the urls that contains the token
    // If one url matching multiple token of search string get the highest ranking
    public HashMap<String, Integer> search(String searchString) {

        // Sanitize search string
        searchString = searchString.replaceAll("[^a-zA-Z0-9]", " ");

        // Create hashmap that holds links that contains search strings
        HashMap<String, Integer> result = new HashMap<>();

        // Get all the tokens of search string
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < searchString.split(" ").length; i++) {
            tokens.addAll(slidingCombination(searchString, i + 1));
        }

        // Go through all the tokens one by one
        for (String token : tokens) {

            // Find if the token exist in store or not
            PriorityQueue<Ranking> queue = store.searchTokens(token.toLowerCase(Locale.ROOT));

            // If not token found in store, continue loop for the next token
            if (queue.isEmpty())
                continue;

            // Go through all the links which contains search string token
            for (Ranking ranking : queue) {
                // If the link already exist in result hashmap but ranking is lower
                // update ranking
                if (result.containsKey(ranking.getUrl())
                        && result.get(ranking.getUrl()) < ranking.getRanking()) {
                    result.put(ranking.getUrl(), ranking.getRanking());
                }

                // If hashmap does not contain that url,
                // simply add it to hash map with mentioned ranking
                if (!result.containsKey(ranking.getUrl())) {
                    result.put(ranking.getUrl(), ranking.getRanking());
                }
            }
        }

        return result;
    }

    // This method will call the passed URL and get HTML content,
    // extract Domain, Title, Keywords in meta tag, H1-H6 heading content and plain text of HTML
    // extracts token out of all type of content
    // and store it in database with relevant ranking
    public void parseWebpage(String url) throws Exception {

        // Try to register url. If Url already registered against database,
        // will return false and skip parsing part
        // If Url not registered, will return true means Url registered and
        // can be ready for parse
        if(!registerUrl(url))
            return;

        // parseWebpageContent will call the passed URL and get HTML content,
        // extract Domain, Title, Keywords in meta tag, H1-H6 heading content and plain text of HTML
        // and store it in HashMap
        HashMap<String, String> content = webpageRenderService.parseWebpageContent(url);

        // addUrl will sanitize URL, get tokens out of it and store it in database
        addUrl(url);

        // addDomain will sanitize domain name, get tokens out of it and store it in database
        if (content.containsKey("DOMAIN"))
            addDomain(content.get("DOMAIN"), url);

        // addTitle will sanitize page title, get tokens out of it and store it in database
        if (content.containsKey("TITLE"))
            addTitle(content.get("TITLE"), url);

        // addMeta will sanitize meta tag keywords, get tokens out of it and store it in database
        if (content.containsKey("KEYWORDS"))
            addMeta(content.get("KEYWORDS"), url);

        // addHeading will sanitize H1-H6 heading content, get tokens out of it and store it in database
        if (content.containsKey("HEADING"))
            addHeading(content.get("HEADING"), url);

        // addText will sanitize entire text of html content, get tokens out of it and store it in database
        if (content.containsKey("TEXT"))
            addText(content.get("TEXT"), url);
    }

    // registerUrl will check if url is already parsed or not.
    // If URL already parsed, return false
    // If URL not parsed, URL will add to UrlDatabase and mark as registered
    private boolean registerUrl(String url) {
        return store.addUrl(url);
    }

    // addDomain will sanitize Domain name,
    // get tokens out of it and
    // store it in database with the highest ranking 6
    private void addDomain(String content, String url) {
        StringTokenizer tokenizer = new StringTokenizer(content, ".");
        while (tokenizer.hasMoreTokens()) {
            store.addDomainTokens(tokenizer.nextToken().toLowerCase(Locale.ROOT),
                    url.toLowerCase(Locale.ROOT));
        }

        tokenizer = new StringTokenizer(content, "-");
        while (tokenizer.hasMoreTokens()) {
            store.addDomainTokens(tokenizer.nextToken().toLowerCase(Locale.ROOT),
                    url.toLowerCase(Locale.ROOT));
        }
    }


    // addUrl will sanitize url,
    // get tokens out of it and
    // store it in database with the ranking 5
    private void addUrl(String url) {
        String urlKeywords = url.replaceAll("[^a-zA-Z0-9]", " ");
        StringTokenizer tokenizer = new StringTokenizer(urlKeywords, " ");
        while (tokenizer.hasMoreTokens()) {
            store.addUrlTokens(tokenizer.nextToken().toLowerCase(Locale.ROOT),
                    url.toLowerCase(Locale.ROOT));
        }
    }

    // addTitle will sanitize content of title tag,
    // get tokens out of it and
    // store it in database with the highest ranking 4
    private void addTitle(String content, String url) {
        StringTokenizer tokenizer = new StringTokenizer(content, " ");
        while (tokenizer.hasMoreTokens()) {
            store.addTitleTokens(tokenizer.nextToken().toLowerCase(Locale.ROOT),
                    url.toLowerCase(Locale.ROOT));
        }
    }

    // addMeta will sanitize content of meta keyword tag,
    // get tokens out of it and
    // store it in database with the highest ranking 3
    public void addMeta(String content, String url) {
        StringTokenizer tokenizer = new StringTokenizer(content, " ");
        while (tokenizer.hasMoreTokens()) {
            store.addMetaTokens(tokenizer.nextToken().toLowerCase(Locale.ROOT),
                    url.toLowerCase(Locale.ROOT));
        }
    }

    // addHeading will sanitize content of all the heading tags (H1-H6),
    // get tokens out of it and
    // store it in database with the highest ranking 2
    public void addHeading(String content, String url) {
        StringTokenizer tokenizer = new StringTokenizer(content, " ");
        while (tokenizer.hasMoreTokens()) {
            store.addHeadingTokens(tokenizer.nextToken().toLowerCase(Locale.ROOT),
                    url.toLowerCase(Locale.ROOT));
        }
    }

    // addHeading will sanitize content text content,
    // get tokens out of it and
    // store it in database with the highest ranking 1 or 0
    // If token is combination of two or more words ranking will be 1 else 0
    public void addText(String content, String url) {
        int wordCount = content.split(" ").length;

        for (int i = 0; i < Math.min(5, wordCount); i++) {
            List<String> tokens = slidingCombination(content, i + 1);
            for (String token : tokens) {
                if (i == 0)
                    store.addWordTokens(token.toLowerCase(Locale.ROOT), url.toLowerCase(Locale.ROOT));
                else
                    store.addStringTokens(token.toLowerCase(Locale.ROOT), url.toLowerCase(Locale.ROOT));
            }
        }
    }

    // Sliding Combination is method that helps to get tokens out of string.
    // E.g. is content string is "Hi, How are you?" and sliding windows is 2
    // This method sanitise string to "Hi How are you"
    // Later take all the subsequent combination with sliding windows size
    // E.g. "Hi How are you" with windows size 1 will return "Hi", "How", "are"
    // E.g. "Hi How are you" with windows size 2 will return "Hi How", "How are", "are you"
    // E.g. "Hi How are you" with windows size 3 will return "Hi How are", "How are you"
    private List<String> slidingCombination(String content, int slidingWindowSize) {
        content = content.replaceAll("[^a-zA-Z0-9 ]", "");

        String[] words = content.split(" ");
        List<String> result = new ArrayList<>();
        for (int i = 0; i < (words.length - slidingWindowSize + 1); i++) {
            String token = String.join(" ", Arrays.copyOfRange(words, i, i + slidingWindowSize));
            result.add(token);
        }

        return result;
    }
}