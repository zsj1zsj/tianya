package tianya;

// replace #{val} with real value;
public class StringPlaceholder {
    String str;

    public StringPlaceholder(String str) {
        this.str = str;
    }

    StringPlaceholder arg(String a, String val) {
        str = str.replaceAll(String.format("#\\{\\s*%s\\s*\\}", a), val);
        return this;
    }

    String build() {
        return this.str;
    }

    public static void main(String[] args) {
        StringPlaceholder sph = new StringPlaceholder("hello world,#{name}!");
        System.out.println(sph.arg("name", "lynn").build());
    }
}
