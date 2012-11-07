package com.levigo.os.utils.swing.hierarchy.inspection;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

import com.levigo.util.swing.flextree.TreeIconProvider;

/**
 * A {@link TreeIconProvider} that will use the class name of an element in the tree to search for
 * appropriate icons. The lookup logic will replace any <code>'.'</code> in the class name with a
 * <code>'/'</code> and prefix it using <code>/icons/</code>. That means all icons on the classpath
 * within the icons folder will be considered. The following file extensions are supported:
 * <ul>
 * <li>.gif</li>
 * <li>.png</li>
 * </ul>
 */
public final class GenericTreeIconProvider extends AbstractIconLoader implements TreeIconProvider {
  private final Map<String, Icon> iconCache = new HashMap<String, Icon>();

  private static final String[] EXTENSIONS = {
      "gif", "png"
  };

  @Override
  public Icon getIcon(TreePath path) {

    Object object = path.getLastPathComponent();

    if (object == null)
      return null;

    Icon icon = null;

    Class<? extends Object> clazz = object.getClass();
    do {
      final String className = clazz.getName();

      icon = iconCache.get(className);

      if (icon == null) {
        final String baseFilename = "/icons/" + className.replace('.', '/');

        for (int i = 0; i < EXTENSIONS.length && icon == null; i++) {
          icon = loadIcon(baseFilename + "." + EXTENSIONS[i]);
        }
        
        if (icon != null) {
          // put this icon into the icon cache
          iconCache.put(className, icon);
        }
        
      }

      // prepare for the next run
      clazz = clazz.getSuperclass();
    } while (icon == null && clazz != null);

    return icon;
  }
}