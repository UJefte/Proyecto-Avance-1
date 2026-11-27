package com.condominios.acceso.service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Manda correos al residente. El cuerpo del correo sale de una plantilla HTML
 * (templates/correo/...) para que el codigo quede separado del diseño.
 * Se manda en segundo plano (@Async) para que la caseta no espere.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;
    private final ITemplateEngine plantillas;

    @Value("${app.correo.activo}")
    private boolean activo;

    @Value("${app.correo.remitente}")
    private String remitente;

    @Value("${app.url-panel}")
    private String urlPanel;

    /** Llego alguien sin QR y el residente tiene que aprobarlo. */
    @Async
    public void visitaEnCaseta(String correo, String residente, String visitante, String tipo, String motivo, String fotoUrl) {
        Context datos = new Context();
        datos.setVariable("residente", residente);
        datos.setVariable("visitante", visitante);
        datos.setVariable("tipo", tipo.toLowerCase());
        datos.setVariable("motivo", motivo);
        datos.setVariable("foto", fotoUrl != null ? urlPanel + fotoUrl : null);
        datos.setVariable("enlace", urlPanel + "/panel/visitas");
        enviar(correo, visitante + " te espera en la caseta", "correo/visita-en-caseta", datos);
    }

    /** Su visita ya entro al condominio con su pase. */
    @Async
    public void visitaEntro(String correo, String residente, String visitante, LocalDateTime hora) {
        Context datos = new Context();
        datos.setVariable("residente", residente);
        datos.setVariable("visitante", visitante);
        datos.setVariable("hora", hora.format(HORA));
        enviar(correo, visitante + " ya entró al condominio", "correo/visita-entro", datos);
    }

    private void enviar(String para, String asunto, String plantilla, Context datos) {
        String html = plantillas.process(plantilla, datos);
        if (!activo) {
            // sin servidor de correo configurado solo lo dejamos en el log
            log.info("Correo (no enviado, CORREO_ACTIVO=false) para {}: {}", para, asunto);
            return;
        }
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(para);
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(mensaje);
            log.info("Correo enviado a {}: {}", para, asunto);
        } catch (Exception e) {
            // si falla el correo no se cae el registro del acceso, solo se avisa en el log
            log.warn("No se pudo mandar el correo a {}: {}", para, e.getMessage());
        }
    }
}
