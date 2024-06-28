package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2;

import java.net.UnknownHostException;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.SimulacijeDAO;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Vozilo;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST Web Service za simulacije
 *
 * @author Martin Friščić
 */
@Path("nwtis/v1/api/simulacije")
public class SimulacijeResurs extends SviResursi {

	private int mreznaVrata;
	
	private String mreznaAdresa;

	private SimulacijeDAO simulacijeDAO = null;

	// dal tu trebaju mrezna vrata

	@PostConstruct
	private void pripremiKorisnikDAO() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
			var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
			this.simulacijeDAO = new SimulacijeDAO(vezaBP);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// @POST
	// @Produces({ MediaType.APPLICATION_JSON })
	// public Response posttJsonDodajSimulacijuVozila(@HeaderParam("Accept") String
	// tipOdgovora, Vozilo novoVozilo) {

	// var odgovor = simulacijeDAO.dodajNovuVoznju(novoVozilo);
	// if (odgovor) {
	// return Response.status(Response.Status.OK).build();
	// } else {
	// return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	// .entity("Neuspješni upis simulacije vozila u bazu podataka.").build();
	// }
	// }

	/**
	 * Dohvaća sve vožnje ili vožnje u intervalu, ako je definiran
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param od          od vremena
	 * @param do          do vremena
	 * @return lista vožnji
	 */

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getJson(@HeaderParam("Accept") String tipOdgovora, @QueryParam("od") long odVremena,
			@QueryParam("do") long doVremena) {
		if (odVremena <= 0 || doVremena <= 0) {
			return Response.status(Response.Status.OK).entity(simulacijeDAO.dohvatiSveVoznje().toArray()).build();
		} else {

			return Response.status(Response.Status.OK)
					.entity(simulacijeDAO.dohvatiVoznjeZaInterval(odVremena, doVremena).toArray()).build();
		}
	}

	/**
	 * Vraća sadržaj komande koju je potrebno poslati serveru
	 * 
	 * @param podaciVozila U ovaj objekt spremaju se podaci koje je potrebno poslati
	 *                     na server
	 */
	private String vratiSadrzajKomandeZaSlanje(Vozilo podaciVozila) {

		var komanda = new StringBuilder();
		komanda.append("VOZILO").append(" ").append(podaciVozila.getId()).append(" ").append(podaciVozila.getBroj())
				.append(" ").append(podaciVozila.getVrijeme()).append(" ").append(podaciVozila.getBrzina()).append(" ")
				.append(podaciVozila.getSnaga()).append(" ").append(podaciVozila.getStruja()).append(" ")
				.append(podaciVozila.getVisina()).append(" ").append(podaciVozila.getGpsBrzina()).append(" ")
				.append(podaciVozila.getTempVozila()).append(" ").append(podaciVozila.getPostotakBaterija()).append(" ")
				.append(podaciVozila.getNaponBaterija()).append(" ").append(podaciVozila.getKapacitetBaterija())
				.append(" ").append(podaciVozila.getTempBaterija()).append(" ").append(podaciVozila.getPreostaloKm())
				.append(" ").append(podaciVozila.getUkupnoKm()).append(" ").append(podaciVozila.getGpsSirina())
				.append(" ").append(podaciVozila.getGpsDuzina());

		return komanda.toString();

	}

	/**
	 * Dodaje novu vožnju u tablicu simulacija. Također i šalje zahtjev poslužitelju
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param novoVozilo  podaci novog vozila
	 * @return OK ako je simulacija uspješno upisana ili INTERNAL_SERVER_ERROR ako
	 *         nije
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response posttJsonDodajVoziloPomocuSimulacije(@HeaderParam("Accept") String tipOdgovora, Vozilo novoVozilo) {

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata,
				vratiSadrzajKomandeZaSlanje(novoVozilo));

		var odgovorBaze = simulacijeDAO.dodajNovuVoznju(novoVozilo);
		if (odgovorBaze) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Neuspješni upis vozila novo u bazu podataka.").build();
		}
	}

	/**
	 * Dohvaća vožnje za definirano vozilo. Vraća ili listu svih vožnji ili listu
	 * vožnji u nekom intervalu ako je interval definiran.
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param id          vozila
	 * @return lista vožnji
	 */
	@Path("/vozilo/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })

	public Response getJsonVoziloId(@HeaderParam("Accept") String tipOdgovora, @QueryParam("od") long odVremena,
			@QueryParam("do") long doVremena, @PathParam("id") int id) {
		if (odVremena <= 0 || doVremena <= 0) {
			return Response.status(Response.Status.OK).entity(simulacijeDAO.dohvatiVoznjeZaVozilo(id)).build();
		}

		else {
			return Response.status(Response.Status.OK)
					.entity(simulacijeDAO.dohvatiVoznjeZaIntervalZaVozilo(id, odVremena, doVremena)).build();
		}

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
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju("NWTiS_REST_S.txt");

		this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));
		
		this.mreznaAdresa = String.valueOf(konfig.dajPostavku("adresaVozila"));
	}

}
