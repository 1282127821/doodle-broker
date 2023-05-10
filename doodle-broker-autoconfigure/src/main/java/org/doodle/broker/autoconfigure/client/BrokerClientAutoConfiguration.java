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
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.util.retry.Retry;

@AutoConfiguration(after = BrokerRSocketAutoConfiguration.class)
@ConditionalOnClass(BrokerClientProperties.class)
@EnableConfigurationProperties(BrokerClientProperties.class)
@ConditionalOnProperty(prefix = BrokerClientProperties.PREFIX, name = "enabled")
public class BrokerClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public BrokerClientRSocketRequesterBuilder brokerClientRSocketRequesterBuilder(
      BrokerClientProperties properties,
      RSocketMessageHandler messageHandler,
      RSocketStrategies strategies) {
    RSocketRequester.Builder builder =
        RSocketRequester.builder()
            .setupMetadata(
                BrokerFrameUtils.setup(properties.getTags()),
                BrokerFrameMimeTypes.BROKER_FRAME_MIME_TYPE)
            .dataMimeType(properties.getDataMimeType())
            .rsocketStrategies(strategies)
            .rsocketConnector(
                connector ->
                    connector
                        .acceptor(messageHandler.responder())
                        .reconnect(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))));
    return new BrokerClientRSocketRequesterBuilder(builder);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(prefix = BrokerClientProperties.PREFIX, name = "auto-connected")
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
