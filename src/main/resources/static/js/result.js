const attemptId = new URLSearchParams(window.location.search).get('id');
const errorBox = document.getElementById('error');

if (requireUser()) {
    if (!attemptId) {
        window.location.href = 'tests.html';
    } else {
        loadResult();
    }
}

async function loadResult() {
    try {
        const r = await api('/api/attempts/' + attemptId + '/result');
        draw(r);
    } catch (e) {
        if (e.status === 409) {
            window.location.href = 'test.html?id=' + attemptId;
            return;
        }
        errorBox.textContent = e.message;
    }
}

function draw(r) {
    document.getElementById('head').classList.add(r.passed ? 'pass' : 'fail');
    document.getElementById('badge').textContent = r.passed ? '✓' : '✕';
    document.getElementById('percent').textContent = r.percent + '%';
    document.getElementById('status').textContent = r.passed ? t('passed') : t('failed');

    document.getElementById('total').textContent = r.totalQuestions;
    document.getElementById('correct').textContent = r.correctCount;
    document.getElementById('wrong').textContent = r.wrongCount;

    document.getElementById('reviewLink').href = 'review.html?id=' + attemptId;

    const mainBtn = document.getElementById('mainBtn');
    if (r.certificateAvailable) {
        mainBtn.textContent = t('downloadCert');
        mainBtn.addEventListener('click', async () => {
            errorBox.textContent = '';
            mainBtn.disabled = true;
            try {
                await downloadFile('/api/attempts/' + attemptId + '/certificate', 'sertifikat.pdf');
            } catch (e) {
                errorBox.textContent = e.message;
            } finally {
                mainBtn.disabled = false;
            }
        });
    } else {
        mainBtn.textContent = t('retake');
        mainBtn.addEventListener('click', () => {
            window.location.href = 'tests.html';
        });
    }

    document.getElementById('resultBox').classList.remove('hidden');
}