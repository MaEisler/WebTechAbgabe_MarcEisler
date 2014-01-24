package models;

public class Nachricht {
	
	public String nachrichtid;
	public String mfgid;
	public String art;
	public String von;
	public String an;
	

	public Nachricht() {

	}
	public Nachricht(String mfgid,String nachrichtid, String art, String von, String an) {
		this.mfgid=mfgid;
		this.nachrichtid = nachrichtid;
		this.art = art;
		this.von = von;
		this.an = an;
		
	}

}
