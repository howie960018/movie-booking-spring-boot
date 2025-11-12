// Authentication Module
const auth = {
    currentUser: null,

    async init() {
        const token = api.getToken();
        if (token) {
            try {
                this.currentUser = await api.getUserProfile();
                this.updateUI();
            } catch (error) {
                console.error('Failed to load user profile:', error);
                api.removeToken();
                this.updateUI();
            }
        } else {
            this.updateUI();
        }
    },

    updateUI() {
        const userSection = document.getElementById('userSection');
        const authSection = document.getElementById('authSection');
        const userName = document.getElementById('userName');
        const adminMenuItem = document.getElementById('adminMenuItem');

        if (this.currentUser) {
            userSection.style.display = 'flex';
            authSection.style.display = 'none';
            userName.textContent = `${this.currentUser.firstName} ${this.currentUser.lastName}`;

            if (this.currentUser.role === 'ADMIN') {
                adminMenuItem.style.display = 'block';
            } else {
                adminMenuItem.style.display = 'none';
                // 如果目前停留在 admin 頁，導回電影頁
                const adminPage = document.getElementById('adminPage');
                if (adminPage && adminPage.classList.contains('active')) {
                    utils.showAlert('您沒有權限訪問管理後台', 'error');
                    navigateToPage('movies');
                }
            }
        } else {
            userSection.style.display = 'none';
            authSection.style.display = 'flex';
            adminMenuItem.style.display = 'none';
        }
    },

    isLoggedIn() {
        return this.currentUser !== null;
    },

    requireAuth() {
        if (!this.isLoggedIn()) {
            utils.showAlert('請先登入', 'warning');
            openLoginModal();
            return false;
        }
        return true;
    }
};

// Modal Management
function openLoginModal() {
    const modal = document.getElementById('loginModal');
    modal.classList.add('show');
}

function closeLoginModal() {
    const modal = document.getElementById('loginModal');
    modal.classList.remove('show');
    document.getElementById('loginForm').reset();
}

function openRegisterModal() {
    const modal = document.getElementById('registerModal');
    modal.classList.add('show');
}

function closeRegisterModal() {
    const modal = document.getElementById('registerModal');
    modal.classList.remove('show');
    document.getElementById('registerForm').reset();
}

// Login Form Handler
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    try {
        await api.login(email, password);
        auth.currentUser = await api.getUserProfile();
        auth.updateUI();
        closeLoginModal();
        utils.showAlert('登入成功！', 'success');
    } catch (error) {
        utils.showAlert('登入失敗：' + error.message, 'error');
    }
});

// Register Form Handler
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const userData = {
        firstName: document.getElementById('registerFirstName').value,
        lastName: document.getElementById('registerLastName').value,
        email: document.getElementById('registerEmail').value,
        phoneNumber: document.getElementById('registerPhone').value,
        password: document.getElementById('registerPassword').value
    };

    try {
        await api.register(userData);
        utils.showAlert('註冊成功！請登入', 'success');
        closeRegisterModal();
        openLoginModal();
    } catch (error) {
        utils.showAlert('註冊失敗：' + error.message, 'error');
    }
});

// Logout Handler
document.getElementById('logoutBtn').addEventListener('click', async () => {
    try {
        await api.logout();
        auth.currentUser = null;
        auth.updateUI();
        utils.showAlert('已登出', 'success');
        // Navigate to movies page
        navigateToPage('movies');
    } catch (error) {
        utils.showAlert('登出失敗：' + error.message, 'error');
    }
});

// Modal Switch Links
document.getElementById('switchToRegister').addEventListener('click', (e) => {
    e.preventDefault();
    closeLoginModal();
    openRegisterModal();
});

document.getElementById('switchToLogin').addEventListener('click', (e) => {
    e.preventDefault();
    closeRegisterModal();
    openLoginModal();
});

// Modal Button Handlers
document.getElementById('loginBtn').addEventListener('click', openLoginModal);
document.getElementById('registerBtn').addEventListener('click', openRegisterModal);

// Close modal when clicking on X or outside
document.querySelectorAll('.modal .close').forEach(closeBtn => {
    closeBtn.addEventListener('click', function() {
        this.closest('.modal').classList.remove('show');
    });
});

document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', function(e) {
        if (e.target === this) {
            this.classList.remove('show');
        }
    });
});
