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
package org.doodle.broker.autoconfigure.server;

import org.doodle.broker.autoconfigure.rsocket.BrokerRSocketAutoConfiguration;
import org.doodle.broker.server.BrokerServerAcceptor;
import org.doodle.broker.server.BrokerServerFactory;
import org.doodle.broker.server.BrokerServerProperties;
import org.doodle.broker.server.context.BrokerServerBootstrap;
import org.doodle.broker.server.netty.NettyBrokerServerFactory;
import org.doodle.design.broker.frame.BrokerFrame;
import org.doodle.design.broker.frame.BrokerFrameExtractor;
import org.doodle.design.broker.frame.BrokerFrameMimeTypes;
import org.doodle.design.broker.rsocket.BrokerRSocketIndex;
import org.doodle.design.broker.rsocket.BrokerRoutingRSocketFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.rsocket.netty.NettyRSocketServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.DefaultMetadataExtractor;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketStrategies;

@AutoConfiguration(after = BrokerRSocketAutoConfiguration.class)
@ConditionalOnClass(BrokerServerProperties.class)
@ConditionalOnBean(BrokerFrameExtractor.class)
@EnableConfigurationProperties(BrokerServerProperties.class)
public class BrokerServerAutoConfiguration {

  public BrokerServerAutoConfiguration(RSocketStrategies strategies) {
    MetadataExtractor metadataExtractor = strategies.metadataExtractor();
    if (metadataExtractor instanceof DefaultMetadataExtractor extractor) {
      extractor.metadataToExtract(
          BrokerFrameMimeTypes.BROKER_FRAME_MIME_TYPE,
          BrokerFrame.class,
          BrokerFrameMimeTypes.BROKER_FRAME_METADATA_KEY);
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerServerAcceptor brokerServerAcceptor(
      BrokerFrameExtractor frameExtractor,
      BrokerRoutingRSocketFactory factory,
      BrokerRSocketIndex rSocketIndex) {
    return new BrokerServerAcceptor(frameExtractor, factory, rSocketIndex);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerServerFactory brokerServerFactory(BrokerServerProperties properties) {
    NettyRSocketServerFactory serverFactory = new NettyRSocketServerFactory();
    PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
    mapper.from(properties::getPort).to(serverFactory::setPort);
    mapper.from(properties::getAddress).to(serverFactory::setAddress);
    mapper.from(properties::getTransport).to(serverFactory::setTransport);
    mapper.from(properties::getFragmentSize).to(serverFactory::setFragmentSize);
    mapper.from(properties::getSsl).to(serverFactory::setSsl);
    return new NettyBrokerServerFactory(serverFactory);
  }

  @Bean
  @ConditionalOnMissingBean
  public BrokerServerBootstrap brokerServerBootstrap(
      BrokerServerFactory serverFactory, BrokerServerAcceptor serverAcceptor) {
    return new BrokerServerBootstrap(serverFactory, serverAcceptor);
  }
}
