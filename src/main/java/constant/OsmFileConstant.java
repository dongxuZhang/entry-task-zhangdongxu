package constant;

import java.util.HashMap;
import java.util.Map;

public class OsmFileConstant {

    public static final String FILE_PATH_PREFIX = "/Users/dongxu.zhang/Downloads/osm/";

    public static final String ID = "ID";
    public static final String OSM_ID_FILE_NAME = "indonesia-latest.osm.pbf";

    public static final String PH = "PH";
    public static final String OSM_PH_FILE_NAME = "philippines-latest.osm.pbf";

    public static final String MY = "MY";
    public static final String OSM_MY_FILE_NAME = "malaysia-singapore-brunei-latest.osm.pbf";

    public static final String TH = "TH";
    public static final String OSM_TH_FILE_NAME = "thailand-latest.osm.pbf";

    public static final String TW = "TW";
    public static final String OSM_TW_FILE_NAME = "taiwan-latest.osm.pbf";

    public static final String VN = "VN";
    public static final String OSM_VN_FILE_NAME = "vietnam-latest.osm.pbf";

    public static final String BR = "BR";
    public static final String OSM_BR_FILE_NAME = "brazil-latest.osm.pbf";

    public static final String SG = "SG";
    public static final String OSM_SG_FILE_NAME = "malaysia-singapore-brunei-latest.osm.pbf";

    public static final Map<Integer, String> INDEX_COUNTRY_MAP = new HashMap<>() {{
        put(0, TW);
        put(1, SG);
        put(2, MY);
        put(3, TH);
        put(4, VN);
        put(5, PH);
        put(6, ID);
        put(7, BR);
    }};

    public static final Map<String, String> OSM_FILE_PATH = new HashMap<>()  {{
        put(OsmFileConstant.ID,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_ID_FILE_NAME);
        put(OsmFileConstant.PH,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_PH_FILE_NAME);
        put(OsmFileConstant.MY,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_MY_FILE_NAME);
        put(OsmFileConstant.TH,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_TH_FILE_NAME);
        put(OsmFileConstant.TW,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_TW_FILE_NAME);
        put(OsmFileConstant.VN,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_VN_FILE_NAME);
        put(OsmFileConstant.BR,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_BR_FILE_NAME);
        put(OsmFileConstant.SG,
            OsmFileConstant.FILE_PATH_PREFIX + OsmFileConstant.OSM_SG_FILE_NAME);
    }};
}
