package tianya;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TianyaAPI {
    // tianya header:including title, author, date, word count
    static String SEL_TITLE = "td:eq(1) > a";
    static String SEL_AUTHOR = "td:eq(2)";
    static String SEL_DATE = "td:eq(3)";
    static String SEL_WORDCNT = "td:eq(4)";
    static String SEL_PAGENUM = "div.page li > a";
    static String SEL_ARTLISTPERPAGE = "table.listTable > tbody > tr";

    //https://tuoshuidu.com/c/tianya/%E7%85%AE%E9%85%92%E8%AE%BA%E5%8F%B2/
    static List<ArticleHeader> fetchPagelistByNo(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements newsHeadlines = doc.select(SEL_ARTLISTPERPAGE);
//        System.out.println(newsHeadlines.size());

        return newsHeadlines.stream().skip(1).map(
                h -> new ArticleHeader(
                        h.select(SEL_TITLE).text(),
                        h.select(SEL_AUTHOR).text(),
                        h.select(SEL_DATE).text(),
                        Integer.parseInt(h.select(SEL_WORDCNT).text())
                )
        ).collect(toList());
    }

    // String url = "https://tuoshuidu.com/c/tianya/%E7%85%AE%E9%85%92%E8%AE%BA%E5%8F%B2/"
    static List<ArticleHeader> allPages(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Element pageNumEle = doc.select(SEL_PAGENUM).last();
        System.out.println(pageNumEle.text());
        int pageNum = Integer.parseInt(pageNumEle.text());

        return IntStream.range(1, pageNum + 1).boxed()
                .map(i -> url + i + ".html")
                .parallel()
//                .limit(100)
                .map(TianyaAPI::fetchPagelistByNo)
                .flatMap(x -> x.stream())
//                .sorted(comparing(ArticleHeader::getWordCnt).reversed())
//                .limit(3)
                .collect(toList());
    }
    
    //String urlpath = "https://github.com/zsj1zsj/tianya/blob/main/TianyaAPI.java";
    String baseURL(String urlpath) throws MalformedURLException {
        URL url = new URL(urlpath);
        String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));
        String base = url.getProtocol() + "://" + url.getHost();
        System.out.println(base);
        return base;
    }
}
