import { Link } from 'react-router-dom';

const NotFound = () => {
    return (
        <div className="not-found-page">
            <h2>Page Not Found</h2>
            <p>We couldn't find the page you were looking for.</p>
            <Link to="/" className="btn btn-primary">Return to Home</Link>
        </div>
    );
};

export default NotFound;