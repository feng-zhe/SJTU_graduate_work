package gmail.henryzhefeng;

import gmail.henryzhefeng.Utils.DataUtil;
import gmail.henryzhefeng.Utils.FileUtil;

public class Main {

    public static void main(String[] args) {
        // write your code here
        String[] sinks = FileUtil.getAllSinksFromDefinition("SourcesAndSinks.txt");
        DataUtil.initData(sinks);
        int[] result = FileUtil.getResult("24pi.apk.result");
        result = null;
    }
}
