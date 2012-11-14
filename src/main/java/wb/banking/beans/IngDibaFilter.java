package wb.banking.beans;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.camel.Body;
import org.apache.camel.Header;

import wb.banking.types.AccountInfo;

public class IngDibaFilter {

	public static final String DELIM = ";";
//	private NumberFormat nf = BigDecimalLayoutForm.getCurrencyInstance(Locale.GERMAN);

	private Map<String, String> accountMap;
	
    public boolean isRelevant(@Body String body) {
        return body.contains("EUR") && !body.contains("gesamt"); 
    }

    public boolean isValidAccountNumber (@Body AccountInfo accInfo) {
        return accountMap.containsKey(accInfo.getAccountNumber());
    }

    public boolean isAggregateable (@Header(value="Account") String acc) {
        return acc != null;
    }


    public AccountInfo transformBody(@Body String body, 
    								 @Header(value="DateOfData") Date dateHeader) {
		body = cleanFormat(body);
		StringTokenizer st = new StringTokenizer(body, DELIM);
		int count = st.countTokens();
		AccountInfo acc = new AccountInfo();
		acc.setDateOfInfo(dateHeader);
		for (int i = 0; i < count; i++) {
			String element = (String)st.nextToken();
			if (i==0) {
				acc.setAccountNumber(element);
				acc.setPurpose(accountMap.get(element));
			} else if (i==1) {
				acc.setAccountType(element);
			} else if (i==3) {
				element = element.replace(" ", "");
				element = element.replace(".", "");
				element = element.replace(",", ".");
				BigDecimal amount;
				try {
					amount = new BigDecimal(element);
				} catch (NumberFormatException e) {
					System.err.println("Element " + element + " couldn't be parsed to Number!:" + e.getMessage());
					amount = new BigDecimal(0);
				}
				acc.setMoneyOTB(amount);
			}
		}
		return acc;
    }
    
    public void setAccountMap(Map<String, String> accountMap) {
		this.accountMap = accountMap;
    }
    
    public String cleanFormat (String body) {
    	body = body.replace(";;", ";-;");
    	body = body.replace("[", "");
    	return body.replace("]", "");
    }
	
//	public static void main(String[] args) {
//		 
//		String s = "0676093165;Extra-Konto;9;3.393,52;EUR";
//		s = s.replace(";;", ";-;");
//		System.out.println(s);
//		StringTokenizer st = new StringTokenizer(s, DELIM);
//		int count = st.countTokens();
//		String newline = "";
//		for (int i = 0; i < count; i++) {
//			String element = (String)st.nextToken();
//			if (i==0) {
//				newline += element;newline += ";";
//				
//			}
//		}
//		System.out.println(newline);
//	}
    
}