package wb.banking;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
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
public class DeleteRouterTest extends CamelTestSupport {

    @Before
    public void setup () throws Exception{
        super.setUp();
        context.addRoutes(new DeleteRouter());
    }


    @Test
    public void deleteIt() throws Exception {
        template().sendBody("seda:startme", "");
    }

    public class DeleteRouter extends RouteBuilder {

        @Ignore
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
                            if (body.contains("id")) {
                                try {
                                    String id = body.substring(1, (body.indexOf("value") - 1));
                                    id = id.replaceAll("id", "\"id\"");
                                    id = id.replaceAll("key", "\"key\"");
                                    id = id.replaceAll(":", ":\"");
                                    id = id.replaceAll(",", "\",");
                                    int ind = id.lastIndexOf(",");
                                    id = id.substring(0, ind);
                                    id += "}";
                                    System.out.println("\n\nID: " + id + "\n\n");
                                    exchange.getIn().setBody(id);
                                    exchange.getIn().setHeader("removable", true);
                                } catch (StringIndexOutOfBoundsException sie) {
                                }
                            }
                        }
                    })
                    .filter(header("removable").isNotNull())
                    .setHeader("CamelHttpMethod", constant("DELETE"))
//                    .to("http://localhost:5984/showaccounts/")
                    ;

        }
    }

}
