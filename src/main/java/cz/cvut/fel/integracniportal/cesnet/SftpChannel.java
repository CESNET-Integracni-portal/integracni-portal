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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.InputStream;

public class SftpChannel {

    private static final Logger logger = Logger.getLogger(SftpChannel.class);

    @Autowired
    private SshDataSource sshDataSource;

    private ChannelSftp sftpChannel;


    @PostConstruct
    private void init() throws Exception {

        sftpChannel = sshDataSource.getSftpChannel();
        if (sftpChannel == null) {
            throw new Exception("Problem obtaining an ssh channel.");
        }

        sftpChannel.connect();
    }

    public InputStream getFile(String filename) throws SftpException {
        InputStream inputStream = sftpChannel.get(filename);
        sftpChannel.disconnect();
        return inputStream;
    }

    @PreDestroy
    private void destroy() {
        if (sftpChannel != null) {
            logger.debug("destroying the pooled resource...");
            sftpChannel.disconnect();
        }
    }

    boolean isValid() {
        return !sftpChannel.isClosed();
    }

    public SshDataSource getSshDataSource() {
        return sshDataSource;
    }

    public void setSshDataSource(SshDataSource sshDataSource) {
        this.sshDataSource = sshDataSource;
    }
}
