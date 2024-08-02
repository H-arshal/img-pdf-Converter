import React from 'react';
import { BrowserRouter as Router, Route, Routes ,Navigate} from 'react-router-dom';
import Navbar from './Pages/navbar';
import ImageUpload from './Pages/ImageUpload';
import PdfUpload from './Pages/PdfUpload';
import './App.css';
import './Styles/styles.css';

import 'toastr/build/toastr.min.css';
function App() {
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

export default App;
