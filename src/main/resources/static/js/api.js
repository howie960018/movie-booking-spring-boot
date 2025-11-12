// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// API Helper Functions
const api = {
    // Get JWT token from localStorage
    getToken() {
        return localStorage.getItem('token');
    },

    // Set JWT token to localStorage
    setToken(token) {
        localStorage.setItem('token', token);
    },

    // Remove JWT token
    removeToken() {
        localStorage.removeItem('token');
    },

    // Get headers with authentication
    getHeaders(includeAuth = true) {
        const headers = {
            'Content-Type': 'application/json'
        };
        if (includeAuth) {
            const token = this.getToken();
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }
        }
        return headers;
    },

    // Generic fetch wrapper
    async request(url, options = {}) {
        try {
            const response = await fetch(`${API_BASE_URL}${url}`, {
                ...options,
                headers: {
                    ...this.getHeaders(options.auth !== false),
                    ...options.headers
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    this.removeToken();
                    window.location.reload();
                }
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            return await response.text();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    },

    // Auth endpoints
    async login(email, password) {
        const data = await this.request('/auth/login', {
            method: 'POST',
            auth: false,
            body: JSON.stringify({ email, password })
        });
        if (data.token) {
            this.setToken(data.token);
        }
        return data;
    },

    async register(userData) {
        return await this.request('/users/register', {
            method: 'POST',
            auth: false,
            body: JSON.stringify(userData)
        });
    },

    async logout() {
        try {
            await this.request('/auth/logout', { method: 'POST' });
        } finally {
            this.removeToken();
        }
    },

    async getUserProfile() {
        return await this.request('/users/profile');
    },

    // Movie endpoints
    async getAllMovies() {
        return await this.request('/movies', { auth: false });
    },

    async getMovieById(id) {
        return await this.request(`/movies/${id}`, { auth: false });
    },

    async createMovie(movieData) {
        return await this.request('/movies', {
            method: 'POST',
            body: JSON.stringify(movieData)
        });
    },

    // Screening endpoints
    async getAllScreenings() {
        return await this.request('/screenings', { auth: false });
    },

    async getScreeningById(id) {
        return await this.request(`/screenings/${id}`, { auth: false });
    },

    async getScreeningsByMovieId(movieId) {
        return await this.request(`/screenings/movie/${movieId}`, { auth: false });
    },

    async getAvailableSeats(screeningId) {
        return await this.request(`/screenings/${screeningId}/available-seats`, { auth: false });
    },

    async createScreening(screeningData) {
        return await this.request('/screenings', {
            method: 'POST',
            body: JSON.stringify(screeningData)
        });
    },

    async deleteScreening(id) {
        return await this.request(`/screenings/${id}`, {
            method: 'DELETE'
        });
    },

    // Booking endpoints
    async createBooking(bookingData) {
        return await this.request('/bookings/create', {
            method: 'POST',
            body: JSON.stringify(bookingData)
        });
    },

    async getAllBookings() {
        return await this.request('/bookings');
    },

    async getBookingById(id) {
        return await this.request(`/bookings/${id}`);
    },

    async getBookingsByUserId(userId) {
        return await this.request(`/bookings/user/${userId}`);
    },

    async getBookingsByScreeningId(screeningId) {
        return await this.request(`/bookings/screening/${screeningId}`);
    },

    async cancelBooking(id) {
        return await this.request(`/bookings/${id}`, {
            method: 'DELETE'
        });
    },

    async getMyBookings() {
        return await this.request('/bookings/me');
    },

    // Payment endpoints
    async confirmPayment(bookingId) {
        return await this.request(`/payments/confirm/${bookingId}`, {
            method: 'POST'
        });
    },

    // Admin endpoints
    async getAllUsers() {
        return await this.request('/users/admin/all');
    }
};

// Utility functions
const utils = {
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-TW', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

    formatDateTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('zh-TW', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    formatTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleTimeString('zh-TW', {
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    formatDuration(minutes) {
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;
        return `${hours}小時${mins}分鐘`;
    },

    showAlert(message, type = 'success') {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type}`;
        alert.textContent = message;
        alert.style.position = 'fixed';
        alert.style.top = '20px';
        alert.style.right = '20px';
        alert.style.zIndex = '10000';
        alert.style.minWidth = '300px';
        alert.style.animation = 'slideIn 0.3s';

        document.body.appendChild(alert);

        setTimeout(() => {
            alert.style.animation = 'fadeOut 0.3s';
            setTimeout(() => alert.remove(), 300);
        }, 3000);
    },

    showLoading(element) {
        element.innerHTML = '<div class="loading"><div class="spinner"></div></div>';
    },

    showEmpty(element, message, icon = 'fa-inbox') {
        element.innerHTML = `
            <div class="empty-state">
                <i class="fas ${icon}"></i>
                <h3>${message}</h3>
            </div>
        `;
    }
};
