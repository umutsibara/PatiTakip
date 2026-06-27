# Fotoğraf Yükleme API

## Endpoint
```
POST http://localhost:3000/api/upload/photo
```

## Kullanım
**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: multipart/form-data
```

**Body (Form Data):**
- Field adı: `photo`
- Dosya tipi: Image (jpg, png, webp vb.)
- Max boyut: 5MB

## Response
```json
{
  "success": true,
  "message": "Fotoğraf başarıyla yüklendi.",
  "data": {
    "foto_id": 1,
    "url": "/uploads/photos/foto-1766570272904-360042111.webp"
  }
}
```

**`foto_id`** = Veritabanı ID'si (ihbar/besleme kaydederken kullan)  
**`url`** = Fotoğraf yolu (Tam URL: `http://localhost:3000` + url)
