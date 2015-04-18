package gmail.henryzhefeng;

import gmail.henryzhefeng.Models.FileSinkInfo;
import gmail.henryzhefeng.Utils.Constants;
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


        // 构造matlab的quadprog命令
        File outDir = new File("./outputDir");
        if (!outDir.exists()) outDir.mkdir();
        try {
            File matlab = new File("./outputDir/matlab.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(matlab, false));
            // 获取指定的样本值
            Map<String, Integer> assignedScores = FileUtil.readAssignedScores("./inputDir/assignment.txt");
            // 构造H矩阵，和F矩阵
            long[][] H = new long[sinks.length][sinks.length];
            long[] F = new long[sinks.length];
            for (FileSinkInfo info : sinkInfos) {
                for (int i = 0; i < sinks.length; i++) {
                    for (int j = 0; j < sinks.length; j++) {
                        H[i][j] += 2 * info.sinks[i] * info.sinks[j];
                    }
                }
                for (int i = 0; i < sinks.length; i++) {
                    F[i] += -2 * assignedScores.get(info.apkName) * info.sinks[i];
                }
            }
            bw.write("H=[");
            for (int row = 0; row < H.length; row++) {
                for (int col = 0; col < H.length; col++) {
                    bw.write(String.valueOf(H[row][col]));
                    bw.write(col == H.length - 1 ? ";" : ",");
                }
            }
            bw.write("]");
            bw.newLine();
            bw.write("F=[");
            for (int i = 0; i < sinks.length; i++) {
                bw.write(String.valueOf(F[i]));
                if (i != sinks.length - 1) bw.write(";");
            }
            bw.write("]");
            bw.newLine();
            // 构造L矩阵
            double[] L = new double[sinks.length];
            for (int i = 0; i < L.length; i++) {
                L[i] = Constants.PARAM_LOW_BOUND;
            }
            bw.write("L=[");
            for (int i = 0; i < L.length; i++) {
                bw.write(String.valueOf(L[i]));
                if (i != L.length - 1) bw.write(";");
            }
            bw.write("]");
            bw.newLine();
            // 写入命令
            bw.write("[x, value]=quadprog(H, F,[],[],[],[],L,[])");
            // 关闭文件
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println("Write matlab file failed!");
            return;
        }

//        // 构造matlab的命令，用于regress命令
//        File outDir = new File("./outputDir");
//        if (!outDir.exists()) outDir.mkdir();
//        try {
//            File matlab = new File("./outputDir/matlab.txt");
//            BufferedWriter bw = new BufferedWriter(new FileWriter(matlab, false));
//            // 构造y参数
//            Map<String, Integer> assignedScores = FileUtil.readAssignedScores("./inputDir/assignment.txt");
//            bw.write("y=[");
//            for (FileSinkInfo info : sinkInfos) {
//                bw.write(assignedScores.get(info.apkName) + " ");
//            }
//            bw.write("]");
//            bw.newLine();
//            // 构造x参数
//            for (int i = 0; i < sinks.length; i++) {
//                StringBuilder builder = new StringBuilder();
//                builder.append("x" + (i + 1));
//                builder.append("=[");
//                for (FileSinkInfo info : sinkInfos) {
//                    builder.append(String.valueOf(info.sinks[i]) + " ");
//                }
//                builder.append("]");
//                bw.write(builder.toString());
//                bw.newLine();
//            }
//            // 构造相关变量并调用命令
//            bw.write("X = [ones(" + sinkInfos.size() + ",1)");
//            for (int i = 0; i < sinks.length; i++) {
//                bw.write(",x" + (i + 1) + "'");
//            }
//            bw.write("]");
//            bw.newLine();
//            bw.write("[b,bint,r,rint,s]=regress(y',X)");
//            // 关闭文件
//            bw.flush();
//            bw.close();
//        } catch (Exception ex) {
//            System.out.println("Error: Writing matlab.txt failed!");
//            return;
//        }

        // 使用matlab的监督学习替代，自己写的效果不好
//        Learner learner = new LinearLearner(sinkInfos);
//        learner.startLearning();
    }
}
