const form = document.getElementById('loginForm');
const errorBox = document.getElementById('error');
const submitBtn = document.getElementById('submitBtn');

renderLangTabs();

if (getToken()) {
    goHome(localStorage.getItem('role'));
}

document.getElementById('togglePassword').addEventListener('click', (event) => {
    const input = document.getElementById('password');
    const show = input.type === 'password';
    input.type = show ? 'text' : 'password';
    event.currentTarget.classList.toggle('active', show);
});

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    errorBox.textContent = '';

    const login = document.getElementById('login').value.trim();
    const password = document.getElementById('password').value;

    if (!login || !password) {
        errorBox.textContent = t('errFillLogin');
        return;
    }

    submitBtn.disabled = true;
    try {
        const data = await api('/api/auth/login', {
            method: 'POST',
            body: { login, password }
        });
        saveAuth(data);
        goHome(data.role);
    } catch (e) {
        errorBox.textContent = e.message;
    } finally {
        submitBtn.disabled = false;
    }
});

function goHome(role) {
    window.location.href = role === 'ADMIN' ? 'admin.html' : 'tests.html';
}