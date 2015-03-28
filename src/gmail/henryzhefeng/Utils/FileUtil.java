package gmail.henryzhefeng.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by �� on 3/28/2015.
 * ������Ҫ������ļ��ж�ȡ�������
 */
public class FileUtil {

    /**
     * ��ȡSourcesAndSinks�ļ��е�����sink��
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
