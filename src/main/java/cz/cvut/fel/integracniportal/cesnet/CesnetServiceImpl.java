package cz.cvut.fel.integracniportal.cesnet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.inject.Provider;
import java.util.List;

@Service
public class CesnetServiceImpl implements CesnetService {

    private static final Logger logger = Logger.getLogger(CesnetServiceImpl.class);

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Provider<SshChannel> sshResourceProvider;

    @Override
    public List<String> getFiles() {
        SshChannel pooledSshResource = sshResourceProvider.get();

        List<String> result = pooledSshResource.sendCommand("dmls -l VO_storage-cache_tape");
        return result;
    }

}
