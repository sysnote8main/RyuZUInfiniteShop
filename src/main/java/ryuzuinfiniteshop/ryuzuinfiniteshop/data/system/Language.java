package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Value;

@Value
public class Language {
    String text;

    public String getText(String... valiables) {
        return text;
    }
}
