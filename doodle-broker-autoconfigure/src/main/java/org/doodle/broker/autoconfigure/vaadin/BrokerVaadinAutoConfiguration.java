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
package org.doodle.broker.autoconfigure.vaadin;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;
import org.doodle.boot.vaadin.EnableVaadin;
import org.doodle.boot.vaadin.views.TabSupplier;
import org.doodle.broker.vaadin.BrokerVaadinProperties;
import org.doodle.broker.vaadin.views.BrokerVaadinView;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(BrokerVaadinProperties.class)
@EnableConfigurationProperties(BrokerVaadinProperties.class)
@EnableVaadin(BrokerVaadinProperties.PREFIX_VIEWS)
public class BrokerVaadinAutoConfiguration {

  @Bean
  public TabSupplier brokerTabView() {
    return (authenticationContext) ->
        new Tab(VaadinIcon.CONNECT_O.create(), new RouterLink("路由网关", BrokerVaadinView.class));
  }
}
