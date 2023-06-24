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

import io.rsocket.RSocket;
import io.rsocket.transport.ClientTransport;
import java.net.URI;
import java.time.Duration;
import org.doodle.boot.rsocket.transport.RSocketClientTransportFactory;
import org.doodle.broker.autoconfigure.rsocket.BrokerRSocketAutoConfiguration;
import org.doodle.broker.client.*;
import org.doodle.design.broker.frame.BrokerFrameMimeTypes;
import org.doodle.design.broker.frame.BrokerFrameUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.util.retry.Retry;

@AutoConfiguration(after = BrokerRSocketAutoConfiguration.class)
@ConditionalOnClass({BrokerClientProperties.class, RSocketRequester.class, RSocketStrategies.class})
@EnableConfigurationProperties(BrokerClientProperties.class)
@ConditionalOnProperty(prefix = BrokerClientProperties.PREFIX, name = "enabled")
public class BrokerClientAutoConfiguration {
  @Bean
  public BrokerClientRSocketConnectorCustomizer brokerClientMessageHandlerCustomizer(
      RSocketMessageHandler messageHandler) {
    return connector -> connector.acceptor(messageHandler.responder());
  }

  @Bean
  public BrokerClientRSocketConnectorCustomizer brokerClientReconnectRetryCustomizer() {
    return connector ->
        connector.reconnect(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2)));
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketConnectorConfigurer brokerClientRSocketConnectorConfigurer(
      ObjectProvider<BrokerClientRSocketConnectorCustomizer> provider) {
    return connector ->
        provider.orderedStream().forEach(customizer -> customizer.customize(connector));
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketRequesterBuilder brokerClientRSocketRequesterBuilder(
      BrokerClientProperties properties,
      RSocketStrategies strategies,
      BrokerClientRSocketConnectorConfigurer connectorConfigurer) {
    return new BrokerClientRSocketRequesterBuilder(
        RSocketRequester.builder()
            .setupMetadata(
                BrokerFrameUtils.setup(properties.getTags()),
                BrokerFrameMimeTypes.BROKER_FRAME_MIME_TYPE)
            .dataMimeType(properties.getDataMimeType())
            .rsocketStrategies(strategies)
            .rsocketConnector(connectorConfigurer));
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = BrokerClientProperties.PREFIX, name = "auto-connected")
  public BrokerClientRSocketRequester brokerClientRSocketRequester(
      BrokerClientRSocketRequesterBuilder builder,
      BrokerClientProperties properties,
      ObjectProvider<RSocketClientTransportFactory> provider) {
    URI uri = properties.getServer().getUri();
    ClientTransport clientTransport =
        provider
            .orderedStream()
            .filter(factory -> factory.supports(uri))
            .findFirst()
            .map(factory -> factory.create(uri))
            .orElseThrow(() -> new IllegalStateException("找不到对应的驱动: " + uri));
    BrokerClientRSocketRequester requester =
        new BrokerClientRSocketRequester(builder.transport(clientTransport));
    requester
        .rsocketClient()
        .source()
        .flatMap(RSocket::onClose)
        .repeat()
        .retryWhen(Retry.indefinitely())
        .delayElements(Duration.ofSeconds(2))
        .subscribe();
    return requester;
  }
}
