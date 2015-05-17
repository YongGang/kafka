/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.kafka.common.network;

import java.nio.channels.SocketChannel;
import java.util.Map;

import org.apache.kafka.common.security.auth.PrincipalBuilder;
import org.apache.kafka.common.config.SecurityConfigs;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.common.KafkaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlainTextChannelBuilder implements ChannelBuilder {
    private static final Logger log = LoggerFactory.getLogger(PlainTextChannelBuilder.class);
    private PrincipalBuilder principalBuilder;

    public void configure(Map<String, ?> configs) throws KafkaException {
        try {
            this.principalBuilder = (PrincipalBuilder) Utils.newInstance((Class<?>) configs.get(SecurityConfigs.PRINCIPAL_BUILDER_CLASS_CONFIG));
            this.principalBuilder.configure(configs);
        } catch (Exception e) {
            throw new KafkaException(e);
        }
    }

    public Channel buildChannel(SocketChannel socketChannel) throws KafkaException {
        Channel channel = null;
        try {
            PlainTextTransportLayer transportLayer = new PlainTextTransportLayer(socketChannel);
            Authenticator authenticator = new DefaultAuthenticator(transportLayer, this.principalBuilder);
            channel = new Channel(transportLayer, authenticator);
        } catch (Exception e) {
            log.warn("Failed to create channel due to ", e);
            throw new KafkaException(e);
        }
        return channel;
    }

    public void close() {
        this.principalBuilder.close();
    }

}
