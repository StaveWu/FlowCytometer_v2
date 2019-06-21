package application.channel.model;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ChannelSeriesRepositoryTest {

    @Test
    public void appendSeries() {
        ChannelSeriesRepository repository = new ChannelSeriesRepository();
        repository.setLocation("E:\\04文档\\陈宇欣\\流式细胞仪\\软件项目树测试\\1.txt");
        List<ChannelSeries> seriesList = new ArrayList<>();
        ChannelSeries series = new ChannelSeries("HEY", new ArrayList<>(Arrays.asList(1., 2., 3.)));
        seriesList.add(series);
        try {
            repository.appendSeries(seriesList);
            repository.appendSeries(seriesList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}