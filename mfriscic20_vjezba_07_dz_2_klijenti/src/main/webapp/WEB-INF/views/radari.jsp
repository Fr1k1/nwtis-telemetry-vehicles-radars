<%@page
	import="edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Radar"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List, java.util.Date, java.text.SimpleDateFormat, edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.podaci.Radar"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>REST MVC - Pregled radara</title>
<style type="text/css">
table, th, td {
	border: 1px solid;
}

th {
	text-align: center;
	font-weight: bold;
}

.desno {
	text-align: right;
}
</style>
</head>
<body>
	<h1>REST MVC - Pregled radara</h1>
	<ul>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kazne/pocetak">Početna
				stranica</a></li>
	</ul>
	<br />
	<table>
		<tr>
			<th>R.br.
			<th>Broj</th>
			<th>Adresa</th>
			<th>Mrezna vrata</th>
			<th>GPS sirina</th>
			<th>GPS duzina</th>
			<th>Maks udaljenost</th>
			<!--  	<th>Adresa registracije</th>
			<th>Mrežna vrata registracije</th>
			<th>Adresa kazne</th>
			<th>Mrežna vrata kazne</th>
			<th>Poštanska adresa radara</th>
					-->



		</tr>
		<%
		int i = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		List<Radar> radari = (List<Radar>) request.getAttribute("radari");
		for (Radar v : radari) {
			i++;
		%>
		<tr>
			<td class="desno"><%=i%></td>
			<td><%=v.getId()%></td>
			<td><%=v.getAdresaRadara()%></td>
			<td><%=v.getMreznaVrataRadara()%></td>
			<td><%=v.getGpsSirina()%></td>
			<td><%=v.getGpsDuzina()%></td>
			<td><%=v.getMaksUdaljenost()%></td>

		</tr>
		<%
		}
		%>
	</table>
</body>
</html>
