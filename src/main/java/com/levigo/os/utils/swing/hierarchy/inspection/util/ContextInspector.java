package com.levigo.os.utils.swing.hierarchy.inspection.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;

import org.jadice.util.log.Logger;
import org.jadice.util.log.LoggerFactory;
import org.jadice.util.swing.action.context.Context;

/**
 * {@link Context} inspection utility which has access to some private details about the
 * {@link Context} object.
 * <p>
 * <b>NOTE</b>: This logic is using a reflective hack to access the children and some other metadata
 * of the context. Using this technique is not necessary in normal projects as the automatic
 * management of context will work as expected. It is used here for analyzing purposes.
 */
public class ContextInspector {
  private static final Logger LOG = LoggerFactory.getLogger(ContextInspector.class);

  public static final ContextInspector INSTANCE = new ContextInspector();

  /**
   * Boolean flag indicating whether we are able to access the children list of the context, or not.
   * If set to <code>true</code> we're able to use the reflective hack. If not this value will be
   * <code>false</code>
   */
  private final boolean enabled;

  private Method getChildrenMethod;
  private Field childAggregationField;
  private Field ancestorAggregationField;

  private ContextInspector() {

    boolean enabled = true;
    try {
      getChildrenMethod = Context.class.getDeclaredMethod("getChildren");
      getChildrenMethod.setAccessible(true);

      childAggregationField = Context.class.getDeclaredField("childAggregation");
      childAggregationField.setAccessible(true);

      ancestorAggregationField = Context.class.getDeclaredField("ancestorAggregation");
      ancestorAggregationField.setAccessible(true);

      // validate that all types that we're going to inspect are as we're expecting.
      enabled = //
      Iterable.class.isAssignableFrom(getChildrenMethod.getReturnType()) //
          && Context.Children.class.isAssignableFrom(childAggregationField.getType()) //
          && Context.Ancestors.class.isAssignableFrom(ancestorAggregationField.getType()) //
      ;
    } catch (Exception e) {
      LOG.debug("Unable to use " + getClass().getSimpleName() + " due to an exception.", e);
      enabled = false;
    }

    if (!enabled) {
      LOG.warn("Unable to use "
          + getClass().getSimpleName()
          + ". This might be due to a version mismatch between the jadice version this class has been implemented for and the jadice libraries on the classpath.");
    }

    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @SuppressWarnings("unchecked")
  public Iterable<Context> getChildren(Context context) {
    if (!enabled)
      return Collections.emptyList();
    try {
      return (Iterable<Context>) getChildrenMethod.invoke(context);
    } catch (Exception e) {
      LOG.info("Failed to invoke getChildren method via reflection.", e);
      return Collections.emptyList();
    }
  }

  public Context.Children getChildAggregation(Context context) {
    if (!enabled)
      return null;
    try {
      return (Context.Children) childAggregationField.get(context);
    } catch (Exception e) {
      LOG.info("Failed to read childAggregation field via reflection.", e);
      return null;
    }
  }

  public Context.Ancestors getAncestorAggregation(Context context) {
    if (!enabled)
      return null;
    try {
      return (Context.Ancestors) ancestorAggregationField.get(context);
    } catch (Exception e) {
      LOG.info("Failed to read ancestorAggregation field via reflection.", e);
      return null;
    }
  }


}
