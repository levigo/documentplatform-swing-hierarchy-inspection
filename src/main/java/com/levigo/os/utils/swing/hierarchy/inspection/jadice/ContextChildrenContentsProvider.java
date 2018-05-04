package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.jadice.util.glazedlists.BasicEventList;
import org.jadice.util.glazedlists.EventList;
import org.jadice.util.glazedlists.GlazedLists;
import org.jadice.util.swing.action.context.Context;
import org.jadice.util.swing.action.context.ContextListener;
import org.jadice.util.swing.flextree.TreeContentProvider;
import org.jadice.util.swing.flextree.TreeIconProvider;
import org.jadice.util.swing.flextree.TreeLabelProvider;

import com.levigo.os.utils.swing.hierarchy.inspection.AbstractIconLoader;
import com.levigo.os.utils.swing.hierarchy.inspection.util.ContextInspector;
import com.levigo.os.utils.swing.hierarchy.inspection.util.EventListSync;

public class ContextChildrenContentsProvider extends AbstractIconLoader
    implements
    TreeContentProvider,
      TreeIconProvider,
      TreeLabelProvider {

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
      Iterable<Context> childrenIterable = ContextInspector.INSTANCE.getChildren(context);

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

  public ContextChildrenContentsProvider() {
  }

  @Override
  public EventList<? extends Object> getChildren(TreePath treePath) {

    // it should be impossible to get here if hasChildren has already returned false. But just to be
    // sure...
    if (!ContextInspector.INSTANCE.isEnabled())
      return null;

    if (treePath.getLastPathComponent() instanceof Context) {

      return GlazedLists.eventListOf(new ContextChildrenNode((Context) treePath.getLastPathComponent()));

    }

    // we are looking at a ContextChildrenNode

    EventList<Context> children = new BasicEventList<Context>();

    Context context = ((ContextChildrenNode) treePath.getLastPathComponent()).getContext();
    Iterable<Context> childrenIterable = ContextInspector.INSTANCE.getChildren(context);

    if (childrenIterable != null) {
      // no need for locking, as we're the only ones holding a reference to the children EventList
      EventListSync.synchronize(childrenIterable, children);
    }

    // registering an event listener that will update the children list on a context changed event.
    context.addContextChangeListener(new SynchronizeChildrenListener(context, children));

    return children;
  }

  @Override
  public boolean hasChildren(TreePath treePath) {
    return ContextInspector.INSTANCE.isEnabled()
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
