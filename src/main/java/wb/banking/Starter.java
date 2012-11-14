package wb.banking;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import wb.banking.beans.IngDibaFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: martinh
 * Date: 13.04.12
 * Time: 00:42
 * To change this template use File | Settings | File Templates.
 */
public class Starter {
    
    public static void main (String[] strings) throws Exception {
        CamelContext context = new DefaultCamelContext();
        Banking banking = new Banking();
        IngDibaFilter filter = new IngDibaFilter();
        filter.setAccountMap(createAccountMap());
        banking.setDibaFilter(filter);
        context.addRoutes(banking);
        context.start();
        Thread.sleep(60000);
    }

    private static Map<String, String> createAccountMap () {
        Map<String, String> m = new HashMap<String, String>();
        m.put("1", "Konto1");
        m.put("2", "Konto2");
        m.put("3", "Konto3");
        m.put("4", "Konto4");
        m.put("5", "Konto5");
        m.put("6", "Konto6");
        return m;
    }
    
}
