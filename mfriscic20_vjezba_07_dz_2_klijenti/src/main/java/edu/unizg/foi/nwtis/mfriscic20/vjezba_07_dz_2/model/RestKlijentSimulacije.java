package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Vozilo;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestKlijentSimulacije {

	@Inject
	private ServletContext context;

	/**
	 * Kopntruktor klase.
	 */
	public RestKlijentSimulacije() {

	}

	public static int trajanjeSek;

	/**
	 * Vraća sve simulacije vožnje
	 *
	 * @return vozila
	 */
	public List<Vozilo> getSimulacijeJSON() {
		RestSimulacije rv = new RestSimulacije();
		List<Vozilo> vozila = rv.getJSON();

		return vozila;
	}

	/**
	 * Vraća simulacije vožnje u intervalu od do.
	 *
	 * @param odVremena početak intervala
	 * @param doVremena kraj intervala
	 * @return vozila
	 */

	public List<Vozilo> getSimulacijeJSON_od_do(long odVremena, long doVremena) {
		RestSimulacije rk = new RestSimulacije();
		List<Vozilo> vozila = rk.getJSON_od_do(odVremena, doVremena);

		return vozila;
	}

	/**
	 * Vraća simulacije za vozilo.
	 *
	 * @param id id vozila
	 * @return vozila
	 */

	public List<Vozilo> getSimulacijeJSON_vozilo(String id) {
		RestSimulacije rk = new RestSimulacije();
		List<Vozilo> vozila = rk.getJSON_vozilo(id);
		return vozila;
	}

	/**
	 * Vraća simulacije za vozilo u intervalu od do..
	 *
	 * @param id        id vozila
	 * @param odVremena početak intervala
	 * @param doVremena kraj intervala
	 * @return vozila
	 */

	public List<Vozilo> getSimulacijeJSON_vozilo_od_do(String id, long odVremena, long doVremena) {
		RestSimulacije rk = new RestSimulacije();
		List<Vozilo> vozila = rk.getJSON_vozilo_od_do(id, odVremena, doVremena);

		return vozila;
	}

	/**
	 * Dodaje simulaciju.
	 *
	 * @param vozilo vozilo
	 * @return odgovor
	 */

	public String postDodavanjeUBazu(Vozilo vozilo) {
		RestSimulacije rr = new RestSimulacije();

		String odgovorString = rr.dodajVoznjuUBazu(vozilo);

		return odgovorString;
	}

	/**
	 * Dodaje simulaciju.
	 *
	 * @param vozilo vozilo
	 * @return odgovor
	 */

	public String postDodavanjeSimulacije(Vozilo vozilo) {
		RestSimulacije rr = new RestSimulacije();

		String odgovorString = rr.dodajUBazu(vozilo);

		return odgovorString;
	}

	/**
	 * Vraća zapis vozila s kreiranim vozilom sa svim podacima na koji su dodani i
	 * redak i id
	 * 
	 * @param redak          redak u bazu
	 * @param brojRetka      broj retka u bazi
	 * @param prethodniRedak prethodni redak za potrebe izračuna
	 */

	public Vozilo postKreiranjeVozila(String redak, int id, String nesto, int nesto2) {
		RestSimulacije rr = new RestSimulacije();

		Vozilo odgovorString = null;
		try {
			odgovorString = rr.vratiVozilo(redak, id, nesto, nesto2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return odgovorString;
	}

	/**
	 * Klasa RestSimulacije.
	 */

	static class RestSimulacije {
		/** web target. */
		private final WebTarget webTarget;

		/** client. */
		private final Client client;

		/** knstanta BASE_URI. */
		private static final String BASE_URI = "http://localhost:9080/";

		/**
		 * Konstruktor klase.
		 */
		public RestSimulacije() {
			client = ClientBuilder.newClient();
			webTarget = client.target(BASE_URI).path("nwtis/v1/api/simulacije");
		}

		/**
		 * Vraća simulacije.
		 *
		 * @return simulacije
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public List<Vozilo> getJSON() throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> simulacije = new ArrayList<Vozilo>();

			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pvozila = jb.fromJson(odgovor, Vozilo[].class);
				simulacije.addAll(Arrays.asList(pvozila));
			}

			return simulacije;
		}

		/**
		 * Vraća simulacije u intervalu od do.
		 *
		 * @param odVremena početak intervala
		 * @param doVremena kraj intervala
		 * @return simulacije
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public List<Vozilo> getJSON_od_do(long odVremena, long doVremena) throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> simulacije = new ArrayList<Vozilo>();

			resource = resource.queryParam("od", odVremena);
			resource = resource.queryParam("do", doVremena);
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pvozila = jb.fromJson(odgovor, Vozilo[].class);
				simulacije.addAll(Arrays.asList(pvozila));
			}

			return simulacije;
		}

		/**
		 * Vraća simulacije za vozilo.
		 *
		 * @param id id vozila
		 * @return kazne
		 * @throws ClientErrorException iznimka kod poziva klijentaon
		 */

		public List<Vozilo> getJSON_vozilo(String id) throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> simulacije = new ArrayList<Vozilo>();

			resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] { id }));
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pvozila = jb.fromJson(odgovor, Vozilo[].class);
				simulacije.addAll(Arrays.asList(pvozila));
			}

			return simulacije;
		}

		/**
		 * Vraća simulacije za vozilo u intervalu od do..
		 *
		 * @param id        id vozila
		 * @param odVremena početak intervala
		 * @param doVremena kraj intervala
		 * @return kazne
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public List<Vozilo> getJSON_vozilo_od_do(String id, long odVremena, long doVremena)
				throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> vozila = new ArrayList<Vozilo>();

			resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] { id }));
			resource = resource.queryParam("od", odVremena);
			resource = resource.queryParam("do", doVremena);
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pkazne = jb.fromJson(odgovor, Vozilo[].class);
				vozila.addAll(Arrays.asList(pkazne));
			}

			return vozila;
		}

		/**
		 * Dodaje voznju.
		 *
		 * @param vozilo vozilo
		 * @return odgovor
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public String dodajVoznjuUBazu(Vozilo vozilo) {
			WebTarget resource = webTarget;
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = request.post(Entity.entity(vozilo, MediaType.APPLICATION_JSON));

			if (restOdgovor.getStatus() == 200) {
				return "Uspjesno se dodala voznja";

			}

			else {
				return "Nije se uspjesno dodalo";
			}

		}

		/**
		 * 
		 * Vraća zapis vozila s kreiranim vozilom sa svim podacima na koji su dodani i
		 * redak i id
		 * 
		 * @param redak          redak u bazu
		 * @param brojRetka      broj retka u bazi
		 * @param prethodniRedak prethodni redak za potrebe izračuna
		 */

		private Vozilo dodajIdIRedniBrojVozilu(String redak, Integer brojRetka, String redakPrethodni, int idVozila) {
			String[] dijeloviRetka = redak.split(",");
			String[] redakPrije;
			Vozilo podaciVozilo;

			if (redakPrethodni == null) {
				redakPrije = dijeloviRetka;
			} else {
				redakPrije = redakPrethodni.split(",");

			}

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

			podaciVozilo = new Vozilo(idVozila, brojRetka, vrijeme, brzina, snaga, struja, visina, gpsBrzina,

					tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm,
					gpsSirina, gpsDuzina);

			return podaciVozilo;

		}

		/**
		 * Vraća zapis vozila s kreiranim vozilom sa svim podacima na koji su dodani i
		 * redak i id, ali koristi se i za izračun vremena između redaka koje je
		 * potrebno za simulaciju
		 * 
		 * @param redak          redak u bazu
		 * @param brojRetka      broj retka u bazi
		 * @param prethodniRedak prethodni redak za potrebe izračuna
		 */

		public Vozilo vratiVozilo(String redak, Integer brojRetka, String redakPrethodni, int idVozila)
				throws InterruptedException {
			Vozilo vozilo = dodajIdIRedniBrojVozilu(redak, brojRetka, redakPrethodni, idVozila);
			Long prethodnoVrijeme = null;

			if (redakPrethodni != null) {
				prethodnoVrijeme = Long.parseLong(redakPrethodni.split(",")[0]);
			}

			long trenutnoVrijeme = Long.parseLong(redak.split(",")[0]);
			if (prethodnoVrijeme != null) {
				long razlikaVremena = trenutnoVrijeme - prethodnoVrijeme;
				double korekcijaVremena = (trajanjeSek / 1000.0);
				double vrijemeSpavanja = korekcijaVremena * razlikaVremena;

				Thread.sleep((long) vrijemeSpavanja);
			}
			return vozilo;
		}

		public String dodajUBazu(Vozilo vozilo) {

			WebTarget resource = webTarget;
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = request.post(Entity.entity(vozilo, MediaType.APPLICATION_JSON));

			if (restOdgovor.getStatus() == 200) {
				return ("Dobro se dodalo u bazu");
			}

			else {
				return ("NIje se dobro dodalooo u baz");
			}

		}

	}

}
