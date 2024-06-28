package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentKazne.RestKazne;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Kazna;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Radar;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestKlijentRadari.
 */
public class RestKlijentRadari {

	/**
	 * Kopntruktor klase.
	 */
	public RestKlijentRadari() {
	}

	/**
	 * Vraća sve radare
	 *
	 * @return radari
	 */
	public List<Radar> getRadariJSON() {
		RestRadari rv = new RestRadari();
		List<Radar> radari = rv.getJSON();

		return radari;
	}

	/**
	 * Vraća radar.
	 *
	 * @param rb redni broj radara
	 * @return radar
	 */

	public Radar getRadarJSON_rb(String rb) {
		RestRadari rr = new RestRadari();
		Radar r = rr.getJSON_rb(rb);
		return r;
	}

	/**
	 * Vraća odgovor koji dobije kod komande reset
	 */

	public String getRadarReset() {
		RestRadari rr = new RestRadari();

		String odgovorString = rr.getJSON_reset();

		return odgovorString;
	}

	/**
	 * Vraća odgovor koji dobije kod komande za provjeru radara
	 * 
	 * @return odgovor
	 */

	public String getRadarProvjeri(String id) {

		RestRadari rr = new RestRadari();

		String odgovorString = rr.getJSON_provjera(id);

		return odgovorString;

	}

	/**
	 * Vraća odgovor koji dobije kod komande za brisanje svih radara
	 * 
	 * @return odgovor
	 */

	public String getRadarObrisiSve() {
		RestRadari rr = new RestRadari();

		String odgovorString = rr.getJSON_brisanje_svih();

		return odgovorString;
	}

	/**
	 * Vraća odgovor koji dobije kod komande za brisanje radara prema id
	 * 
	 * @param id redni broj radara
	 * @return odgovor
	 */

	public String getRadarObrisiPremaId(String id) {
		RestRadari rr = new RestRadari();

		String odgovorString = rr.getJSON_brisanje_prema_id(id);

		return odgovorString;
	}

	/**
	 * Klasa RestRadari.
	 */

	static class RestRadari {

		/** web target. */
		private final WebTarget webTarget;

		/** client. */
		private final Client client;

		/** knstanta BASE_URI. */
		private static final String BASE_URI = "http://localhost:9080/";

		/**
		 * Konstruktor klase.
		 */
		public RestRadari() {
			client = ClientBuilder.newClient();
			webTarget = client.target(BASE_URI).path("nwtis/v1/api/radari");
		}

		/**
		 * Vraća radare.
		 *
		 * @return radari
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public List<Radar> getJSON() throws ClientErrorException {
			WebTarget resource = webTarget;
			List<Radar> radari = new ArrayList<Radar>();

			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var pradari = jb.fromJson(odgovor, Radar[].class);
				radari.addAll(Arrays.asList(pradari));
			}

			return radari;
		}

		/**
		 * Vraća radar.
		 *
		 * @param rb redni broj radara
		 * @return radar
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public Radar getJSON_rb(String rb) throws ClientErrorException {
			WebTarget resource = webTarget;
			resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] { rb }));
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor.getStatus() == 200) {
				String odgovor = restOdgovor.readEntity(String.class);
				var jb = JsonbBuilder.create();
				var radar = jb.fromJson(odgovor, Radar.class);
				return radar;
			}

			return null;
		}

		/**
		 * Vraća odgovor.
		 *
		 * @return odgovor
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public String getJSON_reset() throws ClientErrorException {
			WebTarget resource = webTarget.path("reset");
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor != null) {
				return restOdgovor.readEntity(String.class);

			}

			return "Greska resetiranja";
		}

		/**
		 * Vraća odgovor.
		 *
		 * @return odgovor
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public String getJSON_provjera(String id) throws ClientErrorException {
			WebTarget resource = webTarget.path(String.valueOf(id)).path("provjeri");
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().get();
			if (restOdgovor != null) {
				return restOdgovor.readEntity(String.class);

			}
			return "Greska provjere radara";
		}

		/**
		 * Vraća odgovor.
		 *
		 * @return odgovor
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public String getJSON_brisanje_svih() throws ClientErrorException {
			WebTarget resource = webTarget;
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().delete();
			if (restOdgovor != null) {
				return restOdgovor.readEntity(String.class);

			}

			return "Greska brisanja svih radara";
		}

		/**
		 * Vraća odgovor.
		 *
		 * @return odgovor
		 * @throws ClientErrorException iznimka kod poziva klijenta
		 */

		public String getJSON_brisanje_prema_id(String id) throws ClientErrorException {
			WebTarget resource = webTarget.path(String.valueOf(id));
			Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
			Response restOdgovor = resource.request().delete();
			if (restOdgovor != null) {
				return restOdgovor.readEntity(String.class);

			}

			return "Greska brisanja radara";
		}

	}

}
