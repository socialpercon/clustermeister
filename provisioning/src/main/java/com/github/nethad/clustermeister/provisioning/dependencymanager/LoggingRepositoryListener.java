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
package com.github.nethad.clustermeister.provisioning.dependencymanager;

import com.github.nethad.clustermeister.api.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.AbstractRepositoryListener;
import org.sonatype.aether.RepositoryEvent;

/**
 * Outputs Repository Events to a logger.
 *
 * @author daniel
 */
public class LoggingRepositoryListener extends AbstractRepositoryListener {

    private final static Logger logger =
            LoggerFactory.getLogger(LoggingRepositoryListener.class);

    private final LogLevel infoLogLevel;
    
    private final LogLevel errorLogLevel;

    /**
     * Default Constructor.
     * 
     * Logs events with level INFO and errors with WARN.
     */
    public LoggingRepositoryListener() {
        this(LogLevel.INFO, LogLevel.WARN);
    }
    
    /**
     * Creates a new RepositoryListener with user defined log levels.
     * 
     * @param infoLogLevel  log level for "info"-type messages.
     * @param errorLogLevel log level for "error" messages.
     */
    LoggingRepositoryListener(LogLevel infoLogLevel, LogLevel errorLogLevel) {
        this.infoLogLevel = infoLogLevel;
        this.errorLogLevel = errorLogLevel;
    }
    
    @Override
    public void artifactDeployed(RepositoryEvent event) {
        log(infoLogLevel, "Deployed {} to {}.", 
                event.getArtifact(), event.getRepository());
    }

    @Override
    public void artifactDeploying(RepositoryEvent event) {
        log(infoLogLevel, "Deploying {} to {}.", 
                event.getArtifact(), event.getRepository());
    }

    @Override
    public void artifactDescriptorInvalid(RepositoryEvent event) {
        log(errorLogLevel, "Invalid artifact descriptor for {}: {}.", 
                event.getArtifact(), event.getException().getMessage());
    }

    @Override
    public void artifactDescriptorMissing(RepositoryEvent event) {
        log(errorLogLevel, "Missing artifact descriptor for {}.", 
                event.getArtifact());
    }

    @Override
    public void artifactInstalled(RepositoryEvent event) {
        log(infoLogLevel, "Installed {} to {}.", 
                event.getArtifact(), event.getFile());
    }

    @Override
    public void artifactInstalling(RepositoryEvent event) {
        log(infoLogLevel, "Installing {} to {}.", 
                event.getArtifact(), event.getFile());
    }

    @Override
    public void artifactResolved(RepositoryEvent event) {
        log(infoLogLevel, "Resolved artifact {} from {}.", 
                event.getArtifact(), event.getRepository());
    }

    @Override
    public void artifactDownloading(RepositoryEvent event) {
        log(infoLogLevel, "Downloading artifact {} from {}.", 
                event.getArtifact(), event.getRepository());
    }

    @Override
    public void artifactDownloaded(RepositoryEvent event) {
        log(infoLogLevel, "Downloaded artifact {} from {}.", 
                event.getArtifact(), event.getRepository());
    }

    @Override
    public void artifactResolving(RepositoryEvent event) {
        log(infoLogLevel, "Resolving artifact {}.", event.getArtifact());
    }

    @Override
    public void metadataDeployed(RepositoryEvent event) {
        log(infoLogLevel, "Deployed {} to {}.", 
                event.getMetadata(), event.getRepository());
    }

    @Override
    public void metadataDeploying(RepositoryEvent event) {
        log(infoLogLevel, "Deploying {} to {}.", 
                event.getMetadata(), event.getRepository());
    }

    @Override
    public void metadataInstalled(RepositoryEvent event) {
        log(infoLogLevel, "Installed {} to {}.", 
                event.getMetadata(), event.getFile());
    }

    @Override
    public void metadataInstalling(RepositoryEvent event) {
        log(infoLogLevel, "Installing {} to {}.", 
                event.getMetadata(), event.getFile());
    }

    @Override
    public void metadataInvalid(RepositoryEvent event) {
        log(errorLogLevel, "Invalid metadata {}.", event.getMetadata());
    }

    @Override
    public void metadataResolved(RepositoryEvent event) {
        log(infoLogLevel, "Resolved metadata {} from {}.", 
                event.getMetadata(), event.getRepository());
    }

    @Override
    public void metadataResolving(RepositoryEvent event) {
        log(infoLogLevel, "Resolving metadata {} from {}.", 
                event.getMetadata(), event.getRepository());
    }

    @Override
    public void metadataDownloaded(RepositoryEvent event) {
        log(infoLogLevel, "Downloaded metadata {} from {}.", 
                event.getMetadata(), event.getRepository());
    }

    @Override
    public void metadataDownloading(RepositoryEvent event) {
        log(infoLogLevel, "Downloading metadata {} from {}.", 
                event.getMetadata(), event.getRepository());
    }
    
    private void log(LogLevel logLevel, String messageString, Object... arguments) {
        switch(logLevel) {
            case ERROR: {
                logger.error(messageString, arguments);
                break;
            }
            case WARN: {
                logger.warn(messageString, arguments);
                break;
            }
            case INFO: {
                logger.info(messageString, arguments);
                break;
            }
            case DEBUG: {
                logger.debug(messageString, arguments);
                break;
            }
            case TRACE: {
                logger.trace(messageString, arguments);
                break;
            }
        }
    }
}
