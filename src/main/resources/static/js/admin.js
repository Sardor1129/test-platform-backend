const rowsBox = document.getElementById('rows');
const errorBox = document.getElementById('error');
const emptyBox = document.getElementById('empty');
const subjectFilter = document.getElementById('subjectFilter');
const passedFilter = document.getElementById('passedFilter');

const TYPE_NAMES = { FIRST: 'Birinchi', RETAKE: 'Qayta' };

if (requireAdmin()) {
    renderAdminSidebar('results');
    init();
}

async function init() {
    try {
        const subjects = await api('/api/admin/subjects');
        subjects.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id;
            opt.textContent = s.nameUz;
            subjectFilter.appendChild(opt);
        });
    } catch (e) {
        errorBox.textContent = e.message;
    }
    subjectFilter.addEventListener('change', load);
    passedFilter.addEventListener('change', load);
    load();
}

async function load() {
    errorBox.textContent = '';
    const query = [];
    if (subjectFilter.value) query.push('subjectId=' + subjectFilter.value);
    if (passedFilter.value) query.push('passed=' + passedFilter.value);

    try {
        const list = await api('/api/admin/results' + (query.length ? '?' + query.join('&') : ''));
        draw(list);
    } catch (e) {
        errorBox.textContent = e.message;
    }
}

function cell(text, className) {
    const td = document.createElement('td');
    td.textContent = text;
    if (className) td.className = className;
    return td;
}

function draw(list) {
    rowsBox.innerHTML = '';
    emptyBox.classList.toggle('hidden', list.length > 0);

    list.forEach(r => {
        const tr = document.createElement('tr');
        tr.append(
            cell(r.fullName),
            cell(r.phone),
            cell(r.subjectName),
            cell(r.language.toUpperCase()),
            cell(TYPE_NAMES[r.attemptType] || r.attemptType),
            cell(r.correctCount + ' / ' + r.totalQuestions),
            cell(r.percent + '%', r.passed ? 'pass' : 'fail'),
            cell(formatDateTime(r.finishedAt))
        );
        rowsBox.appendChild(tr);
    });
}