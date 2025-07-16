package com.likfe.ideaplugin.eventbus3;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiIdentifierImpl;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.likfe.ideaplugin.eventbus3.utils.Constants;
import com.likfe.ideaplugin.eventbus3.utils.MLog;
import org.jetbrains.kotlin.psi.*;

/**
 * modify by likfe ( https://github.com/likfe/ ) on 2018/03/05.
 */
public class PsiUtils {

    public static PsiClass getClass(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            return ((PsiClassType) psiType).resolve();
        }
        return null;
    }

    public static boolean isEventBusReceiver(PsiElement psiElement) {
        if (psiElement.getLanguage().is(Language.findLanguageByID("JAVA"))) {

            if (psiElement instanceof PsiMethod) {
                PsiMethod method = (PsiMethod) psiElement;
                if (!hasValidSignature(method)) return false;

                PsiModifierList modifierList = method.getModifierList();
                for (PsiAnnotation psiAnnotation : modifierList.getAnnotations()) {
                    if (safeEquals(psiAnnotation.getQualifiedName(), Constants.FUN_ANNOTATION)) {
                        return true;
                    }
                }
            }
        } else if (psiElement.getLanguage().is(Language.findLanguageByID("kotlin"))) {

            if (psiElement instanceof KtNamedFunction) {
                KtNamedFunction function = (KtNamedFunction) psiElement;
                KtModifierList modifierList = function.getModifierList();
                if (modifierList != null) {
                    for (KtAnnotationEntry annotationEntry : modifierList.getAnnotationEntries()) {
                        KtConstructorCalleeExpression calleeExpression = annotationEntry.getCalleeExpression();
                        if (calleeExpression != null && safeEquals(calleeExpression.getText(), Constants.FUN_ANNOTATION_KT)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasValidSignature(PsiMethod method) {
        return isPublicNotStatic(method) && isVoid(method) && hasSingleParameter(method);
    }

    private static boolean isPublicNotStatic(PsiMethod method) {
        return method.hasModifierProperty("public") && !method.hasModifierProperty("static");
    }

    private static boolean hasSingleParameter(PsiMethod method) {
        return method.getParameterList().getParametersCount() == 1;
    }

    private static boolean isVoid(PsiMethod method) {
        return PsiTypes.voidType().equals(method.getReturnType());
    }

    public static boolean isEventBusPost(PsiElement psiElement) {
        if (psiElement.getLanguage().is(Language.findLanguageByID("JAVA"))) {

            if (psiElement instanceof PsiMethodCallExpressionImpl && psiElement.getFirstChild() != null && psiElement.getFirstChild() instanceof PsiReferenceExpressionImpl) {
                PsiReferenceExpressionImpl all = (PsiReferenceExpressionImpl) psiElement.getFirstChild();
                if (all.getFirstChild() instanceof PsiMethodCallExpressionImpl && all.getLastChild() instanceof PsiIdentifierImpl) {
                    PsiMethodCallExpressionImpl start = (PsiMethodCallExpressionImpl) all.getFirstChild();
                    PsiIdentifierImpl post = (PsiIdentifierImpl) all.getLastChild();
                    if ((safeEquals(post.getText(), Constants.FUN_NAME) || safeEquals(post.getText(), Constants.FUN_NAME2)) && safeEquals(start.getText(), Constants.FUN_START)) {
                        return true;
                    }
                }
            }

            //xx.post()/xx.postSticky()，存在误判的可能
            if (psiElement instanceof PsiCallExpression) {
                PsiCallExpression callExpression = (PsiCallExpression) psiElement;
                PsiMethod method = callExpression.resolveMethod();
                if (method != null) {
                    String name = method.getName();
                    PsiElement parent = method.getParent();
                    if (parent instanceof PsiClass implClass) {
                        if (isEventBusClass(implClass) || isSuperClassEventBus(implClass) || isEventBusFacade(implClass) /*|| containsPostingMethod(implClass)*/) {
                            if (safeEquals(Constants.FUN_NAME, name) || safeEquals(Constants.FUN_NAME2, name) /*|| isPostingMethod(method)*/) {
                                return true;
                            }
                        }

                        if (isPostingMethod(method)) {
                            return true;
                        }
                    }
                }
            }

        } else if (psiElement.getLanguage().is(Language.findLanguageByID("kotlin"))) {

            if (psiElement instanceof KtDotQualifiedExpression) {
                KtDotQualifiedExpression all = (KtDotQualifiedExpression) psiElement;
                if (all.getFirstChild() instanceof KtDotQualifiedExpression && all.getLastChild() instanceof KtCallExpression) {
                    String start = all.getFirstChild().getText();
                    if (start != null && start.equals(Constants.FUN_START)) {
                        KtCallExpression postRoot = (KtCallExpression) all.getLastChild();
                        if (postRoot.getFirstChild() instanceof KtNameReferenceExpression) {
                            KtNameReferenceExpression referenceExpression = (KtNameReferenceExpression) postRoot.getFirstChild();
                            if (referenceExpression.getReferencedName().equals(Constants.FUN_NAME)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isPostingMethod(PsiMethod method) {
        // expected signature: <T, R> void name(T message) { }
        if (!hasSingleParameter(method)) {
            return false;
        }

        if (isAnnotatedPosting(method)) return true;

        PsiMethod[] superMethods = method.findSuperMethods();
        for (PsiMethod superMethod : superMethods) {
            if (isAnnotatedPosting(superMethod)) return true;
        }

        return false;
    }

    private static boolean isAnnotatedPosting(PsiMethod method) {
        return method.getAnnotation(Constants.ANNO_POST_CLASS) != null;
    }

    private static boolean isEventBusFacade(PsiClass psiClass) {
        return isAnnotatedWithPosting(psiClass);
    }

    private static boolean isAnnotatedWithPosting(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (Constants.ANNO_POST_CLASS.equals(annotation.getQualifiedName())) return true;
        }

        PsiClass[] ifaces = psiClass.getInterfaces();
        PsiClass[] supers = psiClass.getSupers();

        // TODO For better performance, walk the class tree rather in breadth than in depth

        for (PsiClass iface : ifaces) {
            if (isAnnotatedWithPosting(iface)) {
                return true;
            }
        }

        for (PsiClass superClass : supers) {
            if (isAnnotatedWithPosting(superClass)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isEventBusClass(PsiClass psiClass) {
        return safeEquals(psiClass.getName(), Constants.FUN_EVENT_CLASS_NAME);
    }

    private static boolean isSuperClassEventBus(PsiClass psiClass) {
        PsiClass[] supers = psiClass.getSupers();
        if (supers.length == 0) {
            return false;
        }
        for (PsiClass superClass : supers) {
            if (safeEquals(superClass.getName(), Constants.FUN_EVENT_CLASS_NAME)) {
                return true;
            }
        }
        return false;
    }

    private static boolean safeEquals(String obj, String value) {
        return obj != null && obj.equals(value);
    }

    public static boolean isKotlin(PsiElement psiElement) {
        return psiElement.getLanguage().is(Language.findLanguageByID("kotlin"));
    }

    public static boolean isJava(PsiElement psiElement) {
        return psiElement.getLanguage().is(Language.findLanguageByID("JAVA"));
    }

    /**
     * is kotlin plug installed and enable
     *
     * @return boolean
     */
    public static boolean checkIsKotlinInstalled() {
        PluginId pluginId = PluginId.findId("org.jetbrains.kotlin");
        if (pluginId != null) {
            IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(pluginId);
            return pluginDescriptor != null && pluginDescriptor.isEnabled();
        }
        return false;
    }

    private static void logPluginList() {
        IdeaPluginDescriptor[] pluginDescriptors = PluginManager.getPlugins();
        MLog.debug("== list plug ==");
        for (IdeaPluginDescriptor item : pluginDescriptors) {
            MLog.debug("id: " + item.getPluginId().getIdString() + " name: " + item.getName() + " isEnable: " + item.isEnabled());
        }
        MLog.debug("== list plug end ==");
    }

}
