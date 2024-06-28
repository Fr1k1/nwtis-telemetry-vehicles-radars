package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentKazne.RestKazne;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentRadari.RestRadari;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Kazna;
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

/**
 * Klasa RestKlijentVozila.
 */
public class RestKlijentVozila {

	@Inject
	private ServletContext context;

	/**
	 * Kopntruktor klase.
	 */
	public RestKlijentVozila() {
	}

	/**
	 * Vraća sve praćene vožnje
	 *
	 * @return vozila
	 */

	public List<Vozilo> getVozilaJSON() {
		RestVozila rv = new RestVozila();
		List<Vozilo> vozila = rv.getJSON();

		return vozila;
	}

	/**
	 * Vraća praćene vožnje u intervalu od do.
	 *
	 * @param odVremena početak intervala
	 * @param doVremena kraj intervala
	 * @return vozila
	 */

	public List<Vozilo> getVozilaJSON_od_do(long odVremena, long doVremena) {
		RestVozila rk = new RestVozila();
		List<Vozilo> vozila = rk.getJSON_od_do(odVremena, doVremena);

		return vozila;
	}

	/**
	 * Vraća praćene vožnje za vozilo.
	 *
	 * @param id id vozila
	 * @return vozila
	 */

	public List<Vozilo> getVoznjeJSON_vozilo(String id) {
		RestVozila rk = new RestVozila();
		List<Vozilo> vozila = rk.getJSON_vozilo(id);
		return vozila;
	}

	/**
	 * Vraća praćene vožnje za vozilo u intervalu od do..
	 *
	 * @param id        id vozila
	 * @param odVremena početak intervala
	 * @param doVremena kraj intervala
	 * @return vozila
	 */

	public List<Vozilo> getVoznjeJSON_vozilo_od_do(String id, long odVremena, long doVremena) {
		RestVozila rk = new RestVozila();
		List<Vozilo> vozila = rk.getJSON_vozilo_od_do(id, odVremena, doVremena);

		return vozila;
	}

	/**
	 * Vraća odgovor koji nastaje pozivom metode za pokretanje praćenja vozila
	 *
	 * @param id id vozila
	 * @return odgovor
	 */

	public String getVoziloStartajPremaId(String id) {
		RestVozila rr = new RestVozila();

		String odgovorString = rr.getJSON_startanje(id);

		return odgovorString;
	}
	
	/**
	 * Vraća odgovor koji nastaje pozivom metode za zaustavljanje praćenja vozila
	 *
	 * @param id id vozila
	 * @return odgovor
	 */

	public String getVoziloZaustaviPremaId(String id) {
		RestVozila rr = new RestVozila();

		String odgovorString = rr.getJSON_zaustavljanje(id);

		return odgovorString;
	}
	
	/**
	 * Dodaje praćeno vozilo.
	 *
	 * @param vozilo vozilo
	 * @return odgovor
	 */

	public String postDodavanjeUBazu(Vozilo vozilo) {
		RestVozila rr = new RestVozila();

		String odgovorString = rr.dodajVoznjuUBazu(vozilo);

		return odgovorString;
	}
	
	/**
	 * Klasa RestVozila.
	 */

	static class RestVozila {

		/** web target. */
		private final WebTarget webTarget;

		/** client. */
		private final Client client;

		/** knstanta BASE_URI. */
		private static final String BASE_URI = "http://localhost:9080/";
		
		/**
		 * Konstruktor klase.
		 */

		public RestVozila() {
			client = ClientBuilder.newClient();
			webTarget = client.target(BASE_URI).path("nwtis/v1/api/vozila");
		}
		
		/**
		 * Vraća praćene vožnje.
		 *
		 * @return voznje
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public List<Vozilo> getJSON() throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> vozila = new ArrayList<Vozilo>();

			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pvozila = jb.fromJson(odgovor, Vozilo[].class);
				vozila.addAll(Arrays.asList(pvozila));
			}

			return vozila;
		}
		
		/**
		 * Vraća praćene vožnje u intervalu od do.
		 *
		 * @param odVremena početak intervala
		 * @param doVremena kraj intervala
		 * @return voznje
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public List<Vozilo> getJSON_od_do(long odVremena, long doVremena) throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> vozila = new ArrayList<Vozilo>();

			resource = resource.queryParam("od", odVremena);
			resource = resource.queryParam("do", doVremena);
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pvozila = jb.fromJson(odgovor, Vozilo[].class);
				vozila.addAll(Arrays.asList(pvozila));
			}

			return vozila;
		}
		
		/**
		 * Vraća praćene vožnje za vozilo.
		 *
		 * @param id id vozila
		 * @return voznje
		 * @throws ClientErrorException iznimka kod poziva klijentaon
		 */

		public List<Vozilo> getJSON_vozilo(String id) throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Vozilo> vozila = new ArrayList<Vozilo>();

			resource = resource.path(java.text.MessageFormat.format("vozilo/{0}", new Object[] { id }));
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pvozila = jb.fromJson(odgovor, Vozilo[].class);
				vozila.addAll(Arrays.asList(pvozila));
			}

			return vozila;
		}
		
		/**
		 * Vraća praćene vožnje za vozilo u intervalu od do..
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
		 * Vraća odgovor koji nastaje pozivom metode za pokretanje praćenja vozila
		 *
		 * @param id id vozila
		 * @return odgovor
		 */

		public String getJSON_startanje(String id) throws ClientErrorException {
			WebTarget resource = webTarget.path("vozilo").path(String.valueOf(id)).path("start");
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor != null) {
				return restOdgovor.readEntity(String.class);

			}
			return "Greska startanja vozila";
		}
		
		/**
		 * Vraća odgovor koji nastaje pozivom metode za zaustavljanje praćenja vozila
		 *
		 * @param id id vozila
		 * @return odgovor
		 */

		public String getJSON_zaustavljanje(String id) throws ClientErrorException {
			WebTarget resource = webTarget.path("vozilo").path(String.valueOf(id)).path("stop");
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor != null) {
				return restOdgovor.readEntity(String.class);

			}
			return "Greska zaustavljanja vozila";
		}

		public String dodajVoznjuUBazu(Vozilo vozilo) {
			WebTarget resource = webTarget;
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = request.post(Entity.entity(vozilo, MediaType.APPLICATION_JSON));

			if (restOdgovor.getStatus() == 200) {
				return "Uspjesno se dodalo vozilo";

			}

			else {
				return "Nije se uspjesno dodalo";
			}

		}

	}

}
