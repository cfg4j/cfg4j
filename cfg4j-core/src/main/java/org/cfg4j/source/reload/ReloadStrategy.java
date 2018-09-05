/*
 * Copyright 2015-2018 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfg4j.source.reload;

/**
 * Defines a reload strategy. An object to be reloaded will be passed through {@link #register(Reloadable)} method.
 * To reload it invoke {@link Reloadable#reload()} method.
 * Call to the {@link #deregister(Reloadable)} method indicates that the {@link Reloadable#reload()} should not be called ever again.
 */
public interface ReloadStrategy {

  /**
   * Register a {@link Reloadable} resource with this strategy. It should take control of reloading the {@code resource}. Strategy should
   * invoke {@link Reloadable#reload()} method to reload the resource.
   *
   * @param resource resource to be registered
   */
  void register(Reloadable resource);

  /**
   * De-register {@link Reloadable} resource from this strategy. Call to this method indicates that the resource
   * should not be reloaded anymore by this strategy.
   *
   * @param resource resource to be deregistered
   */
  void deregister(Reloadable resource);

}
