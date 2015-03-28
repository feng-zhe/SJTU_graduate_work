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

    /**
     * 获取SourcesAndSinks文件中的所有sink。
     */
    public static String[] getAllSinks(String filePath) {
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
    
}
