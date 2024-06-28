package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.kontroler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentSimulacije;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentVozila;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Vozilo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.spi.Context;
import jakarta.faces.render.RenderKit;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Controller
@Path("simulacije")
@RequestScoped
public class KontrolerSimulacija {

	@Inject
	private Models model;

	@Inject
	private BindingResult bindingResult;

	@Inject
	private ServletContext kontekst;

	@GET
	@Path("ispisSimulacija")
	@View("simulacije.jsp")
	public void json() {
		RestKlijentSimulacije k = new RestKlijentSimulacije();
		List<Vozilo> simulacije = k.getSimulacijeJSON();
		model.put("simulacije", simulacije);
	}

	@POST
	@Path("pretrazivanjeSimulacija")
	@View("simulacije.jsp")
	public void json_pi(@FormParam("odVremena") long odVremena, @FormParam("doVremena") long doVremena) {
		RestKlijentSimulacije k = new RestKlijentSimulacije();
		List<Vozilo> simulacije = k.getSimulacijeJSON_od_do(odVremena, doVremena);
		model.put("simulacije", simulacije);
	}

	@POST
	@Path("pretrazivanjeSimulacijaPremaVozilu")
	@View("simulacije.jsp")
	public void json_pi_id_vozila(@FormParam("id") String id) {
		RestKlijentSimulacije k = new RestKlijentSimulacije();
		List<Vozilo> simulacije = k.getSimulacijeJSON_vozilo(id);
		model.put("simulacije", simulacije);
	}

	@POST
	@Path("pretrazivanjeSimulacijaPremaVoziluPremaVremenu")
	@View("simulacije.jsp")
	public void json_pi_id_vrijeme(@FormParam("id") String id, @FormParam("odVremena") long odVremena,
			@FormParam("doVremena") long doVremena) {
		RestKlijentSimulacije k = new RestKlijentSimulacije();
		List<Vozilo> simulacije = k.getSimulacijeJSON_vozilo_od_do(id, odVremena, doVremena);
		model.put("simulacije", simulacije);
	}

	@POST
	@Path("dodavanjeNovogVozila")
	@View("ostali_odgovori.jsp")
	public void json_dodavanje(@FormParam("id") int id, @FormParam("broj") int broj, @FormParam("vrijeme") long vrijeme,
			@FormParam("brzina") double brzina, @FormParam("snaga") double snaga, @FormParam("struja") double struja,
			@FormParam("visina") double visina, @FormParam("gpsBrzina") double gpsBrzina,
			@FormParam("tempVozila") int tempVozila, @FormParam("postotakBaterija") int postotakBaterija,
			@FormParam("naponBaterija") double naponBaterija, @FormParam("kapacitetBaterija") int kapacitetBaterija,
			@FormParam("tempBaterija") int tempBaterija, @FormParam("preostaloKm") double preostaloKm,
			@FormParam("ukupnoKm") double ukupnoKm, @FormParam("gpsSirina") double gpsSirina,
			@FormParam("gpsDuzina") double gpsDuzina) {

		Vozilo vozilo = new Vozilo(id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila,
				postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina,
				gpsDuzina);
		RestKlijentSimulacije k = new RestKlijentSimulacije();
		 String rezultat = k.postDodavanjeUBazu(vozilo);

		 model.put("porukaOdgovora", rezultat);
	}

	@POST
	@Path("pokretanjeSimulacije")

	@View("ostali_odgovori.jsp")
	public void json_pokretanje_simulacije(@FormParam("imeDatoteke") String imeDatoteke,
			@FormParam("idVozila") int idVozila, @FormParam("trajanjeSek") int trajanjeSek,
			@FormParam("vrijemePauza") int vrijemePauza) {

		RestKlijentSimulacije k = new RestKlijentSimulacije();
		kontekst.setAttribute("datoteka", imeDatoteke);

		String naziv = "";

		if (imeDatoteke.equals("NWTiS_DZ1_V1.csv")) {
			naziv = kontekst.getInitParameter("datoteka1");
		}
		if (imeDatoteke.equals("NWTiS_DZ1_V2.csv")) {
			naziv = kontekst.getInitParameter("datoteka2");
		}

		if (imeDatoteke.equals("NWTiS_DZ1_V3.csv")) {
			naziv = kontekst.getInitParameter("datoteka3");
		}

		StringBuilder napravi_putanju = new StringBuilder();

		napravi_putanju.append(kontekst.getRealPath("/WEB-INF")).append(java.io.File.separator);

		String putanjaString = napravi_putanju.toString() + naziv;

		try (BufferedReader citac = new BufferedReader(new FileReader(putanjaString))) {
			String prviRedakTablice = citac.readLine();
			Long prethodnoVrijeme = null;
			String prethodniRedak = null;
			int brojRetka;
			brojRetka = 0;
			String redak;
			RestKlijentSimulacije klijentSimulacije = new RestKlijentSimulacije();

			while ((redak = citac.readLine()) != null) {

				Vozilo vozilo = klijentSimulacije.postKreiranjeVozila(redak, brojRetka, prethodniRedak, idVozila);
				klijentSimulacije.postDodavanjeUBazu(vozilo);

				prethodniRedak = redak;
				brojRetka = brojRetka + 1;

			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}

		model.put("porukaOdgovora", "Simulacija izvršena. Kazne su u bazi.");
	}

}
