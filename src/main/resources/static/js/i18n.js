// ---------- Til ----------

function getLang() {
    return localStorage.getItem("lang") || "uz";
}

function setLang(lang) {
    localStorage.setItem("lang", lang);
}

// ---------- Tarjimalar ----------

const I18N = {
    uz: {
        // menyu
        brand: "Arxiv ishi bo'yicha malaka oshirish ilmiy-metodik markazi",
        menuTests: "Test topshirish",
        menuResults: "Mening natijalarim",
        menuProfile: "Profil",
        logout: "Chiqish",
        // fanlar
        start: "Boshlash",
        noSubjects: "Hozircha fanlar yo'q",
        categoryLabel: "Kategoriya",
        allCategories: "Hammasi",
        otherCategory: "Boshqa",
        subjectTitle: n => n + " fani",
        subjectMeta: (q, m) => q + " ta savol, " + m + " daqiqa",
        // kirish, ro'yxatdan o'tish
        loginTitle: "Tizimga kirish",
        noAccount: "Hali akkauntingiz yo'qmi?",
        haveAccount: "Akkauntingiz bormi?",
        register: "Ro'yxatdan o'tish",
        signIn: "Kirish",
        login: "Login",
        password: "Parol",
        forgotLink: "Parolni unutdingizmi?",
        errFillLogin: "Login va parolni kiriting",
        firstName: "Ism",
        email: "E-mail",
        confirmPassword: "Parolni takrorlang",
        codeLabel: "Tasdiqlash kodi",
        codeHint: e => e + " manziliga yuborilgan kodni kiriting (xat kelmasa, Spam papkasini ham tekshiring)",
        resend: "Qayta yuborish",
        resendWait: n => "Qayta yuborish (" + n + ")",
        errMismatch: "Parollar bir xil emas",
        errEnterCode: "Kodni kiriting",
        // parol tiklash
        forgotTitle: "Parol tiklash",
        forgotHint: "E-mail manzilingizga tekshiruv kodi yuboriladi",
        confirmBtn: "Tasdiqlash",
        newPassword: "Yangi parol kiriting",
        newPasswordRepeat: "Yangi parolni takrorlang",
        passwordChanged: "Parol muvaffaqiyatli o'zgartirildi. Endi yangi parol bilan kiring.",
        // test
        prev: "‹ Oldingisi",
        next: "Keyingisi ›",
        finishTest: "Testni yakunlash",
        confirmFinish: n => n > 0
            ? "Siz " + n + " ta savolga javob bermadingiz. Testni yakunlaysizmi?"
            : "Testni yakunlaysizmi?",
        rightAnswer: "To'g'ri javob ✓",
        wrongAnswer: "Noto'g'ri javob ✕",
        // natija
        resultTitle: "Natija",
        totalQ: "Umumiy savollar soni",
        correctQ: "To'g'ri javoblar soni",
        wrongQ: "Noto'g'ri javoblar soni",
        passed: "Topshirildi",
        failed: "Topshirilmadi",
        viewAnswers: "Javoblarni ko'rish",
        downloadCert: "Sertifikatni yuklab olish",
        retake: "Qayta topshirish",
        backHome: "Asosiy sahifaga qaytish",
        backToResult: "Natijaga qaytish",
        notAnswered: "Bu savolga javob berilmagan",
        // mening natijalarim
        colDate: "Sana",
        colSubject: "Fan nomi",
        colType: "Turi",
        colResult: "Natija",
        colAnswers: "Javoblar",
        colCert: "Sertifikat",
        typeFIRST: "Birinchi topshirish",
        typeRETAKE: "Qayta topshirish",
        view: "Ko'rish",
        download: "Yuklab olish",
        noResults: "Hozircha topshirilgan testlar yo'q",
        // profil
        edit: "Tahrirlash",
        save: "Saqlash",
        cancel: "Bekor qilish",
        changePhoto: "Rasmni almashtirish",
        saved: "Ma'lumotlar saqlandi",
        photoUpdated: "Rasm yangilandi",
        errPhotoSize: "Rasm hajmi 2 MB dan oshmasin"
    },
    ru: {
        brand: "Научно-методический центр повышения квалификации по архивному делу",
        menuTests: "Пройти тест",
        menuResults: "Мои результаты",
        menuProfile: "Профиль",
        logout: "Выйти",
        start: "Начать",
        noSubjects: "Предметов пока нет",
        categoryLabel: "Категория",
        allCategories: "Все",
        otherCategory: "Другое",
        subjectTitle: n => "Предмет: " + n,
        subjectMeta: (q, m) => "Вопросов: " + q + ", время: " + m + " мин.",
        loginTitle: "Вход в систему",
        noAccount: "Ещё нет аккаунта?",
        haveAccount: "Уже есть аккаунт?",
        register: "Регистрация",
        signIn: "Войти",
        login: "Логин",
        password: "Пароль",
        forgotLink: "Забыли пароль?",
        errFillLogin: "Введите логин и пароль",
        firstName: "Имя",
        email: "E-mail",
        confirmPassword: "Повторите пароль",
        codeLabel: "Код подтверждения",
        codeHint: e => "Введите код, отправленный на " + e + " (если письма нет, проверьте папку «Спам»)",
        resend: "Отправить снова",
        resendWait: n => "Отправить снова (" + n + ")",
        errMismatch: "Пароли не совпадают",
        errEnterCode: "Введите код",
        forgotTitle: "Восстановление пароля",
        forgotHint: "Код проверки будет отправлен на ваш e-mail",
        confirmBtn: "Подтвердить",
        newPassword: "Введите новый пароль",
        newPasswordRepeat: "Повторите новый пароль",
        passwordChanged: "Пароль успешно изменён. Теперь войдите с новым паролем.",
        prev: "‹ Назад",
        next: "Далее ›",
        finishTest: "Завершить тест",
        confirmFinish: n => n > 0
            ? "Вы не ответили на вопросов: " + n + ". Завершить тест?"
            : "Завершить тест?",
        rightAnswer: "Правильный ответ ✓",
        wrongAnswer: "Неправильный ответ ✕",
        resultTitle: "Результат",
        totalQ: "Всего вопросов",
        correctQ: "Правильных ответов",
        wrongQ: "Неправильных ответов",
        passed: "Тест сдан",
        failed: "Тест не сдан",
        viewAnswers: "Посмотреть ответы",
        downloadCert: "Скачать сертификат",
        retake: "Пересдать",
        backHome: "На главную",
        backToResult: "Вернуться к результату",
        notAnswered: "На этот вопрос ответа нет",
        colDate: "Дата",
        colSubject: "Предмет",
        colType: "Тип",
        colResult: "Результат",
        colAnswers: "Ответы",
        colCert: "Сертификат",
        typeFIRST: "Первая попытка",
        typeRETAKE: "Пересдача",
        view: "Смотреть",
        download: "Скачать",
        noResults: "Пока нет пройденных тестов",
        edit: "Редактировать",
        save: "Сохранить",
        cancel: "Отмена",
        changePhoto: "Сменить фото",
        saved: "Данные сохранены",
        photoUpdated: "Фото обновлено",
        errPhotoSize: "Размер фото не должен превышать 2 МБ"
    },
    en: {
        brand: "Scientific and Methodological Center for Archival Training",
        menuTests: "Take a test",
        menuResults: "My results",
        menuProfile: "Profile",
        logout: "Log out",
        start: "Start",
        noSubjects: "No subjects yet",
        categoryLabel: "Category",
        allCategories: "All",
        otherCategory: "Other",
        subjectTitle: n => n + " (subject)",
        subjectMeta: (q, m) => q + " questions, " + m + " minutes",
        loginTitle: "Sign in",
        noAccount: "Don't have an account yet?",
        haveAccount: "Already have an account?",
        register: "Sign up",
        signIn: "Sign in",
        login: "Username",
        password: "Password",
        forgotLink: "Forgot your password?",
        errFillLogin: "Enter your username and password",
        firstName: "First name",
        email: "E-mail",
        confirmPassword: "Repeat password",
        codeLabel: "Verification code",
        codeHint: e => "Enter the code sent to " + e + " (check your Spam folder if you don't see it)",
        resend: "Resend",
        resendWait: n => "Resend (" + n + ")",
        errMismatch: "Passwords do not match",
        errEnterCode: "Enter the code",
        forgotTitle: "Reset password",
        forgotHint: "A verification code will be sent to your e-mail",
        confirmBtn: "Confirm",
        newPassword: "Enter new password",
        newPasswordRepeat: "Repeat new password",
        passwordChanged: "Password changed successfully. Now sign in with your new password.",
        prev: "‹ Previous",
        next: "Next ›",
        finishTest: "Finish test",
        confirmFinish: n => n > 0
            ? "You have " + n + " unanswered questions. Finish the test?"
            : "Finish the test?",
        rightAnswer: "Correct answer ✓",
        wrongAnswer: "Wrong answer ✕",
        resultTitle: "Result",
        totalQ: "Total questions",
        correctQ: "Correct answers",
        wrongQ: "Wrong answers",
        passed: "Passed",
        failed: "Failed",
        viewAnswers: "View answers",
        downloadCert: "Download certificate",
        retake: "Retake",
        backHome: "Back to home",
        backToResult: "Back to result",
        notAnswered: "This question was not answered",
        colDate: "Date",
        colSubject: "Subject",
        colType: "Type",
        colResult: "Result",
        colAnswers: "Answers",
        colCert: "Certificate",
        typeFIRST: "First attempt",
        typeRETAKE: "Retake",
        view: "View",
        download: "Download",
        noResults: "No completed tests yet",
        edit: "Edit",
        save: "Save",
        cancel: "Cancel",
        changePhoto: "Change photo",
        saved: "Changes saved",
        photoUpdated: "Photo updated",
        errPhotoSize: "Photo size must not exceed 2 MB"
    }
};

// t('start') yoki t('subjectTitle', 'Arxiv ish')
function t(key, ...args) {
    const dict = I18N[getLang()] || I18N.uz;
    const value = dict[key];
    return typeof value === "function" ? value(...args) : value;
}

// data-i18n="kalit" bo'lgan hamma elementning matnini almashtiradi
function applyTranslations() {
    document.documentElement.lang = getLang();
    document.querySelectorAll("[data-i18n]").forEach(el => {
        const value = t(el.dataset.i18n);
        if (typeof value === "string") {
            el.textContent = value;
        }
    });
}

// ---------- Til tablari ----------
// onChange(lang): til almashganda (matnlar almashgandan keyin) chaqiriladi

function renderLangTabs(onChange) {
    const box = document.getElementById("langTabs");
    if (!box) {
        return;
    }
    const langs = [
        { code: "uz", text: "O'zbek" },
        { code: "ru", text: "Русский" },
        { code: "en", text: "English" }
    ];

    function draw() {
        box.innerHTML = "";
        langs.forEach(l => {
            const btn = document.createElement("button");
            btn.type = "button";
            btn.className = "lang-tab" + (l.code === getLang() ? " active" : "");
            btn.textContent = l.text;
            btn.addEventListener("click", () => {
                setLang(l.code);
                draw();
                applyTranslations();
                if (onChange) {
                    onChange(l.code);
                }
            });
            box.appendChild(btn);
        });
    }
    draw();
}

document.addEventListener("DOMContentLoaded", applyTranslations);