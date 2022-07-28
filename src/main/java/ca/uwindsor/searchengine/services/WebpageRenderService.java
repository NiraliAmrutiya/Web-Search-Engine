package ca.uwindsor.searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class WebpageRenderService {

    private final HttpClient client;
    public WebpageRenderService() {
        this.client = HttpClient.newBuilder().build();
    }

    public HashMap<String, String> parseWebpageContent(String url)
            throws Exception {

        HashMap<String, String> webpageContent = new HashMap<>();
        webpageContent.put("DOMAIN", getDomainName(url));

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> webPageContent = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (webPageContent.statusCode() != 200 ||
                webPageContent.body() == null) {
            throw new Exception("Invalid Url.");
        }

        String webpageContentBody = webPageContent.body();
        webpageContentBody = webpageContentBody.toLowerCase();
        webpageContent.put("TITLE", getTitle(webpageContentBody));
        webpageContent.put("KEYWORDS", getKeywords(webpageContentBody));
        webpageContent.put("HEADING", getHeadings(webpageContentBody));

        webpageContentBody = webpageContentBody.replaceAll("[^a-zA-Z0-9 ]", "");
        webpageContent.put("TEXT", Jsoup.parse(webpageContentBody).text());

        return webpageContent;
    }

    private String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        domain = domain.startsWith("www.") ? domain.substring(4) : domain;
        return domain.replaceAll("[^a-zA-Z0-9 ]", "");
    }

    private String getTitle(String pageContent) {
        Document document = Jsoup.parse(pageContent);
        Elements titleElements = document.getElementsByTag("title");
        if(titleElements == null || titleElements.first() == null) {
        	return "";
        }
        Element titleElement = titleElements.first();
        
        return titleElement.text().replaceAll("[^a-zA-Z0-9 ]", "");
    }

    private String getKeywords(String pageContent) {
        Document document = Jsoup.parse(pageContent);
        Elements metaElements = document.getElementsByTag("meta");
        for (Element element : metaElements) {
            if (element.hasAttr("name")) {
                String name = element.attr("name");
                if (name.equals("keywords")) {
                    return element.attr("content").replaceAll("[^a-zA-Z0-9]", " ");
                }
            }
        }
        return "";
    }

    private String getHeadings(String pageContent) {
        Document document = Jsoup.parse(pageContent);

        StringBuilder sb = new StringBuilder();

        Elements hTags = document.select("h1, h2, h3, h4, h5, h6");
        for (Element hTag : hTags) {
            sb.append(hTag.text().replaceAll("[^a-zA-Z0-9]", " "));
            sb.append(" ");
        }

        return sb.toString();
    }
}
