package gmail.henryzhefeng.Tests;

import gmail.henryzhefeng.Utils.FileUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ем on 3/28/2015.
 */
public class FileUtilTest {

    @Test
    public void testGetAllSinks() throws Exception {
        assertEquals(154, FileUtil.getAllSinksFromDefinition("SourcesAndSinks.txt").length);
    }
}