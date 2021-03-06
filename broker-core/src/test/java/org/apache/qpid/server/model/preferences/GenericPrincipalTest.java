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

package org.apache.qpid.server.model.preferences;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.qpid.test.utils.QpidTestCase;

public class GenericPrincipalTest extends QpidTestCase
{
    private static final String UTF8 = StandardCharsets.UTF_8.name();
    private String _username;
    private String _originType;
    private String _originName;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        _username = "testuser";
        _originType = "authType";
        _originName = "authName";
    }

    public void testParseSimple() throws Exception
    {
        GenericPrincipal p = new GenericPrincipal(String.format("%s@%s('%s')", _username, _originType, _originName));
        assertEquals("unexpected principal name", _username, p.getName());
        assertEquals("unexpected origin type", _originType, p.getOriginType());
        assertEquals("unexpected origin name", _originName, p.getOriginName());
    }

    public void testNoOriginInfo() throws Exception
    {
        try
        {
            new GenericPrincipal("_usernameWithoutOriginInfo");
            fail("GenericPricinpal should reject names without origin info");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }
    }

    public void testRejectQuotes() throws Exception
    {
        final String usernameWithQuote = "_username'withQuote";
        final String originTypeWithQuote = "authType'withQuote";
        final String originNameWithQuote = "authName'withQuote";
        try
        {
            new GenericPrincipal(String.format("%s@%s('%s')", usernameWithQuote, _originType, _originName));
            fail("GenericPricinpal should reject _username with quotes");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }
        try
        {
            new GenericPrincipal(String.format("%s@%s('%s')", _username, originTypeWithQuote, _originName));
            fail("GenericPricinpal should reject origin type with quotes");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }
        try
        {
            new GenericPrincipal(String.format("%s@%s('%s')", _username, _originType, originNameWithQuote));
            fail("GenericPricinpal should reject origin name with quotes");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }

    }

    public void testRejectAtSign() throws Exception
    {
        final String _usernameWithAtSign = "_username@withAtSign";
        final String originTypeWithAtSign = "authType@withAtSign";
        final String originNameWithAtSign = "authName@withAtSign";
        try
        {
            new GenericPrincipal(String.format("%s@%s('%s')", _usernameWithAtSign, _originType, _originName));
            fail("GenericPricinpal should reject _usernames with @ sign");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }
        try
        {
            new GenericPrincipal(String.format("%s@%s('%s')", _username, originTypeWithAtSign, _originName));
            fail("GenericPricinpal should reject origin type with @ sign");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }
        try
        {
            new GenericPrincipal(String.format("%s@%s('%s')", _username, _originType, originNameWithAtSign));
            fail("GenericPricinpal should reject origin name with @ sign");
        }
        catch (IllegalArgumentException e)
        {
            // pass
        }
    }

    public void testUrlEncoded() throws Exception
    {
        final String username = "testuser@withFunky%";
        final String originName = "authName('also')with%funky@Characters'";
        GenericPrincipal p = new GenericPrincipal(String.format("%s@%s('%s')",
                                                                URLEncoder.encode(username, UTF8),
                                                                _originType,
                                                                URLEncoder.encode(originName, UTF8)));
        assertEquals("unexpected principal name", username, p.getName());
        assertEquals("unexpected origin type", _originType, p.getOriginType());
        assertEquals("unexpected origin name", originName, p.getOriginName());
    }
}
