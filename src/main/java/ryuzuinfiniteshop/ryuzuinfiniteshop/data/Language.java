package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import lombok.Value;

@Value
public class Language {
    String text;

    public String getText(String... valiables) {
        return text;
    }
}
