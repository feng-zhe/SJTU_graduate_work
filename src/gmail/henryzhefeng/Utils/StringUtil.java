package gmail.henryzhefeng.Utils;

import java.util.regex.Pattern;

/**
 * Created by ем on 3/28/2015.
 */
public class StringUtil {

    public final static String RESULT_SINK_MARK = "Found a flow to sink";
    public final static String DEFINITION_SINK_MARK = "_SINK_";
    public final static Pattern SOURCE_PATTERN = Pattern.compile("^- \\$.*<.*>.*\\(in <.*>\\)$");

    /**
     * Get the sink part from the result file, if no sink, then return null.
     */
    public static String parseSinkFromResultLine(String line) {
        int index = line.indexOf(RESULT_SINK_MARK);
        // input doesn't contains a sink, return null.
        if (index == -1) {
            return null;
        }
        // input contains a sink
        int indStart = line.indexOf('<');
        int indEnd = line.lastIndexOf('>');
        if (indStart == -1 || indEnd == -1) {
            return null;
        }
        return line.substring(indStart, indEnd + 1);
    }

    public static String parseSinkFromDefinitionLine(String line) {
        int index = line.indexOf(DEFINITION_SINK_MARK);
        // input doesn't contains a sink, return null.
        if (index == -1) {
            return null;
        }
        // input contains a sink
        int indStart = line.indexOf('<');
        int indEnd = line.lastIndexOf('>');
        indEnd = line.lastIndexOf('>', indEnd - 1);
        if (indStart == -1 || indEnd == -1) {
            return null;
        }
        return line.substring(indStart, indEnd + 1);
    }

    /**
     * Judge whether the input line contains source.
     */
    public static boolean isSource(String line) {
        return SOURCE_PATTERN.matcher(line).matches();
    }

    public static String getApkNameFromString(String fileName) {
        int dot = fileName.indexOf('.');
        return fileName.substring(0, dot);
    }
}
