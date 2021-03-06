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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.apache.maven.repository.internal.ArtifactDescriptorUtils;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * A model resolver to assist building of dependency POMs.
 * 
 * Adapted from {@link org.apache.maven.repository.internal.DefaultModelResolver}
 * 
 * @author daniel
 */
public class SimpleModelResolver implements ModelResolver {

    private final RepositorySystem system;
    private final RepositorySystemSession session;
    private final Set<String> repositoryIds;
    private List<RemoteRepository> repositories;

    /**
     * Creates a model resolver to assist building of dependency POMs.
     * 
     * @param system a {@link RepositorySystem}
     * @param session a {@link RepositorySystemSession}
     * @param remoteRepositories remote repositories to use for resolution.
     */
    public SimpleModelResolver(RepositorySystem system, RepositorySystemSession session, 
            List<RemoteRepository> remoteRepositories) {
        this.system = system;
        this.session = session;
        this.repositories = new ArrayList<RemoteRepository>(remoteRepositories);
        this.repositoryIds = new HashSet<String>(
                remoteRepositories.size() < 3 ? 3 : remoteRepositories.size());
        
        for(RemoteRepository repository : remoteRepositories) {
            repositoryIds.add(repository.getId());
        }
    }

    /**
     * Clone Constructor.
     * 
     * @param original a SimpleModelResolver.
     */
    private SimpleModelResolver(SimpleModelResolver original) {
        this.session = original.session;
        this.system = original.system;
        this.repositoryIds = new HashSet<String>(original.repositoryIds);
    }

    @Override
    public void addRepository(Repository repository) throws InvalidRepositoryException {
        if (!repositoryIds.add(repository.getId())) {
            return;
        }

        this.repositories.add(ArtifactDescriptorUtils.toRemoteRepository(repository));
    }

    @Override
    public ModelResolver newCopy() {
        return new SimpleModelResolver(this);
    }

    @Override
    public ModelSource resolveModel(String groupId, String artifactId, String version)
            throws UnresolvableModelException {
        Artifact pomArtifact = new DefaultArtifact(groupId, artifactId, "", "pom", version);

        try {
            ArtifactRequest request = new ArtifactRequest(pomArtifact, repositories, null);
            pomArtifact = system.resolveArtifact(session, request).getArtifact();
        } catch (ArtifactResolutionException ex) {
            throw new UnresolvableModelException(ex.getMessage(), groupId, artifactId, version, ex);
        }

        File pomFile = pomArtifact.getFile();

        return new FileModelSource(pomFile);
    }
}
