package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.kontroler;

import java.util.List;

import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentKazne;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentRadari;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentVozila;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Kazna;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Vozilo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Controller
@Path("vozila")
@RequestScoped
public class KontrolerVozila {

	@Inject
	private Models model;

	@Inject
	private BindingResult bindingResult;

	@GET
	@Path("ispisVozila")
	@View("vozila.jsp")
	public void json() {
		RestKlijentVozila k = new RestKlijentVozila();
		List<Vozilo> vozila = k.getVozilaJSON();
		model.put("vozila", vozila);
	}

	@POST
	@Path("pretrazivanjeVoznji")
	@View("vozila.jsp")
	public void json_pi(@FormParam("odVremena") long odVremena, @FormParam("doVremena") long doVremena) {
		RestKlijentVozila k = new RestKlijentVozila();
		List<Vozilo> vozila = k.getVozilaJSON_od_do(odVremena, doVremena);
		model.put("vozila", vozila);
	}

	@POST
	@Path("pretrazivanjeVoznjiPremaVozilu")
	@View("vozila.jsp")
	public void json_pi_id_vozila(@FormParam("id") String id) {
		RestKlijentVozila k = new RestKlijentVozila();
		List<Vozilo> vozila = k.getVoznjeJSON_vozilo(id);
		model.put("vozila", vozila);
	}

	@POST
	@Path("pretrazivanjeVoznjiPremaVoziluPremaVremenu")
	@View("vozila.jsp")
	public void json_pi_id_vrijeme(@FormParam("id") String id, @FormParam("odVremena") long odVremena,
			@FormParam("doVremena") long doVremena) {
		RestKlijentVozila k = new RestKlijentVozila();
		List<Vozilo> vozila = k.getVoznjeJSON_vozilo_od_do(id, odVremena, doVremena);
		model.put("vozila", vozila);
	}

	@POST
	@Path("startanjeVozilaPremaId")
	@View("ostali_odgovori.jsp")
	public void json_startanje(@FormParam("id") String id) {
		RestKlijentVozila k = new RestKlijentVozila();
		String rezultat = k.getVoziloStartajPremaId(id);

		model.put("porukaOdgovora", rezultat);
	}

	@POST
	@Path("zaustavljanjeVozilaPremaId")
	@View("ostali_odgovori.jsp")
	public void json_zaustavljanje(@FormParam("id") String id) {
		RestKlijentVozila k = new RestKlijentVozila();
		String rezultat = k.getVoziloZaustaviPremaId(id);

		model.put("porukaOdgovora", rezultat);
	}

	@POST
	@Path("dodavanjeNovogPracenja")
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
		RestKlijentVozila k = new RestKlijentVozila();
		String rezultat = k.postDodavanjeUBazu(vozilo);

		model.put("porukaOdgovora", rezultat);
	}

}
