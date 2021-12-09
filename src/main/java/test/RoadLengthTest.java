package test;

import static constant.OsmFileConstant.INDEX_COUNTRY_MAP;
import static constant.OsmFileConstant.OSM_FILE_PATH;

import crosby.binary.osmosis.OsmosisReader;
import java.io.FileInputStream;
import org.apache.commons.lang3.StringUtils;
import service.RoadLengthHandler;

public class RoadLengthTest {

    public static void main(String[] args) throws InterruptedException {
        long begin = System.currentTimeMillis();
        RoadLengthTest roadLengthTest = new RoadLengthTest();
        RoadLengthHandler roadLengthHandler = new RoadLengthHandler();
        for (int i = 0; i < 7; i++) {
            roadLengthTest.test(i, roadLengthHandler);
        }
        long end = System.currentTimeMillis();
        System.out.println("totalTimeCost:" + (end - begin));
    }

    private void test(int index, RoadLengthHandler roadLengthHandler) {
        try {
            String countryName = INDEX_COUNTRY_MAP.get(index);
            if (StringUtils.isBlank(countryName)) {
                return;
            }
            String filePath = OSM_FILE_PATH.get(countryName);
            if (StringUtils.isBlank(filePath)) {
                return;
            }
            long begin = System.currentTimeMillis();
            //step1
            FileInputStream fileInputStream1 = new FileInputStream(filePath);
            OsmosisReader reader1 = new OsmosisReader(fileInputStream1);
            reader1.setSink(roadLengthHandler);
            reader1.run();
            //step2
            FileInputStream fileInputStream2 = new FileInputStream(filePath);
            OsmosisReader reader2 = new OsmosisReader(fileInputStream2);
            reader2.setSink(roadLengthHandler);
            reader2.run();
            long end = System.currentTimeMillis();
            System.out.println("countryName:" + countryName + " timeCost:" + (end - begin));
            System.out.println(
                "countryName:" + countryName + " roadSize:" + roadLengthHandler.getRoadSize());
            System.out.println(
                "countryName:" + countryName + " roadLength:" + roadLengthHandler.getRoadLength());
            System.out.println("totalLength now:" + roadLengthHandler.getTotalLength());
            roadLengthHandler.flush();
        } catch (Exception ignored) {

        } finally {
            roadLengthHandler.flush();
        }
    }
}
