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

    private static final String RESULT_EXTENSION = "result";

    /**
     * ��ȡSourcesAndSinks�ļ��е�����sink��
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
     * �ӽ���ļ��ж�ȡ��Ϣ
     *
     * @return ���ص��Ǹ�int���飬������±��Ӧ����sink��IDֵ��������Ԫ�صĵ�ֵ��ʾ���Ǹ�sink��Ӧ���ٸ�source��
     * ��������⣬�򷵻�null
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
     * �ж��Ƿ���result�ļ�
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
