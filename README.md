# AI MindShield Full Project

## Android App
- Open `AndroidApp` folder in Android Studio
- Add the permissions snippet to your `AndroidManifest.xml` inside `<manifest>` and `<application>` tags
- Build and run on a real Android device
- The app shows front camera preview, logs random emotions every 10 seconds to `emotion_logs.csv` file in app storage
- Starts a stub VPNService simulating a firewall shield

## Streamlit Dashboard
- Navigate to `StreamlitDashboard` folder
- Create a Python virtual environment
- Install dependencies: `pip install -r requirements.txt`
- Run app: `streamlit run app.py`
- Upload the `emotion_logs.csv` file copied from Android device to visualize emotion data

## Notes
- This is Phase 1 of the AI MindShield project
- Future phases include real emotion detection, real firewall VPN blocking, and automatic log syncing

---
Developed by ChatGPT + User collaboration
