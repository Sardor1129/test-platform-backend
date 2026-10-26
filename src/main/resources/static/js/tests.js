const cardsBox = document.getElementById('cards');
const errorBox = document.getElementById('error');

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
        const subjects = await api('/api/subjects?lang=' + getLang());
        drawCards(subjects);
    } catch (e) {
        errorBox.textContent = e.message;
    }
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