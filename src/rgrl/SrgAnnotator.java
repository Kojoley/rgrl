package rgrl;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import rgrl.grammar.psi.*;

import java.util.List;

public class SrgAnnotator implements Annotator {
    private static final Logger LOG = Logger.getInstance("#" + SrgAnnotator.class.getName());

    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof SrgPsiRecord) {
            //SrgPsiRecord record = (SrgPsiRecord) element;

            // Mark no rename like a comment
            if (element.getChildren().length == 2) {
                String s1 = element.getChildren()[0].getText();
                String s2 = element.getChildren()[1].getText();

                if (s1.equals(s2)) {
                    TextRange range = new TextRange(element.getTextRange().getStartOffset() + 4,
                                                    element.getTextRange().getEndOffset());
                    Annotation annotation = holder.createInfoAnnotation(range, null);
                    annotation.setTextAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT);
                }
                /*else {
                    String a1[] = s1.split(" ");
                    String a2[] = s2.split(" ");

                    s1 = a1[0].substring(a1[0].lastIndexOf('/') + 1);
                    s2 = a2[0].substring(a2[0].lastIndexOf('/') + 1);

                    if (s1.equals(s2)) {
                        TextRange range = new TextRange(element.getTextRange().getStartOffset() + 4,
                                                        element.getTextRange().getEndOffset());
                        Annotation annotation = holder.createInfoAnnotation(range, null);

                        if (a1.length == 1 && a2.length == 1 || (a1.length == 2 || a2.length == 2) && a1.length == a2.length && a1[1].equals(a2[1]))
                            annotation.setTextAttributes(DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
                        else
                            annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
                    }
                }*/

                final Project project = element.getProject();
                final JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);

                //String className = null;
                if (element instanceof SrgPsiRecordClass) {
                    final SrgPsiRecordClass record = (SrgPsiRecordClass) element;
                    final SrgPsiClassName srgClassName = record.getRenameTo();
                    if (srgClassName == null) {
                        //
                    }
                    else {
                        resolvePsiClass(srgClassName, psiFacade, holder);
                    }
                }
                else if (element instanceof SrgPsiRecordField) {
                    final SrgPsiRecordField record = (SrgPsiRecordField) element;
                    final SrgPsiFieldName srgPsiFieldName = record.getRenameTo();
                    if (srgPsiFieldName == null) {
                        //
                    }
                    else {
                        SrgPsiReferenceElement fieldRef = srgPsiFieldName.getReferenceElement();

                        final PsiClass cls = resolvePsiClass(fieldRef.getFirstChild(), psiFacade, holder);
                        if (cls != null) {
                            final String fieldName = fieldRef.getName();
                            final PsiField field = cls.findFieldByName(fieldName, false);
                            if (field == null) {
                                Annotation annotation = holder.createErrorAnnotation(fieldRef.getTextRange(), "Can't resolve field '" + fieldName + "'");
                                annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
                            }
                        }
                    }
                }
                else if (element instanceof SrgPsiRecordMethod) {
                    final SrgPsiRecordMethod record = (SrgPsiRecordMethod) element;
                    final SrgPsiMethodSpec srgPsiMethodSpec = record.getRenameTo();
                    if (srgPsiMethodSpec == null) {
                        //
                    }
                    else {
                        final SrgPsiReferenceElement fieldRef = srgPsiMethodSpec.getMethodName().getReferenceElement();

                        final PsiClass cls = resolvePsiClass(fieldRef.getFirstChild(), psiFacade, holder);
                        if (cls != null) {
                            String methodNames = fieldRef.getName();
                            int count = 0;
                            PsiMethod methods[] = cls.findMethodsByName(methodNames, false);
                            try {
                                for (PsiMethod method : methods)
                                    if (RgTools.toSignatureString(method).equals(srgPsiMethodSpec.getText()))
                                        count++;
                            } catch (RgException e) {
                                LOG.error(e);
                                Annotation annotation = holder.createErrorAnnotation(srgPsiMethodSpec.getTextRange(), e.getMessage());
                                annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
                            }

                            if (count == 0) {
                                Annotation annotation = holder.createErrorAnnotation(srgPsiMethodSpec.getTextRange(), "Can't resolve method '" + methodNames + "'");
                                annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
                            }
                            else if (count > 1) {
                                final String error = "Multiple (" + count + ") resolved methods '" + srgPsiMethodSpec.getText() + "'";
                                Annotation annotation = holder.createErrorAnnotation(srgPsiMethodSpec.getTextRange(), error);
                                annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
                                LOG.warn(error);
                            }
                        }

                        final List<SrgPsiJavaType> javaTypeList = srgPsiMethodSpec.getMethodDescriptor().getJavaTypeList();
                        for (SrgPsiJavaType javaType: javaTypeList) {
                            if (javaType instanceof SrgPsiJavaClassType) {
                                final SrgPsiClassName srgPsiClassName = ((SrgPsiJavaClassType) javaType).getClassName();
                                resolvePsiClass(srgPsiClassName, psiFacade, holder);
                            }
                        }
                    }
                }
            }
            else {
                Annotation annotation = holder.createErrorAnnotation(element.getTextRange(), "Uncompleted record");
                annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
            }
        }
    }

    static PsiClass resolvePsiClass(@NotNull final PsiElement classNameElement, @NotNull final JavaPsiFacade psiFacade, @NotNull final AnnotationHolder holder) {
        final String className = classNameElement.getText().replace('/', '.');
        final PsiClass cls = psiFacade.findClass(className, GlobalSearchScope.allScope(psiFacade.getProject()));

        if (cls == null) {
            Annotation annotation = holder.createErrorAnnotation(classNameElement.getTextRange(), "Can't resolve class '" + className + "'");
            annotation.setTextAttributes(DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);
        }

        return cls;
    }
}