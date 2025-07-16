package com.likfe.ideaplugin.eventbus3.java;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import com.likfe.ideaplugin.eventbus3.PsiUtils;
import com.likfe.ideaplugin.eventbus3.ShowSendersAction;
import com.likfe.ideaplugin.eventbus3.ShowReceiversAction;
import com.likfe.ideaplugin.eventbus3.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by kgmyshin on 15/06/08.
 * <p>
 * modify by likfe ( https://github.com/likfe/ ) in 2018/03/06
 * </p>
 */
public class LineMarkerProviderJava implements com.intellij.codeInsight.daemon.LineMarkerProvider {

    /**
     * use Subscribe to find all matched post
     */

    private static final GutterIconNavigationHandler<PsiElement> SHOW_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiIdentifier identifier) {
                        PsiElement methodDeclaration = identifier.getParent();
                        PsiMethod method = (PsiMethod) methodDeclaration;

                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass(Constants.FUN_EVENT_CLASS, GlobalSearchScope.allScope(project));
                        if (eventBusClass == null) return;

                        //post
                        PsiMethod postMethod = eventBusClass.findMethodsByName(Constants.FUN_NAME, false)[0];
                        if (null != postMethod) {
                            PsiType psiType = method.getParameterList().getParameters()[0].getType();
                            SenderFilterJava filter = new SenderFilterJava(psiType);
                            new ShowSendersAction(filter).startFindUsages(postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), Constants.MAX_USAGES);
                        }

                        //postSticky
//                        PsiMethod postMethod2 = eventBusClass.findMethodsByName(Constants.FUN_NAME2, false)[0];
//                        if (null != postMethod2) {
//                            PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();
//
//                            new ShowUsagesAction(new SenderFilterJava(eventClass)).startFindUsages(postMethod2, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), Constants.MAX_USAGES);
//                        }

                    }


                }
            };

    /**
     * use post to find all matched Subscribe
     */

    private static final GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    if (psiElement instanceof PsiIdentifier identifier) {
                        PsiElement reference = identifier.getParent();
                        PsiElement methodCall = reference.getParent();
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) methodCall;
                        try {
                            PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                            if (expressionTypes.length > 0) {
                                PsiClass eventClass = PsiUtils.getClass(expressionTypes[0]);
                                if (eventClass != null) {
                                    new ShowReceiversAction(new ReceiverFilterJava()).startFindUsages(eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), Constants.MAX_USAGES);
                                }
                            }
                        } catch (Exception ee) {
                            ee.fillInStackTrace();
                        }

                    }
                }
            };

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (!PsiUtils.isJava(psiElement)) return null;
        //if (!(psiElement instanceof PsiIdentifier && psiElement.getParent() instanceof PsiMethod)) return null;
        if (PsiUtils.isEventBusPost(psiElement)) {
            PsiReferenceExpression expression = findChildElement(psiElement, PsiReferenceExpression.class);
            PsiIdentifier identifier = findChildElement(expression, PsiIdentifier.class);
            return new LineMarkerInfo<>(identifier, identifier.getTextRange(), Constants.ICON_EGRESS, null, SHOW_RECEIVERS, GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiver(psiElement)) {
            PsiMethod method = (PsiMethod) psiElement;
            PsiIdentifier identifier = findChildElement(method, PsiIdentifier.class);
            return new LineMarkerInfo<>(identifier, identifier.getTextRange(), Constants.ICON_INGRESS, null, SHOW_SENDERS, GutterIconRenderer.Alignment.LEFT);
        }
        return null;
    }

    private static <T extends PsiElement> T findChildElement(@NotNull PsiElement psiElement, Class<T> target) {
        @NotNull PsiElement[] children = psiElement.getChildren();
        for (PsiElement child : children) {
            if (target.isInstance(child)) {
                @SuppressWarnings("unchecked")
                T t = (T) child;
                return t;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> list, @NotNull Collection<? super LineMarkerInfo<?>> collection) {

    }
}
