package AspectTest;

import smm.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public aspect ActionLog {
	//Login successfully
	pointcut pcLogin() : execution(* SmmServer.login(..)); 
	after() returning(User user) : pcLogin() {
		if(user != null) {
			SimpleDateFormat dFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
			String strDate = dFormat.format(new Date());
			System.out.println(String.format("%s - Logged in successfully with user name = '%s'", strDate, user.getUsername()));
		}
    }	
}
