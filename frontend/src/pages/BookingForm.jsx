import { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getFlightById } from '../services/flightService';
import { createBooking } from '../services/bookingService';
import { AuthContext } from '../context/AuthContext';

const BookingForm = () => {
    const { flightId } = useParams();
    const navigate = useNavigate();
    const { currentUser } = useContext(AuthContext);

    const [flight, setFlight] = useState(null);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    const [bookingDetails, setBookingDetails] = useState({
        priorityBoarding: false,
        checkedBaggage: false,
        baggageCount: 0
    });

    // Cost calculation variables
    const priorityBoardingCost = 10;
    const checkedBaggageCost = 25;
    const additionalBaggageCost = 35;

    useEffect(() => {
        const fetchFlightDetails = async () => {
            try {
                const response = await getFlightById(flightId);
                setFlight(response.data);
            } catch (error) {
                console.error('Error fetching flight details:', error);
                setError('Failed to load flight details');
            } finally {
                setLoading(false);
            }
        };

        fetchFlightDetails();
    }, [flightId]);

    const handleCheckboxChange = (e) => {
        const { name, checked } = e.target;
        setBookingDetails(prev => ({
            ...prev,
            [name]: checked
        }));

        // If unchecking baggage, reset baggage count
        if (name === 'checkedBaggage' && !checked) {
            setBookingDetails(prev => ({
                ...prev,
                baggageCount: 0
            }));
        }
    };

    const handleBaggageCountChange = (e) => {
        const count = parseInt(e.target.value);
        setBookingDetails(prev => ({
            ...prev,
            baggageCount: count
        }));
    };

    const calculateTotalPrice = () => {
        if (!flight) return 0;

        let totalPrice = parseFloat(flight.currentPrice);

        if (bookingDetails.priorityBoarding) {
            totalPrice += priorityBoardingCost;
        }

        if (bookingDetails.checkedBaggage) {
            // First bag costs checkedBaggageCost
            totalPrice += checkedBaggageCost;

            // Additional bags cost more
            if (bookingDetails.baggageCount > 1) {
                totalPrice += (bookingDetails.baggageCount - 1) * additionalBaggageCost;
            }
        }

        return totalPrice.toFixed(2);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError('');

        try {
            const bookingData = {
                userId: currentUser.id,
                flightId: parseInt(flightId),
                priorityBoarding: bookingDetails.priorityBoarding,
                checkedBaggage: bookingDetails.checkedBaggage,
                baggageCount: bookingDetails.baggageCount
            };

            const response = await createBooking(bookingData);
            navigate(`/bookings/${response.data.id}`);
        } catch (error) {
            console.error('Error creating booking:', error);
            setError('Failed to create booking. Please try again.');
        } finally {
            setSubmitting(false);
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
        <div className="booking-form-container">
            <h2>Book Your Flight</h2>

            <div className="flight-summary">
                <h3>Flight Details</h3>
                <div className="summary-details">
                    <p><strong>Route:</strong> {flight.departureAirport} → {flight.arrivalAirport}</p>
                    <p><strong>Date:</strong> {new Date(flight.departureTime).toLocaleDateString()}</p>
                    <p><strong>Time:</strong> {new Date(flight.departureTime).toLocaleTimeString()}</p>
                    <p><strong>Base Price:</strong> {flight.currentPrice} €</p>
                </div>
            </div>

            <form onSubmit={handleSubmit} className="booking-options-form">
                <h3>Additional Options</h3>

                <div className="option-group">
                    <div className="checkbox-option">
                        <input
                            type="checkbox"
                            id="priorityBoarding"
                            name="priorityBoarding"
                            checked={bookingDetails.priorityBoarding}
                            onChange={handleCheckboxChange}
                        />
                        <label htmlFor="priorityBoarding">
                            Priority Boarding (+{priorityBoardingCost} €)
                        </label>
                    </div>
                    <p className="option-description">
                        Get early access to the cabin and secure overhead storage for your carry-on luggage.
                    </p>
                </div>

                <div className="option-group">
                    <div className="checkbox-option">
                        <input
                            type="checkbox"
                            id="checkedBaggage"
                            name="checkedBaggage"
                            checked={bookingDetails.checkedBaggage}
                            onChange={handleCheckboxChange}
                        />
                        <label htmlFor="checkedBaggage">
                            Checked Baggage (First bag: +{checkedBaggageCost} €)
                        </label>
                    </div>
                    <p className="option-description">
                        Add checked baggage to your booking (up to 23kg per bag).
                    </p>

                    {bookingDetails.checkedBaggage && (
                        <div className="baggage-count-selector">
                            <label htmlFor="baggageCount">Number of bags:</label>
                            <select
                                id="baggageCount"
                                name="baggageCount"
                                value={bookingDetails.baggageCount}
                                onChange={handleBaggageCountChange}
                            >
                                <option value="0">Select number of bags</option>
                                <option value="1">1 bag (23kg) - {checkedBaggageCost} €</option>
                                <option value="2">2 bags (23kg each) - {checkedBaggageCost + additionalBaggageCost} €</option>
                                <option value="3">3 bags (23kg each) - {checkedBaggageCost + (2 * additionalBaggageCost)} €</option>
                            </select>
                        </div>
                    )}
                </div>

                <div className="booking-total">
                    <h3>Total Price</h3>
                    <div className="total-price">{calculateTotalPrice()} €</div>
                </div>

                <div className="booking-actions">
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={() => navigate(`/flights/${flightId}`)}
                    >
                        Back
                    </button>
                    <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={submitting || (bookingDetails.checkedBaggage && bookingDetails.baggageCount === 0)}
                    >
                        {submitting ? 'Processing...' : 'Confirm Booking'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default BookingForm;