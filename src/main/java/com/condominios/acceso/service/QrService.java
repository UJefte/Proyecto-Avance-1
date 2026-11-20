package com.condominios.acceso.service;

import com.condominios.acceso.exception.ReglaNegocioException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

/** Genera y lee codigos QR con la libreria ZXing. */
@Service
public class QrService {

    // convierte el codigo del pase en una imagen PNG
    public byte[] generarPng(String texto, int tamano) {
        try {
            BitMatrix matriz = new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamano, tamano);
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matriz, "PNG", salida);
            return salida.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("No se pudo generar el QR", e);
        }
    }

    // lee una foto del QR (la que toma el guardia) y regresa el texto que trae
    public String leer(InputStream imagen) {
        try {
            BufferedImage img = ImageIO.read(imagen);
            if (img == null) {
                throw new ReglaNegocioException("El archivo no es una imagen valida");
            }
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(img)));
            return new MultiFormatReader().decode(bitmap).getText();
        } catch (NotFoundException e) {
            throw new ReglaNegocioException("No se encontro ningun codigo QR en la imagen");
        } catch (IOException e) {
            throw new ReglaNegocioException("No se pudo leer la imagen");
        }
    }
}
