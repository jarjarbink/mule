/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.udp;

import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.service.Service;
import org.mule.api.transport.MessageReceiver;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.service.ServiceCompositeMessageSource;
import org.mule.transport.AbstractMessageReceiverTemplateTestCase;

import com.mockobjects.dynamic.Mock;

public class UdpMessageReceiverTestCase extends AbstractMessageReceiverTemplateTestCase
{
    @Override
    public MessageReceiver getMessageReceiver() throws Exception
    {
        endpoint = muleContext.getEndpointFactory().getInboundEndpoint(
            "udp://localhost:10100");
        Mock mockComponent = new Mock(Service.class);
        mockComponent.expect("getResponseRouter");
        mockComponent.expectAndReturn("getInboundRouter", new ServiceCompositeMessageSource());

        return new UdpMessageReceiver(endpoint.getConnector(), (Service) mockComponent.proxy(), endpoint);
    }

    @Override
    public InboundEndpoint getEndpoint() throws Exception
    {
        UdpConnector connector = new UdpConnector(muleContext);
        connector.initialise();
        
        EndpointBuilder builder = new EndpointURIEndpointBuilder("udp://localhost:10100", muleContext);
        builder.setConnector(connector);
        return muleContext.getEndpointFactory().getInboundEndpoint(builder);
    }
}
