(function () {
    let saved = null;
    try { saved = localStorage.getItem('theme'); } catch (e) {}
    const theme = saved || (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
    document.documentElement.setAttribute('data-theme', theme);

    document.addEventListener('DOMContentLoaded', () => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'themeBtn';
        btn.setAttribute('aria-label', 'Theme');

        const draw = () => {
            btn.textContent = document.documentElement.getAttribute('data-theme') === 'dark' ? '☀' : '☾';
        };
        btn.addEventListener('click', () => {
            const next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', next);
            try { localStorage.setItem('theme', next); } catch (e) {}
            draw();
        });
        draw();
        document.body.appendChild(btn);
    });
})();