package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;

/**
 * Klasa PosluziteljZaRegistracijuRadara Implementira sučelje Runnable što znači
 * da će objekte ove klase izvršavati dretva
 */
public class PosluziteljZaRegistracijuRadara implements Runnable {

	private int mreznaVrata;
	private CentralniSustav centralniSustav;

	private Pattern predlozakRegistracijeRadara = Pattern.compile(
			"^RADAR (?<id>\\d+) (?<adresa>\\w+) (?<mreznaVrata>\\d+) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<maksUdaljenost>-?\\d+?)$");

	private Pattern predlozakObrisiJedanRadar = Pattern.compile("^RADAR OBRIŠI (?<id>\\d+)$");

	private Pattern predlozakObrisiSveRadare = Pattern.compile("^RADAR OBRIŠI SVE$");

	private Pattern predlozakprovjeriPostojiLiRadar = Pattern.compile("^RADAR (?<id>\\d+)$");

	private Pattern predlozakResetirajRadare = Pattern.compile("^RADAR RESET$");

	private Pattern predlozakDajSveRadare = Pattern.compile("^RADAR SVI$");

	private Matcher poklapanjeBrisanjaJednoRadara;

	private Matcher poklapanjeBrisanjaSvihRadara;

	private Matcher poklapanjeRegistracijeRadara;
	private Matcher poklapanjeProvjereRadara;
	private Matcher poklapanjeResetiranjaRadara;

	private Matcher poklapanjeVracanjeSvihRadara;

	/**
	 * Konstruktor klase u kojem se postavljaju vrijednosti za mrežna vrata te se
	 * dobiva referenca na centralni sustav jer su neki od podataka iz centralnog
	 * sustava potrebni poslužitelju za registraciju radara
	 */

	public PosluziteljZaRegistracijuRadara(int mreznaVrata, CentralniSustav centralniSustav) {
		super();
		this.mreznaVrata = mreznaVrata;
		this.centralniSustav = centralniSustav;
	}

	/**
	 * Nadjačavanje metode run iz osnovne klase Otvara mrežnu utičnicu na zadanim
	 * mrežnim vratima i radi u jednodretvenom načinu rada sa sinkronim slanjem i
	 * primanjem poruka Čita datoteku i obrađuje retke datoteke Na kraju rada
	 * prekida konekciju s mrežnom utičnicom
	 * 
	 * @throws NumberFormatException Baca iznimku ako dođe do problema brojevnog
	 *                               tipa
	 * @throws IOException           Baca iznimku ako dođe do problema koji su
	 *                               vezani sa ulazom i izlazom
	 */

	@Override
	public void run() {

		boolean kraj = false;

		try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(this.mreznaVrata)) {
			while (!kraj) {
				var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
				BufferedReader citac = new BufferedReader(
						new InputStreamReader(mreznaUticnica.getInputStream(), "utf8"));
				OutputStream out = mreznaUticnica.getOutputStream();
				PrintWriter pisac = new PrintWriter(new OutputStreamWriter(out, "utf8"), true);
				var redak = citac.readLine();

				mreznaUticnica.shutdownInput();
				pisac.println(obradaZahtjeva(redak));

				pisac.flush();
				mreznaUticnica.shutdownOutput();
				mreznaUticnica.close();
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije Ako je zahtjev prazan, tada vraća pogrešku Ako
	 *                zahtjev nije prazan, tada vraća odgovor Prije toga radi se
	 *                obrada zahtjeva u metodi obradaZahtjevaKazna U slučaju
	 *                neispravne komande vraća poruku pogreške
	 */

	public String obradaZahtjeva(String zahtjev) {

		if (zahtjev == null) {
			return "ERROR 10 Neispravna sintaksa komande.";
		}

		var odgovor = obradaZahtjevaRadara(zahtjev);
		if (odgovor != null) {
			return odgovor;
		}

		return "ERROR 10 Neispravna sintaksa komande.";
	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije Ova metoda obrađuje sam zahtjev koji dođe na
	 *                server. Bavi se opcijama ovisno o broju argumenata i sadržaju
	 *                samih argumenata. Na početku metode se pomoću poklapanja
	 *                dobivaju traženi podaci Zatim se provjerava koja opcija metode
	 *                se treba izvršiti Ako se zahtjev poklapa sa
	 *                statusBrisanjaJednogRadara opcijom , tada se iz liste svih
	 *                radara obriše radar čiji je id poslan kao argument Ako se
	 *                zahtjev poklapa sa statusBrisanjaSvihRdara opcijom, tada se iz
	 *                kolekcije svih radara obrišu svi radari Ako se poklapa sa
	 *                statusRegRadara, tada se registrira novi radar. Takva logika
	 *                događa se za sve daljnje opcije- ovisno o sadržaju komande
	 *                radar se može provjeriti, resetirati ili se jednostavno vrate
	 *                svi radari
	 */

	public String obradaZahtjevaRadara(String zahtjev) {

		postaviMatchere(zahtjev);

		var statusRegRadara = poklapanjeRegistracijeRadara.matches();
		var statusBrisanjaJednogRadara = poklapanjeBrisanjaJednoRadara.matches();
		var statusBrisanjaSvihRdara = poklapanjeBrisanjaSvihRadara.matches();
		var statusProvjereRadara = poklapanjeProvjereRadara.matches();
		var statusResetiranjaRadara = poklapanjeResetiranjaRadara.matches();
		var statusVracanjaSvihRadara = poklapanjeVracanjeSvihRadara.matches();
		if (statusRegRadara) {
			return registriranjeRadara();
		}

		if (statusBrisanjaJednogRadara) {
			return brisanjeJednogRadara();
		}

		if (statusBrisanjaSvihRdara) {
			this.centralniSustav.sviRadari.clear();
			return "OK";
		}

		if (statusProvjereRadara) {

			return provjeraRadara();
		}

		if (statusResetiranjaRadara) {

			return resetiranjeRadara();

		}

		if (statusVracanjaSvihRadara) {
			return vracanjeSvihRadara();
		}

		return null;

	}

	/**
	 * Ova metoda služi da pokuša registrirati radar- točnije pokuša dodati radar u
	 * kolekciju svih radara. Prije toga se provjerava postoji li već radar s tim id
	 * i ako da, u tom slučaju javlja se poruka pogreške
	 */

	private String registriranjeRadara() {

		int id = Integer.valueOf(this.poklapanjeRegistracijeRadara.group("id"));

		if (this.centralniSustav.sviRadari.containsKey(id)) {
			return "ERROR 11 Već postoji radar s tim ID";

		}

		else {
			var radar = new PodaciRadara(Integer.valueOf(this.poklapanjeRegistracijeRadara.group("id")),
					this.poklapanjeRegistracijeRadara.group("adresa"),
					Integer.valueOf(this.poklapanjeRegistracijeRadara.group("mreznaVrata")), -1, -1,
					Integer.valueOf(this.poklapanjeRegistracijeRadara.group("maksUdaljenost")), null, -1, null, -1,
					null, Double.valueOf(this.poklapanjeRegistracijeRadara.group("gpsSirina")),
					Double.valueOf(this.poklapanjeRegistracijeRadara.group("gpsDuzina")));

			this.centralniSustav.sviRadari.put(radar.id(), radar);

			return "OK";
		}

	}

	/**
	 * Postavlja matchere koji su potrebni za provjeru ispravnosti komandi koje
	 * dolaze na poslužitelj
	 */

	private void postaviMatchere(String zahtjev) {

		this.poklapanjeRegistracijeRadara = this.predlozakRegistracijeRadara.matcher(zahtjev);
		this.poklapanjeBrisanjaJednoRadara = this.predlozakObrisiJedanRadar.matcher(zahtjev);
		this.poklapanjeBrisanjaSvihRadara = this.predlozakObrisiSveRadare.matcher(zahtjev);
		this.poklapanjeProvjereRadara = this.predlozakprovjeriPostojiLiRadar.matcher(zahtjev);
		this.poklapanjeResetiranjaRadara = this.predlozakResetirajRadare.matcher(zahtjev);
		this.poklapanjeVracanjeSvihRadara = this.predlozakDajSveRadare.matcher(zahtjev);

	}

	/**
	 * Iz kolekcije svih radara briše radar s određenim id ako se taj radar nalazi u
	 * kolekciji svih radara
	 */

	private String brisanjeJednogRadara() {
		int id = Integer.parseInt(this.poklapanjeBrisanjaJednoRadara.group("id"));

		if (this.centralniSustav.sviRadari.containsKey(id)) {
			this.centralniSustav.sviRadari.remove(id);
			return "OK";
		}

		return null;
	}

	/**
	 * Za određeni radar provjeri je li aktivan provjerom nalazi li se radar u listi
	 * radara
	 */

	private String provjeraRadara() {

		int id = Integer.parseInt(this.poklapanjeProvjereRadara.group("id"));
		if (this.centralniSustav.sviRadari.containsKey(id)) {

			// System.out.println("Svi radari su" + this.centralniSustav.sviRadari); // ovo
			// je tu cist za provjeru
			// System.out.println("Taj radar je aktivan");

			return "OK";
		}

		else {
			// System.out.println("Vratil budem error 12");
			return "ERROR 12 taj radar ne postoji u kolekciji radara";
		}

	}

	/**
	 * Šalje naredbu RADAR id svakom radaru u kolekciji radara kako bi se provjerilo
	 * je li neki radar aktivan. Ako radar nije kativan, onda ga briše iz kolekcije.
	 * Vraća OK n m gdje n označava ukupan broj radara u trenutku kad je primljen
	 * zahtjev, a broj m označava broj radara koji nisu bili aktivni kada se poslao
	 * taj zahtjev pa su zato obrisani
	 */

	private String resetiranjeRadara() {

		int sviRadari = this.centralniSustav.sviRadari.size();
		int brojacNeaktivnihRadara = 0;
		Iterator<Map.Entry<Integer, PodaciRadara>> iterator = this.centralniSustav.sviRadari.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Integer, PodaciRadara> jedanPodatakRadara = iterator.next();
			Integer radarId = jedanPodatakRadara.getKey();
			PodaciRadara radar = jedanPodatakRadara.getValue();
			String komandaZaPoslati = "RADAR " + radarId;
			String odgovorOdPosluzitelja = MrezneOperacije.posaljiZahtjevPosluzitelju(radar.adresaRadara(),
					radar.mreznaVrataRadara(), komandaZaPoslati);

			if (odgovorOdPosluzitelja == null || !odgovorOdPosluzitelja.equals("OK")) {
				iterator.remove();
				// za to se mora dodati error 32
				brojacNeaktivnihRadara++;
			}
		}

	//	System.out.println("Svi radari sad nakon brisanja su" + this.centralniSustav.sviRadari);

	//	System.out.println("Rezultat naredbe je" + " OK " + sviRadari + " " + brojacNeaktivnihRadara);
		return "OK " + sviRadari + " " + brojacNeaktivnihRadara;

	}

	/**
	 * Vraća sve radare iz kolekcije radara u centralnom sustavu
	 */

	private String vracanjeSvihRadara() {

		//System.out.println("Vracanje svih radara ide");

		StringBuilder responseBuilder = new StringBuilder("OK {");

		boolean prviClan = true;
		for (Map.Entry<Integer, PodaciRadara> entry : this.centralniSustav.sviRadari.entrySet()) {
			Integer radarId = entry.getKey();
			PodaciRadara radar = entry.getValue();

			if (!prviClan) {
				responseBuilder.append(", ");
			} else {
				prviClan = false;
			}

			responseBuilder.append("[").append(radarId).append(" ").append(radar.adresaRadara()).append(" ")
					.append(radar.mreznaVrataRadara()).append(" ").append(radar.gpsSirina()).append(" ")
					.append(radar.gpsDuzina()).append(" ").append(radar.maksUdaljenost()).append("]");

		}

		responseBuilder.append("}");

		//System.out.println("Svi radari su " + responseBuilder.toString());
		return responseBuilder.toString();

	}

}
