package rgrl;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SrgFileType extends LanguageFileType {
    public static final SrgFileType INSTANCE = new SrgFileType();

    private SrgFileType() {
        super(SrgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Srg file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Srg language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "srg";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SrgIcons.FILE;
    }
}