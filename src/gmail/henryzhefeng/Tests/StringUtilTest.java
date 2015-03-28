package gmail.henryzhefeng.Tests;

import gmail.henryzhefeng.Utils.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by ем on 3/28/2015.
 */
public class StringUtilTest {

    private static final String STRING_SINK = "Found a flow to sink interfaceinvoke $r4.<android.content.SharedPreferences$Editor: android.content.SharedPreferences$Editor putString(java.lang.String,java.lang.String)>(\"appid\", $r1) on line 492, from the following sources:";
    private static final String STRING_SOURCE = "- $r1 = virtualinvoke $r0.<com.dlj24pi.android.SecondEnterActivity: android.view.View findViewById(int)>(2131099660) (in <com.dlj24pi.android.SecondEnterActivity: void init()>)";

    @Test
    public void testGetSinkStr() throws Exception {
        assertNotEquals(StringUtil.getSinkStr(STRING_SINK), null);
        assertEquals(StringUtil.getSinkStr(STRING_SOURCE), null);
        assertEquals(StringUtil.getSinkStr("hello < sssssdfdsa "), null);
        assertEquals(StringUtil.getSinkStr("Found a flow to sink interfaceinvoke $r4.<android.content.SharedPreferences "), null);
        assertNotEquals(StringUtil.getSinkStr("Found a flow to sink interfaceinvoke $r4.<android.content.SharedPreferences>"), null);
    }

    @Test
    public void TestIsSource() {
        assertEquals(StringUtil.isSource(STRING_SOURCE), true);
        assertEquals(StringUtil.isSource(STRING_SINK), false);
    }
}