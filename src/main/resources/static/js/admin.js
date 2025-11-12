// Admin Module
const admin = {
    currentTab: 'movies',

    async init() {
        if (!auth.isLoggedIn()) {
            utils.showAlert('請先登入', 'warning');
            openLoginModal();
            return;
        }
        if (auth.currentUser.role !== 'ADMIN') {
            utils.showAlert('您沒有權限訪問管理後台', 'error');
            navigateToPage('movies');
            return;
        }

        this.loadTab('movies');
    },

    async loadTab(tab) {
        this.currentTab = tab;

        // Update tab buttons
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.tab === tab) {
                btn.classList.add('active');
            }
        });

        const adminContent = document.getElementById('adminContent');
        utils.showLoading(adminContent);

        switch (tab) {
            case 'movies':
                await this.loadMoviesAdmin();
                break;
            case 'screenings':
                await this.loadScreeningsAdmin();
                break;
            case 'bookings':
                await this.loadBookingsAdmin();
                break;
        }
    },

    async loadMoviesAdmin() {
        const adminContent = document.getElementById('adminContent');

        try {
            const movies = await api.getAllMovies();

            adminContent.innerHTML = `
                <div class="admin-section active">
                    <div class="admin-actions">
                        <button class="btn btn-primary" onclick="admin.showAddMovieForm()">
                            <i class="fas fa-plus"></i> 新增電影
                        </button>
                    </div>
                    
                    <div class="data-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>電影名稱</th>
                                    <th>上映日期</th>
                                    <th>片長</th>
                                    <th>簡介</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${movies.map(movie => `
                                    <tr>
                                        <td>${movie.id}</td>
                                        <td>${movie.title}</td>
                                        <td>${utils.formatDate(movie.releaseDate)}</td>
                                        <td>${utils.formatDuration(movie.durationMinutes)}</td>
                                        <td>${movie.description ? movie.description.substring(0, 50) + '...' : '無'}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Failed to load movies:', error);
            adminContent.innerHTML = '<p>載入失敗</p>';
        }
    },

    showAddMovieForm() {
        const adminContent = document.getElementById('adminContent');

        adminContent.innerHTML = `
            <div class="admin-section active">
                <button class="btn btn-text" onclick="admin.loadTab('movies')">
                    <i class="fas fa-arrow-left"></i> 返回列表
                </button>
                
                <h2 style="margin: 2rem 0;">新增電影</h2>
                
                <form id="addMovieForm" style="max-width: 600px;">
                    <div class="form-group">
                        <label>電影名稱</label>
                        <input type="text" id="movieTitle" required>
                    </div>
                    <div class="form-group">
                        <label>上映日期</label>
                        <input type="date" id="movieReleaseDate" required>
                    </div>
                    <div class="form-group">
                        <label>片長（分鐘）</label>
                        <input type="number" id="movieDuration" required>
                    </div>
                    <div class="form-group">
                        <label>簡介</label>
                        <textarea id="movieDescription" rows="5"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">新增電影</button>
                </form>
            </div>
        `;

        document.getElementById('addMovieForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.addMovie();
        });
    },

    async addMovie() {
        const movieData = {
            title: document.getElementById('movieTitle').value,
            releaseDate: document.getElementById('movieReleaseDate').value,
            durationMinutes: parseInt(document.getElementById('movieDuration').value),
            description: document.getElementById('movieDescription').value
        };

        try {
            await api.createMovie(movieData);
            utils.showAlert('電影新增成功！', 'success');
            this.loadTab('movies');
        } catch (error) {
            console.error('Failed to add movie:', error);
            utils.showAlert('新增失敗：' + error.message, 'error');
        }
    },

    async loadScreeningsAdmin() {
        const adminContent = document.getElementById('adminContent');

        try {
            const screenings = await api.getAllScreenings();

            adminContent.innerHTML = `
                <div class="admin-section active">
                    <div class="admin-actions">
                        <button class="btn btn-primary" onclick="admin.showAddScreeningForm()">
                            <i class="fas fa-plus"></i> 新增場次
                        </button>
                    </div>
                    
                    <div class="data-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>電影</th>
                                    <th>場次時間</th>
                                    <th>總座位數</th>
                                    <th>可用座位</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${screenings.map(screening => {
                                    const availableSeats = screening.seats ? 
                                        screening.seats.filter(s => !s.isBooked).length : 0;
                                    const totalSeats = screening.seats ? screening.seats.length : 0;
                                    return `
                                        <tr>
                                            <td>${screening.id}</td>
                                            <td>${screening.movie.title}</td>
                                            <td>${utils.formatDateTime(screening.screeningTime)}</td>
                                            <td>${totalSeats}</td>
                                            <td>${availableSeats}</td>
                                            <td>
                                                <button class="btn btn-danger" 
                                                        onclick="admin.deleteScreening(${screening.id})"
                                                        style="padding: 0.4rem 1rem; font-size: 0.9rem;">
                                                    刪除
                                                </button>
                                            </td>
                                        </tr>
                                    `;
                                }).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Failed to load screenings:', error);
            adminContent.innerHTML = '<p>載入失敗</p>';
        }
    },

    async showAddScreeningForm() {
        const adminContent = document.getElementById('adminContent');

        try {
            const movies = await api.getAllMovies();

            adminContent.innerHTML = `
                <div class="admin-section active">
                    <button class="btn btn-text" onclick="admin.loadTab('screenings')">
                        <i class="fas fa-arrow-left"></i> 返回列表
                    </button>
                    
                    <h2 style="margin: 2rem 0;">新增場次</h2>
                    
                    <form id="addScreeningForm" style="max-width: 600px;">
                        <div class="form-group">
                            <label>選擇電影</label>
                            <select id="screeningMovieId" required>
                                <option value="">請選擇電影</option>
                                ${movies.map(movie => `
                                    <option value="${movie.id}">${movie.title}</option>
                                `).join('')}
                            </select>
                        </div>
                        <div class="form-group">
                            <label>場次時間</label>
                            <input type="datetime-local" id="screeningTime" required>
                        </div>
                        <div class="form-group">
                            <label>座位數</label>
                            <input type="number" id="numberOfSeats" value="100" required>
                        </div>
                        <button type="submit" class="btn btn-primary">新增場次</button>
                    </form>
                </div>
            `;

            document.getElementById('addScreeningForm').addEventListener('submit', async (e) => {
                e.preventDefault();
                await this.addScreening();
            });
        } catch (error) {
            console.error('Failed to load movies for screening form:', error);
        }
    },

    async addScreening() {
        const screeningData = {
            movieId: parseInt(document.getElementById('screeningMovieId').value),
            screeningTime: new Date(document.getElementById('screeningTime').value).toISOString(),
            numberOfSeats: parseInt(document.getElementById('numberOfSeats').value)
        };

        try {
            await api.createScreening(screeningData);
            utils.showAlert('場次新增成功！', 'success');
            this.loadTab('screenings');
        } catch (error) {
            console.error('Failed to add screening:', error);
            utils.showAlert('新增失敗：' + error.message, 'error');
        }
    },

    async deleteScreening(id) {
        if (!confirm('確定要刪除此場次嗎？')) return;

        try {
            await api.deleteScreening(id);
            utils.showAlert('場次已刪除', 'success');
            this.loadTab('screenings');
        } catch (error) {
            console.error('Failed to delete screening:', error);
            utils.showAlert('刪除失敗：' + error.message, 'error');
        }
    },

    async loadBookingsAdmin() {
        const adminContent = document.getElementById('adminContent');

        try {
            const allBookings = await api.getAllBookings();

            adminContent.innerHTML = `
                <div class="admin-section active">
                    <h2 style="margin-bottom: 1.5rem;">所有訂單</h2>
                    
                    <div class="data-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>訂單ID</th>
                                    <th>用戶Email</th>
                                    <th>電影</th>
                                    <th>場次時間</th>
                                    <th>座位</th>
                                    <th>訂票時間</th>
                                    <th>狀態</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${allBookings.map(booking => `
                                    <tr>
                                        <td>#${booking.bookingId}</td>
                                        <td>${booking.userEmail}</td>
                                        <td>${booking.movieTitle}</td>
                                        <td>${utils.formatDateTime(booking.screeningTime)}</td>
                                        <td>${booking.seatNumbers.join(', ')}</td>
                                        <td>${utils.formatDateTime(booking.bookingTime)}</td>
                                        <td>
                                            <span class="booking-status ${booking.status.toLowerCase()}">
                                                ${booking.status === 'CONFIRMED' ? '已確認' : booking.status === 'PENDING' ? '待付款' : '已取消'}
                                            </span>
                                        </td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Failed to load bookings:', error);
            adminContent.innerHTML = '<p>載入失敗</p>';
        }
    }
};

// Tab button click handlers
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        admin.loadTab(btn.dataset.tab);
    });
});
