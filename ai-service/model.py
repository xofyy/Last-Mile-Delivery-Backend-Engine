import pandas as pd
from sklearn.linear_model import LinearRegression
import joblib
import os
import random
from datetime import datetime, timedelta

MODEL_PATH = "demand_model.pkl"

class DemandModel:
    def __init__(self):
        self.model = LinearRegression()
        self.is_trained = False
        self.load_model()

    def load_model(self):
        if os.path.exists(MODEL_PATH):
            self.model = joblib.load(MODEL_PATH)
            self.is_trained = True
            print("Model loaded from disk.")
        else:
            print("No model found. Training with dummy data...")
            self.train_with_dummy_data()

    def train_with_dummy_data(self):
        # Create dummy data: 1000 records
        # Features: hour_of_day (0-23), day_of_week (0-6)
        # Target: demand (0-100)
        
        data = []
        for _ in range(1000):
            hour = random.randint(0, 23)
            day = random.randint(0, 6)
            # Simple logic: Demand is higher in evenings (18-21) and weekends
            base_demand = 20
            if 18 <= hour <= 21:
                base_demand += 50
            if day >= 5: # Weekend
                base_demand += 20
            
            noise = random.randint(-10, 10)
            demand = max(0, base_demand + noise)
            data.append({"hour": hour, "day": day, "demand": demand})

        df = pd.DataFrame(data)
        self.train(df)

    def train(self, df):
        X = df[["hour", "day"]]
        y = df["demand"]
        
        # Train on full dataset for demo purposes to ensure all data is used
        self.model.fit(X, y)
        
        joblib.dump(self.model, MODEL_PATH)
        self.is_trained = True
        print(f"Model trained on {len(df)} records.")

    def predict(self, hour, day):
        if not self.is_trained:
            return 0
        
        prediction = self.model.predict(pd.DataFrame([[hour, day]], columns=["hour", "day"]))
        return max(0, int(prediction[0]))
