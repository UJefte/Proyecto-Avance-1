package com.condominios.acceso;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // para mandar los correos sin hacer esperar al guardia
public class CondominiosAccesoApplication {

    public static void main(String[] args) {
        // los servidores en la nube corren en UTC, asi que fijamos la hora de Mexico
        // para que las vigencias de los pases y la bitacora salgan bien
        TimeZone.setDefault(TimeZone.getTimeZone("America/Mexico_City"));
        SpringApplication.run(CondominiosAccesoApplication.class, args);
    }
}
