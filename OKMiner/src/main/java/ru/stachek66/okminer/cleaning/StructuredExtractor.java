package ru.stachek66.okminer.cleaning;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author alexeyev
 */
public class StructuredExtractor {

    private final Document doc;
    private final int TIMEOUT = 1000 * 3;

    public StructuredExtractor(final URL url) throws IOException {
        this.doc = Jsoup.parse(url, TIMEOUT);
//        System.out.println(doc);
    }

    public List<String> getLinksFromPosts() {
        final LinkedList<String> links = new LinkedList<String>();
        for (Element atag : doc.select("#middleLeftColumn").select("a")) {
            final String href = atag.attr("href");
            if (href.contains("http")) {
                links.add(
                        href.
                                replaceAll(".*http", "http").
                                replaceAll("%3A", ":").
                                replaceAll("%2F", "/").
                                replaceAll("%26", "&").
                                replaceAll("%3F", "?").
                                replaceAll("%3D", "=")
                        //todo: mind other codes
                );
            }
        }
        return links;
    }

    public String getConcatenatedPosts() {
        //mainContentContentColumn
        String text = doc.select("#middleLeftColumn").text();
        return text.replaceAll("\n", " ");
    }

    public String getDescription() {
        String text = "";
        for (Element meta : doc.select("meta")) {
            for (Element description : meta.getElementsByAttributeValue("name", "description")) {
                text += description.attr("content") + " ";
            }
        }
        return text.replaceAll("\n", " ");
    }

}
