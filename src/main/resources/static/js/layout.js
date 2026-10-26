function requireUser() {
    if (!getToken()) {
        window.location.href = 'index.html';
        return false;
    }
    if (localStorage.getItem('role') === 'ADMIN') {
        window.location.href = 'admin.html';
        return false;
    }
    return true;
}

const ICONS = {
    test: '<svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>',
    results: '<svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>',
    profile: '<svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>',
    logout: '<svg viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>'
};

const MENU_ITEMS = [
    { key: 'tests',   href: 'tests.html',   icon: 'test',    textKey: 'menuTests' },
    { key: 'results', href: 'results.html', icon: 'results', textKey: 'menuResults' },
    { key: 'profile', href: 'profile.html', icon: 'profile', textKey: 'menuProfile' }
];

function renderSidebar(activeKey) {
    const box = document.getElementById('sidebar');

    const links = MENU_ITEMS.map(item =>
        '<li><a href="' + item.href + '"' + (item.key === activeKey ? ' class="active"' : '') + '>'
        + ICONS[item.icon] + t(item.textKey) + '</a></li>'
    ).join('');

    box.innerHTML =
        '<div class="brand">' + t('brand') + '</div>'
        + '<div class="user">'
        +   '<div class="avatar" id="sideAvatar"></div>'
        +   '<div class="user-name" id="sideName"></div>'
        + '</div>'
        + '<ul class="menu">' + links + '</ul>'
        + '<div class="menu-bottom"><ul class="menu"><li>'
        +   '<button type="button" id="logoutBtn">' + ICONS.logout + t('logout') + '</button>'
        + '</li></ul></div>';

    document.getElementById('sideName').textContent = localStorage.getItem('fullName') || '';
    document.getElementById('logoutBtn').addEventListener('click', logout);

    applySidebarAvatar();
    syncSidebarProfile();

}

function applySidebarAvatar() {
    const el = document.getElementById('sideAvatar');
    if (!el) {
        return;
    }
    const url = localStorage.getItem('avatarUrl');
    el.style.cssText = url
        ? 'background-image:url("' + API_URL + url + '");background-size:cover;background-position:center;'
        : '';
}

// Sahifa ochilganda profil ma'lumotini serverdan bir marta olib, rasmni yangilaydi
let sidebarSynced = false;

async function syncSidebarProfile() {
    if (sidebarSynced) {
        return;
    }
    sidebarSynced = true;
    try {
        const p = await api('/api/profile');
        if (p.avatarUrl) {
            localStorage.setItem('avatarUrl', p.avatarUrl);
        } else {
            localStorage.removeItem('avatarUrl');
        }
        applySidebarAvatar();
    } catch (e) {
        // menyu rasmsiz qolaveradi
    }
}
