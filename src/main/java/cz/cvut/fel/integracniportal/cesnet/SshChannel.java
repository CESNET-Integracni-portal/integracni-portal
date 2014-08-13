/*
 * Copyright 2014 Simon So
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is the heart-and-soul unit of the sftp channel operation.
 * It is wrapped as such so that it represents a pooled resource of the connection pool.
 *
 * @author sso
 */
public class SshChannel {

    private static final Logger logger = Logger.getLogger(SshChannel.class);

    @Autowired
    private SshDataSource sshDataSource;

    private InputStream in;
    private BufferedReader inBuffered;

    private ChannelExec sshChannel;

    private boolean usable = true;


    /**
     * Initializes the resources.
     * Don't put the logic in the constructor; sftpDataSource haven't been finished initializing.
     *
     * @throws Exception if we are unable to initialize from the beginning,
     *                   just panic and quit the bean creation process.
     */
    @PostConstruct
    private void init() throws Exception {

        sshChannel = sshDataSource.getSshChannel();
        if (sshChannel == null) {
            throw new Exception("Problem obtaining an ssh channel.");
        }

        in = sshChannel.getInputStream();
        inBuffered = new BufferedReader(new InputStreamReader(in));

    }

    public List<String> sendCommand(String command) {
        List<String> response = Collections.emptyList();

        try {
            sshChannel.setCommand(command);
            sshChannel.connect();

            usable = false;

            response = readResponse();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        sshChannel.disconnect();

        return response;
    }

    private List<String> readResponse() throws IOException {
        List<String> response = new ArrayList<String>();
        String line;
        while ((line = inBuffered.readLine()) != null) {
            response.add(line);
        }
        return response;
    }

    @PreDestroy
    private void destroy() {
        if (sshChannel != null) {
            logger.debug("destroying the pooled resource...");
            sshChannel.disconnect();
        }
    }

    boolean isValid() {
        return usable;
    }

    public SshDataSource getSshDataSource() {
        return sshDataSource;
    }

    public void setSshDataSource(SshDataSource sshDataSource) {
        this.sshDataSource = sshDataSource;
    }
}
