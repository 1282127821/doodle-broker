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
package org.doodle.broker.autoconfigure.rsocket;

import org.doodle.design.broker.frame.BrokerFrameDecoder;
import org.doodle.design.broker.frame.BrokerFrameEncoder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = RSocketStrategiesAutoConfiguration.class)
public class BrokerRSocketStrategiesAutoConfiguration {
  @Bean
  public RSocketStrategiesCustomizer brokerFrameRSocketStrategiesCustomizer() {
    return (strategies) ->
        strategies.encoder(new BrokerFrameEncoder()).decoder(new BrokerFrameDecoder());
  }
}
