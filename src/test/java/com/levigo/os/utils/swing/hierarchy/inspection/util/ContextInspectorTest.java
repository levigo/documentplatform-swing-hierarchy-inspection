package com.levigo.os.utils.swing.hierarchy.inspection.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JLabel;

import org.jadice.util.swing.action.context.Context;
import org.junit.Test;


public class ContextInspectorTest {
  @Test
  public void testIsAvailable() throws Exception {
    assertTrue(ContextInspector.INSTANCE.isEnabled());

    Context ctx = Context.install(new JLabel(), Context.Children.ALL, Context.Ancestors.PARENT);

    assertEquals(Context.Children.ALL, ContextInspector.INSTANCE.getChildAggregation(ctx));
    assertEquals(Context.Ancestors.PARENT, ContextInspector.INSTANCE.getAncestorAggregation(ctx));
  }
}
