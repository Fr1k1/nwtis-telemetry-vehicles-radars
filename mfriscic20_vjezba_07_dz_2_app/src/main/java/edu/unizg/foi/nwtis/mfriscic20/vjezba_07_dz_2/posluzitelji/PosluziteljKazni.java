package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciKazne;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;

/**
 * Klasa PosluziteljKazni
 */
public class PosluziteljKazni {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	public int mreznaVrata;

	private String mreznaAdresa;
	private Pattern predlozakKazna = Pattern.compile(
			"^VOZILO (?<id>\\d+) (?<vrijemePocetak>\\d+) (?<vrijemeKraj>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+) (?<gpsSirinaRadar>\\d+[.]\\d+) (?<gpsDuzinaRadar>\\d+[.]\\d+)$");

	private Pattern predlozakTriArgumenta = Pattern.compile("^STATISTIKA (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");

	private Pattern predlozakCetiriArgumenta = Pattern
			.compile("^VOZILO (?<id>\\d+) (?<vrijemeOd>\\d+) (?<vrijemeDo>\\d+)$");

	private Pattern predlozakTest = Pattern.compile("^TEST$");

	private Matcher poklapanjeKazna;
	private Matcher poklapanjeTriArgumenta;
	private Matcher poklapanjeCetiriArgumenta;
	private Matcher poklapanjeTest;
	private volatile Queue<PodaciKazne> sveKazne = new ConcurrentLinkedQueue<>();

	/**
	 * Provjerava broj argumenata Zatim instancira varijablu posluziteljKazni
	 * Preuzimaju se postavke i pokusa se pokrenuti posluzitelj U slucaju pogreske
	 * hvata se iznimka
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 * @throws NeispravnaKonfiguracija Baca iznimku ako dođe do problema s datotekom
	 * @throws NumberFormatException   Baca iznimku ako dođe do problema brojevnog
	 *                                 tipa
	 * @throws UnknownHostException    Baca iznimku ako postoje problemi sa
	 *                                 povezivanjem
	 */

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Broj argumenata nije 1.");
			return;
		}

		PosluziteljKazni posluziteljKazni = new PosluziteljKazni();
		try {
			posluziteljKazni.preuzmiPostavke(args);

			posluziteljKazni.pokreniPosluzitelja();

		} catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	/**
	 * Otvara mrežnu utičnicu na zadanim mrežnim vratima i radi u jednodretvenom
	 * načinu rada sa sinkronim slanjem i primanjem poruka Čita datoteku i obrađuje
	 * retke datoteke Na kraju rada prekida konekciju s mrežnom utičnicom
	 * 
	 * @throws NumberFormatException Baca iznimku ako dođe do problema brojevnog
	 *                               tipa
	 * @throws IOException           Baca iznimku ako dođe do problema koji su
	 *                               vezani sa ulazom i izlazom
	 */

	public void pokreniPosluzitelja() {
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
	 * @param zahtjev Zahtjev koji se dobije preko mrežne komunikacije Kao argument
	 *                uzima zahtjev koji dobiva preko mrežne komunikacije Ako je
	 *                zahtjev prazan, tada vraća pogrešku Ako zahtjev nije prazan,
	 *                tada vraća odgovor Prije toga radi se obrada zahtjeva u metodi
	 *                obradaZahtjevaKazna U slučaju neispravne komande vraća poruku
	 *                pogreške
	 * @return Vraća odgovor ovisno o ishodu obrade
	 */

	public String obradaZahtjeva(String zahtjev) { // handlaj ostale errore
		if (zahtjev == null) {
			return "ERROR 40 Neispravna sintaksa komande.";
		}
		var odgovor = obradaZahtjevaKazna(zahtjev);
		if (odgovor != null) {
			return odgovor;
		}

		return "ERROR 10 Neispravna sintaksa komande.";
	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije Ova metoda obrađuje sam zahtjev koji dođe na
	 *                server. Bavi se opcijama ovisno o broju argumenata. Na početku
	 *                metode se pomoću poklapanja dobivaju traženi podaci Zatim se
	 *                provjerava koja opcija metode se treba izvršiti U slučaju da
	 *                se zahtjev poklapa sa izrazom pod nazivom statusKazna tada se
	 *                u listu svih kazni dodaje kazna Vraća se OK Ako se zahtjev
	 *                poklapa sa statusTri opcijom za tri argumenta, tada se poziva
	 *                metoda obradaStatusaTri koja obrađuje zahtjeve s tri argumenta
	 *                Ako se zahtjev poklapa sa statusCetiri opcijom za četiri
	 *                argumenta, tada se poziva metoda obradaStatusaCetiri koja
	 *                obrađuje zahtjeve s četiri argumenta
	 */

	public String obradaZahtjevaKazna(String zahtjev) {
		this.poklapanjeKazna = this.predlozakKazna.matcher(zahtjev);
		this.poklapanjeTriArgumenta = this.predlozakTriArgumenta.matcher(zahtjev);
		this.poklapanjeCetiriArgumenta = this.predlozakCetiriArgumenta.matcher(zahtjev);
		this.poklapanjeTest = this.predlozakTest.matcher(zahtjev);
		var statusKazna = poklapanjeKazna.matches();
		var statusTri = poklapanjeTriArgumenta.matches();
		var statusCetiri = poklapanjeCetiriArgumenta.matches();
		var statusTest = poklapanjeTest.matches();

		if (statusKazna) {
			var kazna = new PodaciKazne(Integer.valueOf(this.poklapanjeKazna.group("id")),
					Long.valueOf(this.poklapanjeKazna.group("vrijemePocetak")),
					Long.valueOf(this.poklapanjeKazna.group("vrijemeKraj")),
					Double.valueOf(this.poklapanjeKazna.group("brzina")),
					Double.valueOf(this.poklapanjeKazna.group("gpsSirina")),
					Double.valueOf(this.poklapanjeKazna.group("gpsDuzina")),
					Double.valueOf(this.poklapanjeKazna.group("gpsSirinaRadar")),
					Double.valueOf(this.poklapanjeKazna.group("gpsDuzinaRadar")));

			this.sveKazne.add(kazna);
			System.out.println("Id: " + kazna.id() + " Vrijeme od: " + sdf.format(kazna.vrijemePocetak())
					+ "  Vrijeme do: " + sdf.format(kazna.vrijemeKraj()) + " Brzina: " + kazna.brzina() + " GPS: "
					+ kazna.gpsSirina() + ", " + kazna.gpsDuzina());

			String jsonBody = kreirajJsonZaPost(kazna);

			posaljiPostZahtjev(jsonBody);

		}

		if (statusTri) {

			obradaStatusaTri();

		}

		if (statusCetiri) {

			obradaStatusaCetiri();
		}

		if (statusTest) {
			return "OK";
		}

		return "ERROR 49 Pojavila se neka od ostalih pogrešaka.";
	}

	/**
	 * Kreiranje tijela koje će biti poslano post zahtjevom na endpoint za dodavanje
	 * kazni
	 */

	private String kreirajJsonZaPost(PodaciKazne kazna) {
		return String.format(
				"{\n" + "  \"id\": %d,\n" + "  \"vrijemePocetak\": %d,\n" + "  \"vrijemeKraj\": %d,\n"
						+ "  \"brzina\": %f,\n" + "  \"gpsSirina\": %f,\n" + "  \"gpsDuzina\": %f,\n"
						+ "  \"gpsSirinaRadar\": %f,\n" + "  \"gpsDuzinaRadar\": %f\n" + "}",
				kazna.id(), kazna.vrijemePocetak(), kazna.vrijemeKraj(), kazna.brzina(), kazna.gpsSirina(),
				kazna.gpsDuzina(), kazna.gpsSirinaRadar(), kazna.gpsDuzinaRadar());
	}

	/**
	 * Slanje post zahtjeva na endpoint za dodavanje. U slučaju uspješnog dodavanja
	 * vraća OK, u slučaju neuspješnog dodavanja vraća poruku pogreške da je došlo
	 * do neke greške.
	 */

	private String posaljiPostZahtjev(String jsonBody) {

		String url = "http://" + this.mreznaAdresa + ":" + this.mreznaVrata + "/nwtis/v1/api/kazne";
		HttpClient client = HttpClient.newHttpClient();

		// tu mozda ide 5.5!!
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://20.24.5.5:8080/nwtis/v1/api/kazne"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			int statusCode = response.statusCode();
			if (statusCode == 200) {
				return "OK";
			} else {
				System.out.println("ERROR: " + statusCode + " " + response.body());
				throw new RuntimeException("ERROR 42 Neuspjesan POST zahtjev koji hoce dodati kaznu");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ERROR: " + e.getMessage());
		}
	}

	/**
	 * Ova metoda služi za obradu zahtjeva koji imaju tri argumenta Prvo se iz
	 * poklapanja dobivaju vremena koja su potrebna kako bi se izvršio zahtjev, a
	 * zatim se za svako vozilo ispiše koliko ima kazni u zadanom vremenskom
	 * intervalu Ako je sve u redu, vraća se status OK kako bi se poštivali
	 * protokoli zadaće Također, postoji i provjera koja vraća pogrešku pod brojem
	 * 41 ako neko vozilo nema kazni u vremenu za koje je poslan zahtjev Na kraju se
	 * vraća komanda koja je rezultat obrade
	 * 
	 */

	private String obradaStatusaTri() {
		long vrijemeOd = Long.parseLong(this.poklapanjeTriArgumenta.group("vrijemeOd"));
		long vrijemeDo = Long.parseLong(this.poklapanjeTriArgumenta.group("vrijemeDo"));

		Map<Integer, Integer> brojacKazniZaVozilo = new HashMap<>();

		for (PodaciKazne kazna : sveKazne) {
			if (kazna.vrijemeKraj() >= vrijemeOd && kazna.vrijemeKraj() <= vrijemeDo) {
				int idVozila = kazna.id();
				brojacKazniZaVozilo.put(idVozila, brojacKazniZaVozilo.getOrDefault(idVozila, 0) + 1);
			}
		}

		String formatKomande = "OK";

		for (Map.Entry<Integer, Integer> entry : brojacKazniZaVozilo.entrySet()) {
			int idVozila = entry.getKey();
			int brojKazni = entry.getValue();
//			System.out.println("Vozilo ID: " + idVozila + ", Broj kazni: " + brojKazni);
			formatKomande += " " + idVozila + " " + brojKazni + ";";
		}

		if (brojacKazniZaVozilo.isEmpty()) {
			// System.out.println("Ide error 41");
			return "ERROR 41 Vozilo s ovim id nema kazni u ovom vremenu.";
		}

		// System.out.println("Response je" + formatKomande);
		return formatKomande;

	}

	/**
	 * Ova metoda služi za obradu zahtjeva koji imaju četiri argumenta Prvo se iz
	 * poklapanja dobivaju vremena koja su potrebna kako bi se izvršio zahtjev i id
	 * vozila na koje se zahtjev odnosi, a zatim se za određeno vozilo ispišu
	 * detalji njegove kazne u intervalu Ako je sve u redu, vraća se status OK kako
	 * bi se poštivali protokoli zadaće Također, postoji i provjera koja vraća
	 * pogrešku pod brojem 41 ako neko vozilo nema kazni u vremenu za koje je poslan
	 * zahtjev Na kraju se vraća komanda koja je rezultat obrade Metoda zapravo
	 * traži podatke o e-vozilu unutar zadanog vremena
	 * 
	 */

	private String obradaStatusaCetiri() {
		int voziloId = Integer.parseInt(this.poklapanjeCetiriArgumenta.group("id"));

		long vrijemeOd = Long.parseLong(this.poklapanjeCetiriArgumenta.group("vrijemeOd"));
		long vrijemeDo = Long.parseLong(this.poklapanjeCetiriArgumenta.group("vrijemeDo"));

		PodaciKazne najsvjezijaKazna = null;

		for (PodaciKazne kazna : sveKazne) {
			if (kazna.id() == voziloId && kazna.vrijemeKraj() >= vrijemeOd && kazna.vrijemeKraj() <= vrijemeDo) {
				if (najsvjezijaKazna == null || kazna.vrijemeKraj() > najsvjezijaKazna.vrijemeKraj()) {
					najsvjezijaKazna = kazna;
				}
			}
		}

		if (najsvjezijaKazna == null) {
			return "ERROR 41 Vozilo nema kazni u zadanom vremenu.";
		}

		// System.out.println("Najsvjezija kazna u ovom vremenu je" + najsvjezijaKazna);

		StringBuilder komanda = new StringBuilder();

		komanda.append("0K").append(" ").append(najsvjezijaKazna.vrijemeKraj()).append(" ")
				.append(najsvjezijaKazna.brzina()).append(" ").append(najsvjezijaKazna.gpsSirinaRadar()).append(" ")
				.append(najsvjezijaKazna.gpsDuzinaRadar());

		// System.out.println("Response je"+komanda.toString());

		return komanda.toString();
	}

	/**
	 * Preuzimaju se postavke iz konfiguracijske datoteke Prvi argument predstavlja
	 * naziv datoteke iz koje se treba preuzeti konfiguracija Zatim se iz datoteke
	 * preuzimaju mrežna vrata kazne koja su potrebna za mrežnu komunikaciju
	 * 
	 * @throws NeispravnaKonfiguracija Baca iznimku ako dođe do problema s datotekom
	 * @throws NumberFormatException   Baca iznimku ako dođe do problema brojevnog
	 *                                 tipa
	 * @throws UnknownHostException    Baca iznimku ako postoje problemi sa
	 *                                 povezivanjem
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 */

	public void preuzmiPostavke(String[] args)
			throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

		this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));

		this.mreznaAdresa = String.valueOf(konfig.dajPostavku("adresaRegistracije"));
	}
}
