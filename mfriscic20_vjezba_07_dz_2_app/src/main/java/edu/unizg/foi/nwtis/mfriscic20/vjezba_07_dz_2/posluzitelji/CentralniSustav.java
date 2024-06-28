
package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciRadara;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.RedPodaciVozila;

/**
 * Klasa CentralniSustav
 */
public class CentralniSustav {
	

	private int mreznaVrataRadara;
	
	public int mreznaVrataVozila;
	
	private int mreznaVrataNadzora;
	
	private int maksVozila;
	
	private ThreadFactory tvornicaDretvi = Thread.ofVirtual().factory();
	
	/**
	 * sadrži popis svih radara
	 */
	public ConcurrentHashMap<Integer, PodaciRadara> sviRadari = new ConcurrentHashMap<Integer, PodaciRadara>();
	
	/**
	 * sadrži popis svih vozila
	 */

	public ConcurrentHashMap<Integer, RedPodaciVozila> svaVozila = new ConcurrentHashMap<Integer, RedPodaciVozila>();

	/**
	 * Provjerava broj argumenata Zatim instancira varijablu centralniSustav
	 * Preuzimaju se postavke i pokusa se pokrenuti posluzitelj U slucaju pogreske
	 * hvata se iznimka
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 */

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Broj argumenata nije 1.");
			return;
		}

		CentralniSustav centralniSustav = new CentralniSustav();
		try {
			centralniSustav.preuzmiPostavke(args);

			centralniSustav.pokreniPosluzitelja();

		} catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

	/**
	 * Pokreće PosluziteljZaRegistracijuRadara i PosluziteljZaVozila Zatim za svaki
	 * server kreira dretvu te pokreće te dretve Također, pomoću metode join se čeka
	 * da svaka od navedenih dretvi završi svoj rad Ako dođe do pogreške u radu lovi
	 * se iznimka
	 */

	public void pokreniPosluzitelja() {

		PosluziteljZaRegistracijuRadara posluziteljZaRegistracijuRadara = new PosluziteljZaRegistracijuRadara(
				mreznaVrataRadara, this);
		PosluziteljZaVozila posluziteljZaVozila = new PosluziteljZaVozila(mreznaVrataVozila, this);

		Thread dretvaPosluziteljaRadara = tvornicaDretvi.newThread(posluziteljZaRegistracijuRadara);
		Thread dretvaPosluziteljaVozila = tvornicaDretvi.newThread(posluziteljZaVozila);
		dretvaPosluziteljaRadara.start();
		dretvaPosluziteljaVozila.start();

		try {

			dretvaPosluziteljaRadara.join();

			dretvaPosluziteljaVozila.join();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Preuzimaju se postavke iz konfiguracijske datoteke Prvi argument predstavlja
	 * naziv datoteke iz koje se treba preuzeti konfiguracija Zatim se vrijednosti
	 * mrežnih vrata radara, mrežnih vrata vozila, mrežnih vrata nadzora i
	 * maksimalne količine vozila preuzimaju iz datoteke
	 * 
	 * @param args polje argumenata pri pokretanju
	 * @throws NeispravnaKonfiguracija  ako dođe do problema s datotekom konfiguracije
	 * @throws NumberFormatException    ako dođe do problema prilikom parsiranja brojeva
	 * @throws UnknownHostException     ako dođe do problema sa povezivanjem
	 * 
	 */

	public void preuzmiPostavke(String[] args)
			throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

		this.mreznaVrataRadara = Integer.valueOf(konfig.dajPostavku("mreznaVrataRadara"));
		this.mreznaVrataVozila = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
		this.mreznaVrataNadzora = Integer.valueOf(konfig.dajPostavku("mreznaVrataNadzora"));
		this.maksVozila = Integer.valueOf(konfig.dajPostavku("maksVozila"));

	}
}
