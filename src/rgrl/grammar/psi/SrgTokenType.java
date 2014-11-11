package rgrl.grammar.psi;

import com.intellij.psi.tree.IElementType;
import rgrl.SrgLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class SrgTokenType extends IElementType {
    public SrgTokenType(@NotNull @NonNls String debugName) {
        super(debugName, SrgLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "SrgTokenType." + super.toString();
    }
}