from typing import Union
import requests
from fastapi import FastAPI
from app.api.router import router as api_router

host = "0.0.0.0"
port = "7770"
baseUrl = f"{host}:{port}/"

app = FastAPI()
app.include_router(api_router, prefix="/api/v1")

@app.get("/")
def read_root():
    return {"It's": "Running"}

def init():
    url = baseUrl + "/api/energy/elec"
    params = { "start": "2023-10-23", "end": "2025-10-23", "datetimeType": 0}

init()