# Test platformasi: Frontend

Onlayn test platformasining foydalanuvchi va admin interfeysi. Oddiy **HTML, CSS va JavaScript** bilan yozilgan, framework ishlatilmagan.

Backend: [test-platform-backend](https://github.com/Sardor1129/test-platform-backend)

## Sahifalar

**Foydalanuvchi**

| Sahifa | Fayl |
|---|---|
| Tizimga kirish | `index.html` |
| Ro'yxatdan o'tish (ism, email, login, parol, emailga kelgan kod) | `register.html` |
| Parol tiklash (email, kod, yangi parol) | `forgot.html` |
| Testlar ro'yxati | `tests.html` |
| Test ishlash (taymer, savollar navigatsiyasi) | `test.html` |
| Natija | `result.html` |
| Javoblarni ko'rish | `review.html` |
| Mening natijalarim | `results.html` |
| Profil | `profile.html` |

**Admin**

| Sahifa | Fayl |
|---|---|
| Natijalar | `admin.html` |
| Fanlar | `admin-subjects.html` |
| Savollar | `admin-questions.html` |

Interfeys uch tilda: O'zbek, Русский, English (til tablari orqali). Admin sahifalari o'zbek tilida.

Yorug' va qorong'u rejim: tepa o'ng burchakdagi tugma bilan almashtiriladi, tanlov brauzerda saqlanadi.

## Ishga tushirish

Frontend'ni backend ishlab turgan holda oching. Avval backend'ni ishga tushiring (`http://localhost:8080`). Sahifalarni `file://` orqali ochmang, brauzer so'rovlarni to'sib qo'yadi.

Ikki usul bor, ular `js/api.js` dagi `API_URL` qiymatiga bog'liq.

### 1-usul: backend ichidan (tavsiya)

Hamma fayllarni backend loyihasida `src/main/resources/static/` papkasiga ko'chiring va `js/api.js` ni shunday qiling:

```javascript
const API_URL = '';
```

Backend'ni ishga tushirib, `http://localhost:8080/index.html` ni oching. Frontend va API bitta manzilda bo'lgani uchun alohida server kerak emas. Shu usulda saytni internetga ham chiqarish oson.

**Diqqat:** IntelliJ'dagi "brauzerda ochish" ikonkasi sahifani boshqa portda (`63342`) ochadi, bunda `API_URL = ''` ishlamaydi va `Unexpected token '<'` xatosi chiqadi. Faqat `localhost:8080` manzilidan oching.

### 2-usul: frontend alohida (ishlab chiqish uchun)

Frontend loyihasidagi `js/api.js` da backend manzilini ko'rsating:

```javascript
const API_URL = 'http://localhost:8080';
```

Keyin quyidagilardan biri bilan oching:

**IntelliJ IDEA (Ultimate):** `index.html` ni oching va tahrirlagich o'ng yuqorisidagi brauzer ikonkasini bosing.

**Python:** loyiha papkasida terminalda

```
python -m http.server 5500
```

keyin brauzerda `http://localhost:5500/index.html` ni oching.

**VS Code:** Live Server kengaytmasi bilan `index.html` ni oching.

Bu usulda backend frontend manzilidan keladigan so'rovlarga ruxsat bergan bo'lishi kerak (CORS).

> Frontend'ni backend'ga ko'chirganda `api.js` ni nusxalamang yoki nusxalagandan keyin `API_URL = ''` qiling. Aks holda ikkala usul aralashib, xato chiqadi.

## Kirish ma'lumotlari

Admin backend sozlamalarida (`ADMIN_PASSWORD` muhit o'zgaruvchisi) belgilanadi. Oddiy foydalanuvchi ro'yxatdan o'tish sahifasida yaratiladi: ism, email, login, parol kiritiladi, tasdiqlash kodi emailga keladi. Backend `app.mail.enabled: false` rejimida bo'lsa, kod backend konsolida chiqadi.

## Loyiha tuzilmasi

```
frontend/
├── index.html, register.html, forgot.html ...   sahifalar
├── css/
│   └── style.css            umumiy uslublar va ranglar (yorug'/qorong'u)
├── js/
│   ├── api.js               backend bilan aloqa (token, so'rovlar, fayl yuklash)
│   ├── i18n.js              tarjimalar (uz, ru, en)
│   ├── theme.js             yorug'/qorong'u rejim tugmasi
│   ├── layout.js            foydalanuvchi menyusi va til tablari
│   ├── admin-layout.js      admin menyusi
│   └── <sahifa>.js          har sahifaning o'z fayli
└── img/                     rasmlar
```

## Qanday ishlaydi

- Tizimga kirgach, JWT token brauzerning `localStorage` ida saqlanadi va `api.js` uni har so'rovga avtomatik qo'shadi.
- Token eskirsa yoki yaroqsiz bo'lsa, foydalanuvchi login sahifasiga qaytariladi.
- Javob belgilanishi bilan serverga saqlanadi, to'g'ri yoki noto'g'riligi darrov ko'rsatiladi va javob qulflanadi. Sahifa yangilansa ham javoblar yo'qolmaydi.
- Javob belgilangach test o'zi keyingi javobsiz savolga o'tadi, hamma savolga javob berilganda esa avtomatik yakunlanadi.
- Variantlar harflarsiz ko'rsatiladi, tartibi har urinishda aralashadi.
- Taymer qolgan soniyani serverdan oladi va vaqt tugaganda testni o'zi yakunlaydi.
- Sertifikat PDF token bilan yuklab olinadi va faqat testdan o'tganda ko'rinadi.
- Ranglar `css/style.css` ning boshidagi `:root` va `:root[data-theme="dark"]` bloklarida turadi. Mavzuni o'zgartirish yoki yangi rang qo'shish uchun faqat shu yerga tegish kifoya.
- Sahifalardagi matnlar `textContent` bilan qo'yiladi, shuning uchun foydalanuvchi kiritgan matn sahifani buza olmaydi.
