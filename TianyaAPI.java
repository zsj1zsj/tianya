package tianya;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class TianyaAPI {
    //https://tuoshuidu.com/c/tianya/%E7%85%AE%E9%85%92%E8%AE%BA%E5%8F%B2/
    static List<ArticleHeader> fetchPagelistByNo(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements newsHeadlines = doc.select("table.listTable > tbody > tr");
//        System.out.println(newsHeadlines.size());

        return newsHeadlines.stream().skip(1).map(
                h -> new ArticleHeader(
                        h.select("td:eq(1) > a").text(),
                        h.select("td:eq(2)").text(),
                        h.select("td:eq(3)").text(),
                        Integer.parseInt(h.select("td:eq(4)").text())
                )
        ).collect(toList());
    }

    //String url = "https://tuoshuidu.com/c/tianya/%E7%85%AE%E9%85%92%E8%AE%BA%E5%8F%B2/"
    static List<ArticleHeader> allPages(String url ) throws IOException {
        ;
        Document doc = Jsoup.connect(url).get();

        Element pageNumEle = doc.select("div.page li > a").last();
        System.out.println(pageNumEle.text());
        int pageNum = Integer.parseInt(pageNumEle.text());

        List<String> pageUrls = IntStream.range(1, pageNum + 1).boxed().map(i -> url + i + ".html").collect(toList());

        pageUrls.stream().limit(pageNum).map(TianyaAPI::fetchPagelistByNo)
                .flatMap(x -> x.stream())
                .sorted(comparing(ArticleHeader::getRating).reversed())
                .limit(3)
                .forEach(System.out::println);

        return null;
    }
}
