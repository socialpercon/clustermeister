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
package com.github.nethad.clustermeister.api.impl;

import com.github.nethad.clustermeister.api.NodeCapabilities;

/**
 *
 * @author thomas
 */
class NodeCapabilitiesImpl implements NodeCapabilities {

    private int numberOfProcessors;
    private int numberOfProcessingThreads;
    private String jppfConfig;

    public NodeCapabilitiesImpl(int numberOfProcessors, int numberOfProcessingThreads, String jppfConfig) {
        System.out.println("new NodeCapabilitiesImpl("+numberOfProcessors+","+numberOfProcessingThreads+"...)");
        this.numberOfProcessors = numberOfProcessors;
        this.numberOfProcessingThreads = numberOfProcessingThreads;
        this.jppfConfig = jppfConfig;
    }
    
    @Override
    public int getNumberOfProcessors() {
        return numberOfProcessors;
    }

    @Override
    public int getNumberOfProcessingThreads() {
        return numberOfProcessingThreads;
    }

    @Override
    public String getJppfConfig() {
        return jppfConfig;
    }
    
}