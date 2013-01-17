package wb.banking;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import wb.banking.beans.IngDibaFilter;
import wb.banking.exceptions.DuplicateFileException;
import wb.banking.processors.DupChecker;
import wb.banking.processors.IngDibaDateEvaluator;
import wb.banking.processors.StringRemover;

import java.util.List;

public class Banking extends RouteBuilder {
	
    private String inputFileEndpoint = "{{banking.file.inputendpoint}}?delete=true";
    private String outputEndpoint = "{{general.http.databaseUri}}";
    private String archiveEndpoint = "{{banking.file.archive}}";

	private IngDibaFilter dibaFilter;

	@Override
	public void configure() throws Exception {

        onException(DuplicateFileException.class)
                .handled(true)
                .log("Diagnosed Duplicate File Input")
                .to(archiveEndpoint);


        from(inputFileEndpoint)
                .routeId("csvhandling")
                .convertBodyTo(String.class)
                .transform(body().regexReplaceAll("\"", ""))
                .process(new IngDibaDateEvaluator())
                .process(new DupChecker())
                .wireTap("seda:fileoutput")
                .unmarshal().csv()
                .split(body(List.class)).streaming()
                .to("seda:singlepartsroute");

        from("seda:singlepartsroute?concurrentConsumers=12")
                .routeId("singleparts")
                .filter().method(dibaFilter, "isRelevant")
                .bean(dibaFilter, "transformBody")
                .filter().method(dibaFilter, "isValidAccountNumber")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader("CamelHttpMethod", constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(outputEndpoint)
                .process(new BodyPrinter());

        from("seda:fileoutput")
                .routeId("archive")
                .filter(simple("${in.header.test} != 'true'"))
                .to(archiveEndpoint);

    }


    public void setInputFileEndpoint(String inputFileEndpoint) {
        this.inputFileEndpoint = inputFileEndpoint;
    }

    public class BodyPrinter implements Processor {
		@Override
		public void process(Exchange e) throws Exception {
			String body = e.getIn().getBody(String.class);
			System.out.println(body);
		}
	}
	
	public void setDibaFilter(IngDibaFilter dibaFilter) {
		this.dibaFilter = dibaFilter;
	}

    public void setOutputEndpoint(String outputEndpoint) {
        this.outputEndpoint = outputEndpoint;
    }

    public void setArchiveEndpoint(String archiveEndpoint) {
        this.archiveEndpoint = archiveEndpoint;
    }

    //curl  localhost:5984/accounts/_design/accounts/_view/date_value?key=%5B%225409671467%22,1335302700000%5D
    public void save () {
        onException(DuplicateFileException.class)
                .handled(true)
                .log("Diagnosed Duplicate File Input")
                .to(archiveEndpoint + "/dups");

        from(inputFileEndpoint)
                .routeId("csvhandling")
                .convertBodyTo(String.class)
                .process(new StringRemover())
                .process(new IngDibaDateEvaluator())
                .process(new DupChecker())
                .wireTap("seda:fileoutput")
                .unmarshal().csv()
                .split(body(List.class))
                .to("seda:singlepartsroute");

        from("seda:singlepartsroute")
                .routeId("singleparts")
                .filter().method(dibaFilter, "isRelevant")
                .bean(dibaFilter, "transformBody")
                .marshal().json(JsonLibrary.Jackson)
                .setHeader("CamelHttpMethod", constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to(outputEndpoint)
                .process(new BodyPrinter());

        // outputEndpoint:"http4://localhost:5984/accounts/"

        from("seda:fileoutput")
                .routeId("archive")
                .filter(simple("${in.header.test} != 'true'"))
                .to(archiveEndpoint);


    }
}
