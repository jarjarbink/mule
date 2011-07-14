/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.spring.security;

import org.mule.api.MuleMessage;
import org.mule.api.context.notification.SecurityNotificationListener;
import org.mule.context.notification.SecurityNotification;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.transport.http.HttpConnector;
import org.mule.util.concurrent.Latch;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SecureHttpPollingFunctionalTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "secure-http-polling-server.xml,secure-http-polling-client.xml";
    }

    @Test
    public void testPollingHttpConnectorSentCredentials() throws Exception
    {
        final Latch latch = new Latch();
        muleContext.registerListener(new SecurityNotificationListener<SecurityNotification>()
        {
            @Override
            public void onNotification(SecurityNotification notification)
            {
                latch.countDown();
            }
        });
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.request("vm://toclient", 5000);
        assertNotNull(result);
        assertEquals("foo", result.getPayloadAsString());

        result = client.request("vm://toclient2", 1000);
        //This seems a little odd that we forward the exception to the outbound endpoint, but I guess users
        // can just add a filter
        assertNotNull(result);
        final int status = result.getInboundProperty(HttpConnector.HTTP_STATUS_PROPERTY, 0);
        assertEquals(401, status);
        assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
    }
}
