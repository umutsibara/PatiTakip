const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');

async function testUpload() {
    try {
        // 1. Login
        console.log('Login yapılıyor...');
        const loginResponse = await axios.post('http://localhost:3000/api/users/login', {
            eposta: 'u@gmail.com',
            sifre: '1234'
        });
        
        console.log('Login başarılı!');
        console.log('User ID:', loginResponse.data.data.id);
        const token = loginResponse.data.data.token;
        console.log('Token:', token.substring(0, 50) + '...');
        
        // 2. Upload Photo
        console.log('\nFotoğraf yükleniyor...');
        const form = new FormData();
        form.append('photo', fs.createReadStream('images.jpg'));
        
        const uploadResponse = await axios.post('http://localhost:3000/api/upload/photo', form, {
            headers: {
                ...form.getHeaders(),
                'Authorization': `Bearer ${token}`
            }
        });
        
        console.log('\nFotoğraf yükleme başarılı!');
        console.log('Response:', JSON.stringify(uploadResponse.data, null, 2));
        
    } catch (error) {
        console.error('Hata:', error.response ? error.response.data : error.message);
    }
}

testUpload();
