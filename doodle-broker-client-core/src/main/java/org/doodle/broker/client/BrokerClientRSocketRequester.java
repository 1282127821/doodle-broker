/*
 * Copyright (c) 2022-present Doodle. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.doodle.broker.client;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import lombok.RequiredArgsConstructor;
import org.doodle.design.broker.rsocket.BrokerRSocketRequester;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;

@RequiredArgsConstructor
public class BrokerClientRSocketRequester implements BrokerRSocketRequester {

  private final RSocketRequester requester;

  @Override
  public RSocketClient rsocketClient() {
    return this.requester.rsocketClient();
  }

  @Override
  public RSocket rsocket() {
    return this.requester.rsocket();
  }

  @Override
  public MimeType dataMimeType() {
    return this.requester.dataMimeType();
  }

  @Override
  public MimeType metadataMimeType() {
    return this.requester.metadataMimeType();
  }

  @Override
  public RSocketStrategies strategies() {
    return this.requester.strategies();
  }

  @Override
  public RequestSpec route(String route, Object... routeVars) {
    return this.requester.route(route, routeVars);
  }

  @Override
  public RequestSpec metadata(Object metadata, MimeType mimeType) {
    return this.requester.metadata(metadata, mimeType);
  }
}
