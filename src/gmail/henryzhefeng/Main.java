package gmail.henryzhefeng;

import gmail.henryzhefeng.Models.FileSinkInfo;
import gmail.henryzhefeng.Utils.DataUtil;
import gmail.henryzhefeng.Utils.FileUtil;
import gmail.henryzhefeng.Utils.StringUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // 获取所有sink列表
        String[] sinks = FileUtil.getAllSinksFromDefinition("SourcesAndSinks.txt");
        DataUtil.initData(sinks);

        // 分析当前目录下的所有result文件，并保存结果
        List<FileSinkInfo> sinkInfos = new ArrayList<FileSinkInfo>();
        File dir = new File("./inputDir");
        File[] files = dir.listFiles();
        // for test
        for (File file : files) {
            if (FileUtil.isResultFile(file.getName())) {
                FileSinkInfo info = new FileSinkInfo();
                info.sinks = FileUtil.getResult(file.getPath());
                info.apkName = StringUtil.getNameFromString(file.getName());
                sinkInfos.add(info);
            }
        }

        // 构造matlab的输入格式的文件
        File outDir = new File("./outputDir");
        if (!outDir.exists()) outDir.mkdir();
        try {
            File matlab = new File("./outputDir/matlab.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(matlab, false));
            // 构造y参数
            Map<String, Integer> assignedScores = FileUtil.readAssignedScores("./inputDir/assignment.txt");
            bw.write("y=[");
            for (FileSinkInfo info : sinkInfos) {
                bw.write(assignedScores.get(info.apkName) + " ");
            }
            bw.write("]");
            bw.newLine();
            // 构造x参数
            for (int i = 0; i < sinks.length; i++) {
                StringBuilder builder = new StringBuilder();
                builder.append("x" + (i + 1));
                builder.append("=[");
                for (FileSinkInfo info : sinkInfos) {
                    builder.append(String.valueOf(info.sinks[i]) + " ");
                }
                builder.append("]");
                bw.write(builder.toString());
                bw.newLine();
            }
            // 构造相关变量并调用命令
            bw.write("X = [ones(" + sinkInfos.size() + ",1)");
            for (int i = 0; i < sinks.length; i++) {
                bw.write(",x" + (i + 1) + "'");
            }
            bw.write("]");
            bw.newLine();
            bw.write("[b,bint,r,rint,s]=regress(y',X)");
            // 关闭文件
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println("Error: Writing matlab.txt failed!");
            return;
        }

        // 使用matlab的监督学习替代，自己写的效果不好
//        Learner learner = new LinearLearner(sinkInfos);
//        learner.startLearning();
    }
}
