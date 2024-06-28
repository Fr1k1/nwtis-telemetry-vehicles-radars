<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>REST MVC - Početna stranica</title>
</head>
<body>
	<h1>REST MVC - Početna stranica</h1>
	<ul>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kazne/pocetak">Početna
				stranica</a></li>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kazne/ispisKazni">Ispis
				svih kazni</a></li>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/radari/ispisRadara">Ispis
				svih radara</a></li>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/vozila/ispisVozila">Ispis
				svih vozila</a></li>

		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/simulacije/ispisSimulacija">Ispis
				simulacija</a></li>

		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/radari/resetSvihRadara">Resetiranje
				svih radara</a></li>
		<li>

			<h2>Endpointovi za kazne</h2>

			<h2>Pretraživanje kazni u intervalu</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazni">
				<table>
					<tr>
						<td>Od vremena:</td>
						<td><input name="odVremena" /> <input type="hidden"
							name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
					</tr>
					<tr>
						<td>Do vremena:</td>
						<td><input name="doVremena" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati kazne "></td>
					</tr>
				</table>
			</form>
		</li>


		<li>
			<h2>Pretraživanje kazni prema rednom broju</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniPremaId">
				<table>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati kaznu "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<h2>Pretraživanje kazni za neko vozilo</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniPremaVozilu">
				<table>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati kaznu za vozilo "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<h2>Pretraživanje kazni za neko vozilo u nekom vremenu</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/kazne/pretrazivanjeKazniPremaVoziluPremaVremenu">
				<table>

					<tr>
						<td>Od vremena:</td>
						<td><input name="odVremena" /> <input type="hidden"
							name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
					</tr>
					<tr>
						<td>Do vremena:</td>
						<td><input name="doVremena" />
					</tr>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit"
							value=" Dohvati kaznu za vozilo prema vremenu "></td>
					</tr>
				</table>
			</form>
		</li>




		<li>
			<h2>Endpointovi za radare</h2>
			<h2>Pretraživanje radara prema id</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/radari/pretrazivanjeRadaraPremaId">
				<table>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati radar "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<h2>Provjera radara prema id</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/radari/provjeravanjeRadaraPremaId">
				<table>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Provjeri radar "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<!-- linkovi mogu triggerati samo get metode -->
			<h2>Brisanje svih radara</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/radari/brisanjeSvihRadara">
				<input type="hidden" name="_method" value="DELETE">
				<button type="submit">Brisanje svih radara</button>
			</form>

		</li>



		<li>
			<!-- linkovi mogu triggerati samo get metode -->

			<h2>Brisanje određenog radara</h2>

			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/radari/brisanjeRadaraId">
				<input type="hidden" name="_method">

				<table>
					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>


				</table>
				<input type="submit" value="Brisanje radara" />
			</form>


		</li>

		<li>

			<h2>Endpointovi za praćene vožnje</h2>
		</li>
		<li>
			<h2>Pretraživanje vožnji u intervalu</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/vozila/pretrazivanjeVoznji">
				<table>
					<tr>
						<td>Od vremena:</td>
						<td><input name="odVremena" /> <input type="hidden"
							name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
					</tr>
					<tr>
						<td>Do vremena:</td>
						<td><input name="doVremena" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati voznje u intervalu "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<h2>Pretraživanje praćenih vožnji prema id vozila</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/vozila/pretrazivanjeVoznjiPremaVozilu">
				<table>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati vožnje za vozilo "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<h2>Pretraživanje vožnji prema id u intervalu</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/vozila/pretrazivanjeVoznjiPremaVoziluPremaVremenu">
				<table>
					<tr>
						<td>Od vremena:</td>
						<td><input name="odVremena" /> <input type="hidden"
							name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
					</tr>
					<tr>
						<td>Do vremena:</td>
						<td><input name="doVremena" />
					</tr>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit"
							value=" Dohvati voznje u intervalu prema vozilu "></td>
					</tr>
				</table>
			</form>
		</li>



		<li>
			<h2>Pokretanje pracenja vozila</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/vozila/startanjeVozilaPremaId">
				<table>


					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Pokreni pracenje vozila "></td>
					</tr>
				</table>
			</form>
		</li>


		<li>
			<h2>Zaustavljanje pracenja vozila</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/vozila/zaustavljanjeVozilaPremaId">
				<table>


					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Zaustavi pracenje vozila "></td>
					</tr>
				</table>
			</form>
		</li>


		<li>
			<h2>Dodavanje novog praćenja vožnje</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/vozila/dodavanjeNovogPracenja">

				<table>
					<tr>
						<td>ID:</td>
						<td><input type="number" id="id" name="id"></td>
					</tr>
					<tr>
						<td>Broj:</td>
						<td><input type="number" id="broj" name="broj"></td>
					</tr>
					<tr>
						<td>Vrijeme:</td>
						<td><input type="number" id="vrijeme" name="vrijeme"></td>
					</tr>
					<tr>
						<td>Brzina:</td>
						<td><input id="brzina" name="brzina"></td>
					</tr>
					<tr>
						<td>Snaga:</td>
						<td><input id="snaga" name="snaga"></td>
					</tr>
					<tr>
						<td>Struja:</td>
						<td><input id="struja" name="struja"></td>
					</tr>
					<tr>
						<td>Visina:</td>
						<td><input id="visina" name="visina"></td>
					</tr>
					<tr>
						<td>GPS Brzina:</td>
						<td><input id="gpsBrzina" name="gpsBrzina"></td>
					</tr>
					<tr>
						<td>Temperatura Vozila:</td>
						<td><input type="number" id="tempVozila" name="tempVozila"></td>
					</tr>
					<tr>
						<td>Postotak Baterija:</td>
						<td><input type="number" id="postotakBaterija"
							name="postotakBaterija"></td>
					</tr>
					<tr>
						<td>Napon Baterija:</td>
						<td><input id="naponBaterija" name="naponBaterija"></td>
					</tr>
					<tr>
						<td>Kapacitet Baterija:</td>
						<td><input id="kapacitetBaterija" name="kapacitetBaterija"></td>
					</tr>
					<tr>
						<td>Temperatura Baterija:</td>
						<td><input type="number" id="tempBaterija"
							name="tempBaterija"></td>
					</tr>
					<tr>
						<td>Preostalo Km:</td>
						<td><input id="preostaloKm" name="preostaloKm"></td>
					</tr>
					<tr>
						<td>Ukupno Km:</td>
						<td><input id="ukupnoKm" name="ukupnoKm"></td>
					</tr>
					<tr>
						<td>GPS Širina:</td>
						<td><input id="gpsSirina" name="gpsSirina"></td>
					</tr>
					<tr>
						<td>GPS Dužina:</td>
						<td><input id="gpsDuzina" name="gpsDuzina"></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value="Dodaj novo praćenje vozila"></td>
					</tr>
				</table>

			</form>
		</li>

	</ul>

	<h2>Endpointovi za simulacije</h2>

	<ul>


		<li>
			<h2>Pokretanje simulacije</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/simulacije/pokretanjeSimulacije">

				<table>
					<tr>
						<td>Naziv datoteke npr. NWTiS_DZ1_V1.csv :</td>
						<td><input id="imeDatoteke" name="imeDatoteke"></td>
					</tr>
					<tr>
						<td>Id vozila:</td>
						<td><input type="number" id="idVozila" name="idVozila"></td>
					</tr>
					<tr>
						<td>Trajanje sekunde:</td>
						<td><input type="number" id="trajanjeSek" name="trajanjeSek"></td>
					</tr>
					<tr>
						<td>Trajanje pauze:</td>
						<td><input id="vrijemePauza" name="vrijemePauza"></td>
					</tr>

					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value="Pokreni simulaciju"></td>
					</tr>
				</table>

			</form>
		</li>

		<li>

			<h2>Pretraživanje simulacija u intervalu</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/simulacije/pretrazivanjeSimulacija">
				<table>
					<tr>
						<td>Od vremena:</td>
						<td><input name="odVremena" /> <input type="hidden"
							name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
					</tr>
					<tr>
						<td>Do vremena:</td>
						<td><input name="doVremena" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value=" Dohvati simulacije "></td>
					</tr>
				</table>
			</form>
		</li>


		<li>
			<h2>Pretraživanje simulacija prema id vozila</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/simulacije/pretrazivanjeSimulacijaPremaVozilu">
				<table>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit"
							value=" Dohvati simulacije za vozilo "></td>
					</tr>
				</table>
			</form>
		</li>


		<li>
			<h2>Pretraživanje simulacija prema id u intervalu</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/simulacije/pretrazivanjeSimulacijaPremaVoziluPremaVremenu">
				<table>
					<tr>
						<td>Od vremena:</td>
						<td><input name="odVremena" /> <input type="hidden"
							name="${mvc.csrf.name}" value="${mvc.csrf.token}" /></td>
					</tr>
					<tr>
						<td>Do vremena:</td>
						<td><input name="doVremena" />
					</tr>

					<tr>
						<td>Id</td>
						<td><input name="id" />
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit"
							value=" Dohvati simulacije u intervalu prema vozilu "></td>
					</tr>
				</table>
			</form>
		</li>

		<li>
			<h2>Dodavanje nove vožnje za e vozilo</h2>
			<form method="post"
				action="${pageContext.servletContext.contextPath}/mvc/simulacije/dodavanjeNovogVozila">

				<table>
					<tr>
						<td>ID:</td>
						<td><input type="number" id="id" name="id"></td>
					</tr>
					<tr>
						<td>Broj:</td>
						<td><input type="number" id="broj" name="broj"></td>
					</tr>
					<tr>
						<td>Vrijeme:</td>
						<td><input type="number" id="vrijeme" name="vrijeme"></td>
					</tr>
					<tr>
						<td>Brzina:</td>
						<td><input id="brzina" name="brzina"></td>
					</tr>
					<tr>
						<td>Snaga:</td>
						<td><input id="snaga" name="snaga"></td>
					</tr>
					<tr>
						<td>Struja:</td>
						<td><input id="struja" name="struja"></td>
					</tr>
					<tr>
						<td>Visina:</td>
						<td><input id="visina" name="visina"></td>
					</tr>
					<tr>
						<td>GPS Brzina:</td>
						<td><input id="gpsBrzina" name="gpsBrzina"></td>
					</tr>
					<tr>
						<td>Temperatura Vozila:</td>
						<td><input type="number" id="tempVozila" name="tempVozila"></td>
					</tr>
					<tr>
						<td>Postotak Baterija:</td>
						<td><input type="number" id="postotakBaterija"
							name="postotakBaterija"></td>
					</tr>
					<tr>
						<td>Napon Baterija:</td>
						<td><input id="naponBaterija" name="naponBaterija"></td>
					</tr>
					<tr>
						<td>Kapacitet Baterija:</td>
						<td><input id="kapacitetBaterija" name="kapacitetBaterija"></td>
					</tr>
					<tr>
						<td>Temperatura Baterija:</td>
						<td><input type="number" id="tempBaterija"
							name="tempBaterija"></td>
					</tr>
					<tr>
						<td>Preostalo Km:</td>
						<td><input id="preostaloKm" name="preostaloKm"></td>
					</tr>
					<tr>
						<td>Ukupno Km:</td>
						<td><input id="ukupnoKm" name="ukupnoKm"></td>
					</tr>
					<tr>
						<td>GPS Širina:</td>
						<td><input id="gpsSirina" name="gpsSirina"></td>
					</tr>
					<tr>
						<td>GPS Dužina:</td>
						<td><input id="gpsDuzina" name="gpsDuzina"></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="submit" value="Dodaj novo vozilo"></td>
					</tr>
				</table>

			</form>
		</li>

	</ul>
</body>
</html>
