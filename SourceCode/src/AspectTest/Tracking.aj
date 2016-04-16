package AspectTest;

import smm.*;

import java.text.Format;
import java.util.Date;
import org.aspectj.lang.*;
import java.text.SimpleDateFormat;

public aspect Tracking {
	User curUser;
	Cophieu cophieu;
	double priceRatio = 1000;
	SimpleDateFormat dFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");	
	
	pointcut pcLogin() : execution(* SmmServer.login(..)); 
	after() returning(User user) : pcLogin() {
		if(user != null) {
			curUser = user;			
		}
    }	
	
	//1. ================= BEGIN OF Pre-condition check...	====================
	//Check constraint of total buy price and remain price?	
	//public int muaCP(String maCP, int khoiluong, double giamua) {
	pointcut pcBuyStock1(String stockCode, int quantity, double price) : execution(* User.muaCP(String, int, double))
		&& args(stockCode, quantity, price); 
	before(String stockCode, int quantity, double price) : pcBuyStock1(stockCode, quantity, price) {
		String strDate = dFormat.format(new Date());
		double remainPrice = curUser.getTienmat();	
		price = price * priceRatio;
		if((quantity * price) > remainPrice){
			System.out.println(String.format("%s - Buy error: Money is not enough (Total buy price = %f, Remain price = %f).", strDate, (quantity * price), remainPrice));
			assert (quantity * price) > remainPrice;
		}			
	}	
		
	//Check constraint of buy price and ceiling price?	
	//public int muaCP(String maCP, int khoiluong, double giamua) {
	pointcut pcBuyStock2(String stockCode, int quantity, double price) : execution(* User.muaCP(String, int, double))
		&& target(User) && args(stockCode, quantity, price); 
	boolean isError = false;
	before(String stockCode, int quantity, double price) : pcBuyStock2(stockCode, quantity, price) {
		String strDate = dFormat.format(new Date());
		cophieu = SmmServer.getInstance().getCophieu(stockCode);		
		double ceilingPrice = cophieu.getGiaTran();
		if(price > ceilingPrice) {
			System.out.println(String.format("%s - Buy error : The buy price (%f) is great than the ceiling price (%f).", strDate, price, ceilingPrice));
			assert (price > ceilingPrice);			
		}		
	}
	after(String stockCode, int quantity, double price) returning(int code) : pcBuyStock2(stockCode, quantity, price) {	
		String strDate = dFormat.format(new Date());
		cophieu = SmmServer.getInstance().getCophieu(stockCode);			
		double ceilingPrice = cophieu.getGiaTran();
		double remainPrice = curUser.getTienmat();
		if(code == 0 &&  price <= ceilingPrice){			
			if(price <= ceilingPrice  && remainPrice >= 0) {
				System.out.println(String.format("%s - Buy successfully (Code = %s, Qt = %d, Price = %f).", strDate, stockCode, quantity, price));
				assert (code == 0 &&  price <= ceilingPrice);
			}			
		}
	}
	
	//Check constraint of sell price with floor price?
	//public int banCP(String maCP, int khoiluong, double giaban) {
	pointcut pcSellStock(String stockCode, int quantity, double price) : execution(* User.banCP(String, int, double))
		&& target(User) && args(stockCode, quantity, price); 
	before(String stockCode, int quantity, double price) : pcSellStock(stockCode, quantity, price) {
		String strDate = dFormat.format(new Date());
		cophieu = SmmServer.getInstance().getCophieu(stockCode);
		double floorPrice = cophieu.getGiaSan();		
		if(price < floorPrice) {
			System.out.println(String.format("%s - Sell error : The selling price (%f) is less than the floor price (%f).", strDate, price, floorPrice));
			assert (price < floorPrice);
		}	
	}	
	after(String stockCode, int quantity, double price) : pcSellStock(stockCode, quantity, price) {
		String strDate = dFormat.format(new Date());
		double floorPrice = cophieu.getGiaSan();	
		if(price >= floorPrice) {
			System.out.println(String.format("%s - Sell successfully (Code = %s, Qt = %d, Price = %f).", strDate, stockCode, quantity, price));
			assert (price >= floorPrice);
		}		
	}
	//1. ================= END OF Pre-condition check...	====================
	
	
	//2. ================= BEGIN OF control flow check...	====================
	//Control flow of updateGia JoinPoint in Cophieu or SmmServer class
	pointcut cflowUpdateGia() :  cflow( execution(* Cophieu.updateGia()))  && within(Cophieu || SmmServer);	
	after() : cflowUpdateGia()  {		
        //System.out.println("Exec JoinPoint Cophieu.updateGia() at: " + thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName()
                        //+ " --> " + thisJoinPointStaticPart.getSourceLocation().getLine());
    }
	
	//Track all the join points at any calling position in SmmServer class.
	pointcut trackedCall() : call (* *(..)) && within(SmmServer);
	before() : trackedCall()  {
		Signature sig = thisJoinPointStaticPart.getSignature();
		String line = "" 	+ thisJoinPointStaticPart.getSourceLocation().getLine();
	
		String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
		//System.out.println("Call from " + sourceName + " line " + line + "\n   to "
				//+ sig.getDeclaringTypeName() + "." + sig.getName() +"\n");
	}
	//2. ================= END OF control flow check...	====================
}
