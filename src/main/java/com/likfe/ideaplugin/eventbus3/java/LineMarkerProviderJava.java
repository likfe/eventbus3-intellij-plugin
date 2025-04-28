package com.likfe.ideaplugin.eventbus3.java;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import com.likfe.ideaplugin.eventbus3.PsiUtils;
import com.likfe.ideaplugin.eventbus3.ShowUsagesAction;
import com.likfe.ideaplugin.eventbus3.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

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
                    if (psiElement instanceof PsiMethod) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass eventBusClass = javaPsiFacade.findClass(Constants.FUN_EVENT_CLASS, GlobalSearchScope.allScope(project));
                        if (eventBusClass == null) return;

                        PsiMethod method = (PsiMethod) psiElement;

                        //post
                        PsiMethod postMethod = eventBusClass.findMethodsByName(Constants.FUN_NAME, false)[0];
                        if (null != postMethod) {
                            PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();

                            new ShowUsagesAction(new SenderFilterJava(eventClass)).startFindUsages(postMethod, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), Constants.MAX_USAGES);
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
                    if (psiElement instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
                        try {
                            PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
                            if (expressionTypes.length > 0) {
                                PsiClass eventClass = PsiUtils.getClass(expressionTypes[0]);
                                if (eventClass != null) {
                                    new ShowUsagesAction(new ReceiverFilterJava()).startFindUsages(eventClass, new RelativePoint(e), PsiUtilBase.findEditor(psiElement), Constants.MAX_USAGES);
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
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), Constants.ICON,
                    null, SHOW_RECEIVERS, GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiver(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), Constants.ICON,
                    null, SHOW_SENDERS, GutterIconRenderer.Alignment.LEFT);
        }
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> list, @NotNull Collection<? super LineMarkerInfo<?>> collection) {

    }
}
