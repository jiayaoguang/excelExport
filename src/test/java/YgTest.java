import org.junit.Test;
import org.jyg.excel.TemplateUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang on 2021/6/12
 */
public class YgTest {

    @Test
    public void testSplite(){
        System.out.println("1|2|3|4".split("\\|")[0]);
    }

    @Test
    public void testFtl(){
        Map<String,String> map = new HashMap<>();
        map.put("age","int");
        byte[] bs = TemplateUtil.createJavaBytes("Hello",map);
        System.out.println(new String(bs));
    }

}
