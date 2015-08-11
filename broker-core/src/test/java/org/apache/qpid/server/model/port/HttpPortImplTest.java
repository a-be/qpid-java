/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.qpid.server.model.port;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.qpid.server.configuration.IllegalConfigurationException;
import org.apache.qpid.server.configuration.updater.CurrentThreadTaskExecutor;
import org.apache.qpid.server.configuration.updater.TaskExecutor;
import org.apache.qpid.server.logging.EventLogger;
import org.apache.qpid.server.logging.LogMessage;
import org.apache.qpid.server.logging.LogSubject;
import org.apache.qpid.server.model.AuthenticationProvider;
import org.apache.qpid.server.model.Broker;
import org.apache.qpid.server.model.BrokerModel;
import org.apache.qpid.server.model.Model;
import org.apache.qpid.server.security.SecurityManager;
import org.apache.qpid.test.utils.QpidTestCase;

public class HttpPortImplTest extends QpidTestCase
{
    private static final String AUTHENTICATION_PROVIDER_NAME = "test";

    private TaskExecutor _taskExecutor;
    private Broker _broker;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        _taskExecutor = CurrentThreadTaskExecutor.newStartedInstance();
        Model model = BrokerModel.getInstance();

        _broker = mock(Broker.class);
        when(_broker.getTaskExecutor()).thenReturn(_taskExecutor);
        when(_broker.getChildExecutor()).thenReturn(_taskExecutor);
        when(_broker.getModel()).thenReturn(model);
        when(_broker.getEventLogger()).thenReturn(new EventLogger());
        when(_broker.getCategoryClass()).thenReturn(Broker.class);
        when(_broker.getSecurityManager()).thenReturn(new SecurityManager(_broker, false));

        AuthenticationProvider<?> provider = mock(AuthenticationProvider.class);
        when(provider.getName()).thenReturn(AUTHENTICATION_PROVIDER_NAME);
        when(provider.getParent(Broker.class)).thenReturn(_broker);
        when(provider.getMechanisms()).thenReturn(Arrays.asList("PLAIN"));
        when(_broker.getChildren(AuthenticationProvider.class)).thenReturn(Collections.<AuthenticationProvider>singleton(
                provider));
        when(_broker.getChildByName(AuthenticationProvider.class, AUTHENTICATION_PROVIDER_NAME)).thenReturn(provider);

    }

    public void testCreateWithIllegalThreadPoolValues() throws Exception
    {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(HttpPort.PORT, 10000);
        attributes.put(HttpPort.NAME, getTestName());
        attributes.put(HttpPort.THREAD_POOL_MINIMUM, 51);
        attributes.put(HttpPort.THREAD_POOL_MAXIMUM, 50);
        attributes.put(HttpPort.AUTHENTICATION_PROVIDER, AUTHENTICATION_PROVIDER_NAME);


        HttpPortImpl port = new HttpPortImpl(attributes, _broker);
        try
        {
            port.create();
            fail("Creation should fail due to validation check");
        }
        catch (IllegalConfigurationException e)
        {
            // PASS
        }
    }

    public void testChangeWithIllegalThreadPoolValues() throws Exception
    {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(HttpPort.PORT, 10000);
        attributes.put(HttpPort.NAME, getTestName());
        attributes.put(HttpPort.AUTHENTICATION_PROVIDER, AUTHENTICATION_PROVIDER_NAME);


        HttpPortImpl port = new HttpPortImpl(attributes, _broker);
        port.create();

        final Map<String, Object> updates = new HashMap<>();
        updates.put(HttpPort.THREAD_POOL_MINIMUM, 51);
        updates.put(HttpPort.THREAD_POOL_MAXIMUM, 50);
        try
        {
            port.setAttributes(updates);
            fail("Change should fail due to validation check");
        }
        catch (IllegalConfigurationException e)
        {
            // PASS
        }
    }

}
