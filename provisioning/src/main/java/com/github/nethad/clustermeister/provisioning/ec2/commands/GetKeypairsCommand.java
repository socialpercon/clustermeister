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

import com.github.nethad.clustermeister.api.Credentials;
import com.github.nethad.clustermeister.provisioning.CommandLineArguments;
import com.github.nethad.clustermeister.provisioning.CommandLineHandle;
import com.github.nethad.clustermeister.provisioning.ec2.AmazonCommandLineEvaluation;
import com.github.nethad.clustermeister.provisioning.ec2.CredentialsManager;
import java.util.Set;

/**
 * Get all configured keypair names.
 *
 * @author daniel
 */
public class GetKeypairsCommand extends AbstractAmazonExecutableCommand {
    
    private static final String NAME = "keypairs";
    
    private static final String[] ARGUMENTS = null;
    
    private static final String HELP_TEXT = "Get all configured keypair names.";
    
    /**
     * Creates a new command with a command line evaluation reference for access 
     * to the Clustermeister provisioning infrastructure.
     * 
     * @param commandLineEvaluation the command line evaluation instance reference.
     */
    public GetKeypairsCommand(AmazonCommandLineEvaluation commandLineEvaluation) {
        super(NAME, ARGUMENTS, HELP_TEXT, commandLineEvaluation);
    }
    
    @Override
    public void execute(CommandLineArguments arguments) {
        CredentialsManager credentialsManager = 
                getNodeManager().getCredentialsManager();
        CommandLineHandle console = getCommandLineHandle();
        
        console.print("Currently known keypairs:");
        console.print(SEPARATOR_LINE);
        Set<Credentials> credentials = credentialsManager.getAllCredentials();
        
        if(credentials.isEmpty()) {
            console.print("No keypairs configured.");
        } else {
            for(Credentials cred : credentials) {
                console.print(cred.toString());
            }
        }
        console.print(SEPARATOR_LINE);
    }
}
