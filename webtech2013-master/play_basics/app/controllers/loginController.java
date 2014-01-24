package controllers;

import java.net.UnknownHostException;
import java.util.Map;
import play.mvc.Result;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class loginController extends GlobalController {

	public static Result backToLogin() {

		return ok(views.html.login.render("loginController", "", isLoggedIn()));

	}

	public static Result login() {
		Map<String, String[]> parameters = request().body().asFormUrlEncoded();
		String emailUser = parameters.get("email")[0];
		String password = parameters.get("password")[0];

		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("benutzer");

			// Uberpruefung der Eindeutigkeit von Email und Username
			com.mongodb.DBCursor cursor = coll.find();
			BasicDBObject query = new BasicDBObject();
			query.append("email", emailUser);
			query.append("password", password);
			cursor = coll.find(query);
			try {
				if (cursor.count() == 1) {
					String anrede= (String)cursor.next().get("username");
					session("connected", emailUser);
					query.append("fahrer", true);
					cursor = coll.find(query);
					if (cursor.count() == 1) {
						String anredeFahrer= (String)cursor.next().get("username");
						setFahrerTrue(true);
						return ok(views.html.login.render("loginController",
								"Willkommen Fahrer "+anredeFahrer, isLoggedIn()));
					}
					return ok(views.html.login.render("loginController",
							"Willkommen "+anrede, isLoggedIn()));

				} else {
					// session().clear();
					return ok(views.html.login.render("loginController",
							"Passwort oder Email ist falsch.", isLoggedIn()));

				}
			} finally {
				mongoClient.close();
				cursor.close();
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return ok(views.html.login.render("loginController", "", isLoggedIn()));
	}

	public static Result logout() {
		setFahrerTrue(false);
		session().clear();
		return ok(views.html.login.render("loginController", "", isLoggedIn()));
	}

}