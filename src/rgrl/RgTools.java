package rgrl;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import rgrl.grammar.psi.SrgFile;
import rgrl.grammar.psi.impl.SrgPsiElementFactoryImpl;

import java.util.ArrayList;
import java.util.List;

public class RgTools {
    public final static String RENAME_FILE_NAME = "rename." + SrgFileType.INSTANCE.getDefaultExtension();

    public static PsiDirectory getRootDirectory(Project project) throws RgException {
        List<PsiDirectory> directories = new ArrayList<PsiDirectory>();
        final PsiManager psiManager = PsiManager.getInstance(project);
        for (final VirtualFile root : ProjectRootManager.getInstance(project).getContentRoots()) {
            final PsiDirectory directory = psiManager.findDirectory(root);
            if (directory == null) {
                continue;
            }
            directories.add(directory);
        }

        if (directories.size() != 1)
            throw new RgException("There are " + directories.size() + " root directories");

        return directories.get(0);
    }

    public static void putNewRecord(Project project, String text) throws RgException {
        PsiDirectory directory = getRootDirectory(project);

        PsiFile psiFile = directory.findFile(RENAME_FILE_NAME);
        if (psiFile != null) {
            psiFile.add(SrgPsiElementFactoryImpl.createRecordElementFromText(project, text));
        }
        else {
            psiFile = SrgPsiElementFactoryImpl.createFileFromText(project, RENAME_FILE_NAME, text);

            PsiElement addedElement = directory.add(psiFile);
            if (addedElement instanceof SrgFile) {
                psiFile = (SrgFile) addedElement;

                //return psiFile;
            }
            else {
                PsiFile containingFile = addedElement.getContainingFile();
                throw new IncorrectOperationException("Selected class psiFile name '" +
                        containingFile.getName() +  "' mapped to not java psiFile type '"+
                        containingFile.getFileType().getDescription() + "'");
            }
        }
    }

    private static void checkJavaIdentifier(String s) throws RgException {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0)))
            throw new RgException("Invalid character in identifier '" + s + "' at 0");

        for (int i = 1; i < s.length(); i++)
            if (!Character.isJavaIdentifierPart(s.charAt(i)))
                throw new RgException("Invalid character in identifier '" + s + "' at " + String.valueOf(i));
    }

    private static void checkJavaInnerIdentifier(String s) throws RgException {
        if (s.length() == 0)
            throw new RgException("Invalid inner identifier '" + s + "'");

        for (int i = 0; i < s.length(); i++)
            if (!Character.isJavaIdentifierPart(s.charAt(i)))
                throw new RgException("Invalid inner identifier '" + s + "'");
    }

    private static void checkClassSpec(String s) throws RgException {
        if (s.length() == 0)
            throw new RgException("Invalid class specifier '" + s + "'");

        int pos = -1;
        while ((pos = s.lastIndexOf('$')) != -1) {
            // FIXME: is inner identifier can be number or something like can not Character.isJavaIdentifierStart
            checkJavaInnerIdentifier(s.substring(pos + 1));
            s = s.substring(0, pos);
        }

        while ((pos = s.lastIndexOf('/')) != -1) {
            checkJavaIdentifier(s.substring(pos + 1));
            s = s.substring(0, pos);
        }
        checkJavaIdentifier(s);
    }

    /*public static void checkMethodDescriptor(String s) throws RgException {
        if (s.length() == 0 || s.charAt(0) != '(')
            throw new RgException("Invalid method descriptor '" + s + "'");

        s = s.substring(1);

        // Check each type
        while (s.length() > 0 && s.charAt(0) != ')')
            s = checkFirstJavaType(s);

        checkJavaType(s.substring(1));
    }*/

    public static void checkMethodDescriptor(String s) throws RgException {
        int pos = s.lastIndexOf(')');
        if (s.length() == 0 || s.charAt(0) != '(' || pos == -1)
            throw new RgException("Invalid method descriptor '" + s + "'");

        //String rt = s.substring(pos + 1);
        //try {
        //    checkJavaType(rt);
        //}
        //catch (RgException e) {
        //    throw new RgException("Invalid return type '" + rt + "'");
        //}

        // Check return type
        checkJavaType(s.substring(pos + 1));

        s = s.substring(1, pos);

        // Check each type
        while (s.length() > 0)
            s = checkFirstJavaType(s);
    }

    private static void checkJavaType(String s) throws RgException {
        if (!checkFirstJavaType(s).equals(""))
            throw new RgException("Invalid type '" + s + "'");
    }

    /*private static String checkFirstJavaType(String s) throws RgException {
        // Pull off the array specifiers
        while (s.charAt(0) == '[') {
            s = s.substring(1);
            if (s.length() == 0)
                throw new RgException("Invalid type '" + s + "'");
        }

        // Check a type
        int pos = 0;
        switch (s.charAt(0)) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z':
                break;

            case 'L':
                pos = s.indexOf(';');
                if (pos == -1)
                    throw new RgException("Invalid type '" + s + "'");

                // Check the class type
                checkClassSpec(s.substring(0, pos));
                break;

            default:
                throw new RgException("Invalid type '" + s + "'");
        }

        return s.substring(pos + 1);
    }*/

    private static String checkFirstJavaType(String s) throws RgException {
        // Pull off the array specifiers
        int pos = 0, len = s.length();
        while (s.charAt(pos) == '[')
            if (++pos == len)
                throw new RgException("Invalid type '" + s + "'");
        s = s.substring(pos);

        // Check a type
        pos = 0;
        switch (s.charAt(0)) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z':
                break;

            case 'L':
                pos = s.indexOf(';');
                if (pos == -1)
                    throw new RgException("Invalid type '" + s + "'");

                // Check the class type
                checkClassSpec(s.substring(0, pos));
                break;

            default:
                throw new RgException("Invalid type '" + s + "'");
        }

        return s.substring(pos + 1);
    }

    public static String toSignatureString(PsiElement element) throws RgException {
        //PluginManager.getLogger().warn("RGRL: toSignatureString() " + element.toString());

        if (element instanceof PsiMethod)  return toSignatureString((PsiMethod)element);
        if (element instanceof PsiField)   return toSignatureString((PsiField)element);
        if (element instanceof PsiClass)   return toSignatureString((PsiClass)element);
        if (element instanceof PsiPackage) return toSignatureString((PsiPackage)element);

        throw new RgException("Unsupported PsiElement: " + element.toString());
    }

    public static String toSignatureString(PsiPackage pkg) {
        return pkg.getQualifiedName().replaceAll("\\.", "/");
    }

    public static String toSignatureString(PsiClass cls) throws RgException {
        if (cls == null) throw new RgException("PsiClass is null");
        String name = cls.getQualifiedName();
        // INFO: unresolved class="Qclass;", resolved="Lpath/to/class;"
        if (name == null) throw new RgException("Class " + cls.getName() + " is unresolved");
        return name.replaceAll("\\.", "/");
    }

    public static String toSignatureString(PsiField fld) throws RgException {
        return toSignatureString(fld.getContainingClass()) + "/" + fld.getName();
    }

    public static String toSignatureString(PsiMethod mtd) throws RgException {
        StringBuilder signature = new StringBuilder("(");

        for (final PsiParameter p : mtd.getParameterList().getParameters())
            signature.append(toSignatureString(p.getType())); // getTypeNoResolve()

        signature.append(")");
        if (!mtd.isConstructor())
            signature.append(toSignatureString(mtd.getReturnType())); // getReturnTypeNoResolve

        return toSignatureString(mtd.getContainingClass()) + "/" + mtd.getName() + " " + signature.toString();
    }

    public static String toSignatureString(PsiType type) throws RgException {
        // TODO: "T[^;]+;" => TypeVariable
        // TODO: "<[^>]+>" => Generics
        // TODO: "." => InnerClass
        // TODO: ":" => Interface
        // TODO: "^" => Exception
        // TODO: replace recursion on PsiArrayType
        if (type.equals(PsiType.INT))     return "I";
        if (type.equals(PsiType.VOID))    return "V";
        if (type.equals(PsiType.LONG))    return "J";
        if (type.equals(PsiType.CHAR))    return "C";
        if (type.equals(PsiType.BYTE))    return "B";
        if (type.equals(PsiType.SHORT))   return "S";
        if (type.equals(PsiType.FLOAT))   return "F";
        if (type.equals(PsiType.DOUBLE))  return "D";
        if (type.equals(PsiType.BOOLEAN)) return "Z";
        if (type instanceof PsiClassType) return "L" + toSignatureString(((PsiClassType)type).resolve()) + ";";
        if (type instanceof PsiArrayType) return "[" + toSignatureString(((PsiArrayType)type).getComponentType());

        throw new RgException("Unsupported PsiType: " + type.toString());
    }
}
