import React, { useState } from "react";
import axios from "axios";
import toastr from "toastr"; // Import Toastr
import Loader from "../Components/Loader.jsx"; // Import the Loader component
const springURL = process.env.REACT_APP_API_URL || "http://localhost:8080";

const PdfUpload = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [downloadLink, setDownloadLink] = useState("");

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!selectedFile) {
      toastr.error("Please select a PDF file.");
      return;
    }
    setIsSubmitting(true);
    setDownloadLink("");
    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      const response = await axios.post(
        `${springURL}/image-pdf/convert-to-img`,
        formData,
        {
          responseType: "blob",
        }
      );

      const url = window.URL.createObjectURL(
        new Blob([response.data], { type: "application/zip" })
      );
      setDownloadLink(url);
      toastr.success("Images generated successfully!");
    } catch (error) {
      console.error("Error generating images:", error);
      toastr.error("Error generating images. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="main-container">
      <div className="image-upload-container">
        <h2>Select PDF document to convert into Images</h2>
        <form onSubmit={handleSubmit}>
          <input type="file" accept=".pdf" onChange={handleFileChange} />
          <div className="button-loader-container">
            <button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "Converting PDF..." : "Generate Images"}
            </button>
            {isSubmitting && <Loader />}
          </div>
        </form>
        {downloadLink && (
          <a
            href={downloadLink}
            download="images.zip"
            className="download-link"
          >
            Download Images
          </a>
        )}
      </div>
    </div>
  );
};

export default PdfUpload;
