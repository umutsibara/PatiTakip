import requests
import json

# 1. Login
login_url = 'http://localhost:3000/api/users/login'
login_data = {'eposta': 'u@gmail.com', 'sifre': '1234'}
login_response = requests.post(login_url, json=login_data)
login_result = login_response.json()

print(f"Login Status: {login_response.status_code}")
print(f"Login Response: {json.dumps(login_result, indent=2)}")

if login_result.get('success'):
    token = login_result['data']['token']
    print(f"\nToken: {token[:50]}...")
    
    # 2. Upload Photo
    upload_url = 'http://localhost:3000/api/upload/photo'
    headers = {'Authorization': f'Bearer {token}'}
    
    with open('images.jpg', 'rb') as f:
        files = {'photo': ('images.jpg', f, 'image/jpeg')}
        upload_response = requests.post(upload_url, headers=headers, files=files)
    
    print(f"\nUpload Status: {upload_response.status_code}")
    print(f"Upload Response: {json.dumps(upload_response.json(), indent=2)}")
else:
    print("Login failed!")
