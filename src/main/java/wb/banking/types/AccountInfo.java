package wb.banking.types;

import java.math.BigDecimal;
import java.util.Date;

public class AccountInfo {
	
	private Date dateOfInfo;
	private String accountNumber;
	private String purpose;
	private String accountType;
	private BigDecimal moneyOTB;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountType() {
		return accountType;
	}

	public BigDecimal getMoneyOTB() {
		return moneyOTB;
	}
	public void setMoneyOTB(BigDecimal moneyOTB) {
		this.moneyOTB = moneyOTB;
	}
	
	public void setDateOfInfo(Date dateOfInfo) {
		this.dateOfInfo = dateOfInfo;
	}
	public Date getDateOfInfo() {
		return dateOfInfo;
	}
	
	@Override
	public String toString() {
		return "Account: " + accountType + " - " + accountNumber + " - " + moneyOTB;
	}
	

}
