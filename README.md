# ClockWebhookMicroservice
A clock webhook microservice

Examples of rest calls :

- Register endpoint

POST http://localhost:8080/clock/register

{
    "url": "https://webhook.site/c23aaade-cf04-483e-919f-4700e46e691c",
    "interval": 30,
    "unit": "S"
}

- Unregister endpoint

POST http://localhost:8080/clock/unregister?callbackUrl=https://webhook.site/8faf1e95-ec5d-4c18-ac30-d2cc3e795a17


- Frequency endpoint

PUT http://localhost:8080/clock/frequency

{
    "url": "https://webhook.site/c23aaade-cf04-483e-919f-4700e46e691c",
    "interval": 2,
    "unit": "M"
}
