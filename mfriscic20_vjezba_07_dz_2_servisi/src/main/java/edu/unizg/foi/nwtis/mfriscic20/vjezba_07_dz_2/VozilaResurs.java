package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2;

import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.unizg.foi.nwtis.konfiguracije.Konfiguracija;
import edu.unizg.foi.nwtis.konfiguracije.KonfiguracijaApstraktna;
import edu.unizg.foi.nwtis.konfiguracije.NeispravnaKonfiguracija;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Vozilo;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.VoziloDAO;
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
 * REST Web Service za praćene vožnje
 *
 * @author Martin Friščić
 */

@Path("nwtis/v1/api/vozila")
public class VozilaResurs extends SviResursi {

	private int mreznaVrata;

	private String adresaVozila;

	private VoziloDAO voziloDAO = null;

	@PostConstruct
	private void pripremiKorisnikDAO() {
		System.out.println("Pokrećem REST: " + this.getClass().getName());
		try {
			preuzmiPostavke();
			var vezaBP = this.vezaBazaPodataka.getVezaBazaPodataka();
			this.voziloDAO = new VoziloDAO(vezaBP);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Dohvaća sve praćene vožnje ili praćene vožnje u intervalu, ako je definiran
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
			return Response.status(Response.Status.OK).entity(voziloDAO.dohvatiSvaVozila().toArray()).build();
		} else {

			return Response.status(Response.Status.OK)
					.entity(voziloDAO.dohvatiPraceneVoznjeZaInterval(odVremena, doVremena).toArray()).build();
		}
	}

	/**
	 * Dodaje novu vožnju u tablicu praćenih vožnji. Također i šalje zahtjev
	 * poslužitelju
	 *
	 * @param tipOdgovora vrsta MIME odgovora
	 * @param novoVozilo  podaci novog vozila
	 * @return OK ako je praćena vožnja uspješno upisana ili INTERNAL_SERVER_ERROR
	 *         ako nije
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response posttJsonDodajVozilo(@HeaderParam("Accept") String tipOdgovora, Vozilo novoVozilo) {

		var odgovor = voziloDAO.dodajVozilo(novoVozilo);
		if (odgovor) {
			return Response.status(Response.Status.OK).build();
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Neuspješni upis vozila u bazu podataka.").build();
		}
	}

	/**
	 * Dohvaća praćene vožnje za definirano vozilo. Vraća ili listu svih praćenih
	 * vožnji ili listu praćenih vožnji u nekom intervalu ako je interval definiran.
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
			return Response.status(Response.Status.OK).entity(voziloDAO.dohvatiPraceneVoznjeVozila(id)).build();
		}

		else {
			return Response.status(Response.Status.OK)
					.entity(voziloDAO.dohvatiPraceneVoznjeZaIntervalZaVozilo(id, odVremena, doVremena)).build();
		}

	}

	/**
	 * Šalje komandu za pokretanje praćenja vozila s određenim id na poslužitelj.
	 * Vraća odgovor od poslužitelja ili INTERNAL SERVER ERROR ako je došlo do
	 * pogreške
	 */
	@Path("/vozilo/{id}/start")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })

	public Response posaljiStartKomandu(@PathParam("id") int id) {
		var komanda = new StringBuilder();
		komanda.append("VOZILO").append(" ").append("START").append(" ").append(id);

		CompletableFuture<String> rezultatObrade = CompletableFuture.supplyAsync(() -> {
			return MrezneOperacije.posaljiZahtjevPosluzitelju(this.adresaVozila, this.mreznaVrata, komanda.toString());
		});

		try {
			String odgovor = rezultatObrade.get();
			System.out.println("Odgovor: " + odgovor);
			return Response.status(Response.Status.OK).entity("{\"odgovor\":\"" + odgovor + "\"}").build();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška prilikom obrade zahtjeva")
					.build();
		}
	}

	/**
	 * Šalje komandu za prekidanje praćenja vozila s određenim id na poslužitelj.
	 * Vraća odgovor od poslužitelja ili INTERNAL SERVER ERROR ako je došlo do
	 * pogreške
	 */

	@Path("/vozilo/{id}/stop")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })

	public Response posaljiStopKomandu(@HeaderParam("Accept") String tipOdgovora, @PathParam("id") int id) {

		var komanda = new StringBuilder();
		komanda.append("VOZILO").append(" ").append("STOP").append(" ").append(id);

		CompletableFuture<String> rezultatObrade = CompletableFuture.supplyAsync(() -> {
			return MrezneOperacije.posaljiZahtjevPosluzitelju(this.adresaVozila, this.mreznaVrata, komanda.toString());
		});

		try {
			String odgovor = rezultatObrade.get();
			System.out.println("Odgovor: " + odgovor);
			return Response.status(Response.Status.OK).entity("{\"odgovor\":\"" + odgovor + "\"}").build();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greška prilikom obrade zahtjeva")
					.build();
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
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju("NWTiS_REST_V.txt");

		this.mreznaVrata = Integer.valueOf(konfig.dajPostavku("mreznaVrataVozila"));

		this.adresaVozila = String.valueOf(konfig.dajPostavku("adresaVozila"));
	}

}
