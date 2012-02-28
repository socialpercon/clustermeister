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

import com.github.nethad.clustermeister.api.Configuration;
import com.github.nethad.clustermeister.api.Node;
import com.github.nethad.clustermeister.api.NodeType;
import com.github.nethad.clustermeister.api.impl.FileConfiguration;
import com.github.nethad.clustermeister.provisioning.ec2.AmazonInstanceManager;
import com.github.nethad.clustermeister.provisioning.utils.SSHClient;
import com.github.nethad.clustermeister.provisioning.utils.SSHClientExcpetion;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jppf.management.JMXDriverConnectionWrapper;
import org.jppf.process.ProcessLauncher;
import org.jppf.server.DriverLauncher;
import org.jppf.server.JPPFDriver;
import org.jppf.utils.JPPFConfiguration;

/**
 *
 * @author thomas
 */
public class TorqueJPPFDriverDeployer {

    private static final String DEPLOY_BASE_NAME = "jppf-driver";
    private static final String DEPLOY_ZIP = DEPLOY_BASE_NAME + ".zip";
    private static final String DEPLOY_PROPERTIES = DEPLOY_BASE_NAME + ".properties";
    private String host;
    private int port;
    private ProcessLauncher processLauncher;
    private SSHClient sshClient;
    private String user;
    private String privateKeyFilePath;
//    private String passphrase;
    private boolean runExternally = false;

    public TorqueNode execute() {
        if (runExternally) {
            return remoteSetupAndRun();
        } else {
            return localSetupAndRun();
        }
    }

    private TorqueNode remoteSetupAndRun() {
	loadConfiguration();
        sshClient = null;
        try {

            sshClient = new SSHClient(privateKeyFilePath);
            sshClient.connect(user, host, port);
            executeAndSysout("rm -rf " + DEPLOY_BASE_NAME + "*");

            sshClient.sftpUpload(getResourcePath(DEPLOY_ZIP), DEPLOY_ZIP);
            executeAndSysout("unzip " + DEPLOY_ZIP);

            sshClient.sftpUpload(getResourcePath(DEPLOY_PROPERTIES), DEPLOY_BASE_NAME + "/config/" + DEPLOY_PROPERTIES);
            executeAndSysout("chmod +x " + DEPLOY_BASE_NAME + "/startDriver.sh");

            // assume java is installed (installed in ~/jdk-1.7)
//            executeAndSysout("cp -R /home/user/dspicar/jdk-1.7 ~/jdk-1.7");

            executeAndSysout("cd " + DEPLOY_BASE_NAME + ";nohup ./startDriver.sh ~/jdk-1.7/bin/java > nohup.out 2>&1");
			return new TorqueNode(NodeType.DRIVER);
        } catch (SSHClientExcpetion ex) {
            ex.printStackTrace();
        } finally {
            if (sshClient != null) {
                sshClient.disconnect();
            }
        }
		return null;
    }

    private String getResourcePath(String resource) {
        return TorqueJPPFDriverDeployer.class.getResource(resource).getPath();
    }

    private void executeAndSysout(String command) throws SSHClientExcpetion {
        String result = sshClient.sshExec(command, System.err);
        System.out.println("Result: " + result);
    }

    private void loadConfiguration() {
        String home = System.getProperty("user.home");
        String separator = System.getProperty("file.separator");
        Configuration config = new FileConfiguration(home + separator + ".clustermeister" + separator + "torque.properties");

        host = getStringDefaultempty(config, "host");
        port = config.getInt("port", 22);
        user = getStringDefaultempty(config, "user");
        privateKeyFilePath = getStringDefaultempty(config, "privateKey");
//        passphrase = getStringDefaultempty(config, "passphrase");

    }

    private String getStringDefaultempty(Configuration config, String key) {
        return config.getString(key, "");
    }

    public TorqueJPPFDriverDeployer runExternally() {
        runExternally = true;
        return this;
    }

    private TorqueNode localSetupAndRun() {
        processLauncher = new ProcessLauncher("org.jppf.server.JPPFDriver");
        processLauncher.run();
		TorqueNode torqueNode = new TorqueNode(NodeType.DRIVER);
		return torqueNode;
		//        try {
		//            Process process = processLauncher.buildProcess();
		//            process.
		//        } catch (Exception ex) {
		//            Logger.getLogger(TorqueJPPFDriverDeployer.class.getName()).log(Level.SEVERE, null, ex);
		//        }
    }

    public void stopLocalDriver() {
        JMXDriverConnectionWrapper wrapper = new JMXDriverConnectionWrapper("localhost", 11198);
        wrapper.connect();
        try {
            wrapper.restartShutdown(1L, -1L);
        } catch (Exception ex) {
            Logger.getLogger(TorqueJPPFTestSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
