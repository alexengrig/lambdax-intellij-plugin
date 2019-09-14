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

package io.github.alexengrig.lambdax.intellij.plugin.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiLambdaExpression;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public abstract class MyLambdaExpressionJavaElementVisitor extends JavaElementVisitor {
    private final ProblemsHolder problemsHolder;

    protected MyLambdaExpressionJavaElementVisitor(@NotNull ProblemsHolder problemsHolder) {
        this.problemsHolder = problemsHolder;
    }

    protected abstract boolean isValidParameters(PsiLambdaExpression expression);

    protected abstract boolean isValidBody(PsiLambdaExpression expression);

    protected abstract String getDescriptionTemplate();

    protected abstract ProblemHighlightType getHighlightType();

    protected abstract String getName();

    protected abstract String getFamilyName();

    protected abstract BiConsumer<Project, ProblemDescriptor> getProblemFixer(PsiLambdaExpression expression);

    @Override
    public void visitLambdaExpression(PsiLambdaExpression expression) {
        super.visitLambdaExpression(expression);
        if (!isValidParameters(expression) || !isValidBody(expression)) {
            return;
        }
        problemsHolder.registerProblem(
                expression,
                getDescriptionTemplate(),
                getHighlightType(),
                MyLocalQuickFix.of(getName(), getFamilyName(), getProblemFixer(expression))
        );
    }
}
