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

import com.github.nethad.clustermeister.api.impl.KeyPairCredentials;
import com.google.common.base.Objects;
import java.io.File;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * A class representing a user name and key pair credential auto-generated by 
 * AWS EC2.
 *
 * @author daniel
 */
class AmazonGeneratedKeyPairCredentials extends KeyPairCredentials {
    /**
     * The private key.
     */
    final String privateKey;

    /**
     * Creates a credential with user name and key pair.
     * 
     * @param user  the user name.
     * @param privateKey    the private key.
     */
    AmazonGeneratedKeyPairCredentials(String user, String privateKey) {
        super(user, new DummyFile("dummypath"));
        this.privateKey = privateKey;
    }

    @Override
    public String getPrivateKey() {
        return privateKey;
    }
    
    private static class DummyFile extends File {
        public DummyFile(String pathname) {
            super(pathname);
        }

        @Override
        public boolean canRead() {
            return true;
        }

        @Override
        public boolean exists() {
            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != (getClass())) {
            return false;
        }
        AmazonGeneratedKeyPairCredentials otherNode = 
                (AmazonGeneratedKeyPairCredentials) obj;
        return new EqualsBuilder().
                append(user, otherNode.user).
                append(privateKey, otherNode.privateKey).
                isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user, privateKey);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                addValue(user).
                add("privateKey", "<secret>").
                toString();
    }
}
