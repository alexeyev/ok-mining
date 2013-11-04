package ru.stachek66.okminer.cleaning;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;

/**
 * @author alexeyev
 */
public class AllTextExtractor {

    private AllTextExtractor() {
    }

    public static String getText(URL url) throws IOException {
        System.out.println((url));
        return Jsoup.parse(url, 20 * 1000).text();
    }

}
