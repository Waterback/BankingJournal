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

    private String outputEndpoint;


    @Override
    public void configure() throws Exception {

        from("direct:datevaluequery")
               .setHeader(Exchange.HTTP_QUERY, simple("key=${header.timeValue}"))
               .inOut("{{general.http.databaseUri}}/_design/date_value/_view/date_value");
    }

    public void setOutputEndpoint(String outputEndpoint) {
        this.outputEndpoint = outputEndpoint;
    }
}
