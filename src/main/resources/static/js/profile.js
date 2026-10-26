const form = document.getElementById('profileForm');
const errorBox = document.getElementById('error');
const successBox = document.getElementById('success');
const editBtn = document.getElementById('editBtn');
const saveBtn = document.getElementById('saveBtn');
const cancelBtn = document.getElementById('cancelBtn');
const avatarBtn = document.getElementById('avatarBtn');
const avatarInput = document.getElementById('avatarInput');
const avatarImg = document.getElementById('avatar');

// Email tahrirlanmaydi
const EDITABLE = ['firstName', 'login'];

let profile = null;

if (requireUser()) {
    renderSidebar('profile');
    renderLangTabs(() => renderSidebar('profile'));
    load();
}

async function load() {
    try {
        profile = await api('/api/profile');
        fill();
        showAvatar();
    } catch (e) {
        errorBox.textContent = e.message;
    }
}

function fill() {
    EDITABLE.forEach(id => {
        document.getElementById(id).value = profile[id] || '';
    });
    document.getElementById('email').value = profile.email || '';
}

function showAvatar() {
    if (profile.avatarUrl) {
        avatarImg.src = API_URL + profile.avatarUrl;
        localStorage.setItem('avatarUrl', profile.avatarUrl);
    } else {
        avatarImg.removeAttribute('src');
        localStorage.removeItem('avatarUrl');
    }

    applySidebarAvatar();
}

function setEditing(editing) {
    EDITABLE.forEach(id => {
        document.getElementById(id).disabled = !editing;
    });
    editBtn.classList.toggle('hidden', editing);
    saveBtn.classList.toggle('hidden', !editing);
    cancelBtn.classList.toggle('hidden', !editing);
    avatarBtn.classList.toggle('hidden', !editing);
}

editBtn.addEventListener('click', () => {
    errorBox.textContent = '';
    successBox.textContent = '';
    setEditing(true);
});

cancelBtn.addEventListener('click', () => {
    errorBox.textContent = '';
    fill();
    setEditing(false);
});

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    errorBox.textContent = '';
    successBox.textContent = '';

    const body = {};
    EDITABLE.forEach(id => {
        body[id] = document.getElementById(id).value.trim();
    });

    saveBtn.disabled = true;
    try {
        const data = await api('/api/profile', { method: 'PUT', body });
        saveAuth(data);
        profile = await api('/api/profile');
        fill();
        setEditing(false);
        renderSidebar('profile');
        showAvatar();
        successBox.textContent = t('saved');
    } catch (e) {
        errorBox.textContent = e.message;
    } finally {
        saveBtn.disabled = false;
    }
});

avatarBtn.addEventListener('click', () => avatarInput.click());

avatarInput.addEventListener('change', async () => {
    const file = avatarInput.files[0];
    if (!file) {
        return;
    }
    errorBox.textContent = '';
    successBox.textContent = '';

    if (file.size > 2 * 1024 * 1024) {
        errorBox.textContent = t('errPhotoSize');
        avatarInput.value = '';
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        const data = await api('/api/profile/avatar', { method: 'POST', body: formData });
        profile.avatarUrl = data.avatarUrl;
        showAvatar();
        renderSidebar('profile');
        successBox.textContent = t('photoUpdated');
    } catch (e) {
        errorBox.textContent = e.message;
    } finally {
        avatarInput.value = '';
    }
});