package gmail.henryzhefeng.Tests;

import gmail.henryzhefeng.Utils.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ем on 3/28/2015.
 */
public class StringUtilTest {

    private static final String RESULT_SINK = "Found a flow to sink interfaceinvoke $r4.<android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putString(java.lang.String,java.lang.String)>(\"appid\", $r1) on line 492, from the following sources:";
    private static final String DEFINITION_SINK = "<android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putString(java.lang.String,java.lang.String)> -> _SINK_";
    private static final String RESULT_SOURCE = "- $r1 = virtualinvoke $r0.<com.dlj24pi.android.SecondEnterActivity: android.view.View findViewById(int)>(2131099660) (in <com.dlj24pi.android.SecondEnterActivity: void init()>)";

    @Test
    public void testGetSinkFromResult() throws Exception {
        assertNotEquals(StringUtil.getSinkFromResult(RESULT_SINK), null);
        assertEquals(StringUtil.getSinkFromResult(RESULT_SOURCE), null);
        assertEquals(StringUtil.getSinkFromResult("hello < sssssdfdsa "), null);
        assertEquals(StringUtil.getSinkFromResult("Found a flow to sink interfaceinvoke $r4.<android.content.SharedPreferences "), null);
        assertNotEquals(StringUtil.getSinkFromResult("Found a flow to sink interfaceinvoke $r4.<android.content.SharedPreferences>"), null);
    }

    @Test
    public void testGetSinkFromDefinition() throws Exception {
        assertNotEquals(StringUtil.getSinkFromDefinition(DEFINITION_SINK), null);
        assertEquals(StringUtil.getSinkFromDefinition(RESULT_SINK), null);
        assertEquals(StringUtil.getSinkFromDefinition(RESULT_SOURCE), null);
    }

    @Test
    public void TestIsSource() throws Exception {
        assertEquals(StringUtil.isSource(RESULT_SOURCE), true);
        assertEquals(StringUtil.isSource(RESULT_SINK), false);
    }
}