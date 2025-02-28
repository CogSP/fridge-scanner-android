# 🧊 Fridge Scanner

Fridge Scanner is a mobile application designed to help users **manage fridge inventory** efficiently. The app allows users to **scan** and **track food items**, **monitor expiry dates**, **receive notifications**, and **create shopping lists**.

## 🚀 Features

- **🏠 Manage multiple fridges**: Users can create and manage different fridges.
- **🔍 Scan and add items**: Add food items by scanning barcodes.
- **📅 Expiry notifications**: Get alerts for expiring food.
- **🛒 Shopping list**: Easily add missing items.
- **👥 Share fridges**: Collaborate with family and friends.
- **📡 Cloud Sync**: Items are stored and retrieved via a backend API.

---

## 📂 Project Structure

```
📂 FridgeScanner
├── 📂 api                     # API service layer
│   ├── ApiClient.kt           # Retrofit client
│   ├── AuthService.kt         # User authentication service
│   ├── FridgeApiService.kt    # API service for fridge operations
│
├── 📂 data                    # Data models and repository
│   ├── FridgeItem.kt          # Represents fridge items
│   ├── ProductModel.kt        # Product-related data structures
│   ├── ShoppingItem.kt        # Shopping list item model
│   ├── FridgeRepository.kt    # Handles data operations
│
├── 📂 ui                      # UI components
│   ├── 📂 fridgeui
│   │   ├── FridgeScreen.kt    # Displays fridge contents
│   │   ├── FridgeItemCard.kt  # UI for individual items
│   │   ├── ManageFridgesScreen.kt # Manage fridge settings
│   │
│   ├── 📂 login
│   │   ├── Login.kt           # User login screen
│   │   ├── RegisterScreen.kt  # Registration screen
│   │   ├── ForgotPasswordScreen.kt # Password reset
│   │
│   ├── 📂 scanitems
│   │   ├── BarcodeScannerScreen.kt # Handles barcode scanning
│   │   ├── BarcodeOverlay.kt       # UI overlay for scanner
│   │   ├── ScanScreen.kt           # Scan & add items
│   │
│   ├── 📂 shoppinglist
│   │   ├── ShoppingListScreen.kt # Displays shopping list
│   │
│   ├── 📂 notification
│   │   ├── NotificationsScreen.kt  # Shows alerts
│   │   ├── NotificationUtils.kt    # Manages notification logic
│   │
│   ├── 📂 options
│   │   ├── OptionsScreen.kt     # App settings screen
│   │
│   ├── MainActivity.kt          # App entry point
│   ├── Navigation.kt            # Handles app navigation
│
├── 📂 utils                     # Utility functions
│   ├── ToastHelper.kt           # Helper for displaying toasts
│
└── 📂 theme                     # UI Styling
    ├── Color.kt                 # Defines app colors
    ├── Theme.kt                 # Theme configuration
    ├── Type.kt                  # Typography settings
```

---

## 🔧 Technologies Used

- **Kotlin** - Main programming language.
- **Jetpack Compose** - UI framework.
- **Retrofit** - API communication.
- **WorkManager** - Background tasks and notifications.
- **CameraX** - Barcode scanning support.
- **Material3** - UI design components.
- **ML Kit** - Barcode recognition.

---

## 🛠 Setup Instructions

### 1️⃣ Clone the repository

```sh
git clone https://github.com/yourusername/fridge-scanner.git
cd fridge-scanner
```

### 2️⃣ Open the project in **Android Studio**.

### 3️⃣ Configure dependencies:
- Ensure you have the required **Android SDKs** installed.
- Check `ApiClient.kt` for the backend API URL.

### 4️⃣ Run the app on an **emulator or a physical device**.

---

## 🌍 API Endpoints

### **User Authentication**
- `POST /api/user/login` - Login a user.
- `POST /api/user/create` - Register a user.
- `POST /api/user/reset` - Reset password.

### **Fridge Management**
- `POST /api/fridge/create` - Create a fridge.
- `POST /api/fridges/get` - Fetch fridges.
- `POST /api/fridge/share` - Share a fridge.
- `POST /api/fridges/remove` - Delete fridges.

### **Fridge Items**
- `POST /api/fridgeitems/get` - Get items in a fridge.
- `POST /api/fridgeitem/remove` - Remove an item.
- `POST /api/product` - Fetch product details via barcode scanning.

---

## 📌 Contributions are welcome!

### 🎉 Enjoy using **Fridge Scanner** and never waste food again! 🥦🥛🍞

