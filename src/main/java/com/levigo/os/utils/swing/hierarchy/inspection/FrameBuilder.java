/**
 * <pre>
 * Copyright (c) 1995-2014 levigo holding gmbh. All Rights Reserved.
 * 
 * This software is the proprietary information of levigo holding gmbh.
 * Use is subject to license terms.
 * </pre>
 */
package com.levigo.os.utils.swing.hierarchy.inspection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;

/**
 * A simple fluent API for building Hierarchy Inspection Frames. After constructing an instance,
 * various parameters may be set using the fluent methods. (If none are set, defaults are used.)
 * After all Parameters are adjusted as desired, the {@link FrameBuilder#buildFor(Component)} method
 * can be used to build hierarchy inspection frames.
 */
public class FrameBuilder {

  /**
   * Signifies the kind of application to be inspected. Depending on this type, different inspection
   * panels will be created.
   */
  public static enum ApplicationType {
    /**
     * signifies an inspection panel to be used in conjunction with jadice-based applications
     * 
     * @see JadiceSwingHierarchyInspectionPanel for the type of panel to be constructed
     */
    JADICE,

    /**
     * signifies an inspection panel to be used for generic swing applications
     * 
     * @see DefaultSwingHierarchyInspectionPanel for the type of panel to be constructed
     */
    SWING
  }


  private Dimension size;

  private String name;

  private ApplicationType type;

  public FrameBuilder() {
    size = new Dimension(500, 800);
    name = "Hierarchy";
  }

  public FrameBuilder sized(Dimension size) {
    this.size = size;
    return this;
  }

  public FrameBuilder sized(int width, int height) {
    return sized(new Dimension(width, height));
  }

  public FrameBuilder named(String name) {
    this.name = name;
    return this;
  }

  public FrameBuilder forType(ApplicationType type) {
    this.type = type;
    return this;
  }


  public Frame buildFor(Component toBeInspected) {
    final SwingHierarchyInspectionPanel panel = buildPanel(toBeInspected);
    final Frame frame = new Frame(name);
    frame.setLayout(new BorderLayout());
    frame.add(panel, BorderLayout.CENTER);
    frame.setSize(size);
    return frame;
  }

  protected SwingHierarchyInspectionPanel buildPanel(Component toBeInspected) {
    final SwingHierarchyInspectionPanel panel;
    switch (type){
      case JADICE :
        panel = new JadiceSwingHierarchyInspectionPanel(toBeInspected);
        break;

      case SWING :
      default :
        panel = new DefaultSwingHierarchyInspectionPanel(toBeInspected);
        break;
    }
    return panel;
  }

}
