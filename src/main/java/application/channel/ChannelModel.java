package application.channel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelModel {

    private List<ChannelInfo> channelInfos = new ArrayList<>();

    private Gson gson;

    private String channelInfospath = "D:\\files\\文档\\test_project_tree\\12.json";

    public ChannelModel() {
        gson = new Gson();
        loadInfos();
    }

    public void saveInfos() {
        List<ChannelInfoPojo> pojos = channelInfos.stream()
                .map(info -> toChannelInfoPojo(info))
                .collect(Collectors.toList());
        try {
            String json = gson.toJson(pojos);
            Files.write(Paths.get(channelInfospath), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            List<ChannelInfoPojo> pojos = gson.fromJson(new FileReader(channelInfospath),
                    new TypeToken<List<ChannelInfoPojo>>(){}.getType());
            if (pojos != null) {
                channelInfos = pojos.stream()
                        .map(pojo -> toChannelInfo(pojo))
                        .collect(Collectors.toList());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
