package controllers;

import play.mvc.Controller;

public class GlobalController extends Controller {
	public static boolean fährt=true;

	public static boolean isLoggedIn() {

		String user = session("connected");
		if (user != null) {
			return true;
		} else {
			return false;
		}

		// return session("connected") != null;

	}
	
	public static void setFahrerTrue(boolean value){
		 fährt = value;

			
		
		
	} 

	public static boolean isFahrer() {
		
		if (fährt== true) {

			return true;
		} else {
			return false;

		}
	}
}
