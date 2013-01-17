package wb.banking;

import org.apache.camel.component.dataset.DataSet;
import org.apache.camel.component.dataset.DataSetSupport;
import org.apache.camel.component.dataset.SimpleDataSet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: martinh
 * Date: 27.11.12
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class MySimpleDataSet extends DataSetSupport {

    private static SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
    private static Calendar startDate = new GregorianCalendar(1970,1,1);
    private static String text = "\"Meine Finanzen\";\"%s 0:01\"\n" +
            "\"Kunde\";\"Martin Huber\"\n" +
            "\"Kontonummer\";\"Kontoart\";\"Bemerkung\";\"Kontosaldo/Depotwert\";\"Währung\"\n" +
            "\"1\";\"Girokonto\";\"\";\"1.239,7\";\"EUR\"\n" +
            "\"2\";\"Extra-Konto\";\"\";\"795,45\";\"EUR\"\n" +
            "\"3\";\"Extra-Konto\";\"\";\"112187,98\";\"EUR\"\n" +
            "\"4\";\"Extra-Konto\";\"\";\"1.737,54\";\"EUR\"\n" +
            "\"5\";\"Extra-Konto\";\"\";\"387,98\";\"EUR\"\n" +
            "\"6\";\"Direkt-Depot\";\"\";\"102.227,43\";\"EUR\"";

    public MySimpleDataSet (long size) {
        setSize(size);
    }

    public MySimpleDataSet() {

    }


    @Override
    protected Object createMessageBody(long l) {
        int dayadd = (int)l;
        Calendar newDate = (Calendar)startDate.clone();
        newDate.add(Calendar.DAY_OF_YEAR, dayadd);
        Date calAsDate = newDate.getTime();
        String val =  String.format(text, sf.format(calAsDate));
        //System.out.println(val);
        return val;
    }
}
