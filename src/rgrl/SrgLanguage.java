package rgrl;

import com.intellij.lang.Language;

public class SrgLanguage extends Language {
    public static final SrgLanguage INSTANCE = new SrgLanguage();

    private SrgLanguage() {
        super("Srg");
    }
}
