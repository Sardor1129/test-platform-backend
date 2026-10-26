const API_URL = '';


function saveAuth(data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('role', data.role);
    localStorage.setItem('fullName', data.firstName);
}

function getToken() {
    return localStorage.getItem('token');
}

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

async function api(path, options = {}) {
    const headers = { ...(options.headers || {}) };

    if (options.body && !(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(options.body);
    }

    const token = getToken();
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    let response;
    try {
        response = await fetch(API_URL + path, { ...options, headers });
    } catch (e) {
        throw new Error('Serverga ulanib bo\'lmadi. Backend ishlayaptimi?');
    }

    if (response.status === 401 && !path.startsWith('/api/auth/')) {
        logout();
        throw new Error('Qaytadan kiring');
    }

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        const err = new Error((data && data.message) || 'Xatolik yuz berdi');
        err.status = response.status;
        throw err;
    }
    return data;
}

async function downloadFile(path, filename) {
    let response;
    try {
        response = await fetch(API_URL + path, {
            headers: { 'Authorization': 'Bearer ' + getToken() }
        });
    } catch (e) {
        throw new Error('Serverga ulanib bo\'lmadi');
    }

    if (!response.ok) {
        let message = 'Faylni yuklab bo\'lmadi';
        try {
            const data = await response.json();
            if (data && data.message) {
                message = data.message;
            }
        } catch (e) { /* javob JSON emas */ }
        throw new Error(message);
    }

    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
}