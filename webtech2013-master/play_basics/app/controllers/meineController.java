package controllers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import models.Mfg;
import models.Nachricht;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import play.Logger;
import play.mvc.Result;

public class meineController extends GlobalController {

	public static Result backToMeine() {

		return ok("Ohoh");

	}

	public static Result bestaetigen(String nachrichtId) {

		try {
			
			//Nachricht herausholen um email für Suche herauszuholen
			String nId = nachrichtId;
			String emailSucheAn = null;
			String benutzer = null;
			String mfgid = null;
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("nachricht");
			com.mongodb.DBCursor cursor = coll.find();
			BasicDBObject query = new BasicDBObject();
			query.append("nachrichtid", nachrichtId);
			cursor = coll.find(query);

			if (cursor.hasNext()) {
				
				BasicDBObject testObj= (BasicDBObject) cursor.next();
				emailSucheAn =  testObj.getString("von");
				mfgid =  (String) testObj.get("mfgid");
				
			}
			

			
			// Mitfahrgelegenheit von Fahrer herausfinden und Mitfahrereintragen
			mongoClient = new MongoClient("localhost", 27017);
			db = mongoClient.getDB("play_basics");
			coll = db.getCollection("benutzer");
			cursor = coll.find();
			query = new BasicDBObject("email", emailSucheAn);
			cursor = coll.find(query);
			if(cursor.hasNext()){
			benutzer = (String) cursor.next().get("email");
			}
			mongoClient = new MongoClient("localhost", 27017);
			db = mongoClient.getDB("play_basics");
			coll = db.getCollection("mfg");
			cursor = coll.find();
			query = new BasicDBObject("mfgid", mfgid);
			cursor = coll.find(query);

			if (cursor.hasNext()) {
				BasicDBObject test = (BasicDBObject) cursor.next();
				BasicDBObject searchQuery=new BasicDBObject();
				
				if ( test.get("mitfahrer1") != null) {
					test.append("$set", new BasicDBObject().append("mitfahrer1", benutzer));
					searchQuery.append("mfgid", mfgid);
				}
				if ( test.get("mitfahrer2") != null) {
					test.append("$set", new BasicDBObject().append("mitfahrer2", benutzer));
					searchQuery.append("mfgid", mfgid);
				}
				if ( test.get("mitfahrer3" )!= null) {
					test.append("$set", new BasicDBObject().append("mitfahrer3", benutzer));
					searchQuery.append("mfgid", mfgid);
				}
				if ( test.get("mitfahrer4") != null) {
					test.append("$set", new BasicDBObject().append("mitfahrer4", benutzer));
					searchQuery.append("mfgid", mfgid);
				}
				
				coll.update(searchQuery, test);

			}
			
			
			//Bestätigungsnachricht zusammenstellen
			String art = "Bestätigt";
			String vonUser = session("connected");
			String mfgid2 = mfgid;

			mongoClient = new MongoClient("localhost", 27017);
			db = mongoClient.getDB("play_basics");
			coll = db.getCollection("nachricht");
			cursor = coll.find();
			query = new BasicDBObject("nachrichtid", nachrichtId);
			cursor = coll.find(query);

			if (cursor.hasNext()) {
				coll.remove(query);

			}

			BasicDBObject doc = new BasicDBObject("nachrichtid", nId)
					.append("mfgid", mfgid2).append("art", art)
					.append("von", vonUser).append("an", benutzer);
			if (cursor.hasNext()) {
				coll.remove(query);

			}
			coll.insert(doc);

			mongoClient.close();
			cursor.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		List<Mfg> ausgabe = new ArrayList<Mfg>();

		List<Nachricht> ausgabeNachricht = new ArrayList<Nachricht>();

		ausgabe = returnMfg();
		ausgabeNachricht = returnMessages();

		return ok(views.html.mfgMeine.render("suchenController", "",
				isLoggedIn(), ausgabe, ausgabeNachricht));
	}

	public static List<Mfg> returnMfg() {
		
		//Mitfahrgelegenheitliste zusammenstellen die den Nutzer betreffen
		BasicDBList liste = new BasicDBList();
		List<Mfg> übergabe = new ArrayList<Mfg>();
		try {
			String emailUser = session("connected");
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("mfg");
			
			DBObject clause1 = new BasicDBObject("fahrer", emailUser);  
			DBObject clause2 = new BasicDBObject("mitfahrer1", emailUser); 
			DBObject clause3 = new BasicDBObject("mitfahrer2", emailUser);
			DBObject clause4 = new BasicDBObject("mitfahrer3", emailUser); 
			DBObject clause5 = new BasicDBObject("mitfahrer5", emailUser); 
			BasicDBList or = new BasicDBList();
			or.add(clause1);
			or.add(clause2);
			or.add(clause3);
			or.add(clause4);
			or.add(clause5);
			DBObject query = new BasicDBObject("$or", or);		
			com.mongodb.DBCursor cursor = coll.find(query);


			try {
				// DBObject liste erstellen und füllen
				while (cursor.hasNext() ) {
					BasicDBObject dbObj = (BasicDBObject) cursor.next();

					liste.add(dbObj);
					Logger.info("dbObje Meine: " + dbObj.toString());

				}
				// DBObject Liste in MfgListe umwandeln
				for (Object obj : liste) {
					final BasicDBObject mfgASDBObject = (BasicDBObject) obj;
					final Mfg mfg = new Mfg(mfgASDBObject.getString("mfgid"),
							mfgASDBObject.getString("start"),
							mfgASDBObject.getString("ziel"),
							mfgASDBObject.getString("datum"),
							mfgASDBObject.getString("zeit"),
							mfgASDBObject.getString("fahrer"));

					übergabe.add(mfg);
					Logger.info(" Übergabe liste Meine" + übergabe.toString());
				}

			} finally {
				cursor.close();
				mongoClient.close();
			}

		} catch (UnknownHostException e) {

			e.printStackTrace();
		}

		return übergabe;
	}

	public static List<Nachricht> returnMessages() {
		
		//Nachrichtenliste zusammenstellen die für den User sind.
		BasicDBList listeNachricht = new BasicDBList();
		List<Nachricht> übergabeNachricht = new ArrayList<Nachricht>();
		try {
			String emailUser = session("connected");

			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("play_basics");
			DBCollection coll = db.getCollection("nachricht");
			BasicDBObject query = new BasicDBObject("an", emailUser);
			com.mongodb.DBCursor cursor = coll.find(query);

			try {
				// DBObject liste erstellen und füllen
				while (cursor.hasNext()) {
					BasicDBObject dbObj = (BasicDBObject) cursor.next();
					listeNachricht.add(dbObj);
					
				}
				
				// DBObject Liste in MfgListe umwandeln
				for (Object obj : listeNachricht) {
					final BasicDBObject messageAsDBObject = (BasicDBObject) obj;
					final Nachricht message = new Nachricht(
							messageAsDBObject.getString("nachrichtid"),
							messageAsDBObject.getString("mfgid"),
							messageAsDBObject.getString("art"),
							messageAsDBObject.getString("von"),
							messageAsDBObject.getString("an"));

					übergabeNachricht.add(message);	
					
				}

			} finally {
				cursor.close();
				mongoClient.close();
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();

		}
		return übergabeNachricht;

	}

	public static Result meineAnzeigen() {
		//Methode um die Listen zu übergeben.

		List<Mfg> ausgabe = new ArrayList<Mfg>();
		List<Nachricht> ausgabeNachricht = new ArrayList<Nachricht>();
		ausgabe = returnMfg();
		ausgabeNachricht = returnMessages();
	
		return ok(views.html.mfgMeine.render("suchenController", "",isLoggedIn(), ausgabe, ausgabeNachricht));
	}
}