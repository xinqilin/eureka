/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.rx.eureka.server.transport.tcp.replication;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.inject.Singleton;
import com.netflix.rx.eureka.registry.EurekaRegistry;
import com.netflix.rx.eureka.server.EurekaBootstrapConfig;
import com.netflix.rx.eureka.server.metric.EurekaServerMetricFactory;
import com.netflix.rx.eureka.server.transport.tcp.AbstractTcpServer;
import com.netflix.rx.eureka.transport.EurekaTransports;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.metrics.MetricEventsListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tomasz Bak
 */
@Singleton
public class TcpReplicationServer extends AbstractTcpServer {

    private static final Logger logger = LoggerFactory.getLogger(TcpReplicationServer.class);

    @Inject
    public TcpReplicationServer(EurekaBootstrapConfig config,
                                EurekaRegistry eurekaRegistry,
                                @Named("replication") MetricEventsListenerFactory servoEventsListenerFactory,
                                EurekaServerMetricFactory metricFactory) {
        super(eurekaRegistry, servoEventsListenerFactory, config, metricFactory);
    }

    @PostConstruct
    public void start() {
        server = RxNetty.newTcpServerBuilder(
                config.getReplicationPort(),
                new TcpReplicationHandler(eurekaRegistry, metricFactory))
                .pipelineConfigurator(EurekaTransports.replicationPipeline(config.getCodec()))
                .withMetricEventsListenerFactory(servoEventsListenerFactory)
                .build()
                .start();

        logger.info("Starting TCP replication server on port {}...", server.getServerPort());
    }

}
