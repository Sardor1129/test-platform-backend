const rowsBox = document.getElementById('rows');
const errorBox = document.getElementById('error');
const emptyBox = document.getElementById('empty');

if (requireUser()) {
    renderSidebar('results');
    renderLangTabs(() => {
        renderSidebar('results');
        load();
    });
    load();
}

async function load() {
    errorBox.textContent = '';
    try {
        const list = await api('/api/attempts?lang=' + getLang());
        draw(list);
    } catch (e) {
        errorBox.textContent = e.message;
    }
}

function formatDate(iso) {
    const d = new Date(iso);
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    return dd + '.' + mm + '.' + d.getFullYear();
}

function cell(text, className) {
    const td = document.createElement('td');
    td.textContent = text;
    if (className) {
        td.className = className;
    }
    return td;
}

function draw(list) {
    rowsBox.innerHTML = '';
    emptyBox.classList.toggle('hidden', list.length > 0);

    list.forEach(a => {
        const tr = document.createElement('tr');

        tr.append(
            cell(formatDate(a.date)),
            cell(a.subjectName),
            cell(t('type' + a.attemptType)),
            cell(a.percent + '%', a.passed ? 'pass' : 'fail')
        );

        const reviewTd = document.createElement('td');
        const reviewLink = document.createElement('a');
        reviewLink.href = 'review.html?id=' + a.id;
        reviewLink.textContent = t('view');
        reviewTd.appendChild(reviewLink);
        tr.appendChild(reviewTd);

        const certTd = document.createElement('td');
        if (a.certificateAvailable) {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'link-btn';
            btn.textContent = t('download');
            btn.addEventListener('click', async () => {
                errorBox.textContent = '';
                try {
                    await downloadFile('/api/attempts/' + a.id + '/certificate', 'sertifikat.pdf');
                } catch (e) {
                    errorBox.textContent = e.message;
                }
            });
            certTd.appendChild(btn);
        } else {
            certTd.textContent = '—';
        }
        tr.appendChild(certTd);

        rowsBox.appendChild(tr);
    });
}