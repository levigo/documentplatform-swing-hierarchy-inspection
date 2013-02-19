package com.levigo.os.utils.swing.hierarchy.inspection.marshaller;

public interface MarshallerCallback {

  void referenceChild(Object child);

  void traverseChild(Object child);

}