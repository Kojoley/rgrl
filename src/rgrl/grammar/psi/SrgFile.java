package rgrl.grammar.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import rgrl.SrgFileType;
import rgrl.SrgLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SrgFile extends PsiFileBase {
    public SrgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SrgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SrgFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Srg File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}