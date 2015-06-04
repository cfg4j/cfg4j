/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
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
package pl.nort.config.source.refresh;

/**
 * Defines a refresh strategy. An object to be refreshed will be passed through {@link #init(Refreshable)} method.
 * To refresh it invoke {@link Refreshable#refresh()} method.
 * Call to the {@link #shutdown()} method indicates that the {@link Refreshable#refresh()} should not be called ever again.
 */
public interface RefreshStrategy {

  /**
   * Call to this method indicates that the strategy should be initialized and run (if applicable). Provided {@code resource}
   * can be refreshed when necessary through a call to the {@link Refreshable#refresh()} method.
   *
   * @param resource resource to be refreshed
   */
  void init(Refreshable resource);

  /**
   * Call to this method inidicates that the resource provided in {@link #init(Refreshable)} call should not be refreshed ever again.
   */
  void shutdown();

}
