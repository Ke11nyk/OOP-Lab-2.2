import { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getBookingById, confirmBooking, payForBooking, cancelBooking, requestRefund } from '../services/bookingService';
import { AuthContext } from '../context/AuthContext';

const BookingDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { currentUser } = useContext(AuthContext);

    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        const fetchBookingDetails = async () => {
            try {
                const response = await getBookingById(id);
                setBooking(response.data);
            } catch (error) {
                console.error('Error fetching booking details:', error);
                setError('Failed to load booking details');
            } finally {
                setLoading(false);
            }
        };

        fetchBookingDetails();
    }, [id]);

    // Ensure user can only access their own bookings
    useEffect(() => {
        if (booking && currentUser && booking.userId !== currentUser.id) {
            setError('You are not authorized to view this booking');
        }
    }, [booking, currentUser]);

    const handleConfirm = async () => {
        setActionLoading(true);
        try {
            await confirmBooking(id);
            // Refresh booking data
            const response = await getBookingById(id);
            setBooking(response.data);
        } catch (error) {
            console.error('Error confirming booking:', error);
            setError('Failed to confirm booking');
        } finally {
            setActionLoading(false);
        }
    };

    const handlePay = async () => {
        setActionLoading(true);
        try {
            await payForBooking(id);
            // Refresh booking data
            const response = await getBookingById(id);
            setBooking(response.data);
        } catch (error) {
            console.error('Error processing payment:', error);
            setError('Failed to process payment');
        } finally {
            setActionLoading(false);
        }
    };

    const handleCancel = async () => {
        if (!window.confirm('Are you sure you want to cancel this booking?')) {
            return;
        }

        setActionLoading(true);
        try {
            await cancelBooking(id);
            // Refresh booking data
            const response = await getBookingById(id);
            setBooking(response.data);
        } catch (error) {
            console.error('Error cancelling booking:', error);
            setError('Failed to cancel booking');
        } finally {
            setActionLoading(false);
        }
    };

    const handleRefund = async () => {
        if (!window.confirm('Are you sure you want to request a refund?')) {
            return;
        }

        setActionLoading(true);
        try {
            await requestRefund(id);
            // Refresh booking data
            const response = await getBookingById(id);
            setBooking(response.data);
        } catch (error) {
            console.error('Error requesting refund:', error);
            setError('Failed to request refund');
        } finally {
            setActionLoading(false);
        }
    };

    if (loading) {
        return <div className="loading">Loading booking details...</div>;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    if (!booking) {
        return <div className="not-found">Booking not found</div>;
    }

    return (
        <div className="booking-details">
            <h2>Booking Details</h2>

            <div className="booking-info-card">
                <div className="booking-header">
                    <h3>Booking #{booking.bookingReference}</h3>
                    <span className={`status-badge ${booking.status.toLowerCase()}`}>
            {booking.status}
          </span>
                </div>

                <div className="booking-dates">
                    <p><strong>Booked on:</strong> {new Date(booking.bookingDate).toLocaleString()}</p>
                </div>

                <div className="flight-details">
                    <h4>Flight Information</h4>
                    <div className="flight-info-grid">
                        <p><strong>Route:</strong> {booking.flight.departureAirport} → {booking.flight.arrivalAirport}</p>
                        <p><strong>Date:</strong> {new Date(booking.flight.departureTime).toLocaleDateString()}</p>
                        <p><strong>Departure:</strong> {new Date(booking.flight.departureTime).toLocaleTimeString()}</p>
                        <p><strong>Arrival:</strong> {new Date(booking.flight.arrivalTime).toLocaleTimeString()}</p>
                    </div>
                </div>

                <div className="options-details">
                    <h4>Selected Options</h4>
                    <div className="options-grid">
                        <div className="option-item">
                            <span className="option-label">Priority Boarding:</span>
                            <span className={`option-value ${booking.priorityBoarding ? 'yes' : 'no'}`}>
                {booking.priorityBoarding ? 'Yes' : 'No'}
              </span>
                        </div>

                        <div className="option-item">
                            <span className="option-label">Checked Baggage:</span>
                            <span className={`option-value ${booking.checkedBaggage ? 'yes' : 'no'}`}>
                {booking.checkedBaggage ? 'Yes' : 'No'}
              </span>
                        </div>

                        {booking.checkedBaggage && (
                            <div className="option-item">
                                <span className="option-label">Number of Bags:</span>
                                <span className="option-value">{booking.baggageCount}</span>
                            </div>
                        )}
                    </div>
                </div>

                <div className="price-summary">
                    <h4>Price Summary</h4>
                    <div className="price-detail">
                        <span>Base Flight Price:</span>
                        <span>{booking.flight.currentPrice} €</span>
                    </div>

                    {booking.priorityBoarding && (
                        <div className="price-detail">
                            <span>Priority Boarding:</span>
                            <span>10.00 €</span>
                        </div>
                    )}

                    {booking.checkedBaggage && (
                        <div className="price-detail">
                            <span>Checked Baggage ({booking.baggageCount} {booking.baggageCount === 1 ? 'bag' : 'bags'}):</span>
                            <span>{calculateBaggageCost(booking.baggageCount)} €</span>
                        </div>
                    )}

                    <div className="price-total">
                        <span>Total:</span>
                        <span>{booking.totalPrice} €</span>
                    </div>
                </div>

                <div className="booking-actions">
                    {booking.status === 'RESERVED' && (
                        <>
                            <button
                                onClick={handleConfirm}
                                className="btn btn-primary"
                                disabled={actionLoading}
                            >
                                {actionLoading ? 'Processing...' : 'Confirm Booking'}
                            </button>
                            <button
                                onClick={handleCancel}
                                className="btn btn-danger"
                                disabled={actionLoading}
                            >
                                {actionLoading ? 'Processing...' : 'Cancel Booking'}
                            </button>
                        </>
                    )}

                    {booking.status === 'CONFIRMED' && (
                        <>
                            <button
                                onClick={handlePay}
                                className="btn btn-primary"
                                disabled={actionLoading}
                            >
                                {actionLoading ? 'Processing...' : 'Pay Now'}
                            </button>
                            <button
                                onClick={handleCancel}
                                className="btn btn-danger"
                                disabled={actionLoading}
                            >
                                {actionLoading ? 'Processing...' : 'Cancel Booking'}
                            </button>
                        </>
                    )}

                    {booking.status === 'PAID' && (
                        <button
                            onClick={handleRefund}
                            className="btn btn-secondary"
                            disabled={actionLoading}
                        >
                            {actionLoading ? 'Processing...' : 'Request Refund'}
                        </button>
                    )}

                    <button
                        onClick={() => navigate('/my-bookings')}
                        className="btn btn-outline"
                    >
                        Back to My Bookings
                    </button>
                </div>
            </div>
        </div>
    );
};

// Helper function to calculate baggage cost
function calculateBaggageCost(baggageCount) {
    if (baggageCount <= 0) return 0;

    // First bag costs 25€
    let cost = 25;

    // Additional bags cost 35€ each
    if (baggageCount > 1) {
        cost += (baggageCount - 1) * 35;
    }

    return cost.toFixed(2);
}

export default BookingDetails;