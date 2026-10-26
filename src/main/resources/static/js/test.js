const params = new URLSearchParams(window.location.search);
const attemptId = params.get('id');

const errorBox = document.getElementById('error');
const navBox = document.getElementById('nav');
const questionBox = document.getElementById('questionBox');
const timerBox = document.getElementById('timer');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const finishBtn = document.getElementById('finishBtn');

let attempt = null;
let current = 0;
let endTime = 0;
let timerId = null;
let finishing = false;
let saving = false;

if (requireUser()) {
    if (!attemptId) {
        window.location.href = 'tests.html';
    } else {
        loadAttempt();
    }
}


async function loadAttempt() {
    try {
        attempt = await api('/api/attempts/' + attemptId);
    } catch (e) {
        if (e.status === 409) {
            window.location.href = 'result.html?id=' + attemptId;
            return;
        }
        errorBox.textContent = e.message;
        return;
    }

    const langNames = { uz: 'O\'zbek', ru: 'Русский', en: 'English' };
    document.getElementById('langChip').textContent = langNames[attempt.language] || attempt.language;

    startTimer(attempt.remainingSeconds);
    drawAll();
}



function startTimer(seconds) {
    endTime = Date.now() + seconds * 1000;
    tick();
    timerId = setInterval(tick, 1000);
}

function tick() {
    const left = Math.max(0, Math.round((endTime - Date.now()) / 1000));
    const h = String(Math.floor(left / 3600)).padStart(2, '0');
    const m = String(Math.floor((left % 3600) / 60)).padStart(2, '0');
    const s = String(left % 60).padStart(2, '0');
    timerBox.textContent = h + ':' + m + ':' + s;
    timerBox.classList.toggle('warning', left <= 300);

    if (left === 0) {
        doFinish();
    }
}


function drawAll() {
    drawNav();
    drawQuestion();
}

function drawNav() {
    navBox.innerHTML = '';
    attempt.questions.forEach((q, index) => {
        const btn = document.createElement('button');
        let cls = 'q-num';
        if (q.selectedOptionId) {
            cls += q.selectedOptionId === q.correctOptionId ? ' right' : ' wrong';
        }
        if (index === current) {
            cls += ' cur';
        }
        btn.type = 'button';
        btn.className = cls;
        btn.textContent = q.orderNumber;
        btn.addEventListener('click', () => goTo(index));
        navBox.appendChild(btn);
    });
}

function drawQuestion() {
    const q = attempt.questions[current];
    const answered = !!q.selectedOptionId;
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
        const btn = document.createElement('button');
        btn.type = 'button';

        let cls = 'option';
        if (answered) {
            cls += ' static';
            if (o.id === q.correctOptionId) {
                cls += ' correct';
            } else if (o.id === q.selectedOptionId) {
                cls += ' wrong';
            }
        }
        btn.className = cls;

        const letter = document.createElement('span');
        letter.className = 'letter';
        letter.textContent = o.label;

        const text = document.createElement('span');
        text.textContent = o.text;

        btn.append(letter, text);
        if (!answered) {
            btn.addEventListener('click', () => selectOption(o.id));
        }
        list.appendChild(btn);
    });
    questionBox.appendChild(list);

    if (answered) {
        const right = q.selectedOptionId === q.correctOptionId;
        const note = document.createElement('p');
        note.className = right ? 'success-text' : 'error-text';
        note.textContent = right ? t('rightAnswer') : t('wrongAnswer');
        questionBox.appendChild(note);
    }

    prevBtn.disabled = current === 0;
    nextBtn.disabled = current === attempt.questions.length - 1;
}

function goTo(index) {
    current = index;
    errorBox.textContent = '';
    drawAll();
}

prevBtn.addEventListener('click', () => goTo(current - 1));
nextBtn.addEventListener('click', () => goTo(current + 1));


async function selectOption(optionId) {
    const q = attempt.questions[current];
    if (q.selectedOptionId || finishing || saving) {
        return;
    }
    saving = true;
    errorBox.textContent = '';

    try {
        const feedback = await api('/api/attempts/' + attemptId + '/answers', {
            method: 'PUT',
            body: { questionId: q.questionId, optionId }
        });
        q.selectedOptionId = optionId;
        q.correctOptionId = feedback.correctOptionId;
        drawAll();
        scheduleNext(current, q.selectedOptionId === q.correctOptionId ? 1200 : 2500);

    } catch (e) {
        errorBox.textContent = e.message;
        if (e.status === 409) {
            doFinish();                 // vaqt tugagan yoki test yakunlangan
        }
    } finally {
        saving = false;
    }
}


function scheduleNext(fromIndex, delay) {
    setTimeout(() => {
        if (finishing) {
            return;
        }
        const total = attempt.questions.length;
        const hasLeft = attempt.questions.some(q => !q.selectedOptionId);

        if (!hasLeft) {
            doFinish();
            return;
        }

        if (current !== fromIndex) {
            return;
        }
        for (let step = 1; step <= total; step++) {
            const idx = (current + step) % total;
            if (!attempt.questions[idx].selectedOptionId) {
                goTo(idx);
                return;
            }
        }
    }, delay);
}


finishBtn.addEventListener('click', () => {
    const unanswered = attempt.questions.filter(q => !q.selectedOptionId).length;
    if (confirm(t('confirmFinish', unanswered))) {
        doFinish();
    }
});

async function doFinish() {
    if (finishing) {
        return;
    }
    finishing = true;
    clearInterval(timerId);
    finishBtn.disabled = true;

    try {
        await api('/api/attempts/' + attemptId + '/finish', { method: 'POST' });
        window.location.href = 'result.html?id=' + attemptId;
    } catch (e) {
        errorBox.textContent = e.message;
        finishing = false;
        finishBtn.disabled = false;
    }
}