import streamlit as st
import pandas as pd
import cv2
from deepface import DeepFace
import threading
import time
from datetime import datetime
import numpy as np

st.title("AI MindShield Live Emotion & Gender Dashboard")

# DataFrame to store logs
emotion_logs = pd.DataFrame(columns=['timestamp', 'gender', 'emotion', 'mentality'])

# Mentality mapping
mentalities_map = {
    'happy': 'Calm & Productive',
    'neutral': 'Stable & Observant',
    'sad': 'Distracted & Low Mood',
    'angry': 'Aggressive & Risky',
    'fear': 'Anxious & Alert',
    'surprise': 'Surprised & Curious',
    'disgust': 'Disgusted & Avoidant'
}

# Open webcam
cap = cv2.VideoCapture(0)
stop_thread = False

def detect_emotions_genders():
    global emotion_logs, stop_thread
    while not stop_thread:
        ret, frame = cap.read()
        if not ret:
            st.error("Cannot access webcam")
            break

        try:
            # Analyze frame for emotion and gender
            analysis = DeepFace.analyze(frame, actions=['emotion', 'gender'], enforce_detection=False)
            emotion = analysis['dominant_emotion']
            gender = analysis['gender'] if 'gender' in analysis else 'Unknown'
            mentality = mentalities_map.get(emotion, "Unknown")

            timestamp = datetime.now()

            new_row = pd.DataFrame([{
                'timestamp': timestamp,
                'gender': gender,
                'emotion': emotion,
                'mentality': mentality
            }])

            emotion_logs = pd.concat([emotion_logs, new_row], ignore_index=True)

        except Exception as e:
            # Continue on any errors
            pass

        time.sleep(2)  # every 2 seconds detect

# Run detection in background thread
thread = threading.Thread(target=detect_emotions_genders, daemon=True)
thread.start()

# Main Streamlit UI container
placeholder = st.empty()

try:
    while True:
        with placeholder.container():
            st.write("### Live Detected Emotion Logs (Last 20 records)")
            st.dataframe(emotion_logs.tail(20))

            if not emotion_logs.empty:
                st.write("### Emotion Distribution")
                st.bar_chart(emotion_logs['emotion'].value_counts())

                st.write("### Gender Distribution")
                st.bar_chart(emotion_logs['gender'].value_counts())

                st.write("### Mentality Counts")
                st.bar_chart(emotion_logs['mentality'].value_counts())

            # Provide option to download CSV log
            if not emotion_logs.empty:
                csv = emotion_logs.to_csv(index=False).encode('utf-8')
                st.download_button(
                    label="Download emotion_logs.csv",
                    data=csv,
                    file_name='emotion_logs.csv',
                    mime='text/csv'
                )
        time.sleep(5)
except KeyboardInterrupt:
    stop_thread = True
    cap.release()
