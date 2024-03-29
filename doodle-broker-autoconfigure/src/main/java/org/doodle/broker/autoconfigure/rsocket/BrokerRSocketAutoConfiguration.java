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

import com.google.protobuf.Message;
import org.doodle.boot.rsocket.transport.NettyRSocketClientTransportFactory;
import org.doodle.boot.rsocket.transport.RSocketClientTransportFactory;
import org.doodle.design.broker.frame.BrokerFrameExtractor;
import org.doodle.design.broker.rsocket.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;

@AutoConfiguration(after = BrokerRSocketStrategiesAutoConfiguration.class)
@ConditionalOnClass({
  Message.class,
  RSocketStrategies.class,
  ProtobufEncoder.class,
  ProtobufDecoder.class
})
public class BrokerRSocketAutoConfiguration {

  @Bean
  public RSocketClientTransportFactory rSocketClientTransportFactory() {
    return new NettyRSocketClientTransportFactory();
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerFrameExtractor brokerFrameExtractor(RSocketStrategies strategies) {
    return new BrokerFrameExtractor(strategies);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerRSocketIndex brokerRSocketIndex() {
    return new BrokerRSocketIndex();
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerRSocketQuery brokerRSocketQuery(BrokerRSocketIndex rSocketIndex) {
    return new CombinedBrokerRSocketQuery(rSocketIndex);
  }

  @Bean
  @ConditionalOnMissingBean
  public UnicastBrokerRSocketLocator unicastBrokerRSocketLocator(BrokerRSocketQuery query) {
    return new UnicastBrokerRSocketLocator(query);
  }

  @Bean
  @ConditionalOnMissingBean
  public MulticastBrokerRSocketLocator multicastBrokerRSocketLocator(BrokerRSocketQuery query) {
    return new MulticastBrokerRSocketLocator(query);
  }

  @Bean
  @ConditionalOnMissingBean
  public CompositeBrokerRSocketLocator compositeBrokerRSocketLocator(
      ObjectProvider<BrokerRSocketLocator> provider) {
    return new CompositeBrokerRSocketLocator(provider.orderedStream().toList());
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerRoutingRSocketFactory brokerRoutingRSocketFactory(
      CompositeBrokerRSocketLocator locator, BrokerFrameExtractor frameExtractor) {
    return new BrokerRoutingRSocketFactory(locator, frameExtractor);
  }
}
