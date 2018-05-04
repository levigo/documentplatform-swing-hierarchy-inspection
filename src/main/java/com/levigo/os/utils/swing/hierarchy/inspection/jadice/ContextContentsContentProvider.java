package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import org.jadice.util.glazedlists.BasicEventList;
import org.jadice.util.glazedlists.EventList;
import org.jadice.util.glazedlists.GlazedLists;
import org.jadice.util.log.Logger;
import org.jadice.util.log.LoggerFactory;
import org.jadice.util.swing.action.context.Context;
import org.jadice.util.swing.action.context.ContextListener;
import org.jadice.util.swing.flextree.TreeContentProvider;
import org.jadice.util.swing.flextree.TreeIconProvider;
import org.jadice.util.swing.flextree.TreeLabelProvider;

import com.levigo.os.utils.swing.hierarchy.inspection.AbstractIconLoader;
import com.levigo.os.utils.swing.hierarchy.inspection.util.EventListSync;

public class ContextContentsContentProvider extends AbstractIconLoader
    implements
      TreeContentProvider,
      TreeLabelProvider,
      TreeIconProvider {

  public static final Logger LOG = LoggerFactory.getLogger(ContextContentsContentProvider.class);

  public static final class SynchronizeContentsListener implements ContextListener {
    private final EventList<Object> contents;
    private final Context context;

    public SynchronizeContentsListener(Context context, EventList<Object> contents) {
      this.context = context;
      this.contents = contents;
    }

    @Override
    public void contextChanged(Context source) {
      contents.getReadWriteLock().writeLock().lock();
      try {
        // we are not using the Context source here as this might actually be a child context. 
        EventListSync.synchronize(context, contents);
      } finally {
        contents.getReadWriteLock().writeLock().unlock();
      }
    }
  }

  /**
   * This is a marker node that allows us to have a subnode beneath Context.
   */
  public static final class ContextContentsNode {
    private final Context context;

    public ContextContentsNode(Context context) {
      super();
      this.context = context;
    }

    public Context getContext() {
      return context;
    }
  }


  @Override
  public boolean hasChildren(TreePath treePath) {
    Object lastPathComponent = treePath.getLastPathComponent();
    return lastPathComponent instanceof Context || lastPathComponent instanceof ContextContentsNode;
  }

  @Override
  public EventList<? extends Object> getChildren(TreePath treePath) {
    Object lastPathComponent = treePath.getLastPathComponent();

    if (lastPathComponent instanceof Context) {
      // the easy part, we have a static list of "markers"
      Context context = (Context) lastPathComponent;
      return GlazedLists.eventListOf( //
      new ContextContentsNode(context) //
      );
    } else if (lastPathComponent instanceof ContextContentsNode) {

      Context context = ((ContextContentsNode) lastPathComponent).getContext();
      final EventList<Object> contents = new BasicEventList<Object>();

      // initial generation of the context content list
      for (Object contextContent : context) {
        contents.add(contextContent);
      }

      context.addContextChangeListener(new SynchronizeContentsListener(context, contents));

      return contents;
    }

    // should never happen, as we're only asked for children on object for which we returned true in
    // hasChildren.
    return null;
  }

  @Override
  public void updateLabel(TreePath treePath, StyledDocument doc) throws BadLocationException {

    Object lastPathComponent = treePath.getLastPathComponent();
    if (lastPathComponent instanceof ContextContentsNode) {
      doc.insertString(doc.getLength(), "Contents", null);
    }
  }

  private Icon contextContentsIcon;

  @Override
  public Icon getIcon(TreePath treePath) {

    if (treePath.getLastPathComponent() instanceof ContextContentsNode) {
      if (contextContentsIcon == null) {
        contextContentsIcon = loadIcon("/icons/context-contents.png");
      }
      return contextContentsIcon;
    }

    return null;
  }

}
