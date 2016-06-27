package org.cfg4j.provider.bind;

import org.cfg4j.provider.BindStrategy;

/**
 * Base class for bind strategies that depends on property prefix.
 */
public abstract class PrefixBasedBindStrategy implements BindStrategy {

  /**
   * Concatenates two prefix sub-path to correct property path
   * @param prefixHead first part of path
   * @param prefixTail last part of path
   * @return prefix
   */
  protected String buildPrefix(String prefixHead, String prefixTail) {
    return prefixHead + (prefixHead.isEmpty() ? "" : ".") + prefixTail;
  }
}
