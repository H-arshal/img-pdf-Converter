import React from 'react';
import { BrowserRouter as Router, Route, Routes ,Navigate} from 'react-router-dom';
import Navbar from './navbar';
import ImageUpload from './ImageUpload';
import PdfUpload from './PdfUpload';
// import '../App.css';
import '../Styles/styles.css';

import 'toastr/build/toastr.min.css';
function Homepage() {
  return (
    <Router>
      <div className="App">
        <div style={{ position: 'relative' }}>
          <Navbar />
          <Routes>
            <Route path="/" element={<Navigate to="/image-upload" />} />
            <Route path="/image-upload" element={<ImageUpload />} />
            <Route path="/pdf-upload" element={<PdfUpload />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default Homepage;
