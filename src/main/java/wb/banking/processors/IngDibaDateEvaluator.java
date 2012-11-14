package wb.banking.processors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import wb.banking.Banking;
import wb.banking.beans.IngDibaFilter;

public class IngDibaDateEvaluator implements Processor {
    public static final String BASELINE_IDENTIFIER = "Finanzen";
	public static final String DATEOFDATA = "DateOfData";
	
	SimpleDateFormat dfIn = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	SimpleDateFormat dfOut = new SimpleDateFormat("dd.MM.yyyy");
	
	@Override
	public void process(Exchange e) throws Exception {
		String body = e.getIn().getBody(String.class);
		if (body.contains(BASELINE_IDENTIFIER)) {
			StringTokenizer st = new StringTokenizer(body, IngDibaFilter.DELIM);
			st.nextElement(); // to get second element
			String date = (String)st.nextElement();
			Date dateParsed;
			try {
				dateParsed = dfIn.parse(date);
			} catch (Exception e1) {
				e1.printStackTrace();
				dateParsed = new Date();
			}
			System.out.println("Date: " + dateParsed);
//			date = dfOut.format(dateParsed);
//			System.out.println("Date: " + date);
			e.getIn().setHeader(DATEOFDATA, dateParsed);
		}
	}
}