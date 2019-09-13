package io.github.alexengrig.lambdax.intellij.plugin.inspection.collection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class MapXGetInspection extends AbstractBaseJavaLocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitLambdaExpression(PsiLambdaExpression expression) {
                super.visitLambdaExpression(expression);
                PsiParameterList parameters = expression.getParameterList();
                if (parameters.getParametersCount() != 1) {
                    return;
                }
                PsiParameter parameter = parameters.getParameters()[0];
                PsiType parameterType = parameter.getType();
                if (!isMap(parameterType) && Arrays.stream(parameterType.getSuperTypes()).noneMatch(this::isMap)) {
                    return;
                }
                String parameterName = parameter.getName();
                PsiElement body = expression.getBody();
                String prefix = parameterName + ".get(";
                if (body == null || !body.getText().startsWith(prefix)) {
                    return;
                }
                String descriptionTemplate = "LambdaX: to replace the lambda with MapX#get";
                LocalQuickFix localQuickFix = new LocalQuickFix() {
                    @Nls(capitalization = Nls.Capitalization.Sentence)
                    @NotNull
                    @Override
                    public String getFamilyName() {
                        return "LambdaX: use MapX";
                    }

                    @Nls(capitalization = Nls.Capitalization.Sentence)
                    @NotNull
                    @Override
                    public String getName() {
                        return "LambdaX: use MapX#get";
                    }

                    @Override
                    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                        PsiLambdaExpression lambdaExpression = (PsiLambdaExpression) descriptor.getPsiElement();
                        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
                        String bodyText = body.getText();
                        String key = bodyText.substring(bodyText.indexOf(prefix) + prefix.length(), bodyText.length() - 1);
                        PsiMethodCallExpression getCall =
                                (PsiMethodCallExpression) elementFactory.createExpressionFromText("MapX.get(" + key + ")", null);
                        lambdaExpression.replace(getCall);
                    }
                };
                holder.registerProblem(
                        expression,
                        descriptionTemplate,
                        ProblemHighlightType.WEAK_WARNING,
                        localQuickFix
                );
            }

            private boolean isMap(PsiType type) {
                return type.getCanonicalText().startsWith(Map.class.getCanonicalName());
            }
        };
    }
}
