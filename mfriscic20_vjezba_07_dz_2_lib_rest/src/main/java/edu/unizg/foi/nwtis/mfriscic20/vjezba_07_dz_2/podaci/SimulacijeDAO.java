package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa SimulacijeDAO za rad s tablicom Kazne
 *
 */
public class SimulacijeDAO {
	/** Veza na bazu podataka. */
	private Connection vezaBP;

	/**
	 * Instancira novi simulacije DAO.
	 *
	 * @param vezaBP veza na bazu podataka
	 */

	public SimulacijeDAO(Connection vezaBP) {
		super();
		this.vezaBP = vezaBP;
	}

	/**
	 * Dohvati sve voznje.
	 *
	 * @return lista voznji
	 */

	public List<Vozilo> dohvatiSveVoznje() {
		String upit = "SELECT id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina "
				+ "FROM voznje";

		List<Vozilo> vozila = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektSimulacijeVoznje(rs);
				vozila.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(VoziloDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return vozila;
	}

	/**
	 * Dodaje nov zapis voznje.
	 *
	 * @param vozilo vozilo
	 * @return true, ako je uspješno dodavanje
	 */
	public boolean dodajNovuVoznju(Vozilo vozilo) {
		String upit = "INSERT INTO voznje (id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina ) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {

			s.setInt(1, vozilo.getId());
			s.setInt(2, vozilo.getBroj());
			s.setLong(3, vozilo.getVrijeme());
			s.setDouble(4, vozilo.getBrzina());
			s.setDouble(5, vozilo.getSnaga());
			s.setDouble(6, vozilo.getStruja());
			s.setDouble(7, vozilo.getVisina());
			s.setDouble(8, vozilo.getGpsBrzina());
			s.setInt(9, vozilo.getTempVozila());
			s.setInt(10, vozilo.getPostotakBaterija());
			s.setDouble(11, vozilo.getNaponBaterija());
			s.setInt(12, vozilo.getKapacitetBaterija());
			s.setInt(13, vozilo.getTempBaterija());
			s.setDouble(14, vozilo.getPreostaloKm());
			s.setDouble(15, vozilo.getUkupnoKm());
			s.setDouble(16, vozilo.getGpsSirina());
			s.setDouble(17, vozilo.getGpsDuzina());

			int brojAzuriranja = s.executeUpdate();

			return brojAzuriranja == 1;

		} catch (Exception ex) {
			Logger.getLogger(SimulacijeDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	/**
	 * Dohvati zapise svih voznji u intervalu.
	 *
	 * @param odVremena početak intervala
	 * @param doVremena kraj intervala
	 * @return lista pracenih voznji
	 */
	public List<Vozilo> dohvatiVoznjeZaInterval(long odVremena, long doVremena) {
		String upit = "SELECT id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina  "
				+ "FROM voznje WHERE vrijeme >= ? AND vrijeme <= ?";

		List<Vozilo> vozila = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setLong(1, odVremena);
			s.setLong(2, doVremena);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektSimulacijeVoznje(rs);
				vozila.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(SimulacijeDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return vozila;
	}

	/**
	 * Dohvati voznje za neko vozilo.
	 *
	 * @param id id vozila
	 * @return lista voznji
	 */
	public List<Vozilo> dohvatiVoznjeZaVozilo(int id) {
		String upit = "SELECT id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina "
				+ "FROM voznje WHERE id = ?";

		List<Vozilo> vozila = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, id);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektSimulacijeVoznje(rs);
				vozila.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(SimulacijeDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return vozila;
	}

	/**
	 * Dohvati voznje vozila u nekom intervalu.
	 *
	 * @param id        jedinstvena oznaka vozila
	 * @param odVremena početak intervala
	 * @param doVremena kraj intervala
	 * @return lista voznji u intervalu
	 */
	public List<Vozilo> dohvatiVoznjeZaIntervalZaVozilo(int id, long odVremena, long doVremena) {
		String upit = "SELECT id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila, postotakBaterija, naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina  "
				+ "FROM voznje WHERE id= ? AND vrijeme >= ? AND vrijeme <= ?";

		List<Vozilo> vozila = new ArrayList<>();

		try (PreparedStatement s = this.vezaBP.prepareStatement(upit)) {
			s.setInt(1, id);
			s.setLong(2, odVremena);
			s.setLong(3, doVremena);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				var vozilo = kreirajObjektSimulacijeVoznje(rs);
				vozila.add(vozilo);
			}
		} catch (SQLException ex) {
			Logger.getLogger(SimulacijeDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return vozila;
	}

	/**
	 * Kreiraj objekt vozilo koji sluzi za zapis simulacije.
	 *
	 * @param rs skup rezultata SQL upisa
	 * @return vozilo
	 * @throws SQLException SQL iznimka
	 */
	private Vozilo kreirajObjektSimulacijeVoznje(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int broj = rs.getInt("broj");
		long vrijeme = rs.getLong("vrijeme");
		double brzina = rs.getDouble("brzina");
		double snaga = rs.getDouble("snaga");
		double struja = rs.getDouble("struja");
		double visina = rs.getDouble("visina");
		double gpsBrzina = rs.getDouble("gpsBrzina");
		int tempVozila = rs.getInt("tempVozila");
		int postotakBaterija = rs.getInt("postotakBaterija");
		double naponBaterija = rs.getDouble("naponBaterija");
		int kapacitetBaterija = rs.getInt("kapacitetBaterija");
		int tempBaterija = rs.getInt("tempBaterija");
		double preostaloKm = rs.getDouble("preostaloKm");
		double ukupnoKm = rs.getDouble("ukupnoKm");
		double gpsSirina = rs.getDouble("gpsSirina");
		double gpsDuzina = rs.getDouble("gpsDuzina");

		Vozilo v = new Vozilo(id, broj, vrijeme, brzina, snaga, struja, visina, gpsBrzina, tempVozila, postotakBaterija,
				naponBaterija, kapacitetBaterija, tempBaterija, preostaloKm, ukupnoKm, gpsSirina, gpsDuzina);
		return v;
	}

}
