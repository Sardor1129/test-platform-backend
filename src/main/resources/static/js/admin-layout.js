
function requireAdmin() {
    if (!getToken()) {
        window.location.href = 'index.html';
        return false;
    }
    if (localStorage.getItem('role') !== 'ADMIN') {
        window.location.href = 'tests.html';
        return false;
    }
    return true;
}

const ADMIN_ICONS = {
    results: '<svg viewBox="0 0 24 24"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>',
    subjects: '<svg viewBox="0 0 24 24"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>',
    questions: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
    logout: '<svg viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>'
};

const ADMIN_MENU = [
    { key: 'results',   href: 'admin.html',            icon: 'results',   text: 'Natijalar' },
    { key: 'subjects',  href: 'admin-subjects.html',   icon: 'subjects',  text: 'Fanlar' },
    { key: 'questions', href: 'admin-questions.html',  icon: 'questions', text: 'Savollar' }
];

function renderAdminSidebar(activeKey) {
    const box = document.getElementById('sidebar');

    const links = ADMIN_MENU.map(item =>
        '<li><a href="' + item.href + '"' + (item.key === activeKey ? ' class="active"' : '') + '>'
        + ADMIN_ICONS[item.icon] + item.text + '</a></li>'
    ).join('');

    box.innerHTML =
        '<div class="brand">Admin panel</div>'
        + '<div class="user"><div class="avatar"></div>'
        +   '<div class="user-name" id="sideName"></div></div>'
        + '<ul class="menu">' + links + '</ul>'
        + '<div class="menu-bottom"><ul class="menu"><li>'
        +   '<button type="button" id="logoutBtn">' + ADMIN_ICONS.logout + 'Chiqish</button>'
        + '</li></ul></div>';

    document.getElementById('sideName').textContent = localStorage.getItem('fullName') || '';
    document.getElementById('logoutBtn').addEventListener('click', logout);
}

function formatDateTime(iso) {
    const d = new Date(iso);
    const p = n => String(n).padStart(2, '0');
    return p(d.getDate()) + '.' + p(d.getMonth() + 1) + '.' + d.getFullYear()
        + ' ' + p(d.getHours()) + ':' + p(d.getMinutes());
}