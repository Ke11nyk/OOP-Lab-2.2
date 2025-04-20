import api from './api';

export const getAllFlights = () => {
    return api.get('/api/flights');
};

export const getFlightById = (id) => {
    return api.get(`/api/flights/${id}`);
};

export const searchFlights = (departure, arrival, startDate, endDate) => {
    return api.get('/api/flights/search', {
        params: { departure, arrival, startDate, endDate }
    });
};

export const getFlightPriceHistory = (flightId) => {
    return api.get(`/api/price-history/flight/${flightId}`);
};

export const getLatestPriceChange = (flightId) => {
    return api.get(`/api/price-history/flight/${flightId}/latest`);
};