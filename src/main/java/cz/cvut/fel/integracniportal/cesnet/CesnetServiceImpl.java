package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.io.InputStream;
import java.util.List;

@Service
public class CesnetServiceImpl implements CesnetService {

    private static final Logger logger = Logger.getLogger(CesnetServiceImpl.class);

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Provider<SshChannel> sshResourceProvider;

    @Autowired
    Provider<SftpChannel> sftpChannelChannelProvider;

    @Override
    public List<String> getFileList() {
        SshChannel sshChannel = sshResourceProvider.get();

        List<String> result = sshChannel.sendCommand("dmls -l VO_storage-cache_tape");
        return result;
    }

    @Override
    public InputStream getFile(String filename) throws SftpException {
        SftpChannel sftpChannel = sftpChannelChannelProvider.get();
        return sftpChannel.getFile(filename);
    }

}
