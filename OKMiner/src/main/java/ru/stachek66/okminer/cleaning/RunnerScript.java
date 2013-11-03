package ru.stachek66.okminer.cleaning;

import java.io.IOException;
import java.net.URL;

/**
 * @author alexeyev
 */
public class RunnerScript {

    private static void p(Object o) {
        System.out.println(o + "\n");
    }

    public static void main(String[] args) {
        String[] links = {
                "http://odnoklassniki.ru/poln.hohot",
                "http://odnoklassniki.ru/group/44732901687435",
                "http://odnoklassniki.ru/beeline.kazakhstan",
                "http://odnoklassniki.ru/academy",
                "http://odnoklassniki.ru/samsung",
                "http://odnoklassniki.ru/ochenpros",
                "http://odnoklassniki.ru/fczenit"
        };

        for (String link : links) {
            try {
                final StructuredExtractor se = new StructuredExtractor(new URL(link));
                p(se.getLinksFromPosts());
            } catch (IOException e) {
                p("problem");
                e.printStackTrace();
            }
        }
    }

}
