package wb.banking.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: martinh
 * Date: 03.06.12
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class ReformatProcessor implements Processor {

    public static DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yy");

    StringTokenizer tok = new StringTokenizer(",");

    @Override
    public void process(Exchange e) throws Exception {

        String body = e.getIn().getBody(String.class);
        body = body.replaceAll("\\[", "");
        body = body.replaceAll("\\]", "");
        StringTokenizer tok = new StringTokenizer(body, ",");
        Date date=null;
        String account=null;
        String money=null;
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            token = token.trim();
            if (token.contains("key:")) {
                String sub =token.substring(token.indexOf(":")+1);
                date = new Date(Long.parseLong(sub.trim()));
            } else if (token.contains("value:")) {
                account =token.substring(token.indexOf(":")+1);
            } else if (token.contains("id:")) {
            } else {
                if (token.length()>0 ) {
                    try {
                        Double.parseDouble(token);
                        money = token;
                    } catch (NumberFormatException nfe) {
                    }
                }
            }

        }
        e.getIn().setHeader("Account", account);
        if (date==null)return;
        String dateString = dateFormatter.format(date);
        String newline = dateString + "," + account + "," + money;
        e.getIn().setBody(newline);
        e.getIn().setHeader("Account", account);
    }

}
