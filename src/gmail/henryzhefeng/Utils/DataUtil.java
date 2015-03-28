package gmail.henryzhefeng.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ем on 3/28/2015.
 */
public class DataUtil {

    private static String[] mIdToName = null;
    private static Map<String, Integer> mNameToId = new HashMap<String, Integer>();

    public static void initData(String[] data) {
        mIdToName = data;
        for (int i = 0; i < mIdToName.length; i++) {
            mNameToId.put(data[i], i);
        }
    }

    public static String getName(int id) {
        return mIdToName[id];
    }

    public static int getId(String name) {
        if (mNameToId.containsKey(name)) {
            return mNameToId.get(name);
        } else {
            return -1;
        }
    }

    public static int getSinkCount() {
        return mIdToName.length;
    }
}
