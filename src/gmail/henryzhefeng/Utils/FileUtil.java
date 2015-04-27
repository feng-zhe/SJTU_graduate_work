package gmail.henryzhefeng.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by ем on 3/28/2015.
 */
public class FileUtil {

    private static final String RESULT_EXTENSION = "result";

    /**
     * get all sinks from SourcesAndSinks
     */
    public static String[] getAllSinksFromDefinition(String filePath) {
        try {
            List<String> rst = new LinkedList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String sink = StringUtil.parseSinkFromDefinitionLine(line);
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
     * Read sinks and corresponding sources from result file
     *
     * @return the subscript is the id of sinks, the value is the count of sources for the sink
     */
    public static int[] getResult(String filePath) {
        try {
            int[] result = new int[DataUtil.getSinkCount()];
            BufferedReader bd = new BufferedReader(new FileReader(filePath));
            String line = null;
            int id = -1;
            int cnt = 0;
            while ((line = bd.readLine()) != null) {
                String sink = StringUtil.parseSinkFromResultLine(line);
                // this line contains a sink.
                if (sink != null) {
                    // record the cnt of sources for the previous sink
                    if (id > 0) {
                        result[id] = cnt;
                    }
                    id = DataUtil.getId(sink);
                    cnt = 0;
                } else {
                    if (id != -1) {
                        // this line contains a source
                        ++cnt;
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * Determind whether this is a result file
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

    /**
     * Get the socres we assigned to each file
     *
     * @param filePath
     * @return
     */
    public static Map<String, Integer> readAssignedScores(String filePath) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        try {
            BufferedReader bd = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = bd.readLine()) != null) {
                String[] splits = line.split("\\s+");
                result.put(splits[0], Integer.parseInt(splits[1]));
            }
        } catch (Exception ex) {
            return null;
        }
        return result;
    }

}
