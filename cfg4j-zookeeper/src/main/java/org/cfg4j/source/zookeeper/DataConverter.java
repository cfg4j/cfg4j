/*
 *  Copyright 2019 secondriver (secondriver@yeah.net)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.cfg4j.source.zookeeper;

/**
 * Zookeeper ZNode byte[] data convert to String type
 */
public interface DataConverter {
  
  /**
   * Convert ZNode byte[] data to String
   *
   * @param data ZNode data
   * @return ZNode String type data
   */
  String convert(byte[] data);
}
