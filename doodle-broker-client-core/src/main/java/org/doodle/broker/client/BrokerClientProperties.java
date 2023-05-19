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

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = BrokerClientProperties.PREFIX)
public class BrokerClientProperties {
  public static final String PREFIX = "doodle.broker.client";

  final Map<String, String> tags = new LinkedHashMap<>();

  MimeType dataMimeType = MediaType.APPLICATION_PROTOBUF;

  final Server server = new Server();

  @Data
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Server {
    URI uri = URI.create("tcp://localhost:9899");
  }
}
