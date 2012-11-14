package wb.banking;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import wb.banking.beans.IngDibaFilter;
import wb.banking.processors.ReformatProcessor;
import wb.banking.processors.StringRemover;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: martinh
 * Date: 29.05.12
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
public class QueryRouter extends RouteBuilder {

    /**
     * Camel-Endpoint-String for a Timer defined after cron-rules. It starts
     * every minute.
     */
//    private String timerStart = "quartz://zs3vsl/pserunchecker?cron=0+*/5+*+*+*+?+*";
    private String timerStart = "timer://foo?fixedRate=true&period=120000";
    private IngDibaFilter dibaFilter;
    private String outputFileEndpoint = "file:///Users/martinh/Documents/Allgemein/BankingSources/data/output";

    @Override
    public void configure() throws Exception {


        String uri = Banking.outputEndpoint + "_design/eval_query/_view/eval_query";

        System.out.println(uri);

        from(timerStart)
                .setHeader(Exchange.HTTP_URI, simple(uri))
                .to("http://dummy")
                .process(new StringRemover(new String[]{"\"", "\\]", "\\[", "\\}", "\\{" }))
                .unmarshal().csv()
                .split(body(List.class))
                .process(new ReformatProcessor())
                .filter().method(dibaFilter, "isAggregateable")
                .aggregate(header("Account"), new StringAggregationStrategy()).completionTimeout(3000)
                .setHeader("CamelFileName", simple("${in.header.Account}.csv"))
                .to(outputFileEndpoint);

    }


    class StringAggregationStrategy implements AggregationStrategy {

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }

            String oldBody = oldExchange.getIn().getBody(String.class);
            String newBody = newExchange.getIn().getBody(String.class);
            oldExchange.getIn().setBody(oldBody + ",\n" + newBody);
            return oldExchange;
        }
    }

    public void setDibaFilter(IngDibaFilter dibaFilter) {
        this.dibaFilter = dibaFilter;
    }

}
