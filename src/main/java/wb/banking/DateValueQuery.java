package wb.banking;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: martinh
 * Date: 05.05.12
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
public class DateValueQuery extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        String uri = Banking.outputEndpoint + "_design/date_value/_view/date_value?key=${header.timeValue}";
        System.out.println(uri);
        from("direct:datevaluequery")
                .setHeader(Exchange.HTTP_URI, simple(uri))
               .inOut("http://dummyhost");
    }

}
