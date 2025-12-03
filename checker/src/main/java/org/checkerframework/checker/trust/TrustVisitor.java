package org.checkerframework.checker.trust;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.trust.qual.Trust;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.javacutil.TreeUtils;

/** Visitor for the {@link TrustChecker}. */
public class TrustVisitor extends BaseTypeVisitor<TrustAnnotatedTypeFactory> {

  /** Map from method types to their trust levels. */
  protected HashMap<AnnotatedTypeMirror, Integer> methodTrustLevels;

  /**
   * Creates a {@link TrustVisitor}.
   *
   * @param checker the trust checker
   */
  public TrustVisitor(BaseTypeChecker checker) {
    super(checker);
    methodTrustLevels = new HashMap<AnnotatedTypeMirror, Integer>();
  }

  /**
   * Processes a method tree to extract trust level information.
   *
   * @param className the name of the class containing the method
   * @param tree the method tree
   */
  @Override
  public void processMethodTree(String className, MethodTree tree) {
    super.processMethodTree(className, tree);

    ExecutableElement method = TreeUtils.elementFromDeclaration(tree);
    if (atypeFactory.getDeclAnnotation(method, Trust.class) != null) {
      methodTrustLevels.put(
          atypeFactory.getAnnotatedType(method),
          getLevel(atypeFactory.getDeclAnnotation(method, Trust.class).getElementValues()));
    }
  }

  /**
   * Visits a method invocation to check trust level compatibility.
   *
   * @param tree the method invocation tree
   * @param p the visitor parameter
   */
  @Override
  public Void visitMethodInvocation(MethodInvocationTree tree, Void p) {
    AnnotatedTypeMirror invokedMethod = atypeFactory.methodFromUse(tree).executableType;
    Integer trustLevel = null;
    if (invokedMethod.getPrimaryAnnotation(Trust.class) != null) {
      trustLevel = getLevel(invokedMethod.getPrimaryAnnotation(Trust.class).getElementValues());
    }
    if (trustLevel != null) {
      AnnotatedTypeMirror receiverType = atypeFactory.getReceiverType(tree);
      Integer receiverTrustLevel = null;
      if (receiverType.getPrimaryAnnotation(Trust.class) != null) {
        receiverTrustLevel =
            getLevel(receiverType.getPrimaryAnnotation(Trust.class).getElementValues());
      }
      if (receiverTrustLevel != null && receiverTrustLevel < trustLevel) {
        checker.reportError(tree, "trust.level.too.low", receiverTrustLevel, trustLevel);
      }
    }

    return super.visitMethodInvocation(tree, p);
  }

  /**
   * Extracts the trust level value from the annotation element values.
   *
   * @param elementValues the annotation element values
   * @return the trust level value, or null if not found
   */
  private Integer getLevel(
      Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues) {
    if (elementValues == null) {
      return null;
    }
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
        elementValues.entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals("level")) {
        return (Integer) entry.getValue().getValue();
      }
    }
    return null;
  }

  /**
   * Copy and Pasted from Tainted Checker. Don't check that the constructor result is top. Checking
   * that the super() or this() call is a subtype of the constructor result is sufficient.
   */
  @Override
  protected void checkConstructorResult(
      AnnotatedExecutableType constructorType, ExecutableElement constructorElement) {}
}
