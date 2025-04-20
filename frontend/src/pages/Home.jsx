import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllFlights } from '../services/flightService';

const Home = () => {
    const [featuredFlights, setFeaturedFlights] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchFeaturedFlights = async () => {
            try {
                const response = await getAllFlights();
                // Display only active flights, sorted by departure time
                const flights = response.data
                    .filter(flight => flight.active)
                    .sort((a, b) => new Date(a.departureTime) - new Date(b.departureTime))
                    .slice(0, 6); // Display only a few flights
                setFeaturedFlights(flights);
            } catch (error) {
                console.error('Error fetching featured flights:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchFeaturedFlights();
    }, []);

    return (
        <div className="home">
            <section className="hero">
                <div className="hero-content">
                    <h1>Fly for Less with LowCost Airlines</h1>
                    <p>Find the best deals on flights across Europe</p>
                    <Link to="/flights" className="btn btn-primary">Search Flights</Link>
                </div>
            </section>

            <section className="featured-flights">
                <h2>Featured Flights</h2>
                {loading ? (
                    <div className="loading">Loading featured flights...</div>
                ) : (
                    <div className="flight-grid">
                        {featuredFlights.map((flight) => (
                            <div key={flight.id} className="flight-card">
                                <div className="flight-info">
                                    <h3>{flight.departure} to {flight.arrival}</h3>
                                    <p>Date: {new Date(flight.departureTime).toLocaleDateString()}</p>
                                    <p>Time: {new Date(flight.departureTime).toLocaleTimeString()}</p>
                                    <p className="price">From {flight.price} €</p>
                                </div>
                                <Link to={`/flights/${flight.id}`} className="btn btn-secondary">View Details</Link>
                            </div>
                        ))}
                    </div>
                )}
                {!loading && featuredFlights.length === 0 && (
                    <p>No featured flights available at the moment.</p>
                )}
            </section>
        </div>
    );
};

export default Home;