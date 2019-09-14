/*
 * Copyright 2019 LambdaX IntelliJ plugin contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alexengrig.lambdax.intellij.plugin.inspection.collection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import io.github.alexengrig.lambdax.intellij.plugin.inspection.MyLambdaExpressionJavaElementVisitor;
import io.github.alexengrig.lambdax.intellij.plugin.message.MessageBundle;
import io.github.alexengrig.lambdax.intellij.plugin.message.MessageUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

public class MapGetInspection extends AbstractBaseJavaLocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder problemsHolder, boolean isOnTheFly) {
        return new MyLambdaExpressionJavaElementVisitor(problemsHolder) {
            @Override
            protected boolean isValidParameters(PsiLambdaExpression expression) {
                PsiParameterList parameterList = expression.getParameterList();
                if (parameterList.getParametersCount() != 1) {
                    return false;
                }
                PsiParameter parameter = parameterList.getParameters()[0];
                PsiType parameterType = parameter.getType();
                return isMap(parameterType) || Arrays.stream(parameterType.getSuperTypes()).anyMatch(this::isMap);
            }

            private boolean isMap(PsiType type) {
                return type.getCanonicalText().startsWith(Map.class.getCanonicalName());
            }

            @Override
            protected boolean isValidBody(PsiLambdaExpression expression) {
                PsiParameter parameter = expression.getParameterList().getParameters()[0];
                String parameterName = parameter.getName();
                PsiElement body = expression.getBody();
                String prefix = parameterName + ".get("; //FIXME: hardcore
                return body != null && body.getText().startsWith(prefix);
            }

            @Override
            protected String getDescriptionTemplate() {
                String message = MessageBundle.message("inspection.collection.map.get.descriptionTemplate"); //TODO: need constants?
                return MessageUtils.pluginPrefixText(message);
            }

            @Override
            protected ProblemHighlightType getHighlightType() {
                return ProblemHighlightType.WEAK_WARNING;
            }

            @Override
            protected String getName() {
                String message = MessageBundle.message("inspection.collection.map.get.name"); //TODO: need constants?
                String usePrefixedMessage = MessageUtils.usePrefixText(message);
                return MessageUtils.pluginPrefixText(usePrefixedMessage);
            }

            @Override
            protected String getFamilyName() {
                String message = MessageBundle.message("inspection.collection.map.name"); //TODO: need constants?
                String usePrefixedMessage = MessageUtils.usePrefixText(message);
                return MessageUtils.pluginPrefixText(usePrefixedMessage);
            }

            @Override
            protected BiConsumer<Project, ProblemDescriptor> getProblemFixer(PsiLambdaExpression expression) {
                return (project, problemDescriptor) -> {
                    PsiElement body = expression.getBody();
                    PsiLambdaExpression lambdaExpression = (PsiLambdaExpression) problemDescriptor.getPsiElement();
                    PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
                    String bodyText = body.getText();
                    String prefix = expression.getParameterList().getParameters()[0].getName() + ".get("; //FIXME: hardcore
                    String key = bodyText.substring(bodyText.indexOf(prefix) + prefix.length(), bodyText.length() - 1);
                    String expressionText = "MapX.get(" + key + ")";//FIXME: hardcore
                    PsiMethodCallExpression getCall =
                            (PsiMethodCallExpression) elementFactory.createExpressionFromText(expressionText, null);
                    lambdaExpression.replace(getCall);
                };
            }
        };
    }
}
