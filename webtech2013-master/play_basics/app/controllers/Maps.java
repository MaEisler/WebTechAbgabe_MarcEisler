package controllers;

import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

public class Maps extends Controller{

	
	public static Result sample(){
		//reading config parameter. see template for how to read it from there
		String mapkey = Play.application().configuration().getString("mapkey");
		Logger.debug("mapkey: " +mapkey);
		return ok(views.html.maps.map_sample.render());
	}
	
	public static Result getlocation(){
		return ok(views.html.maps.getlocation.render());
	}
}
