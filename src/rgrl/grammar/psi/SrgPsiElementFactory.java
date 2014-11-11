package rgrl.grammar.psi;

import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SrgPsiElementFactory {
    @NotNull
    SrgPsiReferenceElement createReferenceElement(@NotNull String text) throws IncorrectOperationException;
}