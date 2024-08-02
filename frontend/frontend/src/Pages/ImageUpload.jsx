import React, { useState } from "react";
import axios from "axios";
import toastr from "toastr";
import Loader from "../Components/Loader.jsx";

const ImageUpload = () => {
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [downloadLink, setDownloadLink] = useState("");

  const handleFileChange = (event) => {
    setSelectedFiles(event.target.files);
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    if (selectedFiles.length === 0) {
      toastr.error("Please select at least one image.");
      return;
    }

    setIsSubmitting(true);
    setDownloadLink("");

    const formData = new FormData();
    for (const file of selectedFiles) {
      formData.append("images", file);
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/image-pdf/convert-to-pdf",
        formData,
        {
          responseType: "blob",
        }
      );

      const url = window.URL.createObjectURL(
        new Blob([response.data], { type: "application/pdf" })
      );
      setDownloadLink(url);
      toastr.success("PDF generated successfully!");
    } catch (error) {
      console.error("Error generating PDF:", error);
      toastr.error("Error generating PDF. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="main-container">
      <div className="image-upload-container">
        <h2>Select Images to Convert into a PDF Document</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="file"
            multiple
            accept="image/*"
            onChange={handleFileChange}
          />
          <div className="button-loader-container">
            <button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Generating PDF..." : "Generate PDF"}
            </button>
            {isSubmitting && <Loader />} {/* Show Loader when submitting */}
          </div>
        </form>
        {downloadLink && (
          <a
            href={downloadLink}
            download="output.pdf"
            className="download-link"
          >
            Download PDF
          </a>
        )}
      </div>
    </div>
  );
};

export default ImageUpload;
