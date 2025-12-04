from fastapi import FastAPI, BackgroundTasks
from pydantic import BaseModel
from model import DemandModel
import threading
import consumer
import pandas as pd
import sqlite3
import os
from pymongo import MongoClient
from datetime import datetime
from dotenv import load_dotenv

load_dotenv() # Load environment variables from .env file

app = FastAPI()
model = DemandModel()

# MongoDB Setup
MONGO_URI = os.getenv("MONGO_URI")
mongo_client = None
predictions_collection = None

if MONGO_URI:
    try:
        mongo_client = MongoClient(MONGO_URI)
        db = mongo_client.get_database("delivery_ai_db") # Explicitly set DB name
        predictions_collection = db["predictions"]
        predictions_collection.create_index("timestamp", expireAfterSeconds=604800) # 7 days
        print("Connected to MongoDB Atlas!")
    except Exception as e:
        print(f"Failed to connect to MongoDB: {e}")

def log_prediction_to_mongo(data: dict):
    if predictions_collection is not None:
        try:
            predictions_collection.insert_one(data)
        except Exception as e:
            print(f"Failed to log to MongoDB: {e}")

# Start RabbitMQ consumer in a background thread
def run_consumer():
    consumer.start_consuming()

@app.on_event("startup")
async def startup_event():
    t = threading.Thread(target=run_consumer, daemon=True)
    t.start()

class PredictionRequest(BaseModel):
    hour: int
    day: int

@app.get("/")
def read_root():
    return {"message": "AI Demand Prediction Service is Running 🚀"}

@app.get("/predict")
def predict(hour: int, day: int, background_tasks: BackgroundTasks):
    prediction = model.predict(hour, day)
    
    result = {
        "hour": hour,
        "day": day,
        "predicted_demand": prediction,
        "region": "Kadikoy", # Mock region for now
        "timestamp": datetime.utcnow()
    }

    # Log to MongoDB asynchronously
    background_tasks.add_task(log_prediction_to_mongo, result.copy())

    return result

@app.get("/debug/orders")
def debug_orders():
    try:
        conn = sqlite3.connect(consumer.DB_PATH)
        df = pd.read_sql_query("SELECT * FROM orders ORDER BY id DESC", conn)
        conn.close()
        return df.to_dict(orient="records")
    except Exception as e:
        return {"error": str(e)}

@app.post("/train")
def train_model(background_tasks: BackgroundTasks):
    background_tasks.add_task(train_logic)
    return {"message": "Training started in background"}

def train_logic():
    # Load data from SQLite
    conn = sqlite3.connect(consumer.DB_PATH)
    df = pd.read_sql_query("SELECT hour, day FROM orders", conn)
    conn.close()
    
    if len(df) > 10: # Only train if enough data
        # Calculate demand (count of orders per hour/day)
        # This is a simplification. In reality, we'd group by date+hour first.
        # For this demo, let's assume each row is a demand unit and we aggregate.
        
        # Better approach for demo:
        # We just use the raw rows as "occurrence" and maybe add a dummy 'demand' column
        # actually, LinearRegression needs a target.
        # Let's aggregate: Group by Hour/Day -> Count
        
        df_agg = df.groupby(['hour', 'day']).size().reset_index(name='demand')
        model.train(df_agg)
        print("Model retrained with real data!")
    else:
        print("Not enough data to retrain.")
