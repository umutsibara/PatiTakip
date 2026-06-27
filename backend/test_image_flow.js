const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');
const path = require('path');

// User provided photo path
const PHOTO_PATH = 'c:\\Users\\Umut\\Desktop\\_SoftwareDev\\_Projeler\\PatiTakip\\foto.jpg';
const BACKEND_URL = 'http://localhost:3000';

async function testImageFlow() {
    try {
        console.log('1. Checking headers and login...');
        const loginResponse = await axios.post(`${BACKEND_URL}/api/users/login`, {
            eposta: 'u@gmail.com',
            sifre: '1234'
        });
        
        const token = loginResponse.data.data.token;
        console.log('Login successful. Token obtained.');

        console.log(`2. Uploading photo from ${PHOTO_PATH}...`);
        if (!fs.existsSync(PHOTO_PATH)) {
            throw new Error(`File not found at ${PHOTO_PATH}`);
        }

        const form = new FormData();
        form.append('photo', fs.createReadStream(PHOTO_PATH));

        const uploadResponse = await axios.post(`${BACKEND_URL}/api/upload/photo`, form, {
            headers: {
                ...form.getHeaders(),
                'Authorization': `Bearer ${token}`
            }
        });

        console.log('Upload successful!');
        const data = uploadResponse.data.data;
        const imageUrl = data.url;
        const imageId = data.foto_id;
        
        console.log(`Image ID: ${imageId}`);
        console.log(`Image URL Path: ${imageUrl}`);

        const fullImageUrl = `${BACKEND_URL}${imageUrl}`;
        console.log(`3. Attempting to retrieve image from ${fullImageUrl}...`);

        const getResponse = await axios.get(fullImageUrl, { responseType: 'arraybuffer' });
        
        if (getResponse.status === 200) {
            console.log(`SUCCESS: Image retrieved! Size: ${getResponse.data.length} bytes.`);
            console.log('Verification Complete: Image upload and retrieval works.');
        } else {
            console.error(`FAILED: Retrieve status code ${getResponse.status}`);
        }

    } catch (error) {
        console.error('TEST FAILED');
        if (error.response) {
            console.error('Status:', error.response.status);
            console.error('Data:', error.response.data);
        } else {
            console.error('Error:', error.message);
        }
    }
}

testImageFlow();
