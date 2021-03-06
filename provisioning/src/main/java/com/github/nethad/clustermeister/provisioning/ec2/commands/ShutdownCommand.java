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
package com.github.nethad.clustermeister.provisioning.ec2.commands;

import com.github.nethad.clustermeister.api.Node;
import com.github.nethad.clustermeister.provisioning.CommandLineArguments;
import com.github.nethad.clustermeister.provisioning.ec2.AmazonCommandLineEvaluation;
import com.github.nethad.clustermeister.provisioning.ec2.AmazonNode;
import com.github.nethad.clustermeister.provisioning.ec2.AmazonNodeManager;
import com.github.nethad.clustermeister.provisioning.jppf.JPPFManagementByJobsClient;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.*;

/**
 * Shutdown all nodes and the CLI infrastructure.
 *
 * @author daniel
 */
public class ShutdownCommand extends AbstractAmazonExecutableCommand {

    private static final String[] ARGUMENTS = null;
    
    private static final String HELP_TEXT = "Shutdown all nodes and the CLI infrastructure.";
    
    private static final String NAME = "shutdown";

    /**
     * Creates a new command with a command line evaluation reference for access 
     * to the Clustermeister provisioning infrastructure.
     * 
     * @param commandLineEvaluation the command line evaluation instance reference.
     */
    public ShutdownCommand(AmazonCommandLineEvaluation commandLineEvaluation) {
        super(NAME, ARGUMENTS, HELP_TEXT, commandLineEvaluation);
    }

    @Override
    public void execute(CommandLineArguments arguments) {
        AmazonNodeManager nodeManager = getNodeManager();
        JPPFManagementByJobsClient amazonManagementClient = getManagementClient();
        
        getCommandLineHandle().print("Shutting down all nodes.");
        Collection<? extends Node> nodes = nodeManager.getNodes();
        List<ListenableFuture<? extends Object>> futures = 
                new ArrayList<ListenableFuture<? extends Object>>(nodes.size());
        for(Node node : nodes) {
            AmazonNode amazonNode = node.as(AmazonNode.class);
            futures.add(nodeManager.removeNode(amazonNode));
        }
        waitForFuturesToComplete(futures, 
                "Interrupted while waiting for nodes to shut down. Nodes may not all be stopped properly.", 
                "Failed to wait for nodes to stop.", "{} nodes failed to shut down.");
        
        amazonManagementClient.close();
        nodeManager.close();
    }
    
}
