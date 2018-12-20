package application.channel;

import application.starter.FCMRunTimeConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelModel {

    private static final Logger log = LoggerFactory.getLogger(ChannelModel.class);
    private static final FCMRunTimeConfig globalConfig = FCMRunTimeConfig.getInstance();

    private List<ChannelInfo> channelInfos = new ArrayList<>();

    private Gson gson;

    private String channelInfospath;

    public ChannelModel() {
        gson = new Gson();
        channelInfospath = globalConfig.getProjectConfigFolder()
                + File.separator + "channelInfos.json";
        loadInfos();
    }

    public void saveInfos() throws Exception {
        List<ChannelInfoPojo> pojos = channelInfos.stream()
                .map(info -> toChannelInfoPojo(info))
                .collect(Collectors.toList());
        Files.write(Paths.get(channelInfospath), gson.toJson(pojos).getBytes());
    }

    private ChannelInfoPojo toChannelInfoPojo(ChannelInfo info) {
        ChannelInfoPojo res = new ChannelInfoPojo();

        res.setChannelId(info.getChannelId());
        res.setChannelName(info.getChannelName());
        res.setVoltage(info.getVoltage());
        res.setThreshold(info.getThreshold());
        res.setPeakPolicy(info.getPeakPolicy());

        return res;
    }

    private ChannelInfo toChannelInfo(ChannelInfoPojo pojo) {
        ChannelInfo res = new ChannelInfo();

        res.setChannelId(pojo.getChannelId());
        res.setChannelName(pojo.getChannelName());
        res.setVoltage(pojo.getVoltage());
        res.setThreshold(pojo.getThreshold());
        res.setPeakPolicy(pojo.getPeakPolicy());

        return res;
    }

    public void loadInfos() {
        try {
            createJsonFileIfNotExist();
            List<ChannelInfoPojo> pojos = gson.fromJson(new FileReader(channelInfospath),
                    new TypeToken<List<ChannelInfoPojo>>(){}.getType());
            if (pojos != null) {
                channelInfos = pojos.stream()
                        .map(pojo -> toChannelInfo(pojo))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Failed to load information: " + e.getMessage());
        }

    }

    private void createJsonFileIfNotExist() throws IOException {
        Path target = Paths.get(channelInfospath);
        if (!Files.exists(target)) {
            Files.write(target, "".getBytes());
        }
    }

    public void addChannelInfo(ChannelInfo info) {
        channelInfos.add(info);
    }

    public void removeChannelInfo(ChannelInfo info) {
        channelInfos.remove(info);
    }

    public List<ChannelInfo> getChannelInfos() {
        return channelInfos;
    }
}
