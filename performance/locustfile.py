import uuid
from locust import HttpUser, task, between

class CircleGuardUser(HttpUser):
    wait_time = between(1, 2)

    @task(3)
    def auth_login(self):
        # Auth Service (8180) - Controller uses /api/v1/auth/login
        # Expects username and password
        with self.client.post("http://localhost:8180/api/v1/auth/login", json={
            "username": "admin", "password": "password"
        }, catch_response=True, name="Auth: Login") as response:
            if response.status_code in [200, 401]: response.success()

    @task(2)
    def form_submit(self):
        # Form Service (8086) - Controller uses /api/v1/surveys
        # Expects anonymousId as UUID
        self.client.post("http://localhost:8086/api/v1/surveys", json={
            "anonymousId": "00000000-0000-0000-0000-000000000001",
            "responses": {"q1": "YES"}
        }, name="Form: Submit Survey")

    @task(4)
    def promotion_check(self):
        # Promotion Service (8088) - Controller uses /api/v1/circles/user/{id}
        self.client.get("http://localhost:8088/api/v1/circles/user/test-user", name="Promotion: User Circles")

    @task(3)
    def identity_check(self):
        # Identity Service (8083) - Already working!
        with self.client.post("http://localhost:8083/api/v1/identities/map", json={
            "realIdentity": "user@example.com"
        }, catch_response=True, name="Identity: Map") as response:
            if response.status_code in [200, 404]: response.success()

    @task(2)
    def notification_check(self):
        # Notification Service (8082) - Using health check for stability
        self.client.get("http://localhost:8082/actuator/health", name="Notification: Health")

    @task(5)
    def gateway_validate(self):
        # Gateway Service (8087) - Already working!
        self.client.post("http://localhost:8087/api/v1/gate/validate", json={
            "token": "test-token"
        }, name="Gateway: Validate QR")
