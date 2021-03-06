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
 */

package org.apache.qpid.server.consumer;

import org.apache.qpid.server.message.MessageInstance;
import org.apache.qpid.server.message.MessageReference;

public class ConsumerMessageInstancePair
{
    private final ConsumerImpl _consumer;
    private final MessageInstance _entry;
    private final boolean _batch;
    private final MessageReference _reference;

    public ConsumerMessageInstancePair(final ConsumerImpl consumer, final MessageInstance entry, final boolean batch)
    {
        _consumer = consumer;
        _entry = entry;
        _batch = batch;
        _reference = entry.getMessage().newReference();

    }

    public ConsumerImpl getConsumer()
    {
        return _consumer;
    }

    public MessageInstance getEntry()
    {
        return _entry;
    }

    public boolean isBatch()
    {
        return _batch;
    }

    public void release()
    {
        _reference.release();
    }
}
