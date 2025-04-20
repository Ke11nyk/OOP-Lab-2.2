import { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getFlightById, getFlightPriceHistory } from '../services/flightService';
import { AuthContext } from '../context/AuthContext';

const FlightDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { currentUser } = useContext(AuthContext);

    const [flight, setFlight] = useState(null);
    const [priceHistory, setPriceHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchFlightDetails = async () => {
            try {
                const flightResponse = await getFlightById(id);
                setFlight(flightResponse.data);

                const priceHistoryResponse = await getFlightPriceHistory(id);
                setPriceHistory(priceHistoryResponse.data);
            } catch (error) {
                console.error('Error fetching flight details:', error);
                setError('Failed to load flight details');
            } finally {
                setLoading(false);
            }
        };

        fetchFlightDetails();
    }, [id]);

    const handleBookNow = () => {
        if (!currentUser) {
            // If not logged in, redirect to login
            navigate('/login');
        } else {
            // If logged in, proceed to booking form
            navigate(`/book/${id}`);
        }
    };

    if (loading) {
        return <div className="loading">Loading flight details...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    if (!flight) {
        return <div className="not-found">Flight not found</div>;
    }

    return (
        <div className="flight-details">
            <h2>Flight Details</h2>

            <div className="flight-info-card">
                <div className="flight-header">
                    <h3>{flight.departureAirport} → {flight.arrivalAirport}</h3>
                    <span className="flight-status">{flight.isActive ? 'Active' : 'Inactive'}</span>
                </div>

                <div className="flight-info-grid">
                    <div className="info-group">
                        <h4>Departure</h4>
                        <p>{new Date(flight.departureTime).toLocaleString()}</p>
                    </div>

                    <div className="info-group">
                        <h4>Arrival</h4>
                        <p>{new Date(flight.arrivalTime).toLocaleString()}</p>
                    </div>

                    <div className="info-group">
                        <h4>Duration</h4>
                        <p>{calculateDuration(flight.departureTime, flight.arrivalTime)}</p>
                    </div>

                    <div className="info-group">
                        <h4>Aircraft</h4>
                        <p>{flight.aircraft || 'Not specified'}</p>
                    </div>
                </div>

                <div className="flight-price-section">
                    <div className="current-price">
                        <h4>Current Price</h4>
                        <span className="price-display">{flight.currentPrice} €</span>
                    </div>

                    <div className="seats-info">
                        <h4>Available Seats</h4>
                        <span className={`seats-count ${flight.availableSeats < 10 ? 'low-seats' : ''}`}>
              {flight.availableSeats}
            </span>
                    </div>
                </div>

                {flight.isActive && (
                    <button onClick={handleBookNow} className="btn btn-primary book-btn">
                        Book Now
                    </button>
                )}
            </div>

            {priceHistory.length > 0 && (
                <div className="price-history-section">
                    <h3>Price History</h3>
                    <div className="price-history-list">
                        {priceHistory.map((record) => (
                            <div key={record.id} className="price-history-item">
                                <span className="date">{new Date(record.changeTime).toLocaleDateString()}</span>
                                <span className="old-price">{record.oldPrice} €</span>
                                <span className="arrow">→</span>
                                <span className="new-price">{record.newPrice} €</span>
                                <span className="reason">{formatReason(record.reason)}</span>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

// Helper function to calculate flight duration
function calculateDuration(departure, arrival) {
    const departureTime = new Date(departure);
    const arrivalTime = new Date(arrival);

    const durationMs = arrivalTime - departureTime;
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    return `${hours}h ${minutes}m`;
}

// Helper function to format reason enum
function formatReason(reason) {
    if (!reason) return '';

    // Format like "DEMAND_INCREASE" to "Demand Increase"
    return reason
        .toLowerCase()
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

export default FlightDetails;