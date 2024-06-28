#!/bin/bash
set -e

start_server() {
    echo "Starting $1"
    shift
    java "$@" &
}

# pokretanje svih posluzitelja, sleep 2 da se ukljucuju po redu
start_server "CentralniSustav" -cp target/mfriscic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar \
    edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.CentralniSustav NWTiS_DZ1_CS.txt &
    
    sleep 2
    

start_server "PosluziteljRadara1" -cp target/mfriscic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar \
    edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara NWTiS_DZ1_R1.txt &
    
    sleep 2

start_server "PosluziteljRadara2" -cp target/mfriscic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar \
    edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara NWTiS_DZ1_R2.txt &
    
    sleep 2
    
start_server "PosluziteljRadara3" -cp target/mfriscic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar \
    edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara NWTiS_DZ1_R3.txt &
    
	sleep 2
	
start_server "PosluziteljRadara4" -cp target/mfriscic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar \
    edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljRadara NWTiS_DZ1_R4.txt &
    
	sleep 2
	
	
	
start_server "PosluziteljKazni" -cp target/mfriscic20_vjezba_07_dz_2_app-1.1.0-jar-with-dependencies.jar \
    edu.unizg.foi.nwtis.mfriscic20.vjezba_07_dz_2.posluzitelji.PosluziteljKazni NWTiS_DZ1_PK.txt &
    
    sleep 4


# Da se kontejner ne ugasi
tail -f /dev/null
