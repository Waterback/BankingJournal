package wb.banking.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by IntelliJ IDEA.
 * User: martinh
 * Date: 12.04.12
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class StringRemover implements Processor {

    private String[] arrayOfRemovables;

    public StringRemover(String ... removables) {
        arrayOfRemovables=removables;
    }

    @Override
    public void process(Exchange e) throws Exception {
        String body = e.getIn().getBody(String.class);
        for (int i = 0; i < arrayOfRemovables.length; i++) {
            String removeMe= arrayOfRemovables[i];
            body = body.replaceAll(removeMe, "");
        }
        e.getIn().setBody(body);
    }
}
