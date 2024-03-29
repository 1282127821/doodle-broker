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
package org.doodle.broker.server.context;

import org.doodle.broker.server.BrokerServer;
import org.springframework.context.ApplicationEvent;

/** Broker 服务启动发布事件 */
public class BrokerServerInitializedEvent extends ApplicationEvent {

  public BrokerServerInitializedEvent(BrokerServer brokerServer) {
    super(brokerServer);
  }

  public BrokerServer getServer() {
    return getSource();
  }

  @Override
  public BrokerServer getSource() {
    return (BrokerServer) super.getSource();
  }
}
