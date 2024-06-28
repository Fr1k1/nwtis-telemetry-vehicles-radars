package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.PodaciVozila;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.radnici.RadnikZaVozila;

/**
 * Klasa PosluziteljZaVozila Implementira sučelje Runnable što znači da će
 * objekte ove klase izvršavati dretva
 */
public class PosluziteljZaVozila implements Runnable {

	private PodaciVozila podaciVozila;
	public int mreznaVrata;
	static ExecutorService executor;
	private CentralniSustav centralniSustav;
	volatile AtomicInteger brojDretvi = new AtomicInteger(0);
	volatile List<Future<Integer>> odgovori = new ArrayList<Future<Integer>>();

	/**
	 * Konstruktor klase u kojem se postavljaju vrijednosti za mrežna vrata te se
	 * dobiva referenca na centralni sustav jer su neki od podataka iz centralnog
	 * sustava potrebni poslužitelju za vozila
	 */

	public PosluziteljZaVozila(int mreznaVrata, CentralniSustav centralniSustav) {
		super();
		this.mreznaVrata = mreznaVrata;
		this.centralniSustav = centralniSustav;
	}

	/**
	 * Provjerava broj argumenata (može raditi s isključivo jednim parametrom prema
	 * specifikaciji)
	 * 
	 * @param args polje argumenata pri pokretanju
	 * 
	 * 
	 */

	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Broj argumenata nije 1.");
			return;
		}

	}

	/**
	 * Otvara se mrežna utičnica na zadanim mrežnim vratima te se radi u
	 * višedretvenom načinu na asinkroni način putem kanala. Za svaki simulator
	 * vozila kreira se nova virtualna dretva koja izvršava objekt klase radnik za
	 * vozila. U slučaju pogreške u radu, lovi se iznimka koja vraća poruku greške
	 * Postoji i finally blok koji služi kako bi se oslobodili resursi kao dobra
	 * praksa
	 */

	public void run() {

		try {
			final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()
					.bind(new InetSocketAddress(this.mreznaVrata));

			executor = Executors.newVirtualThreadPerTaskExecutor();

			while (true) {
				Future<AsynchronousSocketChannel> kanalPrihvacanja = server.accept();
				AsynchronousSocketChannel klijentskiKanal = kanalPrihvacanja.get();
				odgovori.add(executor.submit(() -> cekajObradiKlijenta(klijentskiKanal)));

			}
		} catch (Exception e) {
			System.out.println("Pogreska u radu posluzitelja: " + e.getMessage());
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}
	}

	// Primjer 33_23 s predavanja

	/**
	 * Metoda služi za obradu zahtjeva klijenta. U try-catch bloku kodu nalazi se
	 * kod za obradu. Za to služi objekt klase radnik za vozila. Zatim se pokreće
	 * nova virtualna dretva koja izvodi logiku obrade. Join služi kako bi se obrada
	 * pričekala prije novog posla. Funkcija sama po sebi vraća broj dretvi, to će
	 * možda zatrebati u kasnijim zadaćama.
	 * 
	 * @param klijentskiKanal Parametar koji služi kako bi se metodi proslijedio kanal klijenta.
	 */

	Integer cekajObradiKlijenta(AsynchronousSocketChannel klijentskiKanal) {

		int broj = brojDretvi.incrementAndGet();
		try {
			var obrada = new RadnikZaVozila(klijentskiKanal, centralniSustav);
			var t = Thread.startVirtualThread(obrada);
			t.join();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		brojDretvi.decrementAndGet();
		return broj;

	}

}
