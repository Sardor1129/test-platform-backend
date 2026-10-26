const attemptId = new URLSearchParams(window.location.search).get('id');

const errorBox = document.getElementById('error');
const navBox = document.getElementById('nav');
const questionBox = document.getElementById('questionBox');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const certBtn = document.getElementById('certBtn');

let review = null;
let current = 0;

if (requireUser()) {
    if (!attemptId) {
        window.location.href = 'tests.html';
    } else {
        document.getElementById('backLink').href = 'result.html?id=' + attemptId;
        load();
    }
}

async function load() {
    try {
        review = await api('/api/attempts/' + attemptId + '/review');
    } catch (e) {
        if (e.status === 409) {
            window.location.href = 'test.html?id=' + attemptId;
            return;
        }
        errorBox.textContent = e.message;
        return;
    }

    document.getElementById('subjectChip').textContent = review.subjectName;
    document.getElementById('percentChip').textContent = review.percent + '%';
    certBtn.classList.toggle('hidden', !review.passed);

    drawAll();
}

function drawAll() {
    drawNav();
    drawQuestion();
}

function drawNav() {
    navBox.innerHTML = '';
    review.questions.forEach((q, index) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'q-num ' + (q.correct ? 'right' : 'wrong') + (index === current ? ' cur' : '');
        btn.textContent = q.orderNumber;
        btn.addEventListener('click', () => goTo(index));
        navBox.appendChild(btn);
    });
}

function drawQuestion() {
    const q = review.questions[current];
    questionBox.innerHTML = '';

    const title = document.createElement('div');
    title.className = 'q-title';
    title.textContent = q.orderNumber + '. ' + q.text;
    questionBox.appendChild(title);

    if (q.imageUrl) {
        const img = document.createElement('img');
        img.className = 'q-image';
        img.src = q.imageUrl;
        img.alt = '';
        questionBox.appendChild(img);
    }

    const list = document.createElement('div');
    list.className = 'options';

    q.options.forEach(o => {
        const row = document.createElement('div');
        let cls = 'option static';
        if (o.correct) {
            cls += ' correct';                 // to'g'ri javob doim yashil
        } else if (o.selected) {
            cls += ' wrong';                   // foydalanuvchi tanlagan, lekin xato
        }
        row.className = cls;

        const letter = document.createElement('span');
        letter.className = 'letter';
        letter.textContent = o.label;

        const text = document.createElement('span');
        text.textContent = o.text;

        row.append(letter, text);
        list.appendChild(row);
    });
    questionBox.appendChild(list);

    if (!q.answered) {
        const note = document.createElement('p');
        note.className = 'empty-text';
        note.textContent = t('notAnswered');
        questionBox.appendChild(note);
    }

    prevBtn.disabled = current === 0;
    nextBtn.disabled = current === review.questions.length - 1;
}

function goTo(index) {
    current = index;
    drawAll();
}

prevBtn.addEventListener('click', () => goTo(current - 1));
nextBtn.addEventListener('click', () => goTo(current + 1));

certBtn.addEventListener('click', async () => {
    errorBox.textContent = '';
    certBtn.disabled = true;
    try {
        await downloadFile('/api/attempts/' + attemptId + '/certificate', 'sertifikat.pdf');
    } catch (e) {
        errorBox.textContent = e.message;
    } finally {
        certBtn.disabled = false;
    }
});