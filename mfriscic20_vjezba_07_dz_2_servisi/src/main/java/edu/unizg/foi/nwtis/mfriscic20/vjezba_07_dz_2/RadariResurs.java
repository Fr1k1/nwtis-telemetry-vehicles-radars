package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.KaznaDAO;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST Web Service za radare
 *
 * @author Martin Friščić
 */

//on ne otvara vezu na bazu jer ne komunicira s njom

@Path("nwtis/v1/api/radari")
public class RadariResurs extends SviResursi {

	private int mreznaVrata;

	private String mreznaAdresa;

	@PostConstruct
	private void pripremiDatoteku() {
		try {
			this.preuzmiPostavke();
		} catch (NumberFormatException e) {

			e.printStackTrace();
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (NeispravnaKonfiguracija e) {

			e.printStackTrace();
		}
	}

	// jos port iz datoteke treba

	/**
	 * Dohvaća sve radare na način da šalje zahtjev i onda odgovor koji dobije
	 * pretvara u JSON oblik
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * 
	 * @return lista radara
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getSviRadari(@HeaderParam("Accept") String tipOdgovora) {

		StringBuilder komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append("SVI");

		String komandaZaSlanjeString = komanda.toString();

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata,
				komandaZaSlanjeString);

		String podaciRadara = odgovor.substring(4);

		podaciRadara = podaciRadara.trim();

		Pattern pattern = Pattern.compile("\\[([^\\[\\]]+)\\]");
		Matcher matcher = pattern.matcher(podaciRadara);

		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("[");

		boolean prvi = true;
		while (matcher.find()) {
			if (!prvi) {
				jsonBuilder.append(",");
			} else {
				prvi = false;
			}
			String radarEntry = matcher.group(1);
			String[] radarInfo = radarEntry.split("\\s+");

		//	System.out.println("Radar info je" + radarInfo);

			jsonBuilder.append("{");
			jsonBuilder.append("\"id\":").append(radarInfo[0]).append(",");
			jsonBuilder.append("\"adresaRadara\":\"").append(radarInfo[1]).append("\",");
			jsonBuilder.append("\"mreznaVrataRadara\":").append(radarInfo[2]).append(",");
			jsonBuilder.append("\"gpsSirina\":").append(radarInfo[3]).append(",");
			jsonBuilder.append("\"gpsDuzina\":").append(radarInfo[4]).append(",");
			jsonBuilder.append("\"maksUdaljenost\":").append(radarInfo[5]);
			jsonBuilder.append("}");
		}

		jsonBuilder.append("]");

		return Response.status(Response.Status.OK).entity(jsonBuilder.toString()).build();
	}

	@GET
	@Path("reset")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response resetSviRadari(@HeaderParam("Accept") String tipOdgovora) {

		StringBuilder komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append("RESET");

		String komandaZaSlanjeString = komanda.toString();

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata,
				komandaZaSlanjeString);

		return Response.status(Response.Status.OK).entity("{\"odgovor\":\"" + odgovor + "\"}").build();
	}

	/**
	 * Dohvaća radar za definirani identifikacijski broj. Funkcionira na način da
	 * pošalje zahtjev da se dobe svi radari te se onda iz njih može dobiti željeni
	 * radar
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param id          identifikacijska oznaka radara
	 * @return radar za određeni id
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response vratiPodatkeRadaraSId(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {

		StringBuilder komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append("SVI");

		String komandaZaSlanjeString = komanda.toString();

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata,
				komandaZaSlanjeString);

		if (odgovor.contains("OK")) {

			String podaciRadara = odgovor.substring(4);

			podaciRadara = podaciRadara.trim();

			Pattern regularniIzraz = Pattern.compile("\\[([^\\[\\]]+)\\]");
			Matcher poklapanje = regularniIzraz.matcher(podaciRadara);

			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("[");

			boolean pronaden = false;
			while (poklapanje.find()) {
				String radarPodatak = poklapanje.group(1);
				String[] radarInfo = radarPodatak.split("\\s+");

				if (Integer.parseInt(radarInfo[0]) == id) {
					pronaden = true;
					jsonBuilder.append("{");
					jsonBuilder.append("\"id\":").append(radarInfo[0]).append(",");
					jsonBuilder.append("\"adresaRadara\":\"").append(radarInfo[1]).append("\",");
					jsonBuilder.append("\"mreznaVrataRadara\":").append(radarInfo[2]).append(",");
					jsonBuilder.append("\"gpsSirina\":").append(radarInfo[3]).append(",");
					jsonBuilder.append("\"gpsDuzina\":").append(radarInfo[4]).append(",");
					jsonBuilder.append("\"maksUdaljenost\":").append(radarInfo[5]);
					jsonBuilder.append("}");
					break;
				}
			}

			jsonBuilder.append("]");

			return Response.status(Response.Status.OK).entity(jsonBuilder.toString()).build();

//			if (!pronaden) {
//				jsonBuilder = new StringBuilder("[]");
//			}

		}

		return Response.status(Response.Status.OK).entity(odgovor).build();
	}

	/**
	 * Provjerava radar s poslanim identifikacijskim brojem.
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param id          identifikacijska oznaka radara
	 * @return odgovor koji dobije od poslužitelja u JSON obliku
	 */
	@GET
	@Path("{id}/provjeri")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response provjeraRadaraSId(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {

		StringBuilder komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append(id);

		String komandaZaSlanjeString = komanda.toString();

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata,
				komandaZaSlanjeString);
		return Response.status(Response.Status.OK).entity("{\"odgovor\":\"" + odgovor + "\"}").build();
	}

	/**
	 * Briše sve radare iz kolekcije radara
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @return odgovor koji dobije od poslužitelja u obliku JSON
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response obrisiSveRadare(@HeaderParam("Accept") String tipOdgovora) {

		StringBuilder komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append("OBRIŠI").append(" ").append("SVE");

		String komandaZaSlanjeString = komanda.toString();

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata, komandaZaSlanjeString);
		return Response.status(Response.Status.OK).entity("{\"odgovor\":\"" + odgovor + "\"}").build();
	}

	/**
	 * Briše radar ovisno o vrijednosti parametra id
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param id          identifikacijska oznaka radara
	 * @return odgovor koji dobije od poslužitelja u obliku JSON
	 */
	@DELETE
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response brisanjeRadaraSId(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {

		StringBuilder komanda = new StringBuilder();
		komanda.append("RADAR").append(" ").append("OBRIŠI").append(" ").append(id);

		String komandaZaSlanjeString = komanda.toString();

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata, komandaZaSlanjeString);
		return Response.status(Response.Status.OK).entity("{\"odgovor\":\"" + odgovor + "\"}").build();
	}

	/**
	 * Preuzimaju se postavke iz konfiguracijske datoteke jer su mrežna vrata
	 * potrebna za komunikaciju.
	 * 
	 * @throws NeispravnaKonfiguracija Baca iznimku ako dođe do problema s datotekom
	 * @throws NumberFormatException   Baca iznimku ako dođe do problema brojevnog
	 *                                 tipa
	 * @throws UnknownHostException    Baca iznimku ako postoje problemi sa
	 *                                 povezivanjem
	 * 
	 * 
	 */
	public void preuzmiPostavke() throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju("NWTiS_REST_R.txt");

		this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataRegistracije"));

		this.mreznaAdresa = String.valueOf(konfig.dajPostavku("adresaRegistracije"));
	}

}
