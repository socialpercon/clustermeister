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
package com.github.nethad.clustermeister.provisioning.torque;

import com.github.nethad.clustermeister.api.NodeConfiguration;
import com.github.nethad.clustermeister.api.NodeType;
import java.io.File;
import java.util.Collection;

/**
 * TODO: node specification class, this represents a not yet created node
 *
 * @author daniel
 */
public class TorqueNodeConfiguration implements NodeConfiguration {
	
	private String driverAddress;
    private int numberOfCpus;

	public TorqueNodeConfiguration() {
        this.numberOfCpus = 1;
	}
	
	public TorqueNodeConfiguration(String driverAddress, int numberOfCpus) {
		this.driverAddress = driverAddress;
        this.numberOfCpus = numberOfCpus;
	}
    
    public static TorqueNodeConfiguration configurationForNode(String driverAddress, int numberOfCpus) {
        return new TorqueNodeConfiguration(driverAddress, numberOfCpus);
    }
	
	@Override
	public NodeType getType() {
		return NodeType.NODE;
	}

	@Override
	public String getDriverAddress() {
		return driverAddress;
	}

	@Override
	public boolean isDriverDeployedLocally() {
		return true;
	}

    public int getNumberOfCpus() {
        return numberOfCpus;
    }

    @Override
    public Collection<File> getArtifactsToPreload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
}
