import api from './api';

export const getAllBookings = () => {
    return api.get('/api/bookings');
};

export const getBookingById = (id) => {
    return api.get(`/api/bookings/${id}/with-flight`);
};

export const getBookingByReference = (reference) => {
    return api.get(`/api/bookings/reference/${reference}`);
};

export const getUserBookings = (userId) => {
    return api.get(`/api/bookings/user/${userId}`);
};

export const getUserBookingsByStatus = (userId, status) => {
    return api.get(`/api/bookings/user/${userId}/status/${status}`);
};

export const createBooking = (bookingData) => {
    return api.post('/api/bookings', bookingData);
};

export const confirmBooking = (id) => {
    return api.patch(`/api/bookings/${id}/confirm`);
};

export const payForBooking = (id) => {
    return api.patch(`/api/bookings/${id}/pay`);
};

export const cancelBooking = (id) => {
    return api.patch(`/api/bookings/${id}/cancel`);
};

export const requestRefund = (id) => {
    return api.patch(`/api/bookings/${id}/refund`);
};