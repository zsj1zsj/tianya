package tianya;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class TianyaAPI {
    // tianya header:including title, author, date, word count
    static String SEL_TITLE = "td:eq(0)";
    static String SEL_AUTHOR = "td:eq(1)";
    static String SEL_CLICK = "td:eq(2)";
    static String SEL_REPLY = "td:eq(3)";
    static String SEL_ARTID = "td:eq(0) > a[href]";
    static String SEL_PAGENUM = "div.page li > a";
    static String SEL_ARTLISTPERPAGE = "table >tbody:gt(1) > tr";/*"table.listTable > tbody > tr";*/

    //https://tuoshuidu.com/c/tianya/%E7%85%AE%E9%85%92%E8%AE%BA%E5%8F%B2/
    static List<ArticleHeader> fetchPagelistByNo(String url) {
        Document doc = null;
        int tries = 3;

        while (--tries > 0) {
            try {
                doc = Jsoup.connect(url).get();
                break;
            } catch (IOException e) {
                System.out.println("tring " + tries);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        Elements newsHeadlines = doc.select(SEL_ARTLISTPERPAGE);
//        System.out.println(newsHeadlines.size());

        return newsHeadlines.stream().map(
                h -> new ArticleHeader(
                        h.select(SEL_TITLE).text(),
                        h.select(SEL_AUTHOR).text(),
                        Integer.parseInt(h.select(SEL_CLICK).text()),
                        Integer.parseInt(h.select(SEL_REPLY).text()),
                        h.select(SEL_ARTID).attr("href")
                )
        ).distinct().collect(toList());
    }


    // 默认10天
    static List<ArticleHeader> pageByPage(String url) {
        return pageByPage(url, 10);
    }

    // http://bbs.tianya.cn/list-no05-1.shtml
    static List<ArticleHeader> pageByPage(String url, int ndays) {
        long now = System.currentTimeMillis();
        long oneYearBefore = now - 3600 * 24 * ndays * 1000L;
        List<ArticleHeader> allArticles = new ArrayList<>();

        String SEL_NEXTID = "div#main div.links > a[href]";
        String baseURL = "http://bbs.tianya.cn";
        String nextPage = url;
        long nextid = now;

        Document doc = null;
        while (nextid > oneYearBefore) {
            try {
                doc = Jsoup.connect(nextPage).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<ArticleHeader> cur = fetchPagelistByNo(nextPage);
            allArticles.addAll(cur);
            String nextPageSuf = doc.select(SEL_NEXTID).last().attr("href");
            nextPage = baseURL + nextPageSuf;
            nextid = Long.parseLong(nextPageSuf.substring(nextPageSuf.lastIndexOf("=") + 1));
            System.out.println(nextid);
        }

        return allArticles;
    }

    // String url = "https://tuoshuidu.com/c/tianya/%E7%85%AE%E9%85%92%E8%AE%BA%E5%8F%B2/"
    // http://bbs.tianya.cn/list.jsp?item=no05&nextid=1637077557000
    static List<ArticleHeader> allPages(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Element pageNumEle = doc.select(SEL_PAGENUM).last();
//        System.out.println(pageNumEle.text());
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
    static String baseURL(String urlpath) throws MalformedURLException {
        URL url = new URL(urlpath);
//        String path = url.getFile().substring(0, url.getFile().lastIndexOf('/'));
        String base = url.getProtocol() + "://" + url.getHost();
        System.out.println(base);
        return base;
    }
}
