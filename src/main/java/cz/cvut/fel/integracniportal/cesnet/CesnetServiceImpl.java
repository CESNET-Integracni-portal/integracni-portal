package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class CesnetServiceImpl implements CesnetService {

    private static final Logger logger = Logger.getLogger(CesnetServiceImpl.class);

    private final String rootDir = "VO_storage-cache_tape";

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Provider<SshChannel> sshResourceProvider;

    @Autowired
    Provider<SftpChannel> sftpChannelChannelProvider;

    @Override
    public List<String> getFileList() {
        SshChannel sshChannel = sshResourceProvider.get();

        List<String> result = sshChannel.sendCommand("dmls -l " + rootDir);
        return result;
    }

    @Override
    public InputStream getFile(String filename) throws SftpException, IOException {
        SftpChannel sftpChannel = sftpChannelChannelProvider.get();
        sftpChannel.cd(rootDir);
        return sftpChannel.getFile(filename);
    }

    @Override
    public void uploadFile(InputStream fileStream, String filename) throws SftpException {
        SftpChannel sftpChannel = sftpChannelChannelProvider.get();
        sftpChannel.cd(rootDir);
        sftpChannel.uploadFile(fileStream, filename);
    }

}
