package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji;

import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.radnici.RadnikZaRadare;

/**
 * Klasa PosluziteljRadara
 */
public class PosluziteljRadara {

	private PodaciRadara podaciRadara;
	private ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();

	/**
	 * Provjerava broj argumenata (može raditi sa jednim ili tri parametra ovisno o
	 * zahtjevu. Nakon toga se instancira objekt klase Poslužitelj radara i
	 * preuzimaju se postavke. Ako se radi o jednom argumentu, tada se pozivaju
	 * metode koje registriraju radar i pokreću poslužitelj. Ako se radi o tri
	 * argumenta, tada se još dodatno provjerava tip argumenata kako bi se izvršila
	 * operacija sukladno danim argumentima. Ako je argument id, tj. broj može se
	 * obrisati radar s tim ID, a ako je tekst SVE onda se jednostavno obrišu svi
	 * radari.
	 * 
	 * @throws NeispravnaKonfiguracija Baca iznimku ako dođe do problema s datotekom
	 * @throws NumberFormatException   Baca iznimku ako dođe do problema brojevnog
	 *                                 tipa
	 * @throws UnknownHostException    Baca iznimku ako postoje problemi sa
	 *                                 povezivanjem
	 * @param args polje argumenata pri pokretanju
	 * 
	 * 
	 * 
	 */

	public static void main(String[] args) {
		if (args.length != 1 && args.length != 3) {
			System.out.println("Broj argumenata nije 1 ili 3.");
			return;
		}

		PosluziteljRadara posluziteljRadara = new PosluziteljRadara();
		try {
			posluziteljRadara.preuzmiPostavke(args);

			if (args.length == 1) {
				posluziteljRadara.registrirajPosluzitelja();

				posluziteljRadara.pokreniPosluzitelja();
			}

			if (args.length == 3) {

				var provjeraTipaArgumentaJedanRadar = args[2].matches("\\d+");

				if (provjeraTipaArgumentaJedanRadar) {

					int id = Integer.parseInt(args[2]);

					posluziteljRadara.obrisiRadarSId(id);

				}

				else {

					posluziteljRadara.obrisiSveRadare();

				}

			}

		} catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	/**
	 * Metoda slaže naredbu za slanje poslužitelju u slučaju ako je potrebno
	 * obrisati iz liste svih radara određeni radar. Zatim se ta naredba pošalje
	 * putem mrežnih operacija.
	 * 
	 * @param id Oznaka radara koji treba biti izbrisan iz liste svih radara
	 */

	private void obrisiRadarSId(int id) {

		StringBuilder naredbaZaBrisanjeBuilder = new StringBuilder();

		naredbaZaBrisanjeBuilder.append("RADAR").append(" ").append("OBRIŠI").append(" ").append(id);

		var pretvoriUString = naredbaZaBrisanjeBuilder.toString();
		MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
				this.podaciRadara.mreznaVrataRegistracije(), pretvoriUString);

	}

	/**
	 * Metoda slaže naredbu za slanje poslužitelju u slučaju kada je potrebno
	 * obrisati sve postojeće radare. Zatim se ta naredba pošalje putem mrežnih
	 * operacija.
	 * 
	 */

	private void obrisiSveRadare() {

		StringBuilder naredbaZaBrisanjeBuilder = new StringBuilder();

		naredbaZaBrisanjeBuilder.append("RADAR").append(" ").append("OBRIŠI").append(" ").append("SVE");

		var pretvoriUString = naredbaZaBrisanjeBuilder.toString();
		MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
				this.podaciRadara.mreznaVrataRegistracije(), pretvoriUString);

	}

	/**
	 * Metoda slaže naredbu za registriranje poslužitelja i zatim šalje zahtjev
	 * poslužitelju.
	 * 
	 */

	public boolean registrirajPosluzitelja() {
		var komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append(this.podaciRadara.id()).append(" ")
				.append(this.podaciRadara.adresaRadara()).append(" ").append(this.podaciRadara.mreznaVrataRadara())
				.append(" ").append(this.podaciRadara.gpsSirina()).append(" ").append(this.podaciRadara.gpsDuzina())
				.append(" ").append(this.podaciRadara.maksUdaljenost());

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.podaciRadara.adresaRegistracije(),
				this.podaciRadara.mreznaVrataRegistracije(), komanda.toString());
		if (odgovor != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Pokreće poslužitelj, za svakog klijenta se kreira nova virtualna dretva koja
	 * izvršava objekt klase radnik za radare.
	 * 
	 */

	public void pokreniPosluzitelja() {

		boolean kraj = false;

		try (ServerSocket mreznaUticnicaPosluzitelja = new ServerSocket(this.podaciRadara.mreznaVrataRadara())) {
			while (!kraj) {
				var mreznaUticnica = mreznaUticnicaPosluzitelja.accept();
				var radnikZaRadare = new RadnikZaRadare(mreznaUticnica, this.podaciRadara, this);

				var dretva = tvornicaDretvi.newThread(radnikZaRadare);
				dretva.start();
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Preuzimaju se postavke iz konfiguracijske datoteke za određeni radar kao što
	 * su mrežna vrata radara ili maksimalna udaljenost tj domet.
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 */

	public void preuzmiPostavke(String[] args)
			throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

		this.podaciRadara = new PodaciRadara(Integer.valueOf(konfig.dajPostavku("id")),
				InetAddress.getLocalHost().getHostName(), Integer.valueOf(konfig.dajPostavku("mreznaVrataRadara")),
				Integer.valueOf(konfig.dajPostavku("maksBrzina")), Integer.valueOf(konfig.dajPostavku("maksTrajanje")),
				Integer.valueOf(konfig.dajPostavku("maksUdaljenost")), konfig.dajPostavku("adresaRegistracije"),
				Integer.valueOf(konfig.dajPostavku("mreznaVrataRegistracije")), konfig.dajPostavku("adresaKazne"),
				Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne")), konfig.dajPostavku("postanskaAdresaRadara"),
				Double.valueOf(konfig.dajPostavku("gpsSirina")), Double.valueOf(konfig.dajPostavku("gpsDuzina")));
	}
}
