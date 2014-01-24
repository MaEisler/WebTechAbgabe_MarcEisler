package controllers;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

import play.Logger;
import play.mvc.Result;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class RegestrierungController extends GlobalController {

	public static Result backToRegis() {
		return redirect("/assets/html/regis.html");
		// return ok(views.html.regis.render("RegistrierungController", ""));

	}

	@SuppressWarnings("deprecation")
	public static Result abschicken() {

		Map<String, String[]> parameters = request().body().asFormUrlEncoded();

		// Parameteruebergaben werden ueberprueft
		if (!(parameters != null && parameters.containsKey("username")
				&& parameters.containsKey("email")
				&& parameters.containsKey("password")
				&& parameters.containsKey("kennzeichen")
				&& parameters.containsKey("passwordWH")
				&& parameters.containsKey("adresse")
				&& parameters.containsKey("adressePLZ")
				&& parameters.containsKey("adresseStadt")
				&& parameters.containsKey("Tag")
				&& parameters.containsKey("Monat") && parameters
					.containsKey("Jahr"))) {

			Logger.warn("Es fehlen einige Eingabefelder");
			return redirect("/assets/html/regis.html");
		}

		// Parameterwerte werden ausgelesen
		String username = parameters.get("username")[0];
		String email = parameters.get("email")[0];
		String password = parameters.get("password")[0];
		String passwordValidierung = parameters.get("passwordWH")[0];
		String kennzeichen = parameters.get("kennzeichen")[0];
		String straße = parameters.get("adresse")[0];
		String plz = parameters.get("adressePLZ")[0];
		String wohnort = parameters.get("adresseStadt")[0];
		boolean fahrer = false;

		// Ueberpruefung ob das Password korrekt wiederholt eingegeben wurde
		if (password.equals("") || passwordValidierung.equals("")) {
			return ok(views.html.regis.render("RegestrierungController",
					"Sie haben das Passwort vergessen.", isLoggedIn()));

		}

		if (!password.equals(passwordValidierung)) {
			return ok(views.html.regis.render("RegestrierungController",
					"Sie haben das Password nicht korrekt eingegeben.",
					isLoggedIn()));
		}

		if (username.equals("")) {

			return ok(views.html.regis.render("RegestrierungController",
					"Sie haben ihren Namen vergessen.", isLoggedIn()));
		}

		if (!kennzeichen.equals("")) {
			fahrer = true;

		}

		// Parameterwerte werden ausgelesen
		String tag = parameters.get("Tag")[0];
		String monat = parameters.get("Monat")[0];
		String jahr = parameters.get("Jahr")[0];
		String geburtsdatum = tag + "." + monat + "." + jahr;

		String alter = "";
		int wiealt;

		// Das Alter wird zunaechst ermittelt
		GregorianCalendar today = new GregorianCalendar();
		GregorianCalendar past = new GregorianCalendar(Integer.parseInt(jahr),
				Integer.parseInt(monat) - 1, Integer.parseInt(tag));

		long difference = today.getTimeInMillis() - past.getTimeInMillis();
		int days = (int) (difference / (1000 * 60 * 60 * 24));
		double years = (double) days / 365;

		StringBuilder sb = new StringBuilder();
		sb.append("");
		sb.append(years);
		alter = sb.toString();

		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String registrierungsdatum = df.format(today.getTime());

		// Bestimmung des Alters als ganze Zahl
		wiealt = (int) Math.floor(Double.parseDouble(alter));

		// Daten werden an die Datenbank uebertragen
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("benutzer");

			// Uberpruefung der Eindeutigkeit von Email und Username
			com.mongodb.DBCursor cursor = coll.find();
			BasicDBObject query = new BasicDBObject("username", username);
			cursor = coll.find(query);

			String sucheUsername = "";
			try {
				while (cursor.hasNext()) {
					sucheUsername += cursor.next();
				}
			} finally {
				cursor.close();
			}

			// Sicherstellung dass der Username eindeutig nur einmal in der
			// Datenbank vorkommt
			if (!sucheUsername.isEmpty()) {
				mongoClient.close();
				return ok(views.html.regis
						.render("RegestrierungController",
								"Der Username ist schon vergeben. Versuchen Sie bitte einen anderen Namen.",
								isLoggedIn()));
			}

			query = new BasicDBObject("email", email);
			cursor = coll.find(query);

			String sucheEmail = "";

			try {
				while (cursor.hasNext()) {
					sucheEmail += cursor.next();
				}
			} finally {
				cursor.close();

			}

			// Email darf nur einmal in der Datenbank vorkommen
			if (!sucheEmail.isEmpty()) {
				mongoClient.close();
				return ok(views.html.regis
						.render("RegestrierungController",
								"Diese Email Adresse wird schon bereits verwendet. Verwenden Sie bitte eine andere Email Adresse.",
								isLoggedIn()));
			}

			BasicDBObject doc = new BasicDBObject("username", username)
					.append("email", email).append("password", password)
					.append("geburtsdatum", geburtsdatum)
					.append("wiealt", wiealt)
					.append("kennzeichen", kennzeichen)
					.append("straße", straße).append("postleitzahl", plz)
					.append("wohnort", wohnort)
					.append("registrierungsdatum", registrierungsdatum)
					.append("fahrer", fahrer);
			coll.insert(doc);
			mongoClient.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return redirect("/loginController/");
	}

}