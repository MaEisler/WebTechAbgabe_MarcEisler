package controllers;

import java.net.UnknownHostException;
import java.util.Map;
import play.Logger;
import play.mvc.Result;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class anlegenController extends GlobalController {

	public static Result backToSite() {

		return ok(views.html.mfgAnlegen.render("anlegenController", "",
				isLoggedIn(), isFahrer()));

	}

	public static Result mfgAnlegen() {

		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		// Parameterwerte werden ausgelesen
		String MfgId=null;
		String start = parameters.get("start")[0];
		String ziel = parameters.get("ziel")[0];
		String datum = parameters.get("datum")[0];
		String uhrzeit = parameters.get("zeit")[0];
		String fahrer = session("connected");
		String mitfahrer1 = null;
		String mitfahrer2 = null;
		String mitfahrer3 = null;
		String mitfahrer4 = null;

		//Abfragen ob alle Pflichtfelder ausgefühlt sind	
		if (start.equals("")) {
			return ok(views.html.mfgAnlegen
					.render("anlegenController",
							"Geben sie einen Startpunkt ein.", isLoggedIn(),
							isFahrer()));

		}

		if (ziel.equals("")) {
			return ok(views.html.mfgAnlegen.render("anlegenController",
					"Geben sie einen Zielpunkt ein.", isLoggedIn(), isFahrer()));

		}

		if (datum.equals("")) {
			return ok(views.html.mfgAnlegen.render("anlegenController",
					"Geben sie das Datum ein.", isLoggedIn(), isFahrer()));

		}

		if (uhrzeit.equals("")) {
			return ok(views.html.mfgAnlegen.render("anlegenController",
					"Geben sie eine Uhrzeit ein.", isLoggedIn(), isFahrer()));

		}
		
		try {
			//Einfügen des Datenstzes indie Datenbank
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("mfg");
			com.mongodb.DBCursor cursor = coll.find();
			Logger.info("Cursor im Anlegen"+ cursor.size());
				//Id Erzeugen
			int preId= cursor.size()+1;
			Integer preState= new Integer(preId);
			MfgId= preState.toString();
			
			BasicDBObject doc = new BasicDBObject("mfgid", MfgId) 
					.append("start", start)
					.append("ziel", ziel).append("datum", datum)
					.append("zeit", uhrzeit).append("fahrer", fahrer)
					.append("mitfahrer1", mitfahrer1)
					.append("mitfahrer2", mitfahrer2)
					.append("mitfahrer3", mitfahrer3)
					.append("mitfahrer4", mitfahrer4);

			coll.insert(doc);
			
			mongoClient.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return redirect("/mfgMeine/");
	}

}