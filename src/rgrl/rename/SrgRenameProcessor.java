package rgrl.rename;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.codehaus.groovy.runtime.metaclass.ConcurrentReaderHashMap;
import org.jetbrains.annotations.NotNull;
import rgrl.RgException;
import rgrl.RgTools;
import rgrl.grammar.psi.*;

import java.util.*;

import static rgrl.RgTools.toSignatureString;

public final class SrgRenameProcessor extends RenamePsiElementProcessor {
    private static final Logger LOG = Logger.getInstance("#" + SrgRenameProcessor.class.getName());
    private static final Key<String> SIGNATURE = Key.create("#" + SrgRenameProcessor.class.getName() + ".SIGNATURE");

    @Override
    public boolean canProcessElement(@NotNull final PsiElement element) {
        return element instanceof PsiMethod
            || element instanceof PsiField
            || element instanceof PsiClass
            || element instanceof PsiPackage
            || element instanceof SrgPsiReferenceElement;
        //return true;
    }

    @Override
    public void prepareRenaming(final PsiElement element, final String newName,
                                final Map<PsiElement, String> allRenames) {
        try {
            LOG.warn("RGRL: prepareRenaming() " + allRenames.size() + "# newName: " + newName + " element: " + element.toString() + " signature: " + toSignatureString(element));
            for (final Map.Entry<PsiElement, String> entry : allRenames.entrySet()) {
                String signature = null;
                if (element instanceof PsiMethod || element instanceof PsiField || element instanceof PsiClass || element instanceof PsiPackage)
                    signature = toSignatureString(entry.getKey());
                LOG.warn("RGRL: prepareRenaming() * newName: " + entry.getValue() + " element: " + entry.getKey().toString() + " signature: " + signature);
            }
        }
        catch (RgException e) {
            allRenames.clear();
            LOG.error("RGRL: prepareRenaming() " + e.getMessage());
        }

        // FIXME: element instanceof PsiMethod: rename can be of base method or only current
        try {
            Map<PsiElement, String> srgRenames = new HashMap<PsiElement, String>();
            for (final Map.Entry<PsiElement, String> entry : allRenames.entrySet()) {
                final PsiElement renameElement = entry.getKey();
                final String renameName = entry.getValue();

                /*if (!element.isEquivalentTo(renameElement) && element instanceof PsiMethod && renameElement instanceof PsiMethod) {
                    PsiMethod renameMethod = (PsiMethod) renameElement;
                    PsiMethod method = (PsiMethod) element;


                    renameMethod.isConstructor();
                }*/

                final List<SrgPsiReferenceElement> remaps = findReferenceRemaps(renameElement);

                if (remaps.size() > 0)
                    for (final SrgPsiReferenceElement ref: remaps)
                        srgRenames.put(ref, renameName);
                else if (newName.equals(renameName) && !element.isEquivalentTo(renameElement) &&
                        element instanceof PsiClass && renameElement instanceof PsiMethod &&
                        isConstructorOfClass((PsiMethod) renameElement, (PsiClass) element)) {
                    LOG.warn("RGRL: skipped constructor with same name: " + toSignatureString(renameElement));
                    //LOG.warn("RGRL: skipped constructor: " + ((PsiMethod) renameElement).toString());
                }
                else
                    throw new RgException("createRemap() Is not currently implemented");
            }
            allRenames.putAll(srgRenames);

//            List<SrgPsiReferenceElement> records = findReferenceRemaps(element);
//
//            if (records.size() > 0) {
//                for (SrgPsiReferenceElement key : records) {
//                    allRenames.put(key, newName);
//                }
//            }
//            else {
//                LOG.error("RGRL: createRemap() Is not implemented");
//                allRenames.clear();
//                /*
//                StringBuilder sb = new StringBuilder();
//
//                // TODO: I don't like this if statement
//                     if (element instanceof PsiMethod)  sb.append("MD: ");
//                else if (element instanceof PsiField)   sb.append("FD: ");
//                else if (element instanceof PsiClass)   sb.append("CL: ");
//                else if (element instanceof PsiPackage) sb.append("PK: ");
//                else throw new RgException("Unknown PsiElement: " + element.toString());
//
//                String signature = toSignatureString(element);
//                sb.append(signature).append(" ").append(signature);
//
//                LOG.warn("RGRL: create new element from text: '" + sb.toString() + "'");
//                SrgPsiRecord key = SrgPsiElementFactoryImpl.createRecordElementFromText(element.getProject(), sb.toString());
//                allRenames.put(key.getRenameTo()..getReferenceElement(), newName);
//                */
//            }
        }
        catch (RgException e) {
            LOG.error("RGRL: " + e.getMessage());
            allRenames.clear();
        }
    }

    private boolean isConstructorOfClass(final PsiMethod method, final PsiClass clazz) {
        return method.isConstructor() && Arrays.asList(clazz.getConstructors()).contains(method);
    }

    @NotNull
    private List<SrgPsiReferenceElement> findReferenceRemaps(final PsiElement element) throws RgException {
        List<SrgPsiReferenceElement> remaps = new ArrayList <SrgPsiReferenceElement>();

        PsiFile file = RgTools.getRootDirectory(element.getProject()).findFile(RgTools.RENAME_FILE_NAME);
        if (file == null)
            return remaps;

        //findReferenceRemaps3(element, file, remaps);
             if (element instanceof PsiMethod)  findReferenceRemaps3((PsiMethod)  element, file, remaps);
        else if (element instanceof PsiField)   findReferenceRemaps3((PsiField)   element, file, remaps);
        else if (element instanceof PsiClass)   findReferenceRemaps3((PsiClass)   element, file, remaps);
        else if (element instanceof PsiPackage) findReferenceRemaps3((PsiPackage) element, file, remaps);
        else throw new RgException("Unknown PsiElement: " + element.toString());

        return remaps;
    }

    private void findReferenceRemaps3(final PsiPackage element, final PsiFile file, final  List<SrgPsiReferenceElement> remaps) throws RgException {
        final String error = "RGRL: can't handle " + element + " renaming right now";
        LOG.error(error);
        throw new RgException(error);
        /*String signature = toSignatureString(element);
        for (PsiElement record : file.getChildren()) {
            // Fast precheck
            final String text = record.getLastChild().getText();
            if (text.startsWith(signature) && (text.length() == signature.length() || text.charAt(text.length()) == '/')) {
                // TODO: rename
            }
        }*/
    }

    private void findReferenceRemaps3(final PsiClass element, final PsiFile file, final  List<SrgPsiReferenceElement> remaps) throws RgException {
        String signature = toSignatureString(element);
        for (PsiElement record : file.getChildren()) {
            if (record instanceof SrgPsiRecordClass) {
                SrgPsiReferenceElement refel = ((SrgPsiRecordClass) record).getRenameTo().getReferenceElement();
                if (refel.getText().equals(signature))
                    remaps.add(refel);
            }
            else if (record instanceof SrgPsiRecordField) {
                //TODO: rename class of method
                //SrgPsiReferenceElement refel = ((SrgPsiRecordField) record).getRenameTo().getClassReferenceElement();
                SrgPsiReferenceElement refel = (SrgPsiReferenceElement) ((SrgPsiRecordField) record).getRenameTo().getReferenceElement().getFirstChild();
                if (refel.getText().equals(signature))
                    remaps.add(refel);
            }
            else if (record instanceof SrgPsiRecordMethod) {
                SrgPsiMethodSpec spec = ((SrgPsiRecordMethod) record).getRenameTo();
                //TODO: rename class of method
                //SrgPsiReferenceElement refel = spec.getMethodName().getClassReferenceElement();
                SrgPsiReferenceElement refel = (SrgPsiReferenceElement) spec.getMethodName().getReferenceElement().getFirstChild();
                if (refel.getText().equals(signature))
                    remaps.add(refel);

                List<SrgPsiJavaType> types = spec.getMethodDescriptor().getJavaTypeList();
                for (SrgPsiJavaType type : types) {
                    // Get component type first
                    // TODO: replace with call to getDeepComponentType()
                    while (type instanceof SrgPsiJavaArrayType)
                        type = ((SrgPsiJavaArrayType) type).getComponentType();
                    //if (type instanceof SrgPsiJavaArrayType)
                    //    type = type.getDeepComponentType();

                    // If class type, rename it
                    if (type instanceof SrgPsiJavaClassType) {
                        SrgPsiClassName cls = ((SrgPsiJavaClassType) type).getClassName();
                        SrgPsiReferenceElement name = cls.getReferenceElement();
                        if (name.getText().equals(signature)) {
                            remaps.add(name);
                        }
                    }
                }
            }
        }
    }

    private void findReferenceRemaps3(final PsiMethod element, final PsiFile file, final  List<SrgPsiReferenceElement> remaps) throws RgException {
        String signature = toSignatureString(element);
        Collection<SrgPsiRecordMethod> records = PsiTreeUtil.findChildrenOfType(file, SrgPsiRecordMethod.class);
        for (SrgPsiRecordMethod record : records) {
            SrgPsiMethodSpec spec = record.getRenameTo();
            if (spec.getText().equals(signature)) // TODO: replace getText!!!
                remaps.add(spec.getMethodName().getReferenceElement());
        }
    }

    private void findReferenceRemaps3(final PsiField element, final PsiFile file, final  List<SrgPsiReferenceElement> remaps) throws RgException {
        String signature = toSignatureString(element);
        Collection<SrgPsiRecordField> records = PsiTreeUtil.findChildrenOfType(file, SrgPsiRecordField.class);
        for (SrgPsiRecordField record : records) {
            SrgPsiFieldName name = record.getRenameTo();
            if (name.getText().equals(signature)) // TODO: replace getText
                remaps.add(name.getReferenceElement());
        }
    }

    @Override
    public void renameElement(final PsiElement element, String newName, UsageInfo[] usages,
                              RefactoringElementListener listener) throws IncorrectOperationException {
        //LOG.warn("RGRL: renameElement() new: " + newName + " for: " + element.getUserData(SIGNATURE));
        LOG.warn("RGRL: renameElement() new: " + newName + " for: " + element.toString() + " isPhysical=" + element.getContainingFile().isPhysical() + " @ " + element.getText());
        if (!(element instanceof SrgPsiReferenceElement)) {
            final String error = "RGRL: not in my competence to rename the " + element.toString();
            LOG.error(error);
            throw new IncorrectOperationException(error);
        }

        ((SrgPsiReferenceElement) element).setName(newName);

        // Check if actually record is not in the file and we should append it
        if (!element.getContainingFile().isPhysical()) {
            LOG.warn("RGRL: renameElement() inject element into renamemap");

            // Get record of reference element
            PsiElement record = element;
            while (!(record instanceof SrgPsiRecord))
                record = record.getParent();

            //PsiElement record = PsiTreeUtil.getTopmostParentOfType(element, SrgPsiRecord.class);

            try {
                PsiDirectory directory = RgTools.getRootDirectory(element.getProject());
                PsiFile file = directory.findFile(RgTools.RENAME_FILE_NAME);
                if (file == null) {
                    LOG.error("RGRL: renameElement() renamemap file is not exists");
                    throw new IncorrectOperationException("Is not implemented yet");
                    //directory.add();
                }
                file.add(element);
            } catch (RgException e) {
                throw new IncorrectOperationException(e.getMessage());
            }
        }
    }
    /*
    @Override
    public void substituteElementToRename(@NotNull final PsiElement element, @NotNull Editor editor, @NotNull Pass<PsiElement> renameCallback) {
        LOG.warn("RGRL: substituteElementToRename() new: " + element.toString() + " @ " + element.getText());
    }

    @Override
    @Nullable
    public Runnable getPostRenameCallback(final PsiElement element, final String newName, final RefactoringElementListener elementListener) {
        //LOG.warn("RGRL: getPostRenameCallback() new: " + newName + " for: " + element.getUserData(SIGNATURE));
        LOG.warn("RGRL: getPostRenameCallback() new: " + newName + " for: " + element.toString() + " @ " + element.getText() );
        return new Runnable() {
            @Override
            public void run() {
                //LOG.warn("RGRL: postRenameCallback() new: " + newName + " for: " + element.getUserData(SIGNATURE));
                LOG.warn("RGRL: postRenameCallback() new: " + newName + " for: " + element.toString() + " @ " + element.getText() );

                // 0. check if
                // 1. put to user data signature BEFORE rename - in prepareRename()
                // 2. take signature from user data HERE
                // 3. generate signature of current state
                // 4. join two signatures and append to renamemap file
            }
        };
    }
    */
}
