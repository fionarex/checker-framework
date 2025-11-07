package org.checkerframework.checker.trust;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.VariableTree;
import java.util.Map;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.trust.qual.Trust;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;

/** Visitor for the {@link TrustChecker}. */
public class TrustVisitor extends BaseTypeVisitor<TrustAnnotatedTypeFactory> {

  /**
   * Creates a {@link TrustVisitor}.
   *
   * @param checker the trust checker
   */
  public TrustVisitor(BaseTypeChecker checker) {
    super(checker);
  }

  /**
   * Visits a method invocation to check that the receiver's trust category is at least as high as
   * the invoked method's trust category.
   */
  @Override
  public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
    AnnotatedTypeMirror invokedMethod = atypeFactory.methodFromUse(node).executableType;

    if (invokedMethod.hasPrimaryAnnotation(Trust.class)) {
      AnnotatedTypeMirror receiver = atypeFactory.getReceiverType(node);

      Integer methodCategory =
          getCategoryValue(invokedMethod.getPrimaryAnnotation(Trust.class).getElementValues());
      Integer receiverCategory =
          receiver != null && receiver.hasPrimaryAnnotation(Trust.class)
              ? getCategoryValue(receiver.getPrimaryAnnotation(Trust.class).getElementValues())
              : null;

      if (methodCategory != null && receiverCategory != null && receiverCategory < methodCategory) {
        checker.reportError(node, "trust.category.too.low", receiverCategory, methodCategory);
      }
    }

    return super.visitMethodInvocation(node, p);
  }

  /**
   * Extracts the trust category value from the annotation element values.
   *
   * @param elementValues the annotation element values
   * @return the trust category value, or null if not found
   */
  private Integer getCategoryValue(
      Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues) {
    if (elementValues == null) return null;
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
        elementValues.entrySet()) {
      if ("category".equals(entry.getKey().getSimpleName().toString())) {
        Object value = entry.getValue().getValue();
        if (value instanceof Integer) {
          return (Integer) value;
        } else if (value instanceof Number) {
          return ((Number) value).intValue();
        }
      }
    }
    return null;
  }

  /**
   * Ensures that variable declarations inherit the Trust annotation from their class if not
   * explicitly annotated.
   */
  @Override
  public Void visitVariable(VariableTree variable, Void p) {
    AnnotatedTypeMirror varType = atypeFactory.getAnnotatedType(variable.getType());
    if (varType.hasPrimaryAnnotation(Trust.class)) {
      Integer classCategory =
          getCategoryValue(varType.getPrimaryAnnotation(Trust.class).getElementValues());
      AnnotatedTypeMirror declaredType = atypeFactory.getAnnotatedType(variable);
      if (classCategory != null
          && (!declaredType.hasPrimaryAnnotation(Trust.class)
              || !getCategoryValue(
                      declaredType.getPrimaryAnnotation(Trust.class).getElementValues())
                  .equals(classCategory))) {
        declaredType.addAnnotation(varType.getPrimaryAnnotation(Trust.class));
      }
    }
    return super.visitVariable(variable, p);
  }
}
