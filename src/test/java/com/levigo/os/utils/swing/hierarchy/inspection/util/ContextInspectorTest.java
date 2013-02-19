package com.levigo.os.utils.swing.hierarchy.inspection.util;

import static org.junit.Assert.*;

import javax.swing.JLabel;

import org.junit.Test;

import com.levigo.util.swing.action.Context;

public class ContextInspectorTest {
  @Test
  public void testIsAvailable() throws Exception {
    assertTrue(ContextInspector.INSTANCE.isEnabled());

    Context ctx = Context.install(new JLabel(), Context.Children.ALL, Context.Ancestors.PARENT);

    assertEquals(Context.Children.ALL, ContextInspector.INSTANCE.getChildAggregation(ctx));
    assertEquals(Context.Ancestors.PARENT, ContextInspector.INSTANCE.getAncestorAggregation(ctx));
  }
}
