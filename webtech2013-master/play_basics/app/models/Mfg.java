package models;





public class Mfg extends Entity{

	public String start;
	public String ziel;
	public String datum;
	public String zeit;
	public String mfgid;
	public String fahrer;

	public Mfg() {

	}
	public Mfg(String mfgid,String start, String ziel, String datum, String zeit,String fahrer) {
		this.mfgid=mfgid;
		this.start = start;
		this.ziel = ziel;
		this.datum = datum;
		this.zeit = zeit;
		this.fahrer=fahrer;
	}


	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getZiel() {
		return ziel;
	}

	public void setZiel(String ziel) {
		this.ziel = ziel;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getZeit() {
		return zeit;
	}

	public void setZeit(String zeit) {
		this.zeit = zeit;
	}

	public String getMfgid() {
		return mfgid;
	}

	public void setMfgid(String mfgid) {
		this.mfgid = mfgid;
	}

	
	@Override
	public String toString() {
		return super.toString() + " Start:" + start + " Ziel:" + ziel
				+ " Datum:" + datum + " Zeit:" + zeit;
	}

}
