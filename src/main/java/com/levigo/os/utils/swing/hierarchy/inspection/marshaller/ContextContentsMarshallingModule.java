package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

import javax.swing.JComponent;
import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;

import com.levigo.os.utils.swing.hierarchy.inspection.util.ContextInspector;
import com.levigo.util.swing.action.Context;


public final class ContextContentsMarshallingModule implements TreeContentMarshallingModule {

  public static final class ContextChildren {
    private final Context context;

    public ContextChildren(Context context) {
      super();
      this.context = context;
    }

    public Context getContext() {
      return context;
    }
  }

  public static final class ContextContents {
    private final Context context;

    public ContextContents(Context context) {
      super();
      this.context = context;
    }

    public Context getContext() {
      return context;
    }
  }

  public static final class ContextOwner {
    private final Context context;

    public ContextOwner(Context context) {
      super();
      this.context = context;
    }

    public Context getContext() {
      return context;
    }
  }

  @Override
  public QName getTagName(TreePath path) {
    if (path.getLastPathComponent() instanceof Context) {
      return Namespace.JADICE.createQName("Context");
    } else if (path.getLastPathComponent() instanceof ContextContentsMarshallingModule.ContextChildren) {
      return Namespace.JADICE.createQName("Children");
    } else if (path.getLastPathComponent() instanceof ContextContentsMarshallingModule.ContextOwner) {
      return Namespace.JADICE.createQName("Owner");
    } else if (path.getLastPathComponent() instanceof ContextContentsMarshallingModule.ContextContents) {
      return Namespace.JADICE.createQName("Contents");
    }
    return null;
  }

  @Override
  public void inspect(TreePath path, MarshallerCallback state) {

    if (path.getLastPathComponent() instanceof JComponent) {

      final Object context = ((JComponent) path.getLastPathComponent()).getClientProperty(Context.PROPERTY_KEY);

      if (context != null && context instanceof Context) {
        state.traverseChild(context);
      }

    } else if (path.getLastPathComponent() instanceof Context) {
      final Context context = (Context) path.getLastPathComponent();
      state.traverseChild(new ContextOwner(context));
      state.traverseChild(new ContextChildren(context));
      state.traverseChild(new ContextContents(context));
    } else if (path.getLastPathComponent() instanceof ContextContentsMarshallingModule.ContextOwner) {

      final Context context = ((ContextContentsMarshallingModule.ContextOwner) path.getLastPathComponent()).getContext();
      final JComponent owner = context.getOwner();
      state.referenceChild(owner);

    } else if (ContextInspector.INSTANCE.isEnabled()
        && path.getLastPathComponent() instanceof ContextContentsMarshallingModule.ContextChildren) {
      final Context context = ((ContextContentsMarshallingModule.ContextChildren) path.getLastPathComponent()).getContext();
      for (final Context c : ContextInspector.INSTANCE.getChildren(context)) {
        state.referenceChild(c);
      }
    } else if (path.getLastPathComponent() instanceof ContextContents) {
      final Context context = ((ContextContentsMarshallingModule.ContextContents) path.getLastPathComponent()).getContext();
      for (final Object child : context) {
        state.referenceChild(child);
      }
    }
  }

  @Override
  public void poulateAttributes(TreePath path, AttributeCallback callback) {
    if (ContextInspector.INSTANCE.isEnabled() && path.getLastPathComponent() instanceof Context) {
      final Context context = (Context) path.getLastPathComponent();
      callback.addAttribute(Namespace.JADICE.createQName("ancestor-aggregation"),
          "" + ContextInspector.INSTANCE.getAncestorAggregation(context));
      callback.addAttribute(Namespace.JADICE.createQName("child-aggregation"),
          "" + ContextInspector.INSTANCE.getChildAggregation(context));

    }
  }
}