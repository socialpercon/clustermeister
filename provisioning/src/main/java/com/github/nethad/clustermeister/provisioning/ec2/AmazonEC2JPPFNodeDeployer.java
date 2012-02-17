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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

	final String driverhost;
	
	public AmazonEC2JPPFNodeDeployer(ComputeServiceContext context, 
			NodeMetadata metadata, LoginCredentials credentials, String driverhost) {
		super(credentials, context, metadata);
		
		this.driverhost = driverhost;
	}
	
	@Override
	public void run() {
		logger.info("Deploying JPPF-Node to {} ({}).", metadata.getId(), 
				metadata.getPublicAddresses().iterator().next());
		
		Properties nodeProperties = new Properties();
		try {
			nodeProperties.load(this.getClass().getResourceAsStream("jppf-node.properties"));
		} catch (IOException ex) {
			logger.warn("Can not read properties file.", ex);
		}
		nodeProperties.setProperty("jppf.server.host", driverhost);
		ByteArrayOutputStream runningConfig = new ByteArrayOutputStream();
		try {
			nodeProperties.store(runningConfig, "Running Config");
		} catch (IOException ex) {
			logger.warn("Can not write running property configuration.", ex);
		}
		
		
		SshClient client = context.utils().sshForNode().apply(
				NodeMetadataBuilder.fromNodeMetadata(metadata).
				credentials(loginCredentials).build());
		client.connect();
		try {
			execute("rm -rf jppf-node*", client);
			upload(client, getClass().getResourceAsStream("jppf-node.zip"), 
					"/home/ec2-user/jppf-node.zip");
			execute("unzip jppf-node.zip", client);
			execute("chmod +x jppf-node/startNode.sh", client);
			upload(client, new ByteArrayInputStream(runningConfig.toByteArray()), 
					"jppf-node/config/jppf-node.properties");
			
			logger.info("Starting JPPF-Node on {}...", metadata.getId());
			final String script = "cd /home/ec2-user/jppf-node\nnohup ./startNode.sh > nohup.out 2>&1";
			RunScriptOptions options = new RunScriptOptions().
					overrideLoginPrivateKey(loginCredentials.getPrivateKey()).
					overrideLoginUser(loginCredentials.getUser()).
					blockOnComplete(false).
					runAsRoot(false).
					nameTask("jppf-node-start");
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
	
}