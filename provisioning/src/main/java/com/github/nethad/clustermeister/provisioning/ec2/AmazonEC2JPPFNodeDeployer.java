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
package com.github.nethad.clustermeister.provisioning.ec2;

import static com.google.common.base.Preconditions.*;
import java.util.Properties;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.SshClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author daniel
 */
public class AmazonEC2JPPFNodeDeployer extends AmazonEC2JPPFDeployer {

    private final static Logger logger =
            LoggerFactory.getLogger(AmazonEC2JPPFNodeDeployer.class);

    public AmazonEC2JPPFNodeDeployer(ComputeServiceContext context,
            NodeMetadata metadata, LoginCredentials credentials,
            AmazonNodeConfiguration nodeConfiguration) {
        super(credentials, context, metadata, nodeConfiguration);
    }

    @Override
    public void run() {
        String publicIp = getPublicIp();
        String privateIp = getPrivateIp();
        checkState(nodeConfiguration.getDriverAddress() != null, "No driver address set.");

        logger.info("Deploying JPPF-Node to {} ({}).", metadata.getId(),
                publicIp);
        Properties nodeProperties = getSettings(nodeConfiguration.getDriverAddress(),
                privateIp, nodeConfiguration.getManagementPort());

        SshClient client = context.utils().sshForNode().apply(
                NodeMetadataBuilder.fromNodeMetadata(metadata).
                credentials(loginCredentials).build());
        client.connect();
        try {
            final String folderName = getFolderName();
            execute("rm -rf " + folderName, client);
            upload(client, getClass().getResourceAsStream("jppf-node.zip"),
                    "/home/ec2-user/" + folderName + ".zip");
            execute("unzip " + folderName + ".zip -d " + folderName, client);
            execute("chmod +x " + folderName + "/jppf-node/startNode.sh", client);
            upload(client, getRunningConfig(nodeProperties),
                    folderName + "/jppf-node/config/jppf-node.properties");

            logger.info("Starting JPPF-Node on {}...", metadata.getId());
            final String script = "cd /home/ec2-user/" + folderName + "/jppf-node\nnohup ./startNode.sh > nohup.out 2>&1";
            RunScriptOptions options = new RunScriptOptions().overrideLoginPrivateKey(
                    loginCredentials.getPrivateKey()).
                    overrideLoginUser(loginCredentials.getUser()).
                    blockOnComplete(false).
                    runAsRoot(false).
                    nameTask(folderName + "-start");
            logExecResponse(context.getComputeService().
                    runScriptOnNode(metadata.getId(), script, options));
            logger.info("JPPF-Node started.");
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
        logger.info("JPPF-Node deployed on {}.", metadata.getId());
    }

    private Properties getSettings(String driverHost, String managementHost, int managementPort) {
        Properties nodeProperties = getPropertiesFromStream(
                this.getClass().getResourceAsStream("jppf-node.properties"));
        nodeProperties.setProperty("jppf.server.host", driverHost);
        nodeProperties.setProperty("jppf.management.host", managementHost);
        nodeProperties.setProperty("jppf.management.port", String.valueOf(managementPort));
        return nodeProperties;
    }
}
