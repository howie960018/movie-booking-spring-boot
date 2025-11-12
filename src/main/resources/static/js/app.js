// Main Application
let currentPage = 'movies';

// Page Navigation
function navigateToPage(pageName) {
    // Hide all pages
    document.querySelectorAll('.page').forEach(page => {
        page.classList.remove('active');
    });

    const targetId = pageName === 'my-bookings' ? 'myBookingsPage' : `${pageName}Page`;
    const targetPage = document.getElementById(targetId);
    if (targetPage) {
        targetPage.classList.add('active');
        currentPage = pageName;
    }

    // Update nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
        if (link.dataset.page === pageName) {
            link.classList.add('active');
        }
    });

    // Load page content
    switch (pageName) {
        case 'movies':
            movies.loadMovies();
            break;
        case 'my-bookings':
            bookings.loadUserBookings();
            break;
        case 'admin':
            admin.init();
            break;
    }
}

// Nav link click handlers
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const page = link.dataset.page;

        // Check auth for my-bookings and admin pages
        if (page === 'my-bookings' || page === 'admin') {
            if (!auth.isLoggedIn()) {
                utils.showAlert('請先登入', 'warning');
                openLoginModal();
                return;
            }

            if (page === 'admin' && auth.currentUser.role !== 'ADMIN') {
                utils.showAlert('您沒有權限訪問管理後台', 'error');
                return;
            }
        }

        navigateToPage(page);
    });
});

// Initialize Application
async function initApp() {
    try {
        // Initialize authentication
        await auth.init();

        // Load initial page
        navigateToPage('movies');

        console.log('Application initialized successfully');
    } catch (error) {
        console.error('Failed to initialize application:', error);
        utils.showAlert('應用程式初始化失敗', 'error');
    }
}

// Start the application when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}
