package com.levigo.os.utils.swing.hierarchy.inspection.jadice;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.tree.TreePath;

import com.levigo.os.utils.swing.hierarchy.inspection.AbstractIconLoader;
import com.levigo.util.base.glazedlists.EventList;
import com.levigo.util.base.glazedlists.GlazedLists;
import com.levigo.util.swing.action.Context;
import com.levigo.util.swing.action.ContextListener;
import com.levigo.util.swing.flextree.TreeContentProvider;
import com.levigo.util.swing.flextree.TreeIconProvider;
import com.levigo.util.swing.flextree.TreeLabelProvider;

public class ContextOwnerContentProvider extends AbstractIconLoader implements TreeContentProvider, TreeLabelProvider, TreeIconProvider {

  public static final class SynchronizeContentsListener implements ContextListener {
    private final EventList<Object> contents;

    public SynchronizeContentsListener(EventList<Object> contents) {
      this.contents = contents;
    }

    @Override
    public void contextChanged(Context source) {

      synchronize(source, contents);
    }

    protected void synchronize(Iterable<Object> source, EventList<Object> target) {

      final List<Object> sourceList = new ArrayList<Object>();

      // first step: check if there are objects in the source, that are not in the target list.
      for (Object s : source) {

        if (!target.contains(s)) {
          target.add(s);
        }

        // adding the elements from the iterable to the sourceList for use in the second step
        sourceList.add(s);

      }

      // second step: Walk through all objects in the target list and check whether they exist
      // on the source side
      for (int i = 0; i < target.size(); i++) {

        Object t = target.get(i);
        if (!sourceList.contains(t)) {
          target.remove(i);
        }
      }
    }
  }

  public static final class UpdateOwnerListener implements ContextListener {
    private final EventList<JComponent> ownerList;

    public UpdateOwnerListener(EventList<JComponent> ownerList) {
      this.ownerList = ownerList;
    }

    @Override
    public void contextChanged(Context source) {

      ownerList.getReadWriteLock().writeLock().lock();
      try {
        if (ownerList.size() != 1) {
          ownerList.clear();
          ownerList.add(source.getOwner());
        } else {
          if (ownerList.get(0) != source.getOwner()) {
            ownerList.set(0, source.getOwner());
          }
        }
      } finally {
        ownerList.getReadWriteLock().writeLock().unlock();
      }

    }
  }

  /**
   * This is a marker node that allows us to have a subnode beneath Context.
   */
  public static final class ContextOwnerNode {
    private final Context context;

    public ContextOwnerNode(Context context) {
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
    return lastPathComponent instanceof Context || lastPathComponent instanceof ContextOwnerNode;
  }

  @Override
  public EventList<? extends Object> getChildren(TreePath treePath) {
    Object lastPathComponent = treePath.getLastPathComponent();

    if (lastPathComponent instanceof Context) {
      // the easy part, we have a static list of "markers"
      Context context = (Context) lastPathComponent;
      return GlazedLists.eventListOf( //
      new ContextOwnerNode(context) //
      );
    } else if (lastPathComponent instanceof ContextOwnerNode) {

      Context context = ((ContextOwnerNode) lastPathComponent).getContext();
      context.addContextChangeListener(new UpdateOwnerListener(GlazedLists.eventListOf(context.getOwner())));

      return GlazedLists.eventListOf(context.getOwner());
    }

    // should never happen, as we're only asked for children on object for which we returned true in
    // hasChildren.
    return null;
  }

  @Override
  public void updateLabel(TreePath treePath, StyledDocument doc) throws BadLocationException {

    Object lastPathComponent = treePath.getLastPathComponent();
    if (lastPathComponent instanceof ContextOwnerNode) {
      doc.insertString(doc.getLength(), "Owner", null);
    }
  }

  private Icon contextOwnerIcon;

  @Override
  public Icon getIcon(TreePath treePath) {

    if (treePath.getLastPathComponent() instanceof ContextOwnerNode) {
      if (contextOwnerIcon == null) {
        contextOwnerIcon = loadIcon("/icons/context-owner.png");
      }
      return contextOwnerIcon;
    }
    return null;
  }

}
