import React from 'react';
import { NavLink } from 'react-router-dom';
import '../Styles/styles.css';

const Navbar = () => {
  return (
    <nav className="navbar">
      <ul className="navbar-links">
        <li><NavLink to="/image-upload" activeClassName="active">Image Upload</NavLink></li>
        <li><NavLink to="/pdf-upload" activeClassName="active">PDF Upload</NavLink></li>
      </ul>
    </nav>
  );
}

export default Navbar;
