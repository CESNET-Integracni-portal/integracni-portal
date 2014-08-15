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

public class SshChannel {

    private static final Logger logger = Logger.getLogger(SshChannel.class);

    @Autowired
    private SshDataSource sshDataSource;

    private InputStream in;
    private BufferedReader inBuffered;

    private ChannelExec sshChannel;

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

    public SshDataSource getSshDataSource() {
        return sshDataSource;
    }

    public void setSshDataSource(SshDataSource sshDataSource) {
        this.sshDataSource = sshDataSource;
    }
}
