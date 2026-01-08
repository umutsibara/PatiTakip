// Test all route imports
console.log('Testing route imports...\n');

try {
    console.log('1. reportRoutes...');
    require('./routes/reportRoutes');
    console.log('✅ reportRoutes OK');
} catch (e) {
    console.error('❌ reportRoutes ERROR:', e.message);
}

try {
    console.log('2. userRoutes...');
    require('./routes/userRoutes');
    console.log('✅ userRoutes OK');
} catch (e) {
    console.error('❌ userRoutes ERROR:', e.message);
}

try {
    console.log('3. extraRoutes...');
    require('./routes/extraRoutes');
    console.log('✅ extraRoutes OK');
} catch (e) {
    console.error('❌ extraRoutes ERROR:', e.message);
}

try {
    console.log('4. uploadRoutes...');
    require('./routes/uploadRoutes');
    console.log('✅ uploadRoutes OK');
} catch (e) {
    console.error('❌ uploadRoutes ERROR:', e.message);
}

try {
    console.log('5. referenceRoutes...');
    require('./routes/referenceRoutes');
    console.log('✅ referenceRoutes OK');
} catch (e) {
    console.error('❌ referenceRoutes ERROR:', e.message);
}

try {
    console.log('6. healthRecordRoutes...');
    require('./routes/healthRecordRoutes');
    console.log('✅ healthRecordRoutes OK');
} catch (e) {
    console.error('❌ healthRecordRoutes ERROR:', e.message);
}

try {
    console.log('7. adminRoutes...');
    require('./routes/adminRoutes');
    console.log('✅ adminRoutes OK');
} catch (e) {
    console.error('❌ adminRoutes ERROR:', e.message);
}

try {
    console.log('8. donationRoutes...');
    require('./routes/donationRoutes');
    console.log('✅ donationRoutes OK');
} catch (e) {
    console.error('❌ donationRoutes ERROR:', e.message);
}

try {
    console.log('9. serviceRoutes...');
    require('./routes/serviceRoutes');
    console.log('✅ serviceRoutes OK');
} catch (e) {
    console.error('❌ serviceRoutes ERROR:', e.message);
}

try {
    console.log('10. commentRoutes...');
    require('./routes/commentRoutes');
    console.log('✅ commentRoutes OK');
} catch (e) {
    console.error('❌ commentRoutes ERROR:', e.message);
}

try {
    console.log('11. likeRoutes...');
    require('./routes/likeRoutes');
    console.log('✅ likeRoutes OK');
} catch (e) {
    console.error('❌ likeRoutes ERROR:', e.message);
}

try {
    console.log('12. chatRoutes...');
    require('./routes/chatRoutes');
    console.log('✅ chatRoutes OK');
} catch (e) {
    console.error('❌ chatRoutes ERROR:', e.message);
}

console.log('\n✅ All imports tested!');
