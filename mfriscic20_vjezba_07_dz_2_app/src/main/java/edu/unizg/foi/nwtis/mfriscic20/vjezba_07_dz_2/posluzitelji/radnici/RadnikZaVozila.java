package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.radnici;

import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciVozila;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.RedPodaciVozila;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.GpsUdaljenostBrzina;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.CentralniSustav;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljZaVozila;

/**
 * Klasa RadnikZaVozila Implementira sučelje Runnable što znači da će objekte
 * ove klase izvršavati dretva
 */
public class RadnikZaVozila implements Runnable {

	AsynchronousSocketChannel klijentskiKanal;

	private AsynchronousSocketChannel mreznaUticnica;

	private PodaciVozila podaciVozila;

	private PosluziteljZaVozila posluziteljVozila;

	private CentralniSustav centralniSustav;

	private Pattern predlozakVozila = Pattern.compile(
			"^VOZILO (?<id>\\d+) (?<broj>\\d+) (?<vrijeme>\\d+) (?<brzina>-?\\d+([.]\\d+)?) (?<snaga>-?\\d+([.]\\d+)?) (?<struja>-?\\d+([.]\\d+)?) (?<visina>-?\\d+([.]\\d+)?) (?<gpsBrzina>-?\\d+([.]\\d+)?) (?<tempVozila>\\d+) (?<postotakBaterija>\\d+) (?<naponBaterija>-?\\d+([.]\\d+)?) (?<kapacitetBaterija>\\d+) (?<tempBaterija>\\d+) (?<preostaloKm>\\d+[.]\\d+) (?<ukupnoKm>\\d+[.]\\d+) (?<gpsSirina>\\d+[.]\\d+) (?<gpsDuzina>\\d+[.]\\d+)$");

	private Pattern predlozakStartanjaVozila = Pattern.compile("^VOZILO START (?<id>\\d+)$");

	private Pattern predlozakZaustavljanjaVozila = Pattern.compile("^VOZILO STOP (?<id>\\d+)$");

	private Matcher poklapanjeVozila;

	private Matcher poklapanjeStartanjaVozila;

	private Matcher poklapanjeZaustavljanjaVozila;

	/**
	 * Konstruktor klase u kojem postoji klijentski kanal radi veze s klijentom i
	 * referenca na centralni sustav jer su neki podaci iz centralnog sustava ovdje
	 * potrebni za rad (kolekcija svih radara)
	 */

	public RadnikZaVozila(AsynchronousSocketChannel klijentskiKanal, CentralniSustav centralniSustav) {

		super();
		this.centralniSustav = centralniSustav;
		this.klijentskiKanal = klijentskiKanal;

	}

	/*
	 * Služi za komunikaciju preko kanala. Unutar metode se obrađuju zahtjevi
	 * (metoda obradaZahtjeva) i šalju se odgovori. Metoda se izvršava kada je veza
	 * s klijentom otvorena.
	 */

	public void run() {

		SocketAddress klijentskaAdresa;
		try {
			klijentskaAdresa = klijentskiKanal.getRemoteAddress();
			// System.out.format("Otvorena veza prema %s%n", klijentskaAdresa);

			try {
				while (true) {
					if ((klijentskiKanal != null) && (klijentskiKanal.isOpen())) {
						var buffer = ByteBuffer.allocate(2048);
						Future<Integer> readBuff = klijentskiKanal.read(buffer);
						readBuff.get();
						var msg = new String(buffer.array()).trim();
						buffer.flip();

						if (msg.isEmpty() == true) {
							break;
						}

						String odgovor = obradaZahtjeva(msg);

						Future<Integer> writeBuff = klijentskiKanal.write(ByteBuffer.wrap(odgovor.getBytes()));
						writeBuff.get();
						buffer.clear();
					} else {
						break;
					}
				}
			} finally {
				this.klijentskiKanal.close();
			}
		} catch (Exception e) {
		}

	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije Ako je zahtjev prazan, tada vraća pogrešku Ako
	 *                zahtjev nije prazan, tada vraća odgovor Prije toga radi se
	 *                obrada zahtjeva u metodi obradaZahtjevaVozila U slučaju
	 *                neispravne komande vraća poruku pogreške
	 */

	public String obradaZahtjeva(String zahtjev) {

		if (zahtjev == null) {
			return "ERROR 20 Neispravna sintaksa komande.";
		}
		var odgovor = obradaZahtjevaVozila(zahtjev);
		if (odgovor != null) {
			return odgovor;
		}

		return "ERROR 20 Neispravna sintaksa komande.";
	}

	/**
	 * @param zahtjev Kao argument uzima zahtjev koji dobiva preko mrežne
	 *                komunikacije Ova metoda obrađuje sam zahtjev koji dođe na
	 *                server. Bavi se time da izračuna koje vozilo je u dometu
	 *                radara kako bi se vozilu mogla dati kazna u slučaju ako vozi
	 *                prebrzo. U metodi se također i priprema komanda za server te
	 *                se šalje zahtjev samom serveru. Pokriva sve slučajeve obrade
	 *                ovisno o sadržaju komande.
	 */

	public String obradaZahtjevaVozila(String zahtjev) {

		this.poklapanjeVozila = this.predlozakVozila.matcher(zahtjev);
		this.poklapanjeStartanjaVozila = this.predlozakStartanjaVozila.matcher(zahtjev);
		this.poklapanjeZaustavljanjaVozila = this.predlozakZaustavljanjaVozila.matcher(zahtjev);
		var statusVozila = poklapanjeVozila.matches();
		var statusStartanja = poklapanjeStartanjaVozila.matches();
		var statusStopiranja = poklapanjeZaustavljanjaVozila.matches();
		if (statusVozila) {

			postaviPodatkeVozila(poklapanjeVozila);
			double gpsSirinaVozila = Double.parseDouble(poklapanjeVozila.group("gpsSirina"));
			double gpsDuzinaVozila = Double.parseDouble(poklapanjeVozila.group("gpsDuzina"));
			// System.out.println("Sva vozila su" + this.centralniSustav.svaVozila);

			if (this.centralniSustav.svaVozila.containsKey(Integer.parseInt(poklapanjeVozila.group("id")))) {

				String jsonBody = kreirajJson(podaciVozila);

				return posaljiZahtjev(jsonBody, gpsSirinaVozila, gpsDuzinaVozila);
			}

			else {

				var vraceniRadar = provjeriDomet(gpsSirinaVozila, gpsDuzinaVozila);
				if (vraceniRadar != null) {
					var novaKomanda = new StringBuilder();
					novaKomanda.append("VOZILO").append(" ").append(this.podaciVozila.id()).append(" ")
							.append(this.podaciVozila.vrijeme()).append(" ").append(this.podaciVozila.brzina())
							.append(" ").append(this.podaciVozila.gpsSirina()).append(" ")
							.append(this.podaciVozila.gpsDuzina());

					MrezneOperacije.posaljiZahtjevPosluzitelju(vraceniRadar.adresaRadara(),
							vraceniRadar.mreznaVrataRadara(), novaKomanda.toString());
					return "OK";
				} else {
					return "ERROR 22 Radar izvan dometa";
				}

			}

		}

		else if (statusStartanja) {

			return statusStartanjaVozila();

		}

		else if (statusStopiranja) {

			return statusStopiranjaVozila();

		}

		else {
			return "ERROR 20 Pogresan format komande"; // tu mozda ide error 29
		}
	}

	/**
	 * Kreiranje tijela koje će biti poslano post zahtjevom na endpont za dodavanje
	 * vozila
	 * 
	 */
	private String kreirajJson(PodaciVozila podaciVozila) {

		return (String.format(
				"{\n" + "  \"id\": %d,\n" + "  \"broj\": %d,\n" + "  \"vrijeme\": %d,\n" + "  \"brzina\": %f,\n"
						+ "  \"snaga\": %f,\n" + "  \"struja\": %f,\n" + "  \"visina\": %f,\n"
						+ "  \"gpsBrzina\": %f,\n" + "  \"tempVozila\": %d,\n" + "  \"postotakBaterija\": %d,\n"
						+ "  \"naponBaterija\": %f,\n" + "  \"kapacitetBaterija\": %d,\n" + "  \"tempBaterija\": %d,\n"
						+ "  \"preostaloKm\": %f,\n" + "  \"ukupnoKm\": %f,\n" + "  \"gpsSirina\": %f,\n"
						+ "  \"gpsDuzina\": %f\n" + "}",
				podaciVozila.id(), podaciVozila.broj(), podaciVozila.vrijeme(), podaciVozila.brzina(),
				podaciVozila.snaga(), podaciVozila.struja(), podaciVozila.visina(), podaciVozila.gpsBrzina(),
				podaciVozila.tempVozila(), podaciVozila.postotakBaterija(), podaciVozila.naponBaterija(),
				podaciVozila.kapacitetBaterija(), podaciVozila.tempBaterija(), podaciVozila.preostaloKm(),
				podaciVozila.ukupnoKm(), podaciVozila.gpsSirina(), podaciVozila.gpsDuzina()));

	}

	/**
	 * Slanje post zahtjeva na endpoint za dodavanje. U slučaju uspješnog dodavanja
	 * vraća OK, u slučaju neuspješnog dodavanja vraća poruku pogreške da je došlo
	 * do neke greške.
	 */

	private String posaljiZahtjev(String tijelo, double gpsSirina, double gpsDuzina) {


		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://20.24.5.5:8080/nwtis/v1/api/vozila"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(tijelo)).build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			int statusCode = response.statusCode();
			if (statusCode == 200) {
				var vraceniRadar = provjeriDomet(gpsSirina, gpsDuzina);
				if (vraceniRadar != null) {
					var novaKomanda = new StringBuilder();
					novaKomanda.append("VOZILO").append(" ").append(this.podaciVozila.id()).append(" ")
							.append(this.podaciVozila.vrijeme()).append(" ").append(this.podaciVozila.brzina())
							.append(" ").append(this.podaciVozila.gpsSirina()).append(" ")
							.append(this.podaciVozila.gpsDuzina());

					MrezneOperacije.posaljiZahtjevPosluzitelju(vraceniRadar.adresaRadara(),
							vraceniRadar.mreznaVrataRadara(), novaKomanda.toString());
					return "OK";
				} else {
					return "ERROR 22 Radar izvan dometa";
				}
			} else {
				System.out.println("ERROR: " + statusCode + " " + response.body());
				return "ERROR 21 Neuspjesan POST zahtjev koji hoce dodati vozila";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}

	/**
	 * Provjerava postoji li vozilo u kolekciji svih vozila. Ako ne postoji, dodaje
	 * ga u red svih vozila kako bi se vozilo moglo pratiti. Vraća OK i ako ima i
	 * ako nema vozila.
	 */

	private String statusStartanjaVozila() {
		// System.out.println("Sva vozila" + this.centralniSustav.svaVozila);

		var id = Integer.valueOf(this.poklapanjeStartanjaVozila.group("id"));

		if (this.centralniSustav.svaVozila.containsKey(id)) {
			// System.out.println("Vec ima to vozilo");
			return "OK";
		}

		else {
			// System.out.println("Jos nema to vozilo");
			RedPodaciVozila redPodaciVozila = new RedPodaciVozila(id);
			this.centralniSustav.svaVozila.put(id, redPodaciVozila);

			// vazan je samo id, ovo je samo placeholder da se moze ostvariti taj
			// konstruktor

			return "OK";

		}
	}

	/**
	 * Provjerava postoji li vozilo u kolekciji svih vozila. Ako postoji, briše ga
	 * iz reda svih vozila koji služi za praćennje.
	 */

	private String statusStopiranjaVozila() {

		var id = Integer.valueOf(this.poklapanjeZaustavljanjaVozila.group("id"));

		if (this.centralniSustav.svaVozila.containsKey(id)) {
			// System.out.println("Vec ima to vozilo, brisem");
			this.centralniSustav.svaVozila.remove(id);

			return "OK";
		}

		else {
			return "OK";
		}

	}

	/**
	 * Postavlja podatke vozila kako bi se moglo napraviti vozilo
	 * 
	 */

	private void postaviPodatkeVozila(Matcher poklapanjeVozila) {

		int id = Integer.parseInt(poklapanjeVozila.group("id"));
		int broj = Integer.parseInt(poklapanjeVozila.group("broj"));
		long vrijeme = Long.parseLong(poklapanjeVozila.group("vrijeme"));
		double brzina = Double.parseDouble(poklapanjeVozila.group("brzina"));
		double snaga = Double.parseDouble(poklapanjeVozila.group("snaga"));
		double struja = Double.parseDouble(poklapanjeVozila.group("struja"));
		double visina = Double.parseDouble(poklapanjeVozila.group("visina"));
		double gpsBrzina = Double.parseDouble(poklapanjeVozila.group("gpsBrzina"));
		int tempVozila = Integer.parseInt(poklapanjeVozila.group("tempVozila"));
		int postotakBaterija = Integer.parseInt(poklapanjeVozila.group("postotakBaterija"));
		double naponBaterija = Double.parseDouble(poklapanjeVozila.group("naponBaterija"));
		int kapacitetBaterija = Integer.parseInt(poklapanjeVozila.group("kapacitetBaterija"));
		int tempBaterija = Integer.parseInt(poklapanjeVozila.group("tempBaterija"));
		double preostaloKm = Double.parseDouble(poklapanjeVozila.group("preostaloKm"));
		double ukupnoKm = Double.parseDouble(poklapanjeVozila.group("ukupnoKm"));

		double gpsSirina = Double.parseDouble(poklapanjeVozila.group("gpsSirina"));
		double gpsDuzina = Double.parseDouble(poklapanjeVozila.group("gpsDuzina"));

		this.podaciVozila = new PodaciVozila(id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila,
				postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina,
				gpsDuzina);

	}

	/**
	 * Izračunava udaljenost između vozila i radara. Množi se s 1000 kako bi se
	 * dobio rezultat u metrima. Ako povratni rezultat nije null, tada se mogu
	 * raditi daljnje operacije s vozilom jer je u dometu radara.
	 */

	public PodaciRadara provjeriDomet(double geoSirinaVozila, double geoDuzinaVozila) {
		for (Map.Entry<Integer, PodaciRadara> entry : centralniSustav.sviRadari.entrySet()) {
			Integer key = entry.getKey();
			PodaciRadara radar = entry.getValue();
			double radarGpsSirina = radar.gpsSirina();
			double radarGpsDuzina = radar.gpsDuzina();

			double kalkulacijaUdaljenosti = GpsUdaljenostBrzina.udaljenostKm(geoSirinaVozila, geoDuzinaVozila,
					radarGpsSirina, radarGpsDuzina) * 1000;

			if (kalkulacijaUdaljenosti <= radar.maksUdaljenost()) {
				return radar;
			}

		}

		return null;
	}
}
