<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>

  <head>
    <link type="text/css" rel="stylesheet" href="/style.css">  
    
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
	
    <meta property="og:title" content="Motiver"/>
    <meta property="og:type" content="sport"/>
    <meta property="og:url" content="http://www.motiver.fi/"/>
    <meta property="og:image" content="http://www.motiver.fi/img/logo.png"/>
    <meta property="og:site_name" content="Motiver"/>
    <meta property="fb:app_id" content="198007390233150"/>
    <meta property="og:description"
          content="Motiver on tavoitteellisen kuntourheilijan harjoitus- ja ravintopäiväkirja. 
                   Sovellus on suunniteltu erityisesti aktiivisille kuntosaliharjoittelijoille 
                   sekä lajiharjoittelun tueksi. Älykäs tulosseuranta muokkaantuu käyttäjän tarpeiden 
                   mukaisesti sekä auttaa saavuttamaan halutut tavoitteet. "/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
    
    <title>Motiver</title>    
    
  	</head>
  	<body>
  	<table width="800" cellpadding=30 align=center>
  	<tr align=center>  	
  	<td colspan=2><img src="/img/logo_big.png">
  	</td></tr>
  	
  	<tr><td>
  	Motiver on tavoitteellisen kuntourheilijan harjoitus- ja ravintopäiväkirja. Sovellus on suunniteltu erityisesti aktiivisille kuntosaliharjoittelijoille sekä lajiharjoittelun tueksi. Älykäs tulosseuranta muokkaantuu käyttäjän tarpeiden mukaisesti sekä auttaa saavuttamaan halutut tavoitteet.
	<br><br><div class="label-title-big">Tieto</div>
	Motiverin tuottaman tiedon hyödyntäminen harjoittelussa luo mahdollisuuden seurata henkilökohtaista kehitystä helposti. Erilaisten analyysien avulla harjoittelulle voidaan asettaa selkeä päämäärä, joka motivoi ja kannustaa parempiin tuloksiin.
	<br><br><div class="label-title-big">Ystävät</div>
	FRIENDS-toiminnon avulla voit jakaa kavereittesi kanssa harjoitusohjelmia, ruokavalion tai henkilökohtaisia tuloksia, joka tekee harjoittelusta hauskempaa. Vastaavasti COACH-toiminnon avulla voit jakaa profiilin haluamasi henkilön, esim. oman personal trainerisi kanssa. Näin oma ohjaajasi voi laatia sinulle sopivan harjoitusohjelman tai ruokavalion sekä asettaa harjoitteluun tavoitteita.
	<br><br><div class="label-title-big">Tili</div>
	Palvelun käyttö on helppoa ja ilmaista. Voit kirjautua palveluun OpenId-tunnuksillasi, joka tekee palvelun käytöstä helppoa ja turvallista eikä erillistä rekister�inti� tarvita!
  	</td>
  	
  	<td width="200">
  	<%
  	out.write("Kirjaudu seuraavilla tunnuksilla:");
  	out.write("<br><br><a href=\""+request.getAttribute("url_google")+"\"><img src=\"/img/openid_google.png\" /></a>");
  	out.write("<br><br><a href=\""+request.getAttribute("url_yahoo")+"\"><img src=\"/img/openid_yahoo.png\" /></a>");
  	out.write("<br><br><a href=\""+request.getAttribute("url_myspace")+"\"><img src=\"/img/openid_myspace.gif\" /></a>");
  	out.write("<br><br><a href=\""+request.getAttribute("url_aol")+"\"><img src=\"/img/openid_aol.png\" /></a>");
  	out.write("<br><br><a href=\""+request.getAttribute("url_myopenid")+"\"><img src=\"/img/openid_myopenid.png\" /></a>");
  	
  	%>
    </td>
    
  	</tr>
  	  	
  	</table>
    


  </body>
  
</html>
