const cardsBox = document.getElementById('cards');
const catBox = document.getElementById('categoryFilter');
const errorBox = document.getElementById('error');

let allSubjects = [];
let categories = [];
let activeCategory = 'all';   // 'all', 'none' yoki kategoriya id si (matn ko'rinishida)

if (requireUser()) {
    renderSidebar('tests');
    renderLangTabs(() => {
        renderSidebar('tests');
        loadSubjects();
    });
    loadSubjects();
}

async function loadSubjects() {
    errorBox.textContent = '';
    try {
        const lang = getLang();
        const result = await Promise.all([
            api('/api/subjects?lang=' + lang),
            api('/api/categories?lang=' + lang)
        ]);
        allSubjects = result[0];
        categories = result[1];
        drawFilter();
        drawCards(filtered());
    } catch (e) {
        errorBox.textContent = e.message;
    }
}

function filtered() {
    if (activeCategory === 'all') {
        return allSubjects;
    }
    if (activeCategory === 'none') {
        return allSubjects.filter(s => s.categoryId == null);
    }
    return allSubjects.filter(s => String(s.categoryId) === activeCategory);
}

function drawFilter() {
    catBox.innerHTML = '';

    // faqat ichida test bor kategoriyalar ko'rsatiladi
    const used = categories.filter(c => allSubjects.some(s => s.categoryId === c.id));
    if (used.length === 0) {
        activeCategory = 'all';
        return;    // kategoriya yo'q, filtr kerak emas
    }

    const items = [{ key: 'all', text: t('allCategories') }];
    used.forEach(c => items.push({ key: String(c.id), text: c.name }));
    if (allSubjects.some(s => s.categoryId == null)) {
        items.push({ key: 'none', text: t('otherCategory') });
    }

    if (!items.some(i => i.key === activeCategory)) {
        activeCategory = 'all';
    }

    const label = document.createElement('label');
    label.setAttribute('for', 'categorySelect');
    label.textContent = t('categoryLabel');

    const select = document.createElement('select');
    select.id = 'categorySelect';
    items.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item.key;
        opt.textContent = item.text;
        select.appendChild(opt);
    });
    select.value = activeCategory;
    select.addEventListener('change', () => {
        activeCategory = select.value;
        drawCards(filtered());
    });

    catBox.append(label, select);
}

function drawCards(subjects) {
    cardsBox.innerHTML = '';

    if (subjects.length === 0) {
        const p = document.createElement('p');
        p.className = 'empty-text';
        p.textContent = t('noSubjects');
        cardsBox.appendChild(p);
        return;
    }

    subjects.forEach(s => {
        const card = document.createElement('div');
        card.className = 'card';

        const title = document.createElement('h3');
        title.textContent = t('subjectTitle', s.name);

        const meta = document.createElement('div');
        meta.className = 'meta';
        meta.textContent = t('subjectMeta', s.questionCount, s.durationMinutes);

        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn btn-primary btn-small';
        btn.textContent = t('start');
        btn.addEventListener('click', () => startTest(s.id, btn));

        card.append(title, meta, btn);
        cardsBox.appendChild(card);
    });
}

async function startTest(subjectId, btn) {
    errorBox.textContent = '';
    btn.disabled = true;
    try {
        const attempt = await api('/api/attempts', {
            method: 'POST',
            body: { subjectId, language: getLang() }
        });
        window.location.href = 'test.html?id=' + attempt.id;
    } catch (e) {
        errorBox.textContent = e.message;
        btn.disabled = false;
    }
}