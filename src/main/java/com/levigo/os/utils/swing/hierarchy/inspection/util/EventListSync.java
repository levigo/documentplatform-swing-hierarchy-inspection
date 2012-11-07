package com.levigo.os.utils.swing.hierarchy.inspection.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.levigo.util.base.glazedlists.EventList;

/**
 * A collection of utility methods to synchronize {@link EventList}s with other {@link Collection}
 * like objects.
 */
public class EventListSync {
  /**
   * Synchronize the given source {@link Iterable} with the target {@link EventList}.
   * <p>
   * <b>NOTE:</b> This method will not acquire a {@link EventList#getReadWriteLock() lock on the
   * Event List}. Locking has to be done in the calling code.
   * 
   * @param source the source {@link Iterable} which has the contents that shall be synchronized
   *          into the target {@link EventList}
   * @param target the target {@link EventList} for which the contents shall be synchronized to the
   *          source {@link Iterable}s contents.
   */
  public static <T> void synchronize(Iterable<? extends T> source, EventList<T> target) {

    if (target.isEmpty()) {
      // a simple shortcut. We only have to all elements in to the source to the target
      for (T o : source)
        target.add(o);
    } else {
      // the slightly more complicated way. We have to go through all elements

      final List<T> sourceList = new ArrayList<T>();

      // first step: check if there are objects in the source, that are not in the target list.
      for (T s : source) {

        if (!target.contains(s)) {
          target.add(s);
        }

        // adding the elements from the iterable to the sourceList for use in the second step
        sourceList.add(s);

      }

      // second step: Walk through all objects in the target list and check whether they exist
      // on the source side

      final List<T> entriesToRemove = new ArrayList<T>();

      for (int i = 0; i < target.size(); i++) {

        T t = target.get(i);
        if (!sourceList.contains(t)) {
          entriesToRemove.add(t);
        }
      }

      if (entriesToRemove.size() > 0)
        target.removeAll(entriesToRemove);
    }
  }

}
