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

import com.github.nethad.clustermeister.api.Credentials;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.Configuration;

/**
 * A utility to help parse Amazon specific configuration values.
 *
 * @author daniel
 */
public class AmazonConfigurationLoader {
    /**
     * AWS Access key ID.
     */
    public static final String ACCESS_KEY_ID = "amazon.access_key_id";
    
    /**
     * AWS secret key.
     */
    public static final String SECRET_KEY = "amazon.secret_key";
    
    /**
     * Amazon key pair configuration.
     */
    public static final String KEYPAIRS = "amazon.keypairs";
    
    /**
     * Private key configuration.
     */
    public static final String PRIVATE_KEY = "private_key";
    
    /**
     * Private key configuration.
     */
    public static final String PUBLIC_KEY = "public_key";
    
    /**
     * User name configuration.
     */
    public static final String USER = "user";
    
    /**
     * The configuration.
     */
    final Configuration configuration;

    /**
     * Create a new Configuration Loader with a specific configuration.
     * 
     * @param configuration the configuration.
     */
    public AmazonConfigurationLoader(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Returns the configured AWS Access Key.
     * 
     * @return the access key.
     */
    public String getAccessKeyId() {
        return checkNotNull(configuration.getString(ACCESS_KEY_ID, null), 
                "No AWS access key ID configured.").trim();
    }
    
    /**
     * Returns the configured Secret Key.
     * 
     * @return the secret key.
     */
    public String getSecretKey() {
        return checkNotNull(configuration.getString(SECRET_KEY, null), 
                "No AWS secret key configured.").trim();
    }
    
    /**
     * Returns a map containing a mapping of configured credentials names to 
     * {@link Credentials} instances..
     * 
     * @return the configured credentials.
     */
    public Map<String, Credentials> getConfiguredCredentials() {
        List<Object> keypairList = configuration.getList(KEYPAIRS);
        Map<String, Map<String, String>> keypairSpecifications = 
                reduceObjectList(keypairList, 
                "Keypairs must be specified as a list of objects.");
        Map<String, Credentials> credentials = 
                Maps.newHashMapWithExpectedSize(keypairSpecifications.size());
        for (Map.Entry<String, Map<String, String>> entry : keypairSpecifications.entrySet()) {
            String keyPairName = entry.getKey();
            Map<String, String> keyPairValues = entry.getValue();
            String user = getCheckedConfigValue(USER, keyPairValues, "keypair", 
                    keyPairName);
            String privateKeyPath = getCheckedConfigValue(PRIVATE_KEY, 
                    keyPairValues, "keypair", keyPairName);
            File privateKey = getCheckedFile(privateKeyPath, PRIVATE_KEY, 
                    "keypair", keyPairName);
            
            String publicKeyPath = keyPairValues.get(PUBLIC_KEY);
            if(publicKeyPath != null) {
                File publicKey = getCheckedFile(publicKeyPath, PUBLIC_KEY, 
                        "keypair", keyPairName);
                credentials.put(keyPairName,
                        new KeyPairCredentials(user, privateKey, publicKey));
            } else {
                credentials.put(keyPairName, 
                        new AmazonConfiguredKeyPairCredentials(user, privateKey, 
                        keyPairName));
            }
        }
        
        return credentials;
    }
    
    private File getCheckedFile(String path, String key, String listObjectCategory, String listObjectName) {
        File file = new File(path);
        checkArgument(file.isFile() && file.canRead(),
                "%s for %s %s can not be read from.", key, listObjectCategory, listObjectName);
        return file;
    }
    
    private String getCheckedConfigValue(String key, Map<String, String> configMap, 
            String listObjectCategory, String listObjectName) {
        checkArgument(configMap.containsKey(key),
                "No key '%s' found for %s %s.", key, listObjectCategory, listObjectName);
        String value = checkNotNull(configMap.get(key),
                "No value for key '%s' found for %s %s.", key, listObjectCategory, 
                listObjectName).trim();
        return value;
    }
    
    private Map<String, Map<String, String>> reduceObjectList(List<Object> list, 
            String errorMessage) {
        try {
            Map<String, Map<String, String>> result = Maps.newLinkedHashMap();
            List<Map<String, Map<String, String>>> mapList = Lists.transform(list, 
                    new Function<Object, Map<String, Map<String, String>>>() {
                        @Override
                        public Map apply(Object input) {
                            return (Map<String, Map<String, String>>) input;
                        }
            });
            for (Map<String, Map<String, String>> map : mapList) {
                for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Map<String, String> value = entry.getValue();
                    result.put(key, value);
                }
            }

            return result;
            
        } catch(ClassCastException ex) {
            throw new IllegalArgumentException(errorMessage, ex);
        }
    }
}
