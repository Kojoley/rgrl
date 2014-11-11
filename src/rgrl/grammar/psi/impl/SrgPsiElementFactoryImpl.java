package rgrl.grammar.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import rgrl.SrgFileType;
import rgrl.grammar.psi.SrgPsiRecord;
import rgrl.grammar.psi.SrgPsiRecordPackage;
import rgrl.grammar.psi.SrgPsiReferenceElement;
import rgrl.grammar.psi.SrgFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SrgPsiElementFactoryImpl /*implements SrgPsiElementFactory*/ {

    @NotNull
    //@Override
    public static SrgPsiReferenceElement createReferenceElement(final Project project, @NotNull String text) throws IncorrectOperationException {
        //SrgTypes.RECORD_TYPE_PACKAGE.toString() != "PK:"
        return (SrgPsiReferenceElement) ((SrgPsiRecordPackage) createRecordElementFromText(project, "PK: . " + text))
                .getRenameTo()
                .getReferenceElement();
        //        .getLastChild()   // PsiSrgPackageName
        //        .getFirstChild(); // PsiSrgReferenceElement
    }

    @NotNull
    //@Override
    public static SrgPsiRecord createRecordElementFromText(final Project project, @NotNull CharSequence text) {
        return (SrgPsiRecord) createFileFromText(project, "dummy", text).getFirstChild();
    }

    @NotNull
    //@Override
    public static SrgFile createFileFromText(final Project project, @NonNls @NotNull String name, @NotNull CharSequence text) {
        if (name.indexOf('.') == -1)
            name += "." + SrgFileType.INSTANCE.getDefaultExtension();

        return (SrgFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, SrgFileType.INSTANCE, text);
    }
}
