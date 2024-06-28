package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.radnici;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystemNotFoundException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.BrzoVozilo;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara;

/**
 * Klasa RadnikZaRadare Implementira sučelje Runnable što znači da će objekte
 * ove klase izvršavati dretva
 */
public class RadnikZaRadare implements Runnable {

	private Socket mreznaUticnica;
	private PodaciRadara podaciRadara;
	private PosluziteljRadara posluziteljRadara;
	private static volatile ConcurrentHashMap<Integer, BrzoVozilo> mapaBrzihVozila = new ConcurrentHashMap<>();

	private Pattern predlozakBrzine = Pattern.compile(
			"^VOZILO (?<id>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");

	private Pattern predlozakResetiranjaRadara = Pattern.compile("^RADAR RESET$");

	private Pattern predlozakRadarId = Pattern.compile("^RADAR (?<id>\\d+)$");

	private Matcher poklapanjeBrzine;

	private Matcher poklapanjaRestiranjaRadara;

	private Matcher poklapanjeIdRadaraMatcher;

	private static volatile BrzoVozilo brzoVozilo;

	public RadnikZaRadare(Socket mreznaUticnica, PodaciRadara podaciRadara, PosluziteljRadara posluziteljRadara) {
		super();
		this.mreznaUticnica = mreznaUticnica;
		this.podaciRadara = podaciRadara;
		this.posluziteljRadara = posluziteljRadara;
	}

	/**
	 * Metoda koja postoji radi sučelja runnable i u ovoj situaciji služi da bi se
	 * obradio
	 * 
	 */
	@Override
	public void run() {

		try {
			BufferedReader citac = new BufferedReader(new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
			OutputStream out = this.mreznaUticnica.getOutputStream();
			PrintWriter pisac = new PrintWriter(new OutputStreamWriter(out, "utf8"), true);
			var redak = citac.readLine();

			this.mreznaUticnica.shutdownInput();
			pisac.println(obradaZahtjeva(redak));

			pisac.flush();
			this.mreznaUticnica.shutdownOutput();
			this.mreznaUticnica.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije Ako je zahtjev prazan, tada vraća pogrešku Ako
	 *                zahtjev nije prazan, tada vraća odgovor Prije toga radi se
	 *                obrada zahtjeva u metodi obradaZahtjevaBrzine U slučaju
	 *                neispravne komande vraća poruku pogreške. Ovisno o rezultatu,
	 *                vraća određenu poruku pogreške ako je potrebno.
	 */

	public String obradaZahtjeva(String zahtjev) {

		if (zahtjev == null) {
			return "ERROR 10 Neispravna sintaksa komande.";
		}
		if (obradaZahtjevaBrzine(zahtjev)) {

		}

		String rezultatObradeResetiranja = obradaZahtjevaResetiranjaRadara(zahtjev);

		String rezultatObradeProvjereId = obradaZahtjevaProvjereIdRadara(zahtjev);

		if (rezultatObradeResetiranja.equals("OK") || rezultatObradeProvjereId.equals("OK")) {

			return "OK";

		}

		if (rezultatObradeProvjereId.equals("ERROR 33 identifikatori ne odgovaraju")) {
			return "ERROR 33 identifikatori ne odgovaraju";
		}

		if (rezultatObradeProvjereId.equals("ERROR 34 posluzitelj kazni nije aktivan")) {
			return "ERROR 34 posluzitelj kazni nije aktivan";
		}

		if (rezultatObradeResetiranja.equals("ERROR 32 posluzitelj za registraciju radara nije aktivan")) {
			return "ERROR 32 posluzitelj za registraciju radara nije aktivan";
		}

		return null;

	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije. Svrha ove metode je da se izračuna prekoračenje
	 *                brzine za neko vozilo. Metoda radi više stvari, a u kratko
	 *                izračunava kojem vozilu treba poslati kaznu na temelju
	 *                udaljenosti od radara i brzine
	 */
	public boolean obradaZahtjevaBrzine(String zahtjev) {

		this.poklapanjeBrzine = this.predlozakBrzine.matcher(zahtjev);
		if (this.poklapanjeBrzine.matches()) {
			double brzina = Double.valueOf(poklapanjeBrzine.group("brzina"));
			int idVozila = Integer.parseInt(poklapanjeBrzine.group("id"));
			if (izracunajPrekoracenje(brzina)) {
				if (!mapaBrzihVozila.containsKey(idVozila) || mapaBrzihVozila.get(idVozila).status() != true) {
					unesiVozilo(idVozila);
				}

				else {

					var razlikaVremena = izracunajVremenskuRazliku(mapaBrzihVozila.get(idVozila).vrijeme(),
							Long.parseLong(poklapanjeBrzine.group("vrijeme")));

					if (razlikaVremena > this.podaciRadara.maksTrajanje()
							&& mapaBrzihVozila.get(idVozila).status() == true) {


						MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaKazne(),
								podaciRadara.mreznaVrataKazne(), dajKomanduZaServer(idVozila));

						BrzoVozilo privremenoBrzoVozilo = mapaBrzihVozila.get(idVozila);

						mapaBrzihVozila.get(idVozila).postaviStatus(false);

						privremenoBrzoVozilo = privremenoBrzoVozilo.postaviStatus(false);

						mapaBrzihVozila.put(idVozila, privremenoBrzoVozilo);
					}

				}

			}

		}

		return false;
	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije. Svrha ove metode je da se obradi zahtjev
	 *                resetiranja radara. To se radi na način da se nakon provjere
	 *                podataka pošalje komanda RADAR id na poslužitelj za
	 *                registraciju radara. Ako se dobije odgovor OK onda je sve u
	 *                redu, a ako nije, onda ponavlja registraciju i vraća OK ako je
	 *                sve prošlo dobro. Vraća i poruke pogreške ovisno o scenariju.
	 */
	public String obradaZahtjevaResetiranjaRadara(String zahtjev) {

		this.poklapanjaRestiranjaRadara = this.predlozakResetiranjaRadara.matcher(zahtjev);

		if (this.poklapanjaRestiranjaRadara.matches()) {

			StringBuilder komanda = new StringBuilder();
			komanda.append("RADAR").append(" ").append(this.podaciRadara.id());

			String komandaZaSlanjeString = komanda.toString();

			String odgovorString = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
					this.podaciRadara.mreznaVrataRegistracije(), komandaZaSlanjeString);


			if (odgovorString == null) {

				return "ERROR 32 posluzitelj za registraciju radara nije aktivan";
			}

			if (odgovorString.equals("OK")) {
				return "OK";
			}

			if (odgovorString.equals("ERROR 12 taj radar ne postoji u kolekciji radara")) {
				// ponavlja registraciju

				if (this.posluziteljRadara.registrirajPosluzitelja()) {
					return "OK";
				}
			}

		}

		return "ERROR 39 Nije moguće registrirati radar";
	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije. Svrha ove metode je da se obradi zahtjev
	 *                provjere radara. To se radi na način da se nakon provjere
	 *                podataka pošalje komanda TEST poslužitelju kazni. To služi
	 *                kako bi se provjerila njegova aktivnost. Ako se kao odgovor
	 *                dobije OK, znači da je sve u redu. Vraća poruku pogreške
	 *                ovisno o scenariju.
	 */

	public String obradaZahtjevaProvjereIdRadara(String zahtjev) {

		this.poklapanjeIdRadaraMatcher = this.predlozakRadarId.matcher(zahtjev);

		if (this.poklapanjeIdRadaraMatcher.matches()) {


			StringBuilder komanda = new StringBuilder();
			komanda.append("TEST");

			String komandaZaSlanjeString = komanda.toString();
			int id = Integer.parseInt(this.poklapanjeIdRadaraMatcher.group("id"));
			if (this.podaciRadara.id() == id) {


				String odgovorString = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaKazne(),
						podaciRadara.mreznaVrataKazne(), komandaZaSlanjeString);

			//	System.out.println("Odgovor od posluzitelja kazni je" + odgovorString);

				if (odgovorString == null) {
				//	System.out.println("Posluzitelj kazni nije aktivan");
					return "ERROR 34 posluzitelj kazni nije aktivan";
				}

				if (odgovorString.equals("OK")) {

				//	System.out.println("Odgovor je ok");
					return "OK";
				}

			} else {
				return "ERROR 33 identifikatori ne odgovaraju";
			}
		}

		return "ERROR 39 Nije moguće registrirati radar";
	}

	/**
	 * U mapu brzih vozila postavlja vozilo koje je napravilo prekršaj to jest
	 * vozilo je prebrzo predguo
	 * 
	 * @param idVozila Identifikator vozila koje treba dobiti kaznu
	 */

	private void unesiVozilo(int idVozila) {

		mapaBrzihVozila.put(idVozila, new BrzoVozilo(Integer.parseInt(poklapanjeBrzine.group("id")), -1,
				Long.parseLong(poklapanjeBrzine.group("vrijeme")), Double.parseDouble(poklapanjeBrzine.group("brzina")),
				Double.parseDouble(poklapanjeBrzine.group("gpsSirina")),
				Double.parseDouble(poklapanjeBrzine.group("gpsDuzina")), true));
	}

	/**
	 * @param brzinaVozila Uzima brzinu vozila i uspoređuje ju s brzinom pojedinog
	 *                     radara da se može izračunati prekoračenje brzine
	 */

	public boolean izracunajPrekoracenje(double brzinaVozila) {

		return (brzinaVozila > this.podaciRadara.maksBrzina()) ? true : false;
	}

	/**
	 * Izračunava vremensku razliku između trenutnog vozila koje se prati i novog
	 * brzog vozila
	 */

	private long izracunajVremenskuRazliku(long vrijemeBrzogVozila, long vrijemeTrenutnogVozila) {
		long vremenskaRazlikaUSekundama = (vrijemeTrenutnogVozila - vrijemeBrzogVozila) / 1000;

		return vremenskaRazlikaUSekundama;
	}

	/**
	 * Priprema komandu za slanje poslužitelju
	 * 
	 * @param idVozila Identifikacijski broj vozila potreban kako bi se znalo koje
	 *                 vozilo dobiva kaznu
	 */

	private String dajKomanduZaServer(int idVozila) {

		var novaKomanda = new StringBuilder();
		novaKomanda.append("VOZILO").append(" ").append(idVozila).append(" ")
				.append(mapaBrzihVozila.get(idVozila).vrijeme()).append(" ").append(poklapanjeBrzine.group("vrijeme"))
				.append(" ").append(mapaBrzihVozila.get(idVozila).brzina()).append(" ")
				.append(mapaBrzihVozila.get(idVozila).gpsSirina()).append(" ")
				.append(mapaBrzihVozila.get(idVozila).gpsDuzina()).append(" ").append(this.podaciRadara.gpsSirina())
				.append(" ").append(this.podaciRadara.gpsDuzina());

		return novaKomanda.toString();

	}

}
