package com.levigo.os.utils.swing.hierarchy.inspection;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.levigo.util.log.Logger;
import com.levigo.util.log.LoggerFactory;
import com.levigo.util.swing.flextree.TreeIconProvider;

/**
 * Utility class to load icons from the classpath. This class is commonly used by
 * {@link TreeIconProvider} implementations to provide the icons for a tree node.
 */
public abstract class AbstractIconLoader {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractIconLoader.class);

  protected Icon loadIcon(String path) {
    Icon icon = null;
    try {
      URL res = getClass().getResource(path);

      if (res == null) {
        LOG.debug("Requested icon not found. Path '" + path + "'");
        return null;
      }

      BufferedImage image = ImageIO.read(res);
      if (image != null) {
        icon = new ImageIcon(image);
      }
    } catch (IOException e) {
      LOG.error("Failed to load icon '" + path + "'", e);
    }
    return icon;
  }
}
