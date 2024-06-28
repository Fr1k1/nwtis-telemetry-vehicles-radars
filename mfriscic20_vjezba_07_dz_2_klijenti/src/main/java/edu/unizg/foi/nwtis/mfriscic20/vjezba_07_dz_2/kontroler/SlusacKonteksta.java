package edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.kontroler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SlusacKonteksta implements ServletContextListener {

    private ServletContext kontekst = null;
    private static final Logger LOGGER = Logger.getLogger(SlusacKonteksta.class.getName());


    @Override
    public void contextInitialized(ServletContextEvent event) {
        kontekst = event.getServletContext();
        String putanja = kontekst.getRealPath("/WEB-INF") + java.io.File.separator;
        
        System.out.println("Kreiranje konteksta");

        String csvString1 = putanja + kontekst.getInitParameter("datoteka1");
        String csvString2 = putanja + kontekst.getInitParameter("datoteka2");
        String csvString3 = putanja + kontekst.getInitParameter("datoteka3");
        

        Map<String, String> csvData = new HashMap<>();
        csvData.putAll(procitajDatoteke(csvString1));
        csvData.putAll(procitajDatoteke(csvString2));
        csvData.putAll(procitajDatoteke(csvString3));

        kontekst.setAttribute("csvData", csvData);
    }

    private Map<String, String> procitajDatoteke(String putanjaDoDatoteke) {
        Map<String, String> csvData = new HashMap<>();
        try (BufferedReader citac = new BufferedReader(new FileReader(putanjaDoDatoteke))) {
            String prviRedakTablice = citac.readLine();
            String redak;
            int redakBroj = 0;
            while ((redak = citac.readLine()) != null) {
                csvData.put(String.valueOf(redakBroj++), redak);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvData;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        kontekst = event.getServletContext();
        System.out.println("Brise se kontekst");
    }
}
