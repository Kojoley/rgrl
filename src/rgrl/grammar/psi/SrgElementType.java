package rgrl.grammar.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import rgrl.SrgLanguage;

public class SrgElementType extends IElementType {
    public SrgElementType(@NotNull @NonNls String debugName) {
        super(debugName, SrgLanguage.INSTANCE);
    }
}