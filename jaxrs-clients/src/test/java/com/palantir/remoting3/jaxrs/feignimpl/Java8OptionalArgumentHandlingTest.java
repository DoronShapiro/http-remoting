/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
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

package com.palantir.remoting3.jaxrs.feignimpl;

import static org.junit.Assert.assertEquals;

import com.palantir.remoting.api.errors.RemoteException;
import com.palantir.remoting3.jaxrs.JaxRsClient;
import com.palantir.remoting3.jaxrs.TestBase;
import io.dropwizard.Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.util.Optional;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class Java8OptionalArgumentHandlingTest extends TestBase {

    @ClassRule
    public static final DropwizardAppRule<Configuration> APP = new DropwizardAppRule<>(Java8TestServer.class,
            "src/test/resources/test-server.yml");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Java8TestServer.TestService service;

    @Before
    public void before() {
        String endpointUri = "http://localhost:" + APP.getLocalPort();
        service = JaxRsClient.create(Java8TestServer.TestService.class, AGENT, createTestConfig(endpointUri));
    }

    @Test
    public void testHeader_optionalEmpty() {
        expectedException.expect(RemoteException.class);
        expectedException.expectMessage("RemoteException: NOT_FOUND (Default:NotFound)");
        service.getValueThrowsNotFoundIfHeaderEmpty(Optional.empty());
    }

    @Test
    public void testHeader_optionalEmptyString() {
        String returnValue = service.getValueThrowsNotFoundIfHeaderEmpty(Optional.of(""));
        assertEquals("Value: ", returnValue);
    }

    @Test
    public void testHeader_optionalValue() {
        String returnValue = service.getValueThrowsNotFoundIfHeaderEmpty(Optional.of("foo"));
        assertEquals("Value: foo", returnValue);
    }

}
