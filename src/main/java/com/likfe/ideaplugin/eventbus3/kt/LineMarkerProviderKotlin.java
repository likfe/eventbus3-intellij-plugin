package com.likfe.ideaplugin.eventbus3.kt;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.find.FindManager;
import com.intellij.find.findUsages.FindUsagesManager;
import com.intellij.find.impl.FindManagerImpl;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import com.likfe.ideaplugin.eventbus3.PsiUtils;
import com.likfe.ideaplugin.eventbus3.ShowUsagesAction;
import com.likfe.ideaplugin.eventbus3.utils.Constants;
import com.likfe.ideaplugin.eventbus3.utils.MLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.*;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinUserTypeStubImpl;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * Created by by likfe ( https://github.com/likfe/ )  on 18/03/05.
 */
public class LineMarkerProviderKotlin implements com.intellij.codeInsight.daemon.LineMarkerProvider {

    private static GutterIconNavigationHandler<PsiElement> SHOW_SENDERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    MLog.debug("kt SHOW_SENDERS 0: " + psiElement.getText());
                    if (PsiUtils.isKotlin(psiElement) && psiElement instanceof KtNamedFunction) {
                        Project project = psiElement.getProject();
                        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
                        Module module = ModuleUtilCore.findModuleForPsiElement(psiElement);
                        PsiClass eventBusClass = psiFacade.findClass(Constants.FUN_EVENT_CLASS, GlobalSearchScope.moduleWithLibrariesScope(module));
                        PsiMethod postMethod = null;
                        if (eventBusClass != null) {
                            postMethod = eventBusClass.findMethodsByName(Constants.FUN_NAME, false)[0];
                            MLog.debug("kt SHOW_SENDERS 1: " + eventBusClass.getText().substring(0, 25));
                            MLog.debug("kt SHOW_SENDERS 2: " + postMethod.getText().substring(0, 20));
                        }
                        //JavaCodeContextType.Declaration dd;
                        //org.jetbrains.kotlin.psi.KtUserType ktUserType;
                        //org.jetbrains.kotlin.psi.KtNameReferenceExpression ktNameReferenceExpression;
                        //com.intellij.psi.impl.source.tree.LeafPsiElement leafPsiElement;
                        LeafPsiElement eventClass = null;
                        KtParameter parameter = null;
                        KtTypeReference typeReference = null;
                        KtUserType ktUserType = null;
                        KtNameReferenceExpression ktNameReferenceExpression = null;
                        KotlinUserTypeStubImpl userTypeStub = null;
                        KtNamedFunction function = (KtNamedFunction) psiElement;
                        KtParameterList parameterList = function.getValueParameterList();
                        if (parameterList != null && parameterList.getParameters().size() == 1) {
                            parameter = parameterList.getParameters().get(0);
                            typeReference = parameter.getTypeReference();
                            ktUserType = (KtUserType) typeReference.getFirstChild();
                            //userTypeStub = new KotlinUserTypeStubImpl(ktUserType.getStub());
                            ktNameReferenceExpression = (KtNameReferenceExpression) ktUserType.getFirstChild();
                            eventClass = (LeafPsiElement) ktNameReferenceExpression.getFirstChild();
                            MLog.debug("kt SHOW_SENDERS 3: " + eventClass.toString());
                        }

                        if (postMethod != null && eventClass != null) {
                            Project project2 = postMethod.getProject();
                            FindUsagesManager findUsagesManager = ((FindManagerImpl) FindManager.getInstance(project)).getFindUsagesManager();
                            //new KotlinFindUsagesHandlerFactory(project).getFindClassOptions();
                            //KotlinFindUsagesHandlerFactory kotlinFindUsagesHandlerFactory = new KotlinFindUsagesHandlerFactory(project);

//                            FindUsagesHandler findUsagesHandler = kotlinFindUsagesHandlerFactory.createFindUsagesHandler(parameter, false);
//                            AbstractFindUsagesDialog dialog2 = findUsagesHandler.getFindUsagesDialog(false, true, true);
//                            dialog2.showAndGet();
                            //dialog2.show();
//
//                            KtClass ktClass = new KtClass(postMethod.getNode());
//                            KotlinFindUsagesHandler dd = new KotlinTypeParameterFindUsagesHandler(ktClass, kotlinFindUsagesHandlerFactory);
//                            //KotlinFindClassUsagesHandler handlers = new KotlinFindClassUsagesHandler(ktClass, kotlinFindUsagesHandlerFactory);
////
////                            //KotlinFindClassUsagesDialog dialog = (KotlinFindClassUsagesDialog) handlers.getFindUsagesDialog(false, true, false);
//                            AbstractFindUsagesDialog dialog = dd.getFindUsagesDialog(false, true, false);
//                            dialog.show();
//
//                            Collection psiReferences = dd.findReferencesToHighlight(parameter, GlobalSearchScope.allScope(project));
//                            for (Object p:psiReferences){
//                                PsiReference p1= (PsiReference) p;
//                                MLog.debug(p1.toString());
//                            }

//
//                            StubBasedPsiElementBase d;


                            //KotlinFindUsagesProvider findUsagesProvider = new KotlinFindUsagesProvider();

                            //findUsagesProvider.getWordsScanner();

                            //DefaultWordsScanner defaultWordsScanner;
                            //FileEditor editor = PsiUtilBase.findEditor(psiElement);
                            //KotlinEditorOptions options;
                            //findUsagesManager.findUsages(eventClass, null, );

                            new ShowUsagesAction(new SenderFilterKotlin(eventClass))
                                    .startFindUsages(
                                            postMethod,
                                            new RelativePoint(e),
                                            PsiUtilBase.findEditor(eventClass),
                                            Constants.MAX_USAGES
                                    );
                        }
                    }
                }
            };

    private static GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS =
            new GutterIconNavigationHandler<PsiElement>() {
                @Override
                public void navigate(MouseEvent e, PsiElement psiElement) {
                    MLog.debug("kt SHOW_RECEIVERS 0: " + psiElement.getText());


                    if (psiElement instanceof KtDotQualifiedExpression) {
                        MLog.debug("kt SHOW_RECEIVERS 1: " + psiElement.getText());

                        try {
                            KtDotQualifiedExpression expression = (KtDotQualifiedExpression) psiElement;
                            KtCallExpression callExpression = (KtCallExpression) expression.getLastChild();
                            KtValueArgumentList argumentList = callExpression.getValueArgumentList();
                            KtValueArgument argument = argumentList.getArguments().get(0);
                            KtNameReferenceExpression referenceExpression = (KtNameReferenceExpression) argument.getFirstChild();
                            LeafPsiElement leafPsiElement = (LeafPsiElement) referenceExpression.getFirstChild();

                            //KtPsiClassWrapper psiClassWrapper= KotlinJavaPsiFacade.getInstance(psiElement.getProject()).findClass(leafPsiElement.getClass(),)

                            MLog.debug("kt SHOW_RECEIVERS 2: " + argument.getText());
                            KtClass ktClass = new KtClass(leafPsiElement.getNode());
                            MLog.debug("kt SHOW_RECEIVERS 3: " + ktClass.getText());
                            new ShowUsagesAction(new ReceiverFilterKotlin())
                                    .startFindUsages(
                                            ktClass, new RelativePoint(e),
                                            PsiUtilBase.findEditor(psiElement),
                                            Constants.MAX_USAGES);

                        } catch (Exception | Error throwable) {
                            throwable.fillInStackTrace();
                        }


//                        PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
//                        if (expressionTypes.length > 0) {
//                            PsiClass eventClass = PsiUtils.getClass(expressionTypes[0]);
//                            if (eventClass != null) {
//                                new ShowUsagesAction(new ReceiverFilterKotlin())
//                                        .startFindUsages(
//                                                eventClass, new RelativePoint(e),
//                                                PsiUtilBase.findEditor(psiElement),
//                                                Constants.MAX_USAGES);
//                            }
//                        }
                    }
                }
            };

    @Nullable
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (!PsiUtils.checkIsKotlinInstalled()) return null;
        if (!PsiUtils.isKotlin(psiElement)) return null;

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
