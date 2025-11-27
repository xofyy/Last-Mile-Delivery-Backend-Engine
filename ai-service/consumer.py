import pika
import json
import sqlite3
import os
import time
from datetime import datetime

DB_PATH = "training_data.db"
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
QUEUE_NAME = "ai.training.data"

def init_db():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute('''CREATE TABLE IF NOT EXISTS orders
                 (id INTEGER PRIMARY KEY AUTOINCREMENT, 
                  order_time TIMESTAMP, 
                  hour INTEGER, 
                  day INTEGER)''')
    conn.commit()
    conn.close()

def save_to_db(order_data):
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    
    # Parse timestamp (assuming ISO format from Java)
    # Example: "2023-10-27T15:30:00"
    try:
        dt = datetime.fromisoformat(order_data.get("orderTime"))
        hour = dt.hour
        day = dt.weekday()
        
        c.execute("INSERT INTO orders (order_time, hour, day) VALUES (?, ?, ?)", 
                  (dt, hour, day))
        conn.commit()
        print(f"Saved order data: Hour={hour}, Day={day}")
    except Exception as e:
        print(f"Error saving to DB: {e}")
    finally:
        conn.close()

def callback(ch, method, properties, body):
    print(f" [x] Received {body}")
    data = json.loads(body)
    save_to_db(data)

def start_consuming():
    init_db()
    
    # Retry connection logic
    while True:
        try:
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(host=RABBITMQ_HOST))
            channel = connection.channel()
            
            channel.queue_declare(queue=QUEUE_NAME, durable=True)
            
            print(' [*] Waiting for messages. To exit press CTRL+C')
            channel.basic_consume(queue=QUEUE_NAME, on_message_callback=callback, auto_ack=True)
            
            channel.start_consuming()
        except Exception as e:
            print(f"RabbitMQ connection failed: {e}. Retrying in 2s...")
            time.sleep(2)

if __name__ == "__main__":
    start_consuming()
