package project.imagetopdf.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ImageToPdfService {

    private static final Logger logger = LoggerFactory.getLogger(ImageToPdfService.class);
    private static final float MARGIN = 1f;

    public void convertImageToPdf(final List<InputStream> imageStreams, ByteArrayOutputStream baos)
            throws DocumentException, IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            for (InputStream imageStream : imageStreams) {
                try {
                    byte[] imageBytes = imageStream.readAllBytes();
                    Image image = Image.getInstance(imageBytes);

                    // Calculate the available width and height for the image considering the 1px margin
                    float pageWidth = document.getPageSize().getWidth();
                    float pageHeight = document.getPageSize().getHeight();
                    float availableWidth = pageWidth - 2 * MARGIN; // Margin on both sides
                    float availableHeight = pageHeight - 2 * MARGIN; // Margin on both sides

                    // Scale the image to fit within the available width and height
                    float imageWidth = image.getWidth();
                    float imageHeight = image.getHeight();

                    // Calculate scaling factors
                    float widthScaler = (availableWidth / imageWidth) * 100;
                    float heightScaler = (availableHeight / imageHeight) * 100;
                    float scaler = Math.min(widthScaler, heightScaler); // Use the smaller factor to maintain aspect ratio

                    // Apply the scaling
                    image.scalePercent(scaler);

                    // Center the image within the page
                    float scaledWidth = image.getScaledWidth();
                    float scaledHeight = image.getScaledHeight();
                    float x = (pageWidth - scaledWidth) / 2; // Center horizontally
                    float y = (pageHeight - scaledHeight) / 2; // Center vertically

                    // Start a new page for each image
                    if (document.isOpen()) {
                        document.newPage();
                    }

                    image.setAbsolutePosition(x, y);

                    // Add the image to the document
                    document.add(image);
                } catch (Exception e) {
                    logger.error("Error adding image to PDF: ", e);
                } finally {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        logger.warn("Failed to close image stream: ", e);
                    }
                }
            }
        } catch (DocumentException e) {
            logger.error("Error creating PDF document: ", e);
            throw e; // Re-throw to signal the failure
        } finally {
            // Ensure the document is closed if not already
            if (document.isOpen()) {
                document.close();
            }
        }
    }

}
