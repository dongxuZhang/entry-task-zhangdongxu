package service;

import static constant.OsmFileConstant.INDEX_COUNTRY_MAP;
import static constant.OsmFileConstant.OSM_FILE_PATH;

import crosby.binary.osmosis.OsmosisReader;
import java.io.FileInputStream;
import org.apache.commons.lang3.StringUtils;

public class TestService2 {

    public static void main(String[] args) throws InterruptedException {
        long begin = System.currentTimeMillis();
        TestService2 testService2 = new TestService2();
        Handler2 handler2 = new Handler2();
//        for (int i = 0; i < 1; i++) {
//            testService.test(i, handler);
//        }
        testService2.test(7, handler2);
        long end = System.currentTimeMillis();
        System.out.println("totalTimeCost:" + (end - begin));
    }

    private void test(int index, Handler2 handler) {
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
            reader1.setSink(handler);
            reader1.run();
            //step2
            FileInputStream fileInputStream2 = new FileInputStream(filePath);
            OsmosisReader reader2 = new OsmosisReader(fileInputStream2);
            reader2.setSink(handler);
            reader2.run();
            long end = System.currentTimeMillis();
            System.out.println("countryName:" + countryName + " timeCost:" + (end - begin));
            System.out.println(
                "countryName:" + countryName + " roadSize:" + handler.getRoadSize());
            System.out.println(
                "countryName:" + countryName + " roadLength:" + handler.getRoadLength());
            System.out.println("totalLength now:" + handler.getTotalLength());
            handler.flush();
        } catch (Exception ignored) {

        } finally {
            handler.flush();
        }
    }
}
