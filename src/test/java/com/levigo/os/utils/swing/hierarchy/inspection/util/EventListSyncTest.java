package com.levigo.os.utils.swing.hierarchy.inspection.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.levigo.util.base.glazedlists.BasicEventList;
import com.levigo.util.base.glazedlists.EventList;

public class EventListSyncTest {

  @Test
  public void testSyncEmptyLists() throws Exception {
    
    EventList<Object> target = new BasicEventList<Object>();
    
    List<Object> source = Arrays.asList();
    
    // this is simply testing that the logic will not fail
    EventListSync.synchronize(source, target);
    
  }
  
  @Test
  public void testSyncContentsToTarget() throws Exception {
    EventList<String> target = new BasicEventList<String>();
    
    List<String> source = Arrays.asList("one", "two", "three");
    
    
    EventListSync.synchronize(source, target);
    
    assertEquals(3, target.size());
    
    assertEquals("one", target.get(0));
    assertEquals("two", target.get(1));
    assertEquals("three", target.get(2));
    
  }
  
  @Test
  public void testSyncEmptyContentsToTarget() throws Exception {
    EventList<String> target = new BasicEventList<String>();
    target.addAll(Arrays.asList("one", "two", "three"));
    
    List<String> source = Arrays.asList();
    
    EventListSync.synchronize(source, target);
    
    assertEquals(0, target.size());
    
  }
  
  @Test
  public void testRemoveSingleEntryInTarget() throws Exception {
    EventList<String> target = new BasicEventList<String>();
    target.addAll(Arrays.asList("one", "two", "three"));
    
    List<String> source = Arrays.asList("one", "three");
    
    EventListSync.synchronize(source, target);
    
    assertEquals(2, target.size());
    
    assertEquals("one", target.get(0));
    assertEquals("three", target.get(1));
    
  }
  
}
