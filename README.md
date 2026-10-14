# Test platformasi: Backend

Foydalanuvchilar ro'yxatdan o'tib, tizimga kirib, fanlar bo'yicha onlayn test topshiradigan platformaning server qismi (REST API).

Frontend: [test-platform-frontend](https://github.com/Sardor1129/test-platform-frontend)

## Imkoniyatlar

**Foydalanuvchi**
- SMS kod bilan ro'yxatdan o'tish va parolni tiklash
- JWT orqali tizimga kirish
- Fan va til (O'zbek / Русский / English) tanlab test topshirish
- Savollar har safar tasodifiy tanlanadi, 1, 2, 3... tartibida raqamlanadi
- Taymer serverda nazorat qilinadi, vaqti tugagan test avtomatik yakunlanadi
- Natijani serverda hisoblash (foiz, to'g'ri va noto'g'ri javoblar soni)
- Javoblarni ko'rish (to'g'ri va xato javoblar belgilanadi)
- Mening natijalarim ro'yxati
- Testdan o'tganda PDF sertifikat (faqat o'tgan urinish uchun)
- Profilni tahrirlash va rasm yuklash

**Admin**
- Fan qo'shish, o'zgartirish, o'chirish (test vaqti va savollar sonini belgilash)
- Savol qo'shish, o'zgartirish, o'chirish (4 variant, bittasi to'g'ri)
- Barcha foydalanuvchilar natijalarini ko'rish va filtrlash

## Texnologiyalar

- Java 17
- Spring Boot 3 (Web, Data JPA, Security, Validation)
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

`src/main/resources/application-example.yml` faylidan nusxa olib, nomini `application.yml` qiling va qiymatlarni to'ldiring:

```yaml
spring:
  datasource:
    password: BAZA_PAROLI
app:
  jwt:
    secret: KAMIDA_32_BELGILI_MAXFIY_KALIT
  admin:
    login: admin
    password: ADMIN_PAROLI
```

`application.yml` git'ga yuklanmaydi (`.gitignore` da), shuning uchun parollar xavfsiz qoladi.

### 4. Shrift (sertifikat uchun)

`DejaVuSans.ttf` faylini `src/main/resources/fonts/` papkasiga qo'ying. Shrift kirill va maxsus belgilarni PDF'da to'g'ri chiqarish uchun kerak.

### 5. Ishga tushirish

```
mvn spring-boot:run
```

yoki IntelliJ IDEA'da `BackendApplication` ni Run qiling. Server `http://localhost:8080` da ishlaydi. Jadvallar birinchi ishga tushirishda avtomatik yaratiladi, admin foydalanuvchi ham o'zi yaratiladi.

## SMS kodlari

Hozirgi holatda tasdiqlash kodi telefonga yuborilmaydi, **backend konsoliga** chiqadi:

```
SMS -> +998901234567 : Tasdiqlash kodi: 483920
```

Haqiqiy SMS yuborish uchun `SmsService` ichini SMS provayderga (masalan Eskiz.uz) ulash kifoya, qolgan kodga tegish shart emas.

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

## Loyiha tuzilmasi

```
uz.testplatform.backend
├── config        xavfsizlik, JWT, sozlamalar
├── entity        bazadagi jadvallar
├── repository    bazaga so'rovlar
├── dto           so'rov va javob obyektlari
├── service       biznes mantiq
├── controller    API endpointlar
└── exception     xatolarni qaytarish
```

## Muhim qoidalar

- To'g'ri javoblar test davomida foydalanuvchiga yuborilmaydi.
- Natija faqat serverda hisoblanadi.
- Javoblarni ko'rish faqat test yakunlangandan keyin ishlaydi.
- Sertifikat faqat o'tish foizidan (standart 60%) yuqori natijada beriladi.
- Testlarda ishlatilgan savol yoki fan o'chirilmaydi, natijalar saqlanib qoladi.
