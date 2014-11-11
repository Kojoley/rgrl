package rgrl.grammar.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import rgrl.grammar.psi.SrgNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class SrgNamedElementImpl extends ASTWrapperPsiElement implements SrgNamedElement {
    public SrgNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}