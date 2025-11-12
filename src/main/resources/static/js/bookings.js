// Bookings Module
const bookings = {
    currentScreening: null,
    selectedSeats: [],
    allSeats: [],

    async startBooking(screeningId) {
        if (!auth.requireAuth()) return;

        try {
            this.currentScreening = await api.getScreeningById(screeningId);
            this.allSeats = await api.getAvailableSeats(screeningId);
            this.selectedSeats = [];

            this.renderSeatSelection();
            openBookingModal();
        } catch (error) {
            console.error('Failed to start booking:', error);
            utils.showAlert('無法載入座位資訊', 'error');
        }
    },

    renderSeatSelection() {
        const bookingContent = document.getElementById('bookingContent');
        const movie = movies.currentMovie;
        const screening = this.currentScreening;

        // Group seats by row
        const seatsByRow = {};
        this.allSeats.forEach(seat => {
            const row = seat.seatNumber.charAt(0);
            if (!seatsByRow[row]) {
                seatsByRow[row] = [];
            }
            seatsByRow[row].push(seat);
        });

        // Sort rows and seats
        const rows = Object.keys(seatsByRow).sort();
        rows.forEach(row => {
            seatsByRow[row].sort((a, b) =>
                a.seatNumber.localeCompare(b.seatNumber)
            );
        });

        bookingContent.innerHTML = `
            <div class="booking-info" style="margin-bottom: 2rem;">
                <h3>${movie.title}</h3>
                <p style="color: var(--text-secondary);">
                    ${utils.formatDateTime(screening.screeningTime)}
                </p>
            </div>

            <div class="seat-selection">
                <div class="screen">
                    <i class="fas fa-tv"></i> 銀幕
                </div>

                <div class="seats-container">
                    ${rows.map(row => `
                        <div class="seat-row" style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem;">
                            <div style="width: 30px; text-align: center; color: var(--text-secondary);">${row}</div>
                            <div style="display: flex; gap: 0.5rem; flex: 1; justify-content: center;">
                                ${seatsByRow[row].map(seat => `
                                    <div class="seat ${seat.isBooked ? 'occupied' : ''}" 
                                         data-seat-id="${seat.id}"
                                         data-seat-number="${seat.seatNumber}"
                                         onclick="bookings.toggleSeat(${seat.id}, '${seat.seatNumber}', ${seat.isBooked})">
                                        ${seat.seatNumber.substring(1)}
                                    </div>
                                `).join('')}
                            </div>
                        </div>
                    `).join('')}
                </div>

                <div class="seat-legend">
                    <div class="legend-item">
                        <div class="legend-box available"></div>
                        <span>可選</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box selected"></div>
                        <span>已選</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box occupied"></div>
                        <span>已售出</span>
                    </div>
                </div>

                <div class="booking-summary">
                    <h3>訂票摘要</h3>
                    <div class="summary-item">
                        <span>已選座位：</span>
                        <span id="selectedSeatsDisplay">未選擇</span>
                    </div>
                    <div class="summary-item">
                        <span>票數：</span>
                        <span id="ticketCount">0</span>
                    </div>
                    <div class="summary-item summary-total">
                        <span>總金額：</span>
                        <span id="totalPrice">NT$ 0</span>
                    </div>
                    <button class="btn btn-primary btn-block" onclick="bookings.confirmBooking()" 
                            id="confirmBookingBtn" disabled>
                        確認訂票
                    </button>
                </div>
            </div>
        `;
    },

    toggleSeat(seatId, seatNumber, isBooked) {
        if (isBooked) return;

        const seatElement = document.querySelector(`[data-seat-id="${seatId}"]`);
        const index = this.selectedSeats.findIndex(s => s.id === seatId);

        if (index > -1) {
            // Deselect
            this.selectedSeats.splice(index, 1);
            seatElement.classList.remove('selected');
        } else {
            // Select (max 10 seats)
            if (this.selectedSeats.length >= 10) {
                utils.showAlert('最多只能選擇 10 個座位', 'warning');
                return;
            }
            this.selectedSeats.push({ id: seatId, seatNumber });
            seatElement.classList.add('selected');
        }

        this.updateBookingSummary();
    },

    updateBookingSummary() {
        const selectedSeatsDisplay = document.getElementById('selectedSeatsDisplay');
        const ticketCount = document.getElementById('ticketCount');
        const totalPrice = document.getElementById('totalPrice');
        const confirmBtn = document.getElementById('confirmBookingBtn');

        const count = this.selectedSeats.length;
        const price = count * 300; // Assuming 300 NT$ per ticket

        selectedSeatsDisplay.textContent = count > 0
            ? this.selectedSeats.map(s => s.seatNumber).join(', ')
            : '未選擇';
        ticketCount.textContent = count;
        totalPrice.textContent = `NT$ ${price.toLocaleString()}`;

        confirmBtn.disabled = count === 0;
    },

    async confirmBooking() {
        if (this.selectedSeats.length === 0) return;

        const bookingData = {
            screeningId: this.currentScreening.id,
            seatNumbers: this.selectedSeats.map(s => s.seatNumber)
        };

        try {
            const result = await api.createBooking(bookingData);
            // 顯示假付款流程
            await this.showPaymentDialog(result);
        } catch (error) {
            console.error('Booking failed:', error);
            utils.showAlert('訂票失敗：' + error.message, 'error');
        }
    },

    async showPaymentDialog(bookingResponse) {
        // 建立簡單的付款 UI（模態內）
        const bookingContent = document.getElementById('bookingContent');
        bookingContent.innerHTML = `
            <div class="booking-summary">
                <h3>付款資訊</h3>
                <div class="summary-item">
                    <span>電影：</span>
                    <span>${bookingResponse.movieTitle}</span>
                </div>
                <div class="summary-item">
                    <span>場次時間：</span>
                    <span>${utils.formatDateTime(bookingResponse.screeningTime)}</span>
                </div>
                <div class="summary-item">
                    <span>座位：</span>
                    <span>${bookingResponse.seatNumbers.join(', ')}</span>
                </div>
                <div class="summary-item summary-total">
                    <span>應付金額：</span>
                    <span>NT$ ${(bookingResponse.totalSeats * 300).toLocaleString()}</span>
                </div>
                <div style="display:flex; gap:1rem; margin-top:1rem;">
                    <button class="btn btn-primary" id="payNowBtn">立即付款</button>
                    <button class="btn btn-outline" id="cancelPaymentBtn">取消</button>
                </div>
            </div>
        `;

        document.getElementById('payNowBtn').onclick = async () => {
            try {
                await api.confirmPayment(bookingResponse.bookingId);
                utils.showAlert('付款成功，訂票已確認！', 'success');
                closeBookingModal();
                if (document.getElementById('myBookingsPage').classList.contains('active')) {
                    this.loadUserBookings();
                }
            } catch (e) {
                utils.showAlert('付款失敗：' + e.message, 'error');
            }
        };
        document.getElementById('cancelPaymentBtn').onclick = () => {
            utils.showAlert('已取消付款，訂單仍為待付款(PENDING)。', 'warning');
            closeBookingModal();
        };
    },

    async loadUserBookings() {
        if (!auth.isLoggedIn()) {
            navigateToPage('movies');
            utils.showAlert('請先登入', 'warning');
            return;
        }

        const bookingsList = document.getElementById('bookingsList');
        utils.showLoading(bookingsList);

        try {
            const userBookings = await api.getMyBookings();
            this.renderUserBookings(userBookings);
        } catch (error) {
            console.error('Failed to load bookings:', error);
            utils.showEmpty(bookingsList, '無法載入訂票記錄', 'fa-exclamation-circle');
        }
    },

    renderUserBookings(userBookings) {
        const bookingsList = document.getElementById('bookingsList');

        if (userBookings.length === 0) {
            utils.showEmpty(bookingsList, '您還沒有任何訂票記錄', 'fa-ticket-alt');
            return;
        }

        bookingsList.innerHTML = userBookings.map(booking => `
            <div class="booking-card">
                <div class="booking-header">
                    <div>
                        <h3 class="booking-movie-title">${booking.movieTitle}</h3>
                        <p style="color: var(--text-secondary); margin-top: 0.5rem;">
                            訂單編號：#${booking.bookingId}
                        </p>
                    </div>
                    <span class="booking-status ${booking.status.toLowerCase()}">
                        ${booking.status === 'CONFIRMED' ? '已確認' : booking.status === 'PENDING' ? '待付款' : '已取消'}
                    </span>
                </div>
                <div class="booking-details">
                    <div class="booking-detail-item">
                        <div class="booking-detail-label">場次時間</div>
                        <div class="booking-detail-value">
                            ${utils.formatDateTime(booking.screeningTime)}
                        </div>
                    </div>
                    <div class="booking-detail-item">
                        <div class="booking-detail-label">座位</div>
                        <div class="booking-detail-value">
                            ${booking.seatNumbers.join(', ')}
                        </div>
                    </div>
                    <div class="booking-detail-item">
                        <div class="booking-detail-label">票數</div>
                        <div class="booking-detail-value">
                            ${booking.seatNumbers.length} 張
                        </div>
                    </div>
                    <div class="booking-detail-item">
                        <div class="booking-detail-label">訂票時間</div>
                        <div class="booking-detail-value">
                            ${utils.formatDateTime(booking.bookingTime)}
                        </div>
                    </div>
                </div>
                ${booking.status === 'CONFIRMED' ? `
                    <div class="booking-actions">
                        <button class="btn btn-danger" onclick="bookings.cancelBooking(${booking.bookingId})">
                            取消訂票
                        </button>
                    </div>
                ` : ''}
            </div>
        `).join('');
    },

    async cancelBooking(bookingId) {
        if (!confirm('確定要取消此訂票嗎？')) return;

        try {
            await api.cancelBooking(bookingId);
            utils.showAlert('已取消訂票', 'success');
            this.loadUserBookings();
        } catch (error) {
            console.error('Failed to cancel booking:', error);
            utils.showAlert('取消訂票失敗：' + error.message, 'error');
        }
    }
};

// Booking Modal Management
function openBookingModal() {
    const modal = document.getElementById('bookingModal');
    modal.classList.add('show');
}

function closeBookingModal() {
    const modal = document.getElementById('bookingModal');
    modal.classList.remove('show');
    bookings.selectedSeats = [];
}
