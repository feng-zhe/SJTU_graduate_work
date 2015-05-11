package gmail.henryzhefeng;

import gmail.henryzhefeng.Models.FileSinkInfo;
import gmail.henryzhefeng.Models.SinkStatistic;
import gmail.henryzhefeng.Utils.Constants;
import gmail.henryzhefeng.Utils.DataUtil;
import gmail.henryzhefeng.Utils.FileUtil;
import gmail.henryzhefeng.Utils.StringUtil;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        String inputDir = null;
        String outputDir = null;
        boolean isAnalyse = false;
        boolean isCalculate = false;
        boolean isStatistic = false;

        // 处理输入参数
        int i = 0;
        try {
            while (i < args.length) {
                if (args[i].equals("-a")) { // 使用分析功能，生成matlab命令
                    isAnalyse = true;
                } else if (args[i].equals("-s")) { // 使用统计功能
                    isStatistic = true;
                } else if (args[i].equals("-c")) { // 利用matlab的结果来计算值
                    isCalculate = true;
                } else if (args[i].equals("-i")) { // 输入文件夹地址
                    inputDir = args[++i];
                } else if (args[i].equals("-o")) { // 输出文件夹地址
                    outputDir = args[++i];
                }
                ++i;
            }
        } catch (Exception ex) {
            if (i + 1 >= args.length) {
                System.out.println("[ARGS] invalid input arguments, exit!");
                return;
            }
        }

        inputDir = inputDir == null ? "./inputDir" : inputDir;
        outputDir = outputDir == null ? "./outputDir" : outputDir;

        // 根据参数，运行对应代码
        if (isAnalyse) {
            runAnalyse(inputDir, outputDir);
        }

        if (isStatistic) {
            runStatistic(inputDir, outputDir);
        }

        if (isCalculate) {
            runCalculate(inputDir, outputDir);
        }

    }

    private static void runAnalyse(String inputDirStr, String outputDirStr) {

        // 获取所有sink列表
        String[] sinks = FileUtil.getAllSinksFromDefinition(Constants.SINK_SOURCE_FILE_NAME);
        DataUtil.initData(sinks);

        // 分析当前目录下的所有result文件，并保存结果与变量中
        List<FileSinkInfo> sinkInfos = new ArrayList<FileSinkInfo>();
        final String INPUT_DIR = inputDirStr;
        File dir = new File(INPUT_DIR);
        File[] files = dir.listFiles();
        // for test
        for (File file : files) {
            if (FileUtil.isResultFile(file.getName())) {
                FileSinkInfo info = new FileSinkInfo();
                info.sinks = FileUtil.getResult(file.getPath());
                info.apkName = StringUtil.getApkNameFromString(file.getName());
                sinkInfos.add(info);
            }
        }

        // 构造输出文件夹
        final String OUTPUT_DIR = outputDirStr;
        File outDir = new File(OUTPUT_DIR);
        if (!outDir.exists()) outDir.mkdirs();

        // 构造matlab的quadprog命令
        try {
            File matlab = new File(OUTPUT_DIR + "/" + Constants.MATLAB_INPUT_FILE_NAME);
            BufferedWriter bw = new BufferedWriter(new FileWriter(matlab, false));
            // 获取指定的样本值
            Map<String, Integer> assignedScores = FileUtil.readAssignedScores(INPUT_DIR + "/" + Constants.EXAMPLE_FILE_NAME);
            // 构造H矩阵，和F矩阵
            long[][] H = new long[sinks.length][sinks.length];
            long[] F = new long[sinks.length];
            for (FileSinkInfo info : sinkInfos) {
                // 当前的文件是作为样本的
                if (assignedScores.containsKey(info.apkName)) {
                    for (int i = 0; i < sinks.length; i++) {
                        for (int j = 0; j < sinks.length; j++) {
                            H[i][j] += 2 * info.sinks[i] * info.sinks[j];
                        }
                    }
                    for (int i = 0; i < sinks.length; i++) {
                        F[i] += -2 * assignedScores.get(info.apkName) * info.sinks[i];
                    }
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

    private static void runStatistic(String inputDirStr, String outputDirStr) {

        // 获取所有sink列表
        String[] sinks = FileUtil.getAllSinksFromDefinition(Constants.SINK_SOURCE_FILE_NAME);
        DataUtil.initData(sinks);

        // 分析当前目录下的所有result文件，并保存结果与变量中
        List<FileSinkInfo> sinkInfos = new ArrayList<FileSinkInfo>();
        final String INPUT_DIR = inputDirStr;
        File dir = new File(INPUT_DIR);
        File[] files = dir.listFiles();
        // for test
        for (File file : files) {
            if (FileUtil.isResultFile(file.getName())) {
                FileSinkInfo info = new FileSinkInfo();
                info.sinks = FileUtil.getResult(file.getPath());
                info.apkName = StringUtil.getApkNameFromString(file.getName());
                sinkInfos.add(info);
            }
        }

        // 构造输出文件夹
        final String OUTPUT_DIR = outputDirStr;
        File outDir = new File(OUTPUT_DIR);
        if (!outDir.exists()) outDir.mkdirs();

        // 后续会用到的比较操作者，使得在优先级队列中按照降序排列
        Comparator<SinkStatistic> comparator = new Comparator<SinkStatistic>() {
            @Override
            public int compare(SinkStatistic o1, SinkStatistic o2) {
                if (o1.count < o2.count) {
                    return 1;
                } else if (o1.count > o2.count) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        // 对应每个apk的分析结果，保存统计结果到对应的文件中
        try {
            for (FileSinkInfo info : sinkInfos) {
                PriorityQueue<SinkStatistic> queue = new PriorityQueue<SinkStatistic>(info.sinks.length, comparator);
                for (int i = 0; i < info.sinks.length; i++) {
                    SinkStatistic sinkStatistic = new SinkStatistic();
                    sinkStatistic.id = i;
                    sinkStatistic.count = info.sinks[i];
                    queue.add(sinkStatistic);
                }
                File statFile = new File(OUTPUT_DIR + "/" + info.apkName + ".txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(statFile, false));
                while (!queue.isEmpty()) {
                    SinkStatistic sinkStatistic = queue.poll();
                    if (sinkStatistic.count == 0) continue;
                    bw.write(DataUtil.getName(sinkStatistic.id));
                    bw.write("\t");
                    bw.write(String.valueOf(sinkStatistic.count));
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            }
        } catch (Exception ex) {
            System.out.println("[OUTPUT] error: can't write statistic files for each apk!");
            return;
        }

        // 对于所有的输入文件，进行总体上的sink统计
        try {
            int[] summarySinks = new int[sinks.length];
            for (FileSinkInfo info : sinkInfos) {
                for (int i = 0; i < info.sinks.length; i++) {
                    summarySinks[i] += info.sinks[i];
                }
            }
            PriorityQueue<SinkStatistic> queue = new PriorityQueue<SinkStatistic>(summarySinks.length, comparator);
            for (int i = 0; i < summarySinks.length; i++) {
                SinkStatistic sink = new SinkStatistic();
                sink.id = i;
                sink.count = summarySinks[i];
                queue.add(sink);
            }
            File summaryFile = new File(OUTPUT_DIR + "/" + Constants.STATISTIC_SUMMARY_FILE_NAME);
            BufferedWriter bw = new BufferedWriter(new FileWriter(summaryFile, false));
            while (!queue.isEmpty()) {
                SinkStatistic sink = queue.poll();
                if (sink.count == 0) continue;
                bw.write(DataUtil.getName(sink.id));
                bw.write("\t\t");
                bw.write(String.valueOf(sink.count));
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println("[OUTPUT] error: can't write summary statistic files!");
            return;
        }
    }

    private static void runCalculate(String inputDir, String outputDir) {

        // 获取所有sink列表
        String[] sinks = FileUtil.getAllSinksFromDefinition(Constants.SINK_SOURCE_FILE_NAME);
        DataUtil.initData(sinks);

        // 从matlab的结果中，获取相关权重
        double[] weight = new double[sinks.length];
        try {
            File matlabRes = new File(inputDir + "/" + Constants.MATLAB_OUTPUT_FILE_NAME);
            BufferedReader bd = new BufferedReader(new FileReader(matlabRes));
            for (int i = 0; i < weight.length; i++) {
                weight[i] = Double.valueOf(bd.readLine().trim());
            }
            bd.close();
        } catch (IOException ex) {
            System.out.println("[CALC] error in getting weights!");
            return;
        }

        // 获取每个文件的sink统计
        List<FileSinkInfo> sinkInfos = new ArrayList<FileSinkInfo>();
        File dir = new File(inputDir);
        File[] files = dir.listFiles();
        // for test
        for (File file : files) {
            if (FileUtil.isResultFile(file.getName())) {
                FileSinkInfo info = new FileSinkInfo();
                info.sinks = FileUtil.getResult(file.getPath());
                info.apkName = StringUtil.getApkNameFromString(file.getName());
                sinkInfos.add(info);
            }
        }

        // 构造输出文件夹
        File outDir = new File(outputDir);
        if (!outDir.exists()) outDir.mkdirs();

        // 计算每个result文件的值，并写入文件中
        try {
            File valuesFile = new File(outputDir + "/" + Constants.CALCULATE_RESULT_FILE_NAME);
            BufferedWriter bw = new BufferedWriter(new FileWriter(valuesFile));
            for (FileSinkInfo info : sinkInfos) {
                bw.write(info.apkName);
                bw.write("\t\t");
                double value = 0;
                for (int i = 0; i < info.sinks.length; i++) {
                    value += info.sinks[i] * weight[i];
                }
                bw.write(String.valueOf(value));
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            System.out.println("[OUT] error in writing calculated values into file!");
            return;
        }
    }

}
