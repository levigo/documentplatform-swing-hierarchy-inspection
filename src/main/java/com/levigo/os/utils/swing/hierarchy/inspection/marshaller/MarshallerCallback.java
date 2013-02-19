package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

public interface MarshallerCallback {

  void traverseChild(Object child);

  void referenceChild(Object child);

}