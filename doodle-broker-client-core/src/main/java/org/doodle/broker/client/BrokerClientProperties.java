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
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@Data
@ConfigurationProperties(prefix = BrokerClientProperties.PREFIX)
public class BrokerClientProperties {
  public static final String PREFIX = "doodle.broker.client";

  private final Map<String, String> tags = new LinkedHashMap<>();

  private MimeType dataMimeType = MimeTypeUtils.APPLICATION_JSON;

  private URI uri = URI.create("tcp://localhost:9891");

  private boolean autoConnect = true;
}
