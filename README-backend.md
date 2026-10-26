# Test platformasi: Backend

Foydalanuvchilar ro'yxatdan o'tib, tizimga kirib, fanlar bo'yicha onlayn test topshiradigan platformaning server qismi (REST API).

Frontend: [test-platform-frontend](https://github.com/Sardor1129/test-platform-frontend)

## Imkoniyatlar

**Foydalanuvchi**
- Email orqali ro'yxatdan o'tish: ism, email, login va parol kiritiladi, tasdiqlash kodi emailga yuboriladi
- Parolni tiklash: kod emailga yuboriladi
- JWT orqali tizimga kirish
- Fan va til (O'zbek / Русский / English) tanlab test topshirish
- Savollar har safar tasodifiy tanlanadi, 1, 2, 3... tartibida raqamlanadi, variantlar tartibi ham har urinishda aralashadi
- Javob belgilanishi bilan to'g'ri yoki noto'g'riligi ko'rsatiladi va javob qulflanadi (o'zgartirib bo'lmaydi)
- Taymer serverda nazorat qilinadi, vaqti tugagan test avtomatik yakunlanadi
- Natijani serverda hisoblash (foiz, to'g'ri va noto'g'ri javoblar soni)
- Javoblarni ko'rish (to'g'ri va xato javoblar belgilanadi)
- Mening natijalarim ro'yxati
- Testdan o'tganda PDF sertifikat (faqat o'tgan urinish uchun)
- Profilni tahrirlash (ism, login) va rasm yuklash

**Admin**
- Fan qo'shish, o'zgartirish, o'chirish: test vaqti, savollar soni va **o'tish foizi** har fan uchun alohida belgilanadi
- Savol qo'shish, o'zgartirish, o'chirish (4 variant, bittasi to'g'ri)
- Barcha foydalanuvchilar natijalarini ko'rish va filtrlash

## Texnologiyalar

- Java 17
- Spring Boot 3 (Web, Data JPA, Security, Validation, Mail)
- PostgreSQL
- JWT (jjwt)
- OpenPDF (sertifikat)
- Maven, Lombok

## Ishga tushirish

### 1. Kerakli dasturlar
- JDK 17 yoki yuqori
- Maven
- PostgreSQL

### 2. Bazani yaratish

```sql
CREATE DATABASE test_platform;
```

### 3. Sozlamalar

`src/main/resources/application-example.yml` faylidan nusxa olib, nomini `application.yml` qiling. Maxfiy qiymatlar faylga yozilmaydi, ular **muhit o'zgaruvchilaridan** o'qiladi:

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
  mail:
    host: smtp.gmail.com
    port: 465
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          ssl:
            enable: true
app:
  jwt:
    secret: ${JWT_SECRET}
  admin:
    login: admin
    password: ${ADMIN_PASSWORD}
  mail:
    enabled: true
    from: ${MAIL_USERNAME}
```

Muhit o'zgaruvchilari:

| O'zgaruvchi | Ma'nosi |
|---|---|
| `DB_PASSWORD` | PostgreSQL paroli |
| `JWT_SECRET` | Kamida 32 belgili tasodifiy maxfiy kalit |
| `ADMIN_PASSWORD` | Admin paroli (kuchli parol qo'ying) |
| `MAIL_USERNAME` | Xat yuboriladigan Gmail manzili |
| `MAIL_PASSWORD` | Gmail **ilova paroli** (16 belgi) |

IntelliJ IDEA'da: **Run → Edit Configurations → Environment variables** ga `DB_PASSWORD=...;JWT_SECRET=...;ADMIN_PASSWORD=...;MAIL_USERNAME=...;MAIL_PASSWORD=...` ko'rinishida yozing (`;` bilan ajratiladi).

`application.yml` git'ga yuklanmaydi (`.gitignore` da), parollar repoga tushmaydi.

### 4. Shrift (sertifikat uchun)

`DejaVuSans.ttf` faylini `src/main/resources/fonts/` papkasiga qo'ying. Shrift kirill va maxsus belgilarni PDF'da to'g'ri chiqarish uchun kerak.

### 5. Ishga tushirish

```
mvn spring-boot:run
```

yoki IntelliJ IDEA'da `BackendApplication` ni Run qiling. Server `http://localhost:8080` da ishlaydi. Jadvallar birinchi ishga tushirishda avtomatik yaratiladi, admin foydalanuvchi ham o'zi yaratiladi.

## Email orqali kodlar

Ro'yxatdan o'tish va parolni tiklash kodlari foydalanuvchining emailiga Gmail SMTP orqali yuboriladi.

**Gmail sozlash:**
1. Platforma uchun alohida Gmail hisobi oching.
2. Hisobda 2 bosqichli tasdiqlashni yoqing.
3. [Ilova parollari](https://myaccount.google.com/apppasswords) sahifasida 16 belgili parol yarating va `MAIL_PASSWORD` ga yozing.

**Kod qoidalari:**
- Kod 5 daqiqa amal qiladi.
- Yangi kod so'rash orasida kamida 1 daqiqa kutiladi.
- Har bir kod uchun 5 ta urinish beriladi, undan keyin yangi kod kerak.

**Sinov rejimi:** `app.mail.enabled: false` qilinsa, xat yuborilmaydi, kod **backend konsoliga** chiqadi:

```
EMAIL (test) -> user@example.com : kod 483920
```

Eslatma: Gmail oddiy hisobdan kuniga taxminan 500 ta xat yuborishga ruxsat beradi. Foydalanuvchilar ko'payganda Brevo yoki Resend kabi xizmatga o'tish mumkin, buning uchun faqat `MailService` ichini o'zgartirish kifoya.

## API'ning qisqacha ro'yxati

| Qism | Endpointlar |
|---|---|
| Auth | `POST /api/auth/login`, `/register/send-code`, `/register/resend-code`, `/register/confirm`, `/password/send-code`, `/password/verify-code`, `/password/reset` |
| Fanlar | `GET /api/subjects?lang=uz` |
| Test | `POST /api/attempts`, `GET /api/attempts/{id}`, `PUT /api/attempts/{id}/answers`, `POST /api/attempts/{id}/finish`, `GET /api/attempts/{id}/result` |
| Natijalar | `GET /api/attempts`, `GET /api/attempts/{id}/review`, `GET /api/attempts/{id}/certificate` |
| Profil | `GET /api/profile`, `PUT /api/profile`, `POST /api/profile/avatar` |
| Admin | `/api/admin/subjects`, `/api/admin/questions`, `GET /api/admin/results` |

`/api/auth/**` ochiq, `/api/admin/**` faqat ADMIN roli uchun, qolganlari tizimga kirgan foydalanuvchi uchun. So'rovlarda `Authorization: Bearer <token>` sarlavhasi kerak.

## Frontend bilan birga ishlatish

Frontend fayllarini (HTML, CSS, JS, rasmlar) `src/main/resources/static/` papkasiga ko'chirsangiz, backend saytni ham o'zi beradi: `http://localhost:8080/index.html`. Bunda `js/api.js` dagi `API_URL` bo'sh (`''`) bo'lishi kerak, so'rovlar o'sha manzilning o'ziga ketadi.

Ochiq sahifalar (`/`, `*.html`, `/css`, `/js`, `/img`, `/uploads`) `SecurityConfig` da hamma uchun ruxsat etilgan.

## Internetga ochish (sinov uchun)

Do'stlar va tanishlar sinab ko'rishi uchun backend'ni vaqtincha internetga chiqarish mumkin. [Cloudflare Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/) ro'yxatdan o'tmasdan ishlaydi:

```
cloudflared tunnel --url http://localhost:8080
```

Terminalda `https://...trycloudflare.com` manzili chiqadi. Kompyuter va backend ishlab turgan vaqtdagina sayt ochiq, tunnel qayta yoqilganda manzil o'zgaradi. Internetga chiqarishdan oldin parollar va `JWT_SECRET` kuchli ekaniga ishonch hosil qiling. Doimiy ishlashi uchun VPS yoki hosting kerak.

## Loyiha tuzilmasi

```
uz.testplatform.backend
├── config        xavfsizlik, JWT, sozlamalar
├── entity        bazadagi jadvallar
├── repository    bazaga so'rovlar
├── dto           so'rov va javob obyektlari
├── service       biznes mantiq (auth, mail, test, sertifikat)
├── controller    API endpointlar
└── exception     xatolarni qaytarish
```

## Muhim qoidalar

- Javob belgilanmagan savolning to'g'ri javobi foydalanuvchiga yuborilmaydi. Javob belgilanganda natija ko'rsatiladi va javob qulflanadi.
- Natija faqat serverda hisoblanadi.
- Javoblarni ko'rish faqat test yakunlangandan keyin ishlaydi.
- Sertifikat faqat fan uchun belgilangan o'tish foizidan yuqori natijada beriladi.
- Variantlar tartibi urinish raqamidan hisoblanadi: bir urinishda doim bir xil, boshqa urinishda boshqacha.
- Testlarda ishlatilgan savol yoki fan o'chirilmaydi, natijalar saqlanib qoladi.
