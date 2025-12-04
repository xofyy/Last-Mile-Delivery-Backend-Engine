import random
from locust import HttpUser, task, between

class DeliveryUser(HttpUser):
    wait_time = between(1, 5)
    token = None
    headers = {}

    def on_start(self):
        # 1. Register a new user for each simulated user
        email = f"user_{random.randint(1, 100000)}@example.com"
        password = "password123"
        full_name = "Test User"

        self.client.post("/api/auth/register", json={
            "fullName": full_name,
            "email": email,
            "password": password
        })

        # 2. Login to get JWT token
        response = self.client.post("/api/auth/login", json={
            "email": email,
            "password": password
        })

        if response.status_code == 200:
            self.token = response.json().get("accessToken")
            self.headers = {
                "Authorization": f"Bearer {self.token}",
                "Content-Type": "application/json"
            }
        else:
            print(f"Login failed for {email}: {response.text}")

    @task
    def create_order(self):
        if not self.token:
            return

        # Assuming Restaurant with ID 1 exists
        # If not, you need to insert it manually into the database:
        # INSERT INTO restaurants (name, latitude, longitude, address) VALUES ('Burger King', 41.0, 29.0, 'Istanbul');
        
        payload = {
            "restaurantId": 1, 
            "totalAmount": random.randint(100, 1000)
        }

        self.client.post("/api/orders", json=payload, headers=self.headers)

class AIServiceUser(HttpUser):
    wait_time = between(1, 5)
    # If you want to test AI service separately, point --host to it.
    # Or if running together, we might need absolute URLs if ports differ.
    # For simplicity, we assume --host points to the AI service OR we use a custom env var.
    
    # In K8s/Local dev, AI service might be on a different port (e.g. 8000 vs 8080)
    # You can run this user specifically: locust -f locustfile.py AIServiceUser --host http://localhost:8000

    @task(10)
    def predict(self):
        # Random hour (0-23) and day (0-6)
        hour = random.randint(0, 23)
        day = random.randint(0, 6)
        
        self.client.get(f"/predict?hour={hour}&day={day}")

    @task(1)
    def train(self):
        # Trigger training less frequently
        self.client.post("/train")
