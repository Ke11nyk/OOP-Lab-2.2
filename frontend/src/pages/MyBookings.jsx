import { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { getUserBookings } from '../services/bookingService';
import { AuthContext } from '../context/AuthContext';

const MyBookings = () => {
    const { currentUser } = useContext(AuthContext);
    const [bookings, setBookings] = useState([]);
    const [filteredBookings, setFilteredBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filter, setFilter] = useState('ALL');

    useEffect(() => {
        if (!currentUser) return;

        const fetchUserBookings = async () => {
            try {
                const response = await getUserBookings(currentUser.id);
                setBookings(response.data);
                setFilteredBookings(response.data);
            } catch (error) {
                console.error('Error fetching bookings:', error);
                setError('Failed to load your bookings');
            } finally {
                setLoading(false);
            }
        };

        fetchUserBookings();
    }, [currentUser]);

    useEffect(() => {
        if (filter === 'ALL') {
            setFilteredBookings(bookings);
        } else {
            setFilteredBookings(bookings.filter(booking => booking.status === filter));
        }
    }, [filter, bookings]);

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    };

    if (loading) {
        return <div className="loading">Loading your bookings...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    return (
        <div className="my-bookings">
            <h2>My Bookings</h2>

            <div className="booking-filter">
                <label htmlFor="statusFilter">Filter by status:</label>
                <select
                    id="statusFilter"
                    value={filter}
                    onChange={handleFilterChange}
                >
                    <option value="ALL">All Bookings</option>
                    <option value="RESERVED">Reserved</option>
                    <option value="CONFIRMED">Confirmed</option>
                    <option value="PAID">Paid</option>
                    <option value="CANCELLED">Cancelled</option>
                    <option value="REFUNDED">Refunded</option>
                </select>
            </div>

            {filteredBookings.length > 0 ? (
                <div className="bookings-list">
                    {filteredBookings.map((booking) => (
                        <div key={booking.id} className="booking-card">
                            <div className="booking-header">
                                <h3>Booking #{booking.bookingReference}</h3>
                                <span className={`status-badge ${booking.status.toLowerCase()}`}>
                  {booking.status}
                </span>
                            </div>

                            <div className="booking-flight-info">
                                <p><strong>Flight:</strong> {booking.flight.departureAirport} → {booking.flight.arrivalAirport}</p>
                                <p><strong>Date:</strong> {new Date(booking.flight.departureTime).toLocaleDateString()}</p>
                                <p><strong>Time:</strong> {new Date(booking.flight.departureTime).toLocaleTimeString()}</p>
                            </div>

                            <div className="booking-options">
                                <p>
                                    <strong>Priority Boarding:</strong>
                                    <span>{booking.priorityBoarding ? 'Yes' : 'No'}</span>
                                </p>
                                <p>
                                    <strong>Checked Baggage:</strong>
                                    <span>{booking.checkedBaggage ? `Yes (${booking.baggageCount} bags)` : 'No'}</span>
                                </p>
                            </div>

                            <div className="booking-price">
                                <strong>Total Price:</strong> {booking.totalPrice} €
                            </div>

                            <Link to={`/bookings/${booking.id}`} className="btn btn-secondary">
                                View Details
                            </Link>
                        </div>
                    ))}
                </div>
            ) : (
                <p className="no-bookings-message">
                    {filter === 'ALL'
                        ? "You don't have any bookings yet."
                        : `You don't have any ${filter.toLowerCase()} bookings.`}
                </p>
            )}
        </div>
    );
};

export default MyBookings;