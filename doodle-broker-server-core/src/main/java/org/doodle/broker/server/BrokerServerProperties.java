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
package org.doodle.broker.server;

import java.net.InetAddress;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.web.server.Ssl;
import org.springframework.util.unit.DataSize;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = BrokerServerProperties.PREFIX)
public class BrokerServerProperties {
  public static final String PREFIX = "doodle.broker.server";

  Integer port = 9899;

  InetAddress address;

  RSocketServer.Transport transport = RSocketServer.Transport.TCP;

  DataSize fragmentSize;

  @NestedConfigurationProperty Ssl ssl;
}
