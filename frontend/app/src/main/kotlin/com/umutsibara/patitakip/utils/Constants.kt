package com.umutsibara.patitakip.utils

object Constants {
    // API Base URL - FİZİKSEL CİHAZ İÇİN (Bilgisayarınızın IP adresini yazın)
    const val BASE_URL = "http://192.168.1.4:3000/api/" // Sunucu IP adresi
    
    // Shared Preferences Keys
    const val PREF_NAME = "PatiTakip_Prefs"
    const val KEY_TOKEN = "jwt_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Request Codes
    const val REQUEST_IMAGE_CAPTURE = 1001
    const val REQUEST_IMAGE_PICK = 1002
    const val REQUEST_LOCATION_PERMISSION = 1003
    
    // Category Types
    const val CATEGORY_REPORT = "REPORT"
    const val CATEGORY_FEEDING = "FEEDING"
    const val CATEGORY_ADOPTION = "ADOPTION"
    const val CATEGORY_SERVICE = "SERVICE"
    const val CATEGORY_DONATION = "DONATION"
    
    // Report Types
    const val REPORT_TYPE_HEALTH = "Saglik"
    const val REPORT_TYPE_FOOD = "Ac lik"
    const val REPORT_TYPE_LOST = "Kayip"
    const val REPORT_TYPE_GENERAL = "Genel"
    
    // Animal Types
    const val ANIMAL_TYPE_CAT = "STREET_CAT"
    const val ANIMAL_TYPE_DOG = "STREET_DOG"
    const val ANIMAL_TYPE_BIRD = "BIRD"
    const val ANIMAL_TYPE_OTHER = "OTHER"
    
    // Message Types
    const val MESSAGE_TYPE_TEXT = "metin"
    const val MESSAGE_TYPE_IMAGE = "resim"
    const val MESSAGE_TYPE_LOCATION = "konum"
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val DEFAULT_OFFSET = 0
    
    // Map Settings
    const val DEFAULT_ZOOM = 15f
    const val DEFAULT_RADIUS_KM = 5.0
}
