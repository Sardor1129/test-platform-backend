const subjectSelect = document.getElementById('subjectSelect');
const langSelect = document.getElementById('langSelect');
const listBox = document.getElementById('list');
const emptyBox = document.getElementById('empty');
const errorBox = document.getElementById('error');
const successBox = document.getElementById('success');
const form = document.getElementById('questionForm');
const formTitle = document.getElementById('formTitle');
const formError = document.getElementById('formError');
const optionRows = document.getElementById('optionRows');
const saveBtn = document.getElementById('saveBtn');

const LETTERS = ['A', 'B', 'C', 'D'];

let questions = [];
let editingId = null;       // null bo'lsa yangi savol

if (requireAdmin()) {
    renderAdminSidebar('questions');
    buildOptionRows();
    init();
}

// 4 ta variant qatorini bir marta chizamiz
function buildOptionRows() {
    optionRows.innerHTML = '';
    LETTERS.forEach((letter, i) => {
        const row = document.createElement('div');
        row.className = 'option-row';

        const l = document.createElement('span');
        l.className = 'letter';
        l.textContent = letter;

        const input = document.createElement('input');
        input.type = 'text';
        input.id = 'opt' + i;
        input.placeholder = letter + ' variant';

        const label = document.createElement('label');
        const radio = document.createElement('input');
        radio.type = 'radio';
        radio.name = 'correct';
        radio.value = i;
        label.append(radio, ' To\'g\'ri');

        row.append(l, input, label);
        optionRows.appendChild(row);
    });
}

async function init() {
    try {
        const subjects = await api('/api/admin/subjects');
        if (subjects.length === 0) {
            errorBox.textContent = 'Avval "Fanlar" bo\'limida fan qo\'shing';
            document.getElementById('newBtn').disabled = true;
            return;
        }
        subjects.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id;
            opt.textContent = s.nameUz;
            subjectSelect.appendChild(opt);
        });
    } catch (e) {
        errorBox.textContent = e.message;
        return;
    }
    subjectSelect.addEventListener('change', () => { hideForm(); load(); });
    langSelect.addEventListener('change', () => { hideForm(); load(); });
    load();
}

async function load() {
    errorBox.textContent = '';
    try {
        questions = await api('/api/admin/questions?subjectId=' + subjectSelect.value
            + '&language=' + langSelect.value);
        draw();
    } catch (e) {
        errorBox.textContent = e.message;
    }
}

function draw() {
    listBox.innerHTML = '';
    emptyBox.classList.toggle('hidden', questions.length > 0);

    questions.forEach((q, index) => {
        const card = document.createElement('div');
        card.className = 'q-card';

        const text = document.createElement('div');
        text.className = 'q-text';
        text.textContent = (index + 1) + '. ' + q.text;
        card.appendChild(text);

        q.options.forEach(o => {
            const opt = document.createElement('div');
            opt.className = 'opt' + (o.correct ? ' correct' : '');
            opt.textContent = o.label + ') ' + o.text;
            card.appendChild(opt);
        });

        const actions = document.createElement('div');
        actions.className = 'row-actions';

        const edit = document.createElement('button');
        edit.type = 'button';
        edit.className = 'link-btn';
        edit.textContent = 'Tahrirlash';
        edit.addEventListener('click', () => startEdit(q));

        const del = document.createElement('button');
        del.type = 'button';
        del.className = 'link-danger';
        del.textContent = 'O\'chirish';
        del.addEventListener('click', () => remove(q));

        actions.append(edit, del);
        card.appendChild(actions);
        listBox.appendChild(card);
    });
}

// ---------- Forma ----------

function showForm(title) {
    formTitle.textContent = title;
    formError.textContent = '';
    form.classList.remove('hidden');
    form.scrollIntoView({ behavior: 'smooth' });
}

function hideForm() {
    form.classList.add('hidden');
    form.reset();
    editingId = null;
}

document.getElementById('newBtn').addEventListener('click', () => {
    form.reset();
    editingId = null;
    successBox.textContent = '';
    showForm('Yangi savol');
});

document.getElementById('cancelBtn').addEventListener('click', hideForm);

function startEdit(q) {
    form.reset();
    editingId = q.id;
    successBox.textContent = '';
    document.getElementById('text').value = q.text;
    document.getElementById('imageUrl').value = q.imageUrl || '';
    q.options.forEach((o, i) => {
        document.getElementById('opt' + i).value = o.text;
        if (o.correct) {
            document.querySelector('input[name="correct"][value="' + i + '"]').checked = true;
        }
    });
    showForm('Savolni tahrirlash');
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    formError.textContent = '';
    successBox.textContent = '';

    const checked = document.querySelector('input[name="correct"]:checked');
    if (!checked) {
        formError.textContent = 'To\'g\'ri javobni belgilang';
        return;
    }

    const options = LETTERS.map((_, i) => ({
        text: document.getElementById('opt' + i).value.trim(),
        correct: Number(checked.value) === i
    }));

    const body = {
        subjectId: Number(subjectSelect.value),
        language: langSelect.value,
        text: document.getElementById('text').value.trim(),
        imageUrl: document.getElementById('imageUrl').value.trim() || null,
        options
    };

    saveBtn.disabled = true;
    try {
        if (editingId === null) {
            await api('/api/admin/questions', { method: 'POST', body });
        } else {
            await api('/api/admin/questions/' + editingId, { method: 'PUT', body });
        }
        hideForm();
        successBox.textContent = 'Saqlandi';
        await load();
    } catch (e) {
        formError.textContent = e.message;
    } finally {
        saveBtn.disabled = false;
    }
});

async function remove(q) {
    if (!confirm('Savolni o\'chirasizmi?')) {
        return;
    }
    errorBox.textContent = '';
    successBox.textContent = '';
    try {
        await api('/api/admin/questions/' + q.id, { method: 'DELETE' });
        await load();
    } catch (e) {
        errorBox.textContent = e.message;      // masalan: "Bu savol testlarda ishlatilgan..."
    }
}