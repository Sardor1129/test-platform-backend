const step1 = document.getElementById('step1');
const step2 = document.getElementById('step2');
const error1 = document.getElementById('error1');
const error2 = document.getElementById('error2');
const resendBtn = document.getElementById('resendBtn');

let email = '';
let timerId = null;
let secondsLeft = 0;

if (getToken()) {
    window.location.href = localStorage.getItem('role') === 'ADMIN' ? 'admin.html' : 'tests.html';
}

renderLangTabs(() => {
    drawHint();
    drawResendText();
});

document.querySelectorAll('.eye').forEach(btn => {
    btn.addEventListener('click', () => {
        const input = document.getElementById(btn.dataset.target);
        const show = input.type === 'password';
        input.type = show ? 'text' : 'password';
        btn.classList.toggle('active', show);
    });
});

function drawHint() {
    if (email) {
        document.getElementById('phoneHint').textContent = t('codeHint', email);
    }
}

function drawResendText() {
    resendBtn.textContent = secondsLeft > 0 ? t('resendWait', secondsLeft) : t('resend');
}

// ---------- 1-bosqich ----------

step1.addEventListener('submit', async (event) => {
    event.preventDefault();
    error1.textContent = '';

    const body = {
        firstName: document.getElementById('firstName').value.trim(),
        email: document.getElementById('email').value.trim().toLowerCase(),
        login: document.getElementById('login').value.trim(),
        password: document.getElementById('password').value,
        confirmPassword: document.getElementById('confirmPassword').value
    };

    if (body.password !== body.confirmPassword) {
        error1.textContent = t('errMismatch');
        return;
    }

    const btn = document.getElementById('submit1');
    btn.disabled = true;
    try {
        await api('/api/auth/register/send-code', { method: 'POST', body });
        email = body.email;
        showStep2();
    } catch (e) {
        error1.textContent = e.message;
    } finally {
        btn.disabled = false;
    }
});

function showStep2() {
    step1.classList.add('hidden');
    step2.classList.remove('hidden');
    drawHint();
    document.getElementById('code').focus();
    startResendTimer();
}

// ---------- "Qayta yuborish": 60 soniya ----------

function startResendTimer() {
    secondsLeft = 60;
    resendBtn.disabled = true;
    drawResendText();
    clearInterval(timerId);

    timerId = setInterval(() => {
        secondsLeft--;
        if (secondsLeft <= 0) {
            clearInterval(timerId);
            resendBtn.disabled = false;
        }
        drawResendText();
    }, 1000);
}

resendBtn.addEventListener('click', async () => {
    error2.textContent = '';
    try {
        await api('/api/auth/register/resend-code', { method: 'POST', body: { email } });
        startResendTimer();
    } catch (e) {
        error2.textContent = e.message;
    }
});

// ---------- 2-bosqich ----------

step2.addEventListener('submit', async (event) => {
    event.preventDefault();
    error2.textContent = '';

    const code = document.getElementById('code').value.trim();
    if (!code) {
        error2.textContent = t('errEnterCode');
        return;
    }

    const btn = document.getElementById('submit2');
    btn.disabled = true;
    try {
        const data = await api('/api/auth/register/confirm', {
            method: 'POST',
            body: { email, code }
        });
        saveAuth(data);
        window.location.href = 'tests.html';
    } catch (e) {
        error2.textContent = e.message;
        btn.disabled = false;
    }
});