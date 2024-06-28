package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.kontroler;

import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.model.RestKlijentRadari;
import edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Radar;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.mvc.binding.BindingResult;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Controller
@Path("radari")
@RequestScoped
public class KontrolerRadara {

	@Inject
	private Models model;

	@Inject
	private BindingResult bindingResult;

	@GET
	@Path("ispisRadara")
	@View("radari.jsp")
	public void json() {
		RestKlijentRadari k = new RestKlijentRadari();
		List<Radar> radari = k.getRadariJSON();
		model.put("radari", radari);
	}

	@POST
	@Path("pretrazivanjeRadaraPremaId")
	@View("radari.jsp")
	public void json_pi_id(@FormParam("id") String id) {
		RestKlijentRadari k = new RestKlijentRadari();
		Radar noviRadar = k.getRadarJSON_rb(id);
		List<Radar> radari = new ArrayList<Radar>();

		if (noviRadar != null) {
			radari.add(noviRadar);
		}

		model.put("radari", radari);
	}

	@GET
	@Path("resetSvihRadara")
	@View("ostali_odgovori.jsp")
	public void json_reset() {
		RestKlijentRadari k = new RestKlijentRadari();
		String rezultat = k.getRadarReset();

		model.put("porukaOdgovora", rezultat);

	}

	@POST
	@Path("provjeravanjeRadaraPremaId")
	@View("ostali_odgovori.jsp")
	public void json_provjera(@FormParam("id") String id) {
		RestKlijentRadari k = new RestKlijentRadari();
		String rezultat = k.getRadarProvjeri(id);

		model.put("porukaOdgovora", rezultat);
	}

	@DELETE
	@Path("brisanjeSvihRadara")
	@View("ostali_odgovori.jsp")
	public void json_obrisi() {
		RestKlijentRadari k = new RestKlijentRadari();
		String rezultat = k.getRadarObrisiSve();

		model.put("porukaOdgovora", rezultat);

	}

	@POST
	@Path("brisanjeRadaraId")
	@View("ostali_odgovori.jsp")
	public void json_obrisi_jednog(@FormParam("id") String id) {
		RestKlijentRadari k = new RestKlijentRadari();
		String rezultat = k.getRadarObrisiPremaId(id);

		model.put("porukaOdgovora", rezultat);

	}

}
