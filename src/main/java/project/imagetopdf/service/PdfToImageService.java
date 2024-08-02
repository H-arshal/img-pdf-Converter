package project.imagetopdf.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfToImageService {

    public byte[] convertPdfToImageZip(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getInputStream().readAllBytes())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                for (int page = 0; page < document.getNumberOfPages(); page++) {
                    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                    ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", imageOutputStream);

                    byte[] imageBytes = imageOutputStream.toByteArray();
                    ZipEntry zipEntry = new ZipEntry("page_" + page + ".png");
                    zos.putNextEntry(zipEntry);
                    zos.write(imageBytes);
                    zos.closeEntry();
                }
            }

            return baos.toByteArray();
        }
    }
}
