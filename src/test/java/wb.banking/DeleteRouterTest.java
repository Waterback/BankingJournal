package wb.banking;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.dataset.DataSet;
import org.apache.camel.component.dataset.SimpleDataSet;
import org.apache.camel.impl.DefaultConsumerTemplate;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wb.banking.exceptions.DuplicateFileException;
import wb.banking.processors.IngDibaDateEvaluator;
import wb.banking.processors.StringRemover;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: martinh
 * Date: 26.06.12
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */

public class DeleteRouterTest extends CamelSpringTestSupport {

    @Before
    public void setup () throws Exception{
        super.setUp();
        context.addRoutes(new DeleteRouter());
    }

    @Test
    public void fillWithDataSet () throws Exception {
        context.addRoutes(new FillRouter());
        Thread.sleep(3000);
    }

    //@Ignore
    //@Test
    public void deleteIt() throws Exception {
        template().sendBody("seda:startme", "");
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("test-camel-context.xml");
    }

    public class FillRouter extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("dataset:myDataSet").to("seda:input");
        }
    }

    public class DeleteRouter extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("seda:startme")
                    .setHeader("CamelHttpMethod", constant("GET"))
                    .to("http://localhost:5984/showaccounts/_all_docs")
                    .convertBodyTo(String.class)
                    .process(new StringRemover(new String[]{"\"", "\\[", "\\]"}))
                    .unmarshal().csv()
                    .split(body(List.class))
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            String body = exchange.getIn().getBody(String.class);
                            if (body.contains("id") && !body.contains("_design")) {
                                try {
                                    String id = body.substring(1, (body.indexOf("value") - 1));
                                    id = id.replaceAll("id", "\"id\"");
                                    id = id.replaceAll("key", "\"key\"");
                                    id = id.replaceAll(":", ":\"");
                                    id = id.replaceAll(",", "\",");
                                    int ind = id.lastIndexOf(",");
                                    id = id.substring(0, ind);
                                    id += "}";
                                    System.out.println("\nID: " + id + "\n");
                                    exchange.getIn().setBody(String.format("http://localhost:5984/showaccounts/${in.header.body}", id));
                                    System.out.println("Body:" + exchange.getIn().getBody(String.class));
                                    exchange.getIn().setHeader("removable", "false");
                                } catch (StringIndexOutOfBoundsException sie) {
                                }
                            } else {
                                exchange.getIn().setHeader("removable", "false");
                            }
                        }
                    })
                    .filter(header("removable").isEqualTo("true"))
                    .log("going to remove:" + simple("${in.header.body}"))
                    .setHeader("CamelHttpMethod", constant("DELETE"))
                    .setHeader(Exchange.HTTP_URI, simple("${in.header.body}"))
                    .to("http://dummyhost");


        }
    }

}
