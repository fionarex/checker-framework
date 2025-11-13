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
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;

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
   * Visits a method invocation to check that the receiver's trust level is at least as high as the
   * invoked method's trust level.
   */
  @Override
  public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
    AnnotatedTypeMirror invokedMethod = atypeFactory.methodFromUse(node).executableType;

    if (invokedMethod.hasPrimaryAnnotation(Trust.class)) {
      Integer methodTrustLevel =
          getLevel(invokedMethod.getPrimaryAnnotation(Trust.class).getElementValues());

      AnnotatedTypeMirror receiver = atypeFactory.getReceiverType(node);
      Integer receiverCategory = null;
      if (receiver != null && receiver.hasPrimaryAnnotation(Trust.class)) {
        receiverCategory =
            getLevel(receiver.getPrimaryAnnotation(Trust.class).getElementValues());
      }

      if (methodTrustLevel != null
          && receiverCategory != null
          && receiverCategory < methodTrustLevel) {
        checker.reportError(node, "trust.level.too.low", receiverCategory, methodTrustLevel);
      }

      for (int i = 0; i < node.getArguments().size(); i++) {
        AnnotatedTypeMirror argType = atypeFactory.getAnnotatedType(node.getArguments().get(i));
        Integer argCategory =
            argType.hasPrimaryAnnotation(Trust.class)
                ? getLevel(argType.getPrimaryAnnotation(Trust.class).getElementValues())
                : null;
        if (methodTrustLevel != null && argCategory != null && argCategory < methodTrustLevel) {
          checker.reportError(
              node.getArguments().get(i), "trust.level.too.low", argCategory, methodTrustLevel);
        }
      }
    }

    return super.visitMethodInvocation(node, p);
  }

  /**
   * Extracts the trust level value from the annotation element values.
   *
   * @param elementValues the annotation element values
   * @return the trust level value, or null if not found
   */
  private Integer getLevel(
      Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues) {
    if (elementValues == null) return null;
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
        elementValues.entrySet()) {
      if ("level".equals(entry.getKey().getSimpleName().toString())) {
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
      Integer classTrustLevel =
          getLevel(varType.getPrimaryAnnotation(Trust.class).getElementValues());
      AnnotatedTypeMirror declaredType = atypeFactory.getAnnotatedType(variable);
      if (classTrustLevel != null
          && (!declaredType.hasPrimaryAnnotation(Trust.class)
              || !getLevel(declaredType.getPrimaryAnnotation(Trust.class).getElementValues())
                  .equals(classTrustLevel))) {
        declaredType.addAnnotation(varType.getPrimaryAnnotation(Trust.class));
      }
    }
    return super.visitVariable(variable, p);
  }

  /**
   * Copy and Pasted from Tainted Checker Don't check that the constructor result is top. Checking
   * that the super() or this() call is a subtype of the constructor result is sufficient.
   */
  @Override
  protected void checkConstructorResult(
      AnnotatedExecutableType constructorType, ExecutableElement constructorElement) {}
}
