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
package org.cfg4j.source.reload.strategy;

import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ReloadStrategy} that reloads the resource only once - the moment the {@link #register(Reloadable)} is called.
 */
public class ImmediateReloadStrategy implements ReloadStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(ImmediateReloadStrategy.class);

  @Override
  public void register(Reloadable resource) {
    LOG.debug("Registering resource " + resource);
    resource.reload();
  }

  @Override
  public void deregister(Reloadable resource) {
    LOG.debug("De-registering resource " + resource);
  }

  @Override
  public String toString() {
    return "ImmediateReloadStrategy{}";
  }
}
