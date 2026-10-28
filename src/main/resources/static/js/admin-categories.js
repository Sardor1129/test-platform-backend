const form = document.getElementById('categoryForm');
const rowsBox = document.getElementById('rows');
const errorBox = document.getElementById('error');
const successBox = document.getElementById('success');
const saveBtn = document.getElementById('saveBtn');
const cancelBtn = document.getElementById('cancelBtn');
const formTitle = document.getElementById('formTitle');

const FIELDS = ['nameUz', 'nameRu', 'nameEn'];

let categories = [];
let editingId = null;

if (requireAdmin()) {
    renderAdminSidebar('categories');
    load();
}

async function load() {
    try {
        categories = await api('/api/admin/categories');
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
    categories.forEach(c => {
        const tr = document.createElement('tr');
        tr.append(cell(c.id), cell(c.nameUz), cell(c.nameRu), cell(c.nameEn),
            cell(c.subjectCount));

        const td = document.createElement('td');
        const actions = document.createElement('div');
        actions.className = 'row-actions';

        const edit = document.createElement('button');
        edit.type = 'button';
        edit.className = 'link-btn';
        edit.textContent = 'Tahrirlash';
        edit.addEventListener('click', () => startEdit(c));

        const del = document.createElement('button');
        del.type = 'button';
        del.className = 'link-danger';
        del.textContent = 'O\'chirish';
        del.addEventListener('click', () => remove(c));

        actions.append(edit, del);
        td.appendChild(actions);
        tr.appendChild(td);
        rowsBox.appendChild(tr);
    });
}

function startEdit(c) {
    editingId = c.id;
    FIELDS.forEach(f => { document.getElementById(f).value = c[f]; });
    formTitle.textContent = 'Kategoriyani tahrirlash (ID: ' + c.id + ')';
    cancelBtn.classList.remove('hidden');
    errorBox.textContent = '';
    successBox.textContent = '';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetForm() {
    editingId = null;
    form.reset();
    formTitle.textContent = 'Yangi kategoriya qo\'shish';
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
        nameEn: document.getElementById('nameEn').value.trim()
    };

    saveBtn.disabled = true;
    try {
        if (editingId === null) {
            await api('/api/admin/categories', { method: 'POST', body });
        } else {
            await api('/api/admin/categories/' + editingId, { method: 'PUT', body });
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

async function remove(c) {
    if (!confirm('"' + c.nameUz + '" kategoriyasini o\'chirasizmi?')) {
        return;
    }
    errorBox.textContent = '';
    successBox.textContent = '';
    try {
        await api('/api/admin/categories/' + c.id, { method: 'DELETE' });
        await load();
    } catch (e) {
        errorBox.textContent = e.message;   // masalan: "Bu kategoriyada testlar bor..."
    }
}