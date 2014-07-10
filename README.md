Hierarchy Inspection
====================

Provides a tree view of the components in a Java Swing-based application. If this application uses the jadice GUI Context framework, it is possible to display Context contents and state information.

Requirements
------------
* Maven 3 for building the project
* levigo-utils swing module
  * This module is part of the levigo jadice application suite for which a valid license is necessary.
  * This module provides the flex tree feature which is used to build the hierarchy tree

Usage
-----

* Download the source and integrate it as a project into your IDE. Alternatively, run a build via maven and put the resulting jar on your application's class path.
* Instantiate and show Inspection Frames for every Component you would like to inspect.

Example
-------
Produce an inspection frame for a regular Swing Application JFrame and set it visible. As it is the case for any Swing code, this example needs to be run on the Event Dispatch Thread.
```java
JFrame frameToBeInspected = /* ... */;

Frame hierarchyFrame = new FrameBuilder()
  .forType(ApplicationType.SWING)
  .named("Swing Hierarchy")
  .sized(800, 600)
  .buildFor(frameToBeInspected);

hierarchyFrame.setVisible(true);
```

