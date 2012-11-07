package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import com.levigo.os.utils.swing.hierarchy.inspection.AbstractIconLoader;
import com.levigo.os.utils.swing.hierarchy.inspection.util.EventListSync;
import com.levigo.util.base.glazedlists.BasicEventList;
import com.levigo.util.base.glazedlists.EventList;
import com.levigo.util.base.glazedlists.GlazedLists;
import com.levigo.util.log.Logger;
import com.levigo.util.log.LoggerFactory;
import com.levigo.util.swing.action.Context;
import com.levigo.util.swing.action.ContextListener;
import com.levigo.util.swing.flextree.TreeContentProvider;
import com.levigo.util.swing.flextree.TreeIconProvider;
import com.levigo.util.swing.flextree.TreeLabelProvider;

public class ContextChildrenContentsProvider extends AbstractIconLoader
    implements
      TreeContentProvider,
      TreeIconProvider,
      TreeLabelProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ContextChildrenContentsProvider.class);

  public final class SynchronizeChildrenListener implements ContextListener {
    private final EventList<Context> children;
    private final Context context;

    public SynchronizeChildrenListener(Context context, EventList<Context> children) {
      this.context = context;
      this.children = children;
    }

    @Override
    public void contextChanged(Context source) {
      // we are not using the Context source here as this might actually be a child context.
      Iterable<Context> childrenIterable = getChildren(context);

      children.getReadWriteLock().writeLock().lock();
      try {
        if (childrenIterable != null)
          EventListSync.synchronize(childrenIterable, children);
        else
          // assuming that the list shall be cleared
          children.clear();
      } finally {
        children.getReadWriteLock().writeLock().unlock();
      }
    }
  }

  /**
   * This is a marker node that allows us to have a subnode beneath Context.
   */
  public static final class ContextChildrenNode {
    private final Context context;

    public ContextChildrenNode(Context context) {
      super();
      this.context = context;
    }

    public Context getContext() {
      return context;
    }
  }


  /**
   * Boolean flag indicating whether we are able to access the children list of the context, or not.
   * If set to <code>true</code> we're able to use the reflective hack. If not this value will be
   * <code>false</code>
   */
  private final boolean enabled;

  private Method getChildrenMethod;

  public ContextChildrenContentsProvider() {
    boolean enabled = true;
    try {
      // NOTE: This logic is using a reflective hack to access the children of the context. Using
      // this technique is not necessary in normal projects as the automatic management of context
      // will work as expected. It is used here for analyzing purposes.

      getChildrenMethod = Context.class.getDeclaredMethod("getChildren");
      getChildrenMethod.setAccessible(true);
      enabled = Iterable.class.isAssignableFrom(getChildrenMethod.getReturnType());
    } catch (Exception e) {
      LOG.warn("Unable to use "
          + getClass().getSimpleName()
          + ". This might be due to a version mismatch between the jadice version this class has been implemented for and the jadice libraries on the classpath.");
      LOG.debug("Unable to use " + getClass().getSimpleName() + " due to an exception.", e);
      enabled = false;
    }

    this.enabled = enabled;
  }

  @Override
  public EventList<? extends Object> getChildren(TreePath treePath) {

    // it should be impossible to get here if hasChildren has already returned false. But just to be
    // sure...
    if (!enabled)
      return null;

    if (treePath.getLastPathComponent() instanceof Context) {

      return GlazedLists.eventListOf(new ContextChildrenNode((Context) treePath.getLastPathComponent()));

    }

    // we are looking at a ContextChildrenNode

    EventList<Context> children = new BasicEventList<Context>();

    Context context = ((ContextChildrenNode) treePath.getLastPathComponent()).getContext();
    Iterable<Context> childrenIterable = getChildren(context);

    if (childrenIterable != null) {
      // no need for locking, as we're the only ones holding a reference to the children EventList
      EventListSync.synchronize(childrenIterable, children);
    }

    // registering an event listener that will update the children list on a context changed event.
    context.addContextChangeListener(new SynchronizeChildrenListener(context, children));

    return children;
  }

  @SuppressWarnings("unchecked")
  protected Iterable<Context> getChildren(Context context) {

    try {
      return (Iterable<Context>) getChildrenMethod.invoke(context);
    } catch (Exception e) {
      LOG.info("Failed to invoke getChildren method via reflection.", e);
      return null;
    }

  }

  @Override
  public boolean hasChildren(TreePath treePath) {

    return enabled
        && (treePath.getLastPathComponent() instanceof Context || treePath.getLastPathComponent() instanceof ContextChildrenNode);
  }

  private Icon contextChildrenIcon;

  @Override
  public Icon getIcon(TreePath treePath) {
    if (treePath.getLastPathComponent() instanceof ContextChildrenNode) {
      if (contextChildrenIcon == null)
        contextChildrenIcon = loadIcon("/icons/context-children.png");
      return contextChildrenIcon;
    }
    return null;
  }
  
  @Override
  public void updateLabel(TreePath treePath, StyledDocument doc) throws BadLocationException {
    if (treePath.getLastPathComponent() instanceof ContextChildrenNode) {
      doc.insertString(doc.getLength(), "Children", null);
    }
  }
}
