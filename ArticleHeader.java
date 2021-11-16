package tianya;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleHeader {
    String title;
    String author;
    String date;
    int rating;
}
