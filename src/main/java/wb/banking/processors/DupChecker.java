package wb.banking.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import wb.banking.exceptions.DuplicateFileException;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: martinh
 * Date: 05.05.12
 * Time: 00:45
 * To change this template use File | Settings | File Templates.
 */
public class DupChecker implements Processor {

    private ProducerTemplate template = null;

    @Override
    public void process(Exchange exchange) throws Exception {

        Date d = (Date)exchange.getIn().getHeader(IngDibaDateEvaluator.DATEOFDATA);
        Long time = d.getTime();

        System.out.println("DupChecker:" + d + " => " + time.toString());

        String o = getTemplate(exchange).
                requestBodyAndHeader("direct:datevaluequery", "Dummy", "timeValue", time.toString(), String.class);

        if (o.contains("\"key\":" + time.toString())) {
            throw new DuplicateFileException();
        }
        System.out.println("-------------------------   ");

    }

    private ProducerTemplate getTemplate(Exchange exchange) {
        if (template == null) {
            template = exchange.getContext().createProducerTemplate();
        }
        return template;
    }


}
