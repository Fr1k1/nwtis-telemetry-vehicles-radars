package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.klijenti;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciVozila;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.radnici.RadnikZaVozila;

/**
 * Klasa SimulatorVozila
 * Služi kako bi slala podatke o vozilima u sustav
 */
public class SimulatorVozila {
	


	private String adresaVozila;
	
	/**
	 * trajanje brze vožnje
	 */
	private int trajanjeSek;
	
	private int mreznaVrataVozila;
	
	private int trajanjePauze;
	
	private RadnikZaVozila radnikZaVozila;
	
	private PodaciVozila podaciVozila;
	
	private int id;
	


	private String csvDatoteka;

	/**
	 * Provjerava broj argumenata koji u ovom slučaju može biti jedino tri za
	 * ispravan rad. Zatim se instancira objekt klase simulatorVozila i pokušaju se
	 * preuzeti postavke. Preuzimaju se postavke i pokusa se pokrenuti posluzitelj U
	 * slucaju pogreške hvata se iznimka
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 */

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Broj argumenata nije 3");
			return;
		}

		SimulatorVozila simulatorVozila = new SimulatorVozila();

		try {

			simulatorVozila.preuzmiPostavke(args);
			try {
				simulatorVozila.ucitajPodatkeIzCsv();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
			System.out.println(e.getMessage());
			return;

		}
	}

	/**
	 * Preuzimaju se postavke iz konfiguracijske datoteke. Prvi argument predstavlja
	 * naziv datoteke iz koje se treba preuzeti konfiguracija Zatim se za drugi i
	 * treći argument uzimaju argumenti dani pri izvođenju koji su potrebni te oni
	 * predstavljaju naziv csv datoteke i ID vozila koje će biti pokrenuto pomoću te
	 * datoteke. Zatim se iz datoteke preuzimaju ostali podaci.
	 * @throws NeispravnaKonfiguracija Baca iznimku ako dođe do problema s datotekom
	 * @throws NumberFormatException   Baca iznimku ako dođe do problema brojevnog
	 *                                 tipa
	 * @throws UnknownHostException    Baca iznimku ako postoje problemi sa
	 *                                 povezivanjem
	 * 
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 */

	public void preuzmiPostavke(String[] args)

			throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {

		final String drugiArgument = args[1];
		final Integer treciArgument = Integer.valueOf(args[2]);
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
		this.adresaVozila = InetAddress.getLocalHost().getHostName();
		this.mreznaVrataVozila = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
		this.trajanjeSek = Integer.valueOf(konfig.dajPostavku("trajanjeSek"));
		this.trajanjePauze = Integer.valueOf(konfig.dajPostavku("trajanjePauze"));

		this.csvDatoteka = drugiArgument;
		this.id = treciArgument;

	}

	/**
	 * Metoda služi za spajanje na mrežnu utičnicu od Poslužitelja vozila. Nakon
	 * spajanja otvara se datoteka s podacima vozila i čita se redak po redak te se
	 * šalju pripremljeni podaci koji se pripreme u metodi dodajIdIRedniBrojVozilu.
	 * Komande se šalju asinkrono i ne čeka se na odgovor. Dodatno, u metodi se
	 * događa sinkronizacija pomoću metode sleep (periodično izvršavanje).
	 * 
	 * U slučaju nekog neuspjeha, baca se iznimka
	 * 
	 * 
	 */

	private void ucitajPodatkeIzCsv() throws Exception {

		AsynchronousSocketChannel klijentovKanal = AsynchronousSocketChannel.open();

		SocketAddress serverAddr = new InetSocketAddress(this.adresaVozila, this.mreznaVrataVozila);

		Future<Void> result = klijentovKanal.connect(serverAddr);
		result.get();
		try (BufferedReader citac = new BufferedReader(new FileReader(csvDatoteka))) {
			String prviRedakTablice = citac.readLine();
			Long prethodnoVrijeme = null;
			Integer brojRetka;
			brojRetka = 0;
			String redak;
			while ((redak = citac.readLine()) != null) {
				dodajIdIRedniBrojVozilu(redak, brojRetka);
				var zaSlanje = vratiSadrzajKomandeZaSlanje();
				byte[] podaci = zaSlanje.getBytes(StandardCharsets.UTF_8);
				ByteBuffer buffer = ByteBuffer.wrap(podaci);
				Future<Integer> writeBuff = klijentovKanal.write(buffer);
				long trenutnoVrijeme = Long.parseLong(redak.split(",")[0]);
				if (prethodnoVrijeme != null) {
					long razlikaVremena = trenutnoVrijeme - prethodnoVrijeme;
					double korekcijaVremena = (trajanjeSek / 1000.0);
					double vrijemeSpavanja = korekcijaVremena * razlikaVremena;

					Thread.sleep((long) vrijemeSpavanja);
				}
				prethodnoVrijeme = trenutnoVrijeme;
				Thread.sleep(this.trajanjePauze);
				brojRetka = brojRetka + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Priprema sadržaj komande za slanje serveru kako bi odgovarala dogovorenom
	 * regularnom izrazu
	 * @return Vraća komandu koju je potrebno poslati na server
	 */

	private String vratiSadrzajKomandeZaSlanje() {

		var komanda = new StringBuilder();
		komanda.append("VOZILO").append(" ").append(this.podaciVozila.id()).append(" ").append(this.podaciVozila.broj())
				.append(" ").append(this.podaciVozila.vrijeme()).append(" ").append(this.podaciVozila.brzina())
				.append(" ").append(this.podaciVozila.snaga()).append(" ").append(this.podaciVozila.struja())
				.append(" ").append(this.podaciVozila.visina()).append(" ").append(this.podaciVozila.gpsBrzina())
				.append(" ").append(this.podaciVozila.tempVozila()).append(" ")
				.append(this.podaciVozila.postotakBaterija()).append(" ").append(this.podaciVozila.naponBaterija())
				.append(" ").append(this.podaciVozila.kapacitetBaterija()).append(" ")
				.append(this.podaciVozila.tempBaterija()).append(" ").append(this.podaciVozila.preostaloKm())
				.append(" ").append(this.podaciVozila.ukupnoKm()).append(" ").append(this.podaciVozila.gpsSirina())
				.append(" ").append(this.podaciVozila.gpsDuzina());

		return komanda.toString();

	}

	/**
	 * Dodaje id i redni broj vozilu iz csv datoteke kako bi zapis o vozilu bio
	 * potpun. Zatim kreira objekt sa podacima koji su pročitani.
	 * 
	 * @param redak Predstavlja jedan redak u csv datoteci
	 * @param brojRetka Predstavlja broj retka u csv datoteci
	 * 
	 */

	private void dodajIdIRedniBrojVozilu(String redak, Integer brojRetka) {
		String[] dijeloviRetka = redak.split(",");

		long vrijeme = Long.parseLong(dijeloviRetka[0]);
		double brzina = Double.parseDouble(dijeloviRetka[1]);
		double snaga = Double.parseDouble(dijeloviRetka[2]);
		double struja = Double.parseDouble(dijeloviRetka[3]);
		double visina = Double.parseDouble(dijeloviRetka[4]);
		double gpsBrzina = Double.parseDouble(dijeloviRetka[5]);
		int tempVozila = Integer.parseInt(dijeloviRetka[6]);
		int postotakBaterija = Integer.parseInt(dijeloviRetka[7]);
		double naponBaterija = Double.parseDouble(dijeloviRetka[8]);
		int kapacitetBaterija = Integer.parseInt(dijeloviRetka[9]);
		int tempBaterija = Integer.parseInt(dijeloviRetka[10]);
		double preostaloKm = Double.parseDouble(dijeloviRetka[11]);
		double ukupnoKm = Double.parseDouble(dijeloviRetka[12]);
		double gpsSirina = Double.parseDouble(dijeloviRetka[13]);
		double gpsDuzina = Double.parseDouble(dijeloviRetka[14]);

		this.podaciVozila = new PodaciVozila(this.id, brojRetka, vrijeme, brzina, snaga, struja, visina, gpsBrzina,

				tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm,
				gpsSirina, gpsDuzina);
		

	}

}
