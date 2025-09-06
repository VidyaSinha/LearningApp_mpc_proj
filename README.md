
<div style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: center;">

  <img width="180" alt="Splash Screen" src="https://github.com/user-attachments/assets/788d5b2c-1359-49be-a577-b608d3ba097f" />
  
  <img width="180" alt="Login Screen" src="https://github.com/user-attachments/assets/1b96d618-c561-42a2-b7ba-d866f5d97e6f" />
 
 
  <img width="180" alt="History Screen" src="https://github.com/user-attachments/assets/bab9c111-cdc6-458f-a577-92b2432ae754" />

  <img width="180" alt="Quiz Screen" src="https://github.com/user-attachments/assets/32e20114-1f06-40b1-8c8e-bcb993501d29" />
   <img width="180" alt="Result Screen" src="https://github.com/user-attachments/assets/5009530a-6109-4cd6-bf8f-c84fd2ebcc32" />

   <img width="180" alt="Main Screen" src="https://github.com/user-attachments/assets/439dac78-61d9-4c34-b9cb-4f8a3651834e" />


  

</div>


##  Overview

ObjectLingo is a **kid-friendly, camera-first learning application** that transforms object detection into an engaging educational experience. Children can capture photos of objects around them and learn about them through AI-powered detection, interactive quizzes, and a beautiful, bubbly user interface designed specifically for young learners.

##  Key Features

###  **Core Learning Features**
- **Smart Camera Integration**: Capture photos with the device camera or select from gallery
- **AI-Powered Object Detection**: Uses ML Kit for real-time object recognition and labeling
- **Detailed Object Analysis**: Provides confidence scores, color, size, and quality information
- **Visual Annotations**: Draws bounding boxes and labels directly on detected objects

###  **Interactive Learning**
- **Quiz System**: Interactive multiple-choice quizzes based on detected objects
- **Learning History**: Track all discovered objects with detailed metadata
- **Favorites System**: Mark favorite discoveries for easy access
- **Progress Tracking**: Monitor learning progress and achievements

### **Kid-Friendly Design**
- **Vibrant, Bubbly UI**: Bright colors, rounded corners, and playful gradients
- **Emoji-Rich Interface**: Engaging emojis throughout the app for better child engagement
- **Large Touch Targets**: Buttons and elements sized appropriately for small fingers
- **Prominent Image Display**: Images take up 50% of the screen for better visibility
- **Encouraging Messages**: Positive, child-friendly feedback and instructions

### **User Management**
- **User Authentication**: Secure login and signup system
- **Persistent Storage**: All learning data saved locally on device
- **Session Management**: Seamless user experience with proper back stack handling

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: AndroidX with Material Design 3
- **Machine Learning**: Google ML Kit (Object Detection + Image Labeling)
- **Architecture**: MVVM with Activity Result APIs
- **Storage**: Local file system + SharedPreferences (JSON)
- **Image Processing**: Android Canvas API for annotations



###  Splash Screen
- **App Branding**: Clean, professional splash screen with app logo
- **Loading Animation**: Progress bar showing app initialization
- **Smooth Transitions**: Seamless flow to main application

### Main Screen
- **Hero Image Area**: Large, prominent image display (50% of screen)
- **Camera Hint Overlay**: Encouraging "Tap to take a photo!" message
- **Action Buttons**: 
  - **Camera Button**: Direct camera access with fun styling
  - **Gallery Button**: Photo picker integration
- **Quick Actions Panel**:
  - **Recent**: View last 5 discoveries
  - **Favorites**: Access starred items
  - **History**: Complete learning history

### Quiz Screen
- **Image Display**: Full-screen object image for analysis
- **Multiple Choice Questions**: Interactive quiz format
- **Progress Tracking**: Question counter and navigation
- **Answer Selection**: Radio button interface for answers

### History Screen
- **Discovery List**: Scrollable list of all learned objects
- **Thumbnail Previews**: Small image previews for each item
- **Detailed Information**: Object name, confidence, color, size, quality
- **Management Actions**: Star favorites and delete items
- **Search & Filter**: Find specific discoveries quickly

### Result Screen
- **Score Display**: Clear presentation of quiz results
- **Performance Feedback**: Encouraging messages based on performance
- **Progress Summary**: Overall learning progress
- **Action Buttons**: Continue learning or retry quizzes

###  Login Screen
- **User Authentication**: Secure login interface
- **Sign Up Integration**: Easy account creation
- **Form Validation**: Input validation and error handling
- **Remember Me**: Optional session persistence

## Getting Started

### Prerequisites
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK 24+ (Android 7.0+)
- Physical device or emulator with camera support

### Installation
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/objectlingo.git
   cd objectlingo
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Wait for Gradle sync to complete

3. **Run the application**
   - Connect a physical device or start an emulator
   - Click the "Run" button or use `Ctrl+R`
   - Grant camera permissions when prompted

### First Launch
1. **Splash Screen**: App initializes and loads ML models
2. **Login/Signup**: Create account or sign in
3. **Main Screen**: Start discovering objects around you!

## 📖 Usage Guide

### Taking Photos
1. **Camera**: Tap the 📷 Camera button to take a new photo
2. **Gallery**: Tap the 🖼️ Gallery button to select existing photos
3. **Detection**: Wait for AI analysis (usually 2-3 seconds)
4. **Results**: View detected objects with detailed information

### Learning with Quizzes
1. **Start Quiz**: Tap "Start Quiz" from the history screen
2. **Answer Questions**: Select the correct object name
3. **Track Progress**: Monitor your score and improvement
4. **Review Results**: See detailed feedback on your performance

### Managing Discoveries
1. **View History**: Access all your past discoveries
2. **Star Favorites**: Mark interesting objects for quick access
3. **Delete Items**: Remove unwanted entries
4. **Search**: Find specific discoveries quickly

## 🔧 Configuration

### Permissions
The app requires the following permissions:
- `CAMERA`: For taking photos
- `READ_EXTERNAL_STORAGE`: For accessing gallery (Android 12 and below)

### ML Kit Models
- **Object Detection**: Automatically downloads on first use
- **Image Labeling**: Provides additional context for objects
- **Offline Operation**: Works without internet connection

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/example/learningapp/
│   │   ├── MainActivity.kt              # Main camera and detection logic
│   │   ├── Detector.kt                 # ML Kit integration
│   │   ├── DetectionResult.kt          # Data models
│   │   ├── ScanHistoryStore.kt         # Data persistence
│   │   ├── HistoryActivity.kt          # History management
│   │   ├── LoginActivity.kt            # User authentication
│   │   ├── SignUpActivity.kt           # Account creation
│   │   └── SplashActivity.kt           # App initialization
│   ├── res/
│   │   ├── layout/                     # UI layouts
│   │   ├── drawable/                   # Icons and backgrounds
│   │   ├── values/                     # Colors, strings, themes
│   │   └── mipmap/                     # App icons
│   └── AndroidManifest.xml             # App configuration
├── build.gradle.kts                    # Dependencies and build config
└── proguard-rules.pro                  # Code obfuscation rules
```

## Key Components

### **MainActivity.kt**
- Camera and gallery integration
- ML Kit object detection pipeline
- UI state management
- Image annotation and display

### **Detector.kt**
- ML Kit model initialization
- Object detection processing
- Result formatting and confidence scoring
- Error handling and fallbacks

### **ScanHistoryStore.kt**
- Local data persistence
- JSON serialization/deserialization
- File management for images
- CRUD operations for scan history

### **HistoryActivity.kt**
- Discovery list display
- Search and filter functionality
- Favorites management
- Delete operations

## 🔮 Future Enhancements

### **Planned Features**
- **Voice Integration**: Audio descriptions and pronunciation
- **Multi-language Support**: Learn in different languages
- **Social Features**: Share discoveries with friends
- **Achievement System**: Badges and rewards for learning milestones
- **Analytics Dashboard**: Detailed learning progress reports
- **Custom Themes**: Personalize the app appearance
- **Cloud Sync**: Backup and sync across devices

### **Technical Improvements**
- **Room Database**: Replace SharedPreferences with proper database
- **Image Optimization**: Better compression and thumbnail generation
- **Performance**: Faster detection and smoother animations
- **Testing**: Comprehensive unit and integration tests
- **Security**: Enhanced data protection and privacy features

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### **Development Setup**
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Google ML Kit** for powerful on-device machine learning
- **Android Team** for excellent development tools and frameworks
- **Open Source Community** for inspiration and support
- **Beta Testers** for valuable feedback and suggestions

## 📞 Support

- **Issues**: Report bugs and request features on [GitHub Issues](https://github.com/yourusername/objectlingo/issues)
- **Discussions**: Join community discussions on [GitHub Discussions](https://github.com/yourusername/objectlingo/discussions)
- **Email**: Contact us at support@objectlingo.app

---

