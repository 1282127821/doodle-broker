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
package org.doodle.broker.autoconfigure.client;

import io.rsocket.transport.ClientTransport;
import java.net.URI;
import org.doodle.boot.rsocket.transport.RSocketClientTransportFactory;
import org.doodle.broker.autoconfigure.rsocket.BrokerRSocketAutoConfiguration;
import org.doodle.broker.client.BrokerClientProperties;
import org.doodle.broker.client.BrokerClientRSocketRequester;
import org.doodle.broker.client.BrokerClientRSocketRequesterBuilder;
import org.doodle.design.broker.frame.BrokerFrameMimeTypes;
import org.doodle.design.broker.frame.BrokerFrameUtils;
import org.doodle.design.broker.rsocket.BrokerRSocketRequester;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

@AutoConfiguration(after = BrokerRSocketAutoConfiguration.class)
@ConditionalOnClass(BrokerClientProperties.class)
@EnableConfigurationProperties(BrokerClientProperties.class)
public class BrokerClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketRequesterBuilder brokerClientRSocketRequesterBuilder(
      BrokerClientProperties properties,
      RSocketStrategies strategies,
      RSocketConnectorConfigurer configurer) {
    RSocketRequester.Builder builder =
        RSocketRequester.builder()
            .setupMetadata(
                BrokerFrameUtils.setup(properties.getTags()),
                BrokerFrameMimeTypes.BROKER_FRAME_MIME_TYPE)
            .dataMimeType(properties.getDataMimeType())
            .rsocketStrategies(strategies)
            .rsocketConnector(configurer);
    return new BrokerClientRSocketRequesterBuilder(builder);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = BrokerClientProperties.PREFIX, name = ".enabled")
  public BrokerClientRSocketRequester brokerClientRSocketRequester(
      BrokerRSocketRequester.Builder builder,
      BrokerClientProperties properties,
      ObjectProvider<RSocketClientTransportFactory> provider) {
    URI uri = properties.getUri();
    ClientTransport clientTransport =
        provider
            .orderedStream()
            .filter(factory -> factory.supports(uri))
            .findFirst()
            .map(factory -> factory.create(uri))
            .orElseThrow(() -> new IllegalStateException("找不到对应的驱动: " + uri));
    return new BrokerClientRSocketRequester(builder.transport(clientTransport));
  }
}
