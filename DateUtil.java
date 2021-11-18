package tianya;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {
    static Date today() {
        return Date.from(Instant.now());
    }

    static long betweenDays(LocalDate d1, LocalDate d2) {
        return ChronoUnit.DAYS.between(d1, d2);
    }

    static long betweenWeeks(LocalDate d1, LocalDate d2) {
        return ChronoUnit.WEEKS.between(d1, d2);
    }

    public static void main(String[] args) {
        int startNo = 9226;
        LocalDate startDate = LocalDate.of(2021, 1, 2);

        String mp3 = "http://audiocdn.economist.com/sites/default/files/AudioArchive/2021/%s/Issue_%s_%s_The_Economist_Full_edition.zip";
        String mobi = "https://raw.githubusercontent.com/hehonghui/the-economist-ebooks/master/01_economist/te_%s/TheEconomist.%s.mobi";

        LocalDate today = LocalDate.now();
        LocalDate lastestDate = today.minusDays(betweenDays(startDate, today) % 7);
        String lastestDateMp3 = lastestDate.format(DateTimeFormatter.ofPattern("YYYYMMDD"));
        System.out.format(mp3, lastestDateMp3, betweenWeeks(startDate, today) + startNo, lastestDateMp3);
        String latestDateMobi = lastestDate.format(DateTimeFormatter.ofPattern("YYYY.MM.DD"));
        System.out.println();
        System.out.format(mobi, latestDateMobi, latestDateMobi);
    }
}
