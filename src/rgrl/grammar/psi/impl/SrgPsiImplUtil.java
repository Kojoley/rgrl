package rgrl.grammar.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import rgrl.SrgIcons;
import rgrl.grammar.psi.SrgPsiReferenceElement;
import rgrl.grammar.psi.SrgTypes;

import javax.swing.*;

class SrgPsiImplUtil {

    public static SrgPsiReferenceElement getDeepest(SrgPsiReferenceElement element) {
        //PsiElement child = PsiTreeUtil.getDeepestFirst(element);
        //return (SrgPsiReferenceElement) (child instanceof SrgPsiReferenceElement ? child : child.getParent());

        for (PsiElement child = element; child != null && child instanceof SrgPsiReferenceElement; child = element.getFirstChild())
            element = (SrgPsiReferenceElement) child;
        return element;

        //PsiElement child = element.getFirstChild();
        //if (child instanceof SrgPsiReferenceElement)
        //    element = (SrgPsiReferenceElement) child;
    }

    @Nullable @NonNls
    public static String getName(SrgPsiReferenceElement element) {
        return getNameIdentifier(element).getText();
    }


    public static PsiElement setName(SrgPsiReferenceElement element, String newName) throws IncorrectOperationException {
        ASTNode keyNode = getNameIdentifier(element).getNode();
        if (keyNode != null) {

            SrgPsiReferenceElement refel = SrgPsiElementFactoryImpl.createReferenceElement(element.getProject(), newName);
            ASTNode newKeyNode = refel.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(SrgPsiReferenceElement element) {
        //ASTNode keyNode = element.getNode().findChildByType(SrgTypes.IDENTIFIER);
        ASTNode keyNode = element.getNode().findChildByType(TokenSet.create(SrgTypes.IDENTIFIER, SrgTypes.DOT));

        return keyNode != null ? keyNode.getPsi() : null;
    }

    public static String toString(SrgPsiReferenceElement element) {
        return "SrgPsiReferenceElement: " + element.getText();
    }

    public static ItemPresentation getPresentation(final SrgPsiReferenceElement element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return SrgIcons.FILE;
            }
        };
    }
}
