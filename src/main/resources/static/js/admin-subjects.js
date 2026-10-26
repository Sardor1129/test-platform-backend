const form = document.getElementById('subjectForm');
const rowsBox = document.getElementById('rows');
const errorBox = document.getElementById('error');
const successBox = document.getElementById('success');
const saveBtn = document.getElementById('saveBtn');
const cancelBtn = document.getElementById('cancelBtn');
const formTitle = document.getElementById('formTitle');

const FIELDS = ['nameUz', 'nameRu', 'nameEn', 'durationMinutes', 'questionCount', 'passPercent'];

let subjects = [];
let editingId = null;

if (requireAdmin()) {
    renderAdminSidebar('subjects');
    load();
}

async function load() {
    try {
        subjects = await api('/api/admin/subjects');
        draw();
    } catch (e) {
        errorBox.textContent = e.message;
    }
}

function cell(text) {
    const td = document.createElement('td');
    td.textContent = text;
    return td;
}

function draw() {
    rowsBox.innerHTML = '';
    subjects.forEach(s => {
        const tr = document.createElement('tr');
        tr.append(cell(s.id), cell(s.nameUz), cell(s.durationMinutes + ' daqiqa'),
            cell(s.questionCount), cell(s.passPercent + '%'));

        const td = document.createElement('td');
        const actions = document.createElement('div');
        actions.className = 'row-actions';

        const edit = document.createElement('button');
        edit.type = 'button';
        edit.className = 'link-btn';
        edit.textContent = 'Tahrirlash';
        edit.addEventListener('click', () => startEdit(s));

        const del = document.createElement('button');
        del.type = 'button';
        del.className = 'link-danger';
        del.textContent = 'O\'chirish';
        del.addEventListener('click', () => remove(s));

        actions.append(edit, del);
        td.appendChild(actions);
        tr.appendChild(td);
        rowsBox.appendChild(tr);
    });
}

function startEdit(s) {
    editingId = s.id;
    FIELDS.forEach(f => { document.getElementById(f).value = s[f]; });
    formTitle.textContent = 'Fanni tahrirlash (ID: ' + s.id + ')';
    cancelBtn.classList.remove('hidden');
    errorBox.textContent = '';
    successBox.textContent = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetForm() {
    editingId = null;
    form.reset();
    formTitle.textContent = 'Yangi fan qo\'shish';
    cancelBtn.classList.add('hidden');
}

cancelBtn.addEventListener('click', () => {
    errorBox.textContent = '';
    resetForm();
});

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    errorBox.textContent = '';
    successBox.textContent = '';

    const body = {
        nameUz: document.getElementById('nameUz').value.trim(),
        nameRu: document.getElementById('nameRu').value.trim(),
        nameEn: document.getElementById('nameEn').value.trim(),
        durationMinutes: Number(document.getElementById('durationMinutes').value) || null,
        questionCount: Number(document.getElementById('questionCount').value) || null,
        passPercent: Number(document.getElementById('passPercent').value) || null

    };

    saveBtn.disabled = true;
    try {
        if (editingId === null) {
            await api('/api/admin/subjects', { method: 'POST', body });
        } else {
            await api('/api/admin/subjects/' + editingId, { method: 'PUT', body });
        }
        successBox.textContent = 'Saqlandi';
        resetForm();
        await load();
    } catch (e) {
        errorBox.textContent = e.message;
    } finally {
        saveBtn.disabled = false;
    }
});

async function remove(s) {
    if (!confirm('"' + s.nameUz + '" fanini o\'chirasizmi?')) {
        return;
    }
    errorBox.textContent = '';
    successBox.textContent = '';
    try {
        await api('/api/admin/subjects/' + s.id, { method: 'DELETE' });
        await load();
    } catch (e) {
        errorBox.textContent = e.message;      // masalan: "Bu fanda savollar bor..."
    }
}