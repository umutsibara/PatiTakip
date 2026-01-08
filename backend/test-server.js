const express = require('express');
const app = express();

app.get('/', (req, res) => {
    res.json({ message: 'Test Server Running!' });
});

const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Test sunucu ${PORT} portunda çalışıyor...`);
});

console.log('After listen call');
