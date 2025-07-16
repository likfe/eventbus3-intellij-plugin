package com.likfe.ideaplugin.eventbus3.java;

import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.likfe.ideaplugin.eventbus3.Filter;
import org.apache.groovy.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SenderFilterJava implements Filter {
    private static final Logger log = LoggerFactory.getLogger(SenderFilterJava.class);

    private final PsiType eventType;

    public SenderFilterJava(PsiType eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean shouldShow(Usage usage) {
        try {
            UsageInfo2UsageAdapter adapter = (UsageInfo2UsageAdapter) usage;
            return shouldShowReference(adapter.getElement());

        } catch (FilterConfusedException e) {
            Object[] params = Arrays.concat(new Object[] {usage}, e.values);
            String template = "\nSenderFilterJava: usage {}\n" + e.getMessage();
            log.warn(template, params);
            return false;
        }
    }

    private boolean shouldShowReference(PsiElement reference) throws FilterConfusedException {
        switch (reference) {
            case PsiReferenceExpression referenceExpression -> {
                return shouldShowElement(reference, referenceExpression.getParent());
            }

            case null, default -> throw new FilterConfusedException("""
                                                                    reference not yet supported {}""",
                                                                    reference);
        }
    }

    private boolean shouldShowElement(PsiElement reference, PsiElement element) throws FilterConfusedException {
        switch (element) {
            case PsiMethodCallExpression methodCallExpression -> {
                return shouldShowMethodCallExpression(reference, methodCallExpression);
            }

            case null, default -> throw new FilterConfusedException("""
                                                                    reference {}
                                                                    element not yet supported {}""",
                                                                    reference, element);
        }
    }

    private boolean shouldShowMethodCallExpression(PsiElement reference, PsiMethodCallExpression methodCallExpression) throws FilterConfusedException {
        PsiExpressionList argumentList = methodCallExpression.getArgumentList();
        PsiExpression[] expressions = argumentList.getExpressions();
        if (expressions.length != 1) {
            throw new FilterConfusedException("invalid method signature for: {}", methodCallExpression);
        }
        return shouldShowExpression(reference, expressions[0]);
    }

    private boolean shouldShowExpression(PsiElement reference, PsiExpression expression) throws FilterConfusedException {
        switch (expression) {
            case PsiReferenceExpression argumentReferenceExpression -> {
                return shouldShowType(argumentReferenceExpression.getType());
            }

            case PsiNewExpression argumentNewExpression -> {
                return shouldShowType(argumentNewExpression.getType());
            }

            case PsiMethodCallExpression argumentMethodCallExpression -> {
                PsiMethod method = argumentMethodCallExpression.resolveMethod();
                if (method == null) {
                    throw new FilterConfusedException("""
                                                      reference {}
                                                      argument {}
                                                      method not resolved: {}""",
                                                      reference, expression, argumentMethodCallExpression);
                }
                return shouldShowType(method.getReturnType());
            }

            case null, default -> throw new FilterConfusedException("""
                                                                    reference {}
                                                                    expression not yet supported {}""",
                                                                    reference, expression);
        }
    }

    private boolean shouldShowType(PsiType psiType) {
        if (psiType == null) {
            return false;
        }

        return eventType.isAssignableFrom(psiType);
    }
}
