const step1 = document.getElementById('step1');
const step2 = document.getElementById('step2');
const step3 = document.getElementById('step3');
const resendBtn = document.getElementById('resendBtn');

let email = '';
let code = '';
let timerId = null;
let secondsLeft = 0;

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

function show(step) {
    [step1, step2, step3].forEach(s => s.classList.add('hidden'));
    step.classList.remove('hidden');
}

// ---------- 1-bosqich: email ----------

step1.addEventListener('submit', async (event) => {
    event.preventDefault();
    const error = document.getElementById('error1');
    error.textContent = '';

    email = document.getElementById('email').value.trim().toLowerCase();

    const btn = document.getElementById('submit1');
    btn.disabled = true;
    try {
        await api('/api/auth/password/send-code', { method: 'POST', body: { email } });
        drawHint();
        show(step2);
        startResendTimer();
        document.getElementById('code').focus();
    } catch (e) {
        error.textContent = e.message;
    } finally {
        btn.disabled = false;
    }
});

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
    const error = document.getElementById('error2');
    error.textContent = '';
    try {
        await api('/api/auth/password/send-code', { method: 'POST', body: { email } });
        startResendTimer();
    } catch (e) {
        error.textContent = e.message;
    }
});

// ---------- 2-bosqich: kod ----------

step2.addEventListener('submit', async (event) => {
    event.preventDefault();
    const error = document.getElementById('error2');
    error.textContent = '';

    const value = document.getElementById('code').value.trim();
    if (!value) {
        error.textContent = t('errEnterCode');
        return;
    }

    const btn = document.getElementById('submit2');
    btn.disabled = true;
    try {
        await api('/api/auth/password/verify-code', {
            method: 'POST',
            body: { email, code: value }
        });
        code = value;
        clearInterval(timerId);
        show(step3);
        document.getElementById('newPassword').focus();
    } catch (e) {
        error.textContent = e.message;
    } finally {
        btn.disabled = false;
    }
});

// ---------- 3-bosqich: yangi parol ----------

step3.addEventListener('submit', async (event) => {
    event.preventDefault();
    const error = document.getElementById('error3');
    error.textContent = '';

    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        error.textContent = t('errMismatch');
        return;
    }

    const btn = document.getElementById('submit3');
    btn.disabled = true;
    try {
        await api('/api/auth/password/reset', {
            method: 'POST',
            body: { email, code, newPassword, confirmPassword }
        });
        alert(t('passwordChanged'));
        window.location.href = 'index.html';
    } catch (e) {
        error.textContent = e.message;
        btn.disabled = false;
    }
});