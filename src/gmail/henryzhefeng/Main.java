package gmail.henryzhefeng;

import gmail.henryzhefeng.Leaner.Learner;
import gmail.henryzhefeng.Leaner.LinearLearner;
import gmail.henryzhefeng.Models.FileSinkInfo;
import gmail.henryzhefeng.Utils.DataUtil;
import gmail.henryzhefeng.Utils.FileUtil;
import gmail.henryzhefeng.Utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 初始化sinks
        String[] sinks = FileUtil.getAllSinksFromDefinition("SourcesAndSinks.txt");
        DataUtil.initData(sinks);

        // 分析当前目录下的所有result文件，并保存结果
        List<FileSinkInfo> sinkInfos = new ArrayList<FileSinkInfo>();
        File dir = new File(".");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (FileUtil.isResultFile(file.getName())) {
                FileSinkInfo info = new FileSinkInfo();
                info.sinks = FileUtil.getResult("24pi.apk.result");
                info.apkName = StringUtil.getNameFromString(file.getName());
            }
        }

        // 学习
        Learner learner = new LinearLearner(sinkInfos);
        learner.startLearning();
    }
}
