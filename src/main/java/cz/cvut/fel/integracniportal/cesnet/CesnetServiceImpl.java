package cz.cvut.fel.integracniportal.cesnet;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class CesnetServiceImpl implements CesnetService {

    private static final Logger logger = Logger.getLogger(CesnetServiceImpl.class);

    @Value(value = "${ssh.server}")
    private String sshServer;
    @Value(value = "${ssh.port}")
    private int sshPort;
    @Value(value = "${ssh.user}")
    private String sshUser;
    @Value(value = "${ssh.password}")
    private String sshPassword;

    @Override
    public List<String> getFiles() {
        List<String> result = new ArrayList<String>();

        List<String> commands = new ArrayList<String>();
        commands.add("ls -la");

        try {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(sshUser, sshServer, sshPort);
            session.setPassword(sshPassword);
            session.setConfig(config);
            session.connect();


            Channel channel=session.openChannel("exec");
            // TODO: run more commands
            ((ChannelExec)channel).setCommand(commands.get(0));
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);

            // Read response
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    stringBuilder.append(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    logger.error("exit-status: " + channel.getExitStatus());
                    break;
                }
                try{
                    Thread.sleep(100);
                } catch (Exception ee) {}
            }
            result.add(stringBuilder.toString());

            // Cleanup
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }
}
