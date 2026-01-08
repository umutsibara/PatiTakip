const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');

const PHOTO_PATH = 'c:\\Users\\Umut\\Desktop\\_SoftwareDev\\_Projeler\\PatiTakip\\foto.jpg';
const BACKEND_URL = 'http://localhost:3000';

async function verifyReportImage() {
    try {
        console.log('1. Login...');
        const loginResponse = await axios.post(`${BACKEND_URL}/api/users/login`, {
            eposta: 'u@gmail.com',
            sifre: '1234'
        });
        const token = loginResponse.data.data.token;
        const userId = loginResponse.data.data.id;
        console.log('Login successful.');

        console.log('2. Upload Photo...');
        const form = new FormData();
        form.append('photo', fs.createReadStream(PHOTO_PATH));
        const uploadResponse = await axios.post(`${BACKEND_URL}/api/upload/photo`, form, {
            headers: { ...form.getHeaders(), 'Authorization': `Bearer ${token}` }
        });
        const photoId = uploadResponse.data.data.foto_id;
        console.log(`Photo uploaded. ID: ${photoId}`);

        console.log('3. Create Report with Photo...');
        const reportResponse = await axios.post(`${BACKEND_URL}/api/reports`, {
            kullanici_id: userId,
            baslik: 'Test Report with Image',
            aciklama: 'Testing image integration',
            ihbar_turu: 'Genel',
            kategori: 'REPORT',
            hayvan_turu: 'Kedi',
            enlem: 41.0,
            boylam: 29.0,
            foto_id: photoId
        }, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const reportId = reportResponse.data.data.ihbar_id;
        console.log(`Report created. ID: ${reportId}`);

        console.log('4. Verify Image URL in Report List...');
        const listResponse = await axios.get(`${BACKEND_URL}/api/reports?category=REPORT`);
        const report = listResponse.data.data.find(r => r.id === reportId);
        
        if (report && report.imageUrls && report.imageUrls.length > 0) {
            console.log('SUCCESS: Report contains image URL:', report.imageUrls[0]);
        } else {
            console.error('FAILED: Report missing image URL', report);
        }

    } catch (error) {
        console.error('TEST FAILED:', error.message);
        if (error.response) console.error(error.response.data);
    }
}

verifyReportImage();
