package org.cfg4j.source.zookeeper;

/**
 * Author: secondriver
 * Created: 2019/7/29
 */
public interface DataConverter {
  
  String convert(byte[] data);
}
