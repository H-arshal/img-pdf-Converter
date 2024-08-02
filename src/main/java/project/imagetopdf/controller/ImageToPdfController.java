package project.imagetopdf.controller;

import com.itextpdf.text.DocumentException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.InputStreamResource;
import project.imagetopdf.service.ImageToPdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import project.imagetopdf.service.PdfToImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/image-pdf")
public class ImageToPdfController {

    @Autowired
    private ImageToPdfService imageToPdfService;
    @Autowired
    private PdfToImageService pdfToImageService;

    public ImageToPdfController(PdfToImageService pdfToImageService) {
        this.pdfToImageService = pdfToImageService;
    }

    @PostMapping("/convert-to-pdf")
    public ResponseEntity<Resource> convertToPdf(
            @RequestParam List<MultipartFile> images) {
        try {
            // Convert MultipartFile to InputStream
            List<InputStream> imageStreams = new ArrayList<>();
            System.out.println("Getting Images...");
            for (MultipartFile file : images) {
                imageStreams.add(file.getInputStream());
            }
            System.out.println("Images Loaded Successfully...");

            // Create PDF and write to ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.out.println("Creating Pdf...");
            imageToPdfService.convertImageToPdf(imageStreams, baos);
            System.out.println("Pdf Created...");

            // Convert ByteArrayOutputStream to ByteArrayResource
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // Return file as response with proper headers for download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.pdf\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Error occurred: " + e.getMessage()).getBytes()));
        }
    }

    @RequestMapping("/convert-to-img")
    public ResponseEntity<InputStreamResource> convertPdfToImage(@RequestParam("file") MultipartFile file) {
        try {
            byte[] zipBytes = pdfToImageService.convertPdfToImageZip(file);
            ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
            InputStreamResource resource = new InputStreamResource(bais);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=images.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(zipBytes.length)
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputStreamResource(new ByteArrayInputStream(("Error occurred: " + e.getMessage()).getBytes())));
        }
    }
}
