package gmail.henryzhefeng.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 哲 on 3/28/2015.
 * 本类主要负责从文件中读取相关内容
 */
public class FileUtil {

    private static final String RESULT_EXTENSION = "result";

    /**
     * 获取SourcesAndSinks文件中的所有sink。
     */
    public static String[] getAllSinksFromDefinition(String filePath) {
        try {
            List<String> rst = new LinkedList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String sink = StringUtil.getSinkFromDefinition(line);
                if (sink != null) {
                    rst.add(sink);
                }
            }
            return rst.toArray(new String[rst.size()]);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 从结果文件中读取信息
     *
     * @return 返回的是个int数组，数组的下标对应的是sink的ID值，而数组元素的的值表示的是该sink对应多少个source。
     * 如果出问题，则返回null
     */
    public static int[] getResult(String filePath) {
        try {
            int[] result = new int[DataUtil.getSinkCount()];
            BufferedReader bd = new BufferedReader(new FileReader(filePath));
            String line = null;
            int id = -1;
            int cnt = 0;
            while ((line = bd.readLine()) != null) {
                String sink = StringUtil.getSinkFromResult(line);
                // this line contains a sink.
                if (sink != null) {
                    // record the cnt of sources for the previous sink
                    if (id > 0) {
                        result[id] = cnt;
                    }
                    id = DataUtil.getId(sink);
                    cnt = 0;
                }
                // this line contains a source
                else {
                    ++cnt;
                }
            }
            return result;
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * 判断是否是result文件
     */
    public static boolean isResultFile(String fileName) {
        int dot = fileName.lastIndexOf('.');
        String extension = fileName.substring(dot + 1, fileName.length());
        if (extension.equals(RESULT_EXTENSION)) {
            return true;
        } else {
            return false;
        }
    }

}
