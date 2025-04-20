import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { searchFlights } from '../services/flightService';

const FlightSearch = () => {
    const [searchParams, setSearchParams] = useState({
        departure: '',
        arrival: '',
        startDate: '',
        endDate: ''
    });
    const [flights, setFlights] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searched, setSearched] = useState(false);
    const [error, setError] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setSearchParams(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSearched(true);

        try {
            // Format dates for API
            const formattedStartDate = searchParams.startDate ?
                new Date(searchParams.startDate).toISOString() : '';

            const formattedEndDate = searchParams.endDate ?
                new Date(searchParams.endDate).toISOString() : '';

            // Only search if at least departure and arrival are specified
            if (searchParams.departure && searchParams.arrival) {
                const response = await searchFlights(
                    searchParams.departure,
                    searchParams.arrival,
                    formattedStartDate,
                    formattedEndDate
                );
                setFlights(response.data);
            } else {
                setError('Please specify at least departure and arrival locations');
            }
        } catch (error) {
            console.error('Error searching flights:', error);
            setError('An error occurred while searching for flights');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flight-search">
            <h2>Search Flights</h2>

            <form onSubmit={handleSubmit} className="search-form">
                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="departure">From</label>
                        <input
                            type="text"
                            id="departure"
                            name="departure"
                            value={searchParams.departure}
                            onChange={handleChange}
                            placeholder="City or Airport"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="arrival">To</label>
                        <input
                            type="text"
                            id="arrival"
                            name="arrival"
                            value={searchParams.arrival}
                            onChange={handleChange}
                            placeholder="City or Airport"
                            required
                        />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="startDate">Start Date</label>
                        <input
                            type="date"
                            id="startDate"
                            name="startDate"
                            value={searchParams.startDate}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="endDate">End Date</label>
                        <input
                            type="date"
                            id="endDate"
                            name="endDate"
                            value={searchParams.endDate}
                            onChange={handleChange}
                        />
                    </div>
                </div>

                <button type="submit" className="btn btn-primary" disabled={loading}>
                    {loading ? 'Searching...' : 'Search Flights'}
                </button>
            </form>

            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <div className="loading">Searching for flights...</div>
            ) : (
                <>
                    {searched && (
                        <div className="search-results">
                            <h3>Flight Results</h3>

                            {flights.length > 0 ? (
                                <div className="flight-list">
                                    {flights.map((flight) => (
                                        <div key={flight.id} className="flight-item">
                                            <div className="flight-details">
                                                <div className="route">
                                                    <h4>{flight.departureAirport} → {flight.arrivalAirport}</h4>
                                                </div>
                                                <div className="time">
                                                    <p>Departure: {new Date(flight.departureTime).toLocaleString()}</p>
                                                    <p>Arrival: {new Date(flight.arrivalTime).toLocaleString()}</p>
                                                </div>
                                                <div className="price-info">
                                                    <span className="price">{flight.currentPrice} €</span>
                                                    <span className="seats-left">{flight.availableSeats} seats left</span>
                                                </div>
                                            </div>
                                            <div className="flight-actions">
                                                <Link to={`/flights/${flight.id}`} className="btn btn-secondary">View Details</Link>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p>No flights found matching your criteria.</p>
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default FlightSearch;