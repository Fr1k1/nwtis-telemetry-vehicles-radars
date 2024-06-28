/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2;

import java.net.UnknownHostException;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Kazna;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.KaznaDAO;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.pomocnici.MrezneOperacije;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST Web Service uz korištenje klase Kazna
 *
 * @author Dragutin Kermek
 */
@Path("nwtis/v1/api/kazne")
public class KazneResurs extends SviResursi {
	private KaznaDAO kaznaDAO = null;

	private int mreznaVrata;
	
	private String mreznaAdresa;

	@PostConstruct
	private void pripremiKorisnikDAO() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
			var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
			this.kaznaDAO = new KaznaDAO(vezaBP);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Dohvaća sve kazne ili kazne u intervalu, ako je definiran
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param od          od vremena
	 * @param do          do vremena
	 * @return lista kazni
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getJson(@HeaderParam("Accept") String tipOdgovora, @QueryParam("od") long odVremena,
			@QueryParam("do") long doVremena) {
		if (odVremena <= 0 || doVremena <= 0) {
			return Response.status(Response.Status.OK).entity(kaznaDAO.dohvatiSveKazne().toArray()).build();
		} else {
			return Response.status(Response.Status.OK).entity(kaznaDAO.dohvatiKazne(odVremena, doVremena).toArray())
					.build();
		}
	}

	/**
	 * Dohvaća kaznu za definirani redni broj
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param rb          redni broj zapisa
	 * @return lista kazni
	 */
	@Path("{rb}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getJsonKaznaRb(@HeaderParam("Accept") String tipOdgovora, @PathParam("rb") int rb) {

		return Response.status(Response.Status.OK).entity(kaznaDAO.dohvatiKaznu(rb)).build();
	}

	/**
	 * Dohvaća kazne za definirano vozilo
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param id          vozila
	 * @return lista kazni
	 */
	@Path("/vozilo/{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getJsonKaznaVozilo(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id,
			@QueryParam("od") long odVremena, @QueryParam("do") long doVremena) {

		if (odVremena <= 0 || doVremena <= 0) {
		//	System.out.println("Neki od njih je null");
			return Response.status(Response.Status.OK).entity(kaznaDAO.dohvatiKazneVozila(id)).build();
		}

		else {
			return Response.status(Response.Status.OK).entity(kaznaDAO.dohvatiKazneVozila(id, odVremena, doVremena))
					.build();
		}

	}

	/**
	 * Provjerava stanje
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @return OK
	 */
	@HEAD
	@Produces({ MediaType.APPLICATION_JSON })
	public Response head(@HeaderParam("Accept") String tipOdgovora) {

		if (provjeriPosluzitelja()) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Neuspješni upis kazne u bazu podataka.").build();
		}
	}

	/**
	 * Dodaje novu kaznu.
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param novaKazna   podaci nove kazne
	 * @return OK ako je kazna uspješno upisana ili INTERNAL_SERVER_ERROR ako nije
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response posttJsonDodajKaznu(@HeaderParam("Accept") String tipOdgovora, Kazna novaKazna) {

		var odgovor = kaznaDAO.dodajKaznu(novaKazna);
		if (odgovor) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Neuspješni upis kazne u bazu podataka.").build();
		}
	}

	private boolean provjeriPosluzitelja() {
		var poruka = new StringBuilder();
		poruka.append("TEST").append("\n");

		var odgovor = MrezneOperacije.posaljiZahtjevPosluzitelju(this.mreznaAdresa, this.mreznaVrata, poruka.toString());

		if (odgovor != null) {
			return true;
		} else {
			return false;
		}
	}

	public void preuzmiPostavke() throws NeispravnaKonfiguracija, NumberFormatException, UnknownHostException {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju("NWTiS_REST_K.txt");

		this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataKazne"));
		
		this.mreznaAdresa=String.valueOf(konfig.dajPostavku("adresaKazne"));
	}
}
