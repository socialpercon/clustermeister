/*
 * Copyright 2012 The Clustermeister Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.nethad.clustermeister.provisioning.jppf;

import com.github.nethad.clustermeister.api.JPPFConstants;
import com.github.nethad.clustermeister.node.common.builders.PropertyConfiguratedJPPFComponentBuilder;

/**
 * Builds a new {@link JPPFManagementByJobsClient} component.
 *
 * @author daniel
 */
public class ManagementByJobsClientBuilder 
        extends PropertyConfiguratedJPPFComponentBuilder<JPPFManagementByJobsClient> {
    
    /**
     * Creates a new builder.
     * 
     * @param host the server host
     * @param port the server port
     */
    public ManagementByJobsClientBuilder(String host, int port) {
        String driverName = "driver-1";
        properties.setProperty(JPPFConstants.DISCOVERY_ENABLED, "false");
        properties.setProperty(JPPFConstants.DRIVERS, driverName);
        properties.setProperty(String.format(
                JPPFConstants.DRIVER_SERVER_HOST_PATTERN, driverName), host);
        properties.setProperty(String.format(
                JPPFConstants.DRIVER_SERVER_PORT_PATTERN, driverName), String.valueOf(port));
        properties.setProperty(JPPFConstants.JVM_OPTIONS, 
                "-Dlog4j.configuration=log4j-mclient.properties");
    }
    
    @Override
    protected JPPFManagementByJobsClient doBuild() {
        return new JPPFManagementByJobsClient();
    }
}
