// Movies Module
const movies = {
    allMovies: [],
    currentMovie: null,

    async loadMovies() {
        const moviesList = document.getElementById('moviesList');
        utils.showLoading(moviesList);

        try {
            this.allMovies = await api.getAllMovies();
            this.renderMovies();
        } catch (error) {
            console.error('Failed to load movies:', error);
            utils.showEmpty(moviesList, '無法載入電影資料', 'fa-exclamation-circle');
        }
    },

    renderMovies() {
        const moviesList = document.getElementById('moviesList');

        if (this.allMovies.length === 0) {
            utils.showEmpty(moviesList, '目前沒有上映中的電影', 'fa-film');
            return;
        }

        moviesList.innerHTML = this.allMovies.map(movie => `
            <div class="movie-card" onclick="movies.showMovieDetail(${movie.id})">
                <div class="movie-poster">
                    <i class="fas fa-film"></i>
                </div>
                <div class="movie-info">
                    <h3 class="movie-title">${movie.title}</h3>
                    <div class="movie-meta">
                        <span><i class="far fa-calendar"></i> ${utils.formatDate(movie.releaseDate)}</span>
                        <span><i class="far fa-clock"></i> ${utils.formatDuration(movie.durationMinutes)}</span>
                    </div>
                    <p class="movie-description">${movie.description || '暫無簡介'}</p>
                </div>
            </div>
        `).join('');
    },

    async showMovieDetail(movieId) {
        try {
            this.currentMovie = await api.getMovieById(movieId);
            const screenings = await api.getScreeningsByMovieId(movieId);

            this.renderMovieDetail(screenings);
            navigateToPage('movieDetail');
        } catch (error) {
            console.error('Failed to load movie detail:', error);
            utils.showAlert('無法載入電影詳情', 'error');
        }
    },

    renderMovieDetail(screenings) {
        const movieDetail = document.getElementById('movieDetail');
        const movie = this.currentMovie;

        movieDetail.innerHTML = `
            <div class="movie-detail-header">
                <div class="movie-detail-poster">
                    <i class="fas fa-film"></i>
                </div>
                <div class="movie-detail-info">
                    <h1>${movie.title}</h1>
                    <div class="movie-detail-meta">
                        <span><i class="far fa-calendar"></i> ${utils.formatDate(movie.releaseDate)}</span>
                        <span><i class="far fa-clock"></i> ${utils.formatDuration(movie.durationMinutes)}</span>
                    </div>
                    <p class="movie-detail-description">${movie.description || '暫無詳細簡介'}</p>
                </div>
            </div>
            
            <div class="screenings-section">
                <h2>場次時刻表</h2>
                ${screenings.length > 0 ? `
                    <div class="screenings-grid">
                        ${screenings.map(screening => {
                            const availableSeats = screening.seats ? 
                                screening.seats.filter(s => !s.isBooked).length : 0;
                            return `
                                <div class="screening-card" onclick="bookings.startBooking(${screening.id})">
                                    <div class="screening-time">${utils.formatTime(screening.screeningTime)}</div>
                                    <div class="screening-date">${utils.formatDate(screening.screeningTime)}</div>
                                    <div class="screening-seats">
                                        <i class="fas fa-chair"></i> ${availableSeats} 個座位可選
                                    </div>
                                </div>
                            `;
                        }).join('')}
                    </div>
                ` : `
                    <div class="empty-state">
                        <i class="fas fa-calendar-times"></i>
                        <h3>目前沒有可預訂的場次</h3>
                    </div>
                `}
            </div>
        `;
    }
};

// Back to Movies Button
document.getElementById('backToMovies').addEventListener('click', () => {
    navigateToPage('movies');
});

