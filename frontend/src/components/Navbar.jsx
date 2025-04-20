import { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
    const { currentUser, logout } = useContext(AuthContext);
    console.log("CurrentUser in Navbar:", currentUser);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-logo">
                    LowCost Airlines
                </Link>
                <ul className="nav-menu">
                    <li className="nav-item">
                        <Link to="/" className="nav-link">Home</Link>
                    </li>
                    <li className="nav-item">
                        <Link to="/flights" className="nav-link">Flights</Link>
                    </li>
                    {currentUser ? (
                        <>
                            <li className="nav-item">
                                <Link to="/my-bookings" className="nav-link">My Bookings</Link>
                            </li>
                            <li className="nav-item">
                                <span className="nav-link login">
                                    Hello, {currentUser?.login || 'User'}
                                </span>
                            </li>
                            <li className="nav-item">
                                <button onClick={handleLogout} className="nav-button logout">Logout</button>
                            </li>
                        </>
                    ) : (
                        <>
                            <li className="nav-item">
                                <Link to="/login" className="nav-link">Login</Link>
                            </li>
                            <li className="nav-item">
                                <Link to="/register" className="nav-link register">Register</Link>
                            </li>
                        </>
                    )}
                </ul>
            </div>
        </nav>
    );
};

export default Navbar;