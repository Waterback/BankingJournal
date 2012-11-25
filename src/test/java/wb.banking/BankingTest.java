package wb.banking;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import wb.banking.beans.IngDibaFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankingTest extends CamelTestSupport {

    private static final String inputEP = "direct:input";

    MockEndpoint mock;
    IngDibaFilter filter;
    
    @Before
    public void setup () throws Exception {
        super.setUp();
        context.addRoutes(new FileCopyRoute());
        context.addRoutes(new DateValueQueryTester());
        filter = new IngDibaFilter();
        filter.setAccountMap(createAccountMap());
    }

    @Test
    public void runTest () throws Exception {
        mock = context.getEndpoint("mock:output", MockEndpoint.class);
        mock.setExpectedMessageCount(6);
        mock.setResultWaitTime(1500);

        Banking banking = new Banking();
        banking.setInputFileEndpoint(inputEP);
        banking.setArchiveEndpoint("mock:archive");
        banking.setOutputEndpoint("mock:output");
        banking.setDibaFilter(filter);
        context.addRoutes(banking);

        mock.assertIsSatisfied();

        List<Exchange> list = mock.getReceivedExchanges();
        Exchange e = list.get(0);
        String expectedDoc = "{\"dateOfInfo\":82860000,\"accountNumber\":\"1\",\"purpose\":\"Konto1\",\"accountType\":\"Girokonto\",\"moneyOTB\":1239.7}";
        Assert.assertEquals(expectedDoc, e.getIn().getBody(String.class));
        
    }

    @Ignore
    @Test
    public void couchDbRequestSuccessTest() throws Exception {
        String o = template.requestBodyAndHeader("direct:datevaluequery", "Dummy", "timeValue", "1335302700000", String.class);
        System.out.println(o);
        Assert.assertTrue(o.contains("\"key\":1335302700000"));
    }

    @Ignore
    @Test
    public void couchDbRequestFailTest() throws Exception {
        String o = template.requestBodyAndHeader("direct:datevaluequery", "Dummy", "timeValue", "1023928392", String.class);
        System.out.println(o);
        Assert.assertFalse(o.contains("\"key\":1335302700000"));
        Assert.assertTrue(o.contains("\"rows\":[]"));
    }



    public class FileCopyRoute extends RouteBuilder {

        @Override
        public void configure() throws Exception {
             from("file:src/data/test/unit?noop=true")
                    .setHeader("Test", constant("true"))
                    .to(inputEP);
        }
    }

    public class DateValueQueryTester extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("direct:datevaluequery")
                    .setBody(simple("justtest"));
        }
    }
    
    private Map<String, String> createAccountMap () {
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