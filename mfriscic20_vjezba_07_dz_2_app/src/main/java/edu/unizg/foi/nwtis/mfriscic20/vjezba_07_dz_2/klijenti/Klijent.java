package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.klijenti;

import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;

/**
 * Klasa Klijent
 */
public class Klijent {
	
	private String adresaKazne;
	
	private int mreznaVrataKazne;
	
	private long vrijemeOd;
	
	private long vrijemeDo;
	
	/**
	 * Id vozila koje treba dobiti kaznu
	 * */
	private String id;

	/**
	 * Provjerava broj argumenata koji u ovom slučaju može biti 3 ili 4 ovisno o
	 * operaciji. Zatim instancira varijablu klijenta Preuzimaju se postavke i
	 * pokuša se poslati komanda poslužitelju za kazne preko mrežne komunikacije.
	 * 
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

		if (args.length != 4 && args.length != 3) {
			System.out.println("Broj argumenata nije 3 ili 4");
			return;
		}

		Klijent klijent = new Klijent();

		try {

			klijent.preuzmiPostavke(args);

			String komanda = klijent.vratiSadrzajKomandeZaSlanje(args.length);

			MrezneOperacije.posaljiZahtjevPosluzitelju(klijent.adresaKazne, klijent.mreznaVrataKazne, komanda);


		}

		catch (NeispravnaKonfiguracija | NumberFormatException | UnknownHostException e) {
			System.out.println(e.getMessage());
			return;

		}

	}
	
	
	/**
	 * Preuzimaju se postavke iz konfiguracijske datoteke. U ovom slučaju, postoji više opcija preuzimanja postavki.
	 *  Prvi argument predstavlja
	 * naziv datoteke iz koje se treba preuzeti konfiguracija. Ako je broj argumenata tri, onda se dobiveni argumenti parsiraju kako
	 * bi mogli predstavljati određeno vrijeme za izračun perioda za kazne. Ako je broj argumenata četiri, onda postoji jedan parametar više
	 * koji označava identifikacijski broj vozila za koji se treba ispisati njegove kazne u od do vremenu.
	 * Iz konfiguracijske datoteke još se uzimaju i podaci o mrežnim vratima i adresi kazne.
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

		if (args.length == 3) {

			final String drugiArgument = args[1];
			final String treciArgument = args[2];

			this.vrijemeOd = Long.parseLong(drugiArgument);
			this.vrijemeDo = Long.parseLong(treciArgument);

		}

		if (args.length == 4) {

			final String drugiArgument = args[1];
			final String treciArgument = args[2];
			final String cetvrtiArgument = args[3];

			this.vrijemeOd = Long.parseLong(treciArgument);
			this.vrijemeDo = Long.parseLong(cetvrtiArgument);
			this.id = drugiArgument;

		}

		this.adresaKazne = InetAddress.getLocalHost().getHostName();
		this.mreznaVrataKazne = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));

	}
	
	/**
	 * Metoda služi kako bi ovisno o broju argumenata pri pokretanju pripremila komandu za slanje poslužitelju kazni.
	 * @param velicina Argument koji se proslijedi zapravo označava koliko ima argumenata u zahtjevu pa se prema tome može poslati
	 * određena komanda
	 * @return Vraća komandu koja će biti poslana poslužitelju
	 * */

	public String vratiSadrzajKomandeZaSlanje(int velicina) {

		var komanda = new StringBuilder();

		if (velicina == 4)
			komanda.append("VOZILO").append(" ").append(this.id).append(" ").append(this.vrijemeOd).append(" ")
					.append(this.vrijemeDo);
		if (velicina == 3)
			komanda.append("STATISTIKA").append(" ").append(this.vrijemeOd).append(" ").append(this.vrijemeDo);

		return komanda.toString();

	}

}
