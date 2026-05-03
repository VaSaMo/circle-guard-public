import uuid
import os
from locust import HttpUser, task, between, tag

class CircleGuardUser(HttpUser):
    wait_time = between(1, 2)

    @tag('auth')
    @task(3)
    def auth_login(self):
        host = os.getenv('AUTH_HOST', 'http://localhost:8180')
        with self.client.post(f"{host}/api/v1/auth/login", json={
            "username": "admin", "password": "password"
        }, catch_response=True, name="Auth: Login") as response:
            if response.status_code in [200, 401]: response.success()

    @tag('form')
    @task(2)
    def form_submit(self):
        host = os.getenv('FORM_HOST', 'http://localhost:8086')
        self.client.post(f"{host}/api/v1/surveys", json={
            "anonymousId": "00000000-0000-0000-0000-000000000001",
            "responses": {"q1": "YES"}
        }, name="Form: Submit Survey")

    @tag('promotion')
    @task(4)
    def promotion_check(self):
        host = os.getenv('PROMOTION_HOST', 'http://localhost:8088')
        self.client.get(f"{host}/api/v1/circles/user/test-user", name="Promotion: User Circles")

    @tag('identity')
    @task(3)
    def identity_check(self):
        host = os.getenv('IDENTITY_HOST', 'http://localhost:8083')
        with self.client.post(f"{host}/api/v1/identities/map", json={
            "realIdentity": "user@example.com"
        }, catch_response=True, name="Identity: Map") as response:
            if response.status_code in [200, 404]: response.success()

    @tag('notification')
    @task(2)
    def notification_check(self):
        host = os.getenv('NOTIFICATION_HOST', 'http://localhost:8082')
        self.client.get(f"{host}/actuator/health", name="Notification: Health")

    @tag('gateway')
    @task(5)
    def gateway_validate(self):
        host = os.getenv('GATEWAY_HOST', 'http://localhost:8087')
        self.client.post(f"{host}/api/v1/gate/validate", json={
            "token": "test-token"
        }, name="Gateway: Validate QR")
