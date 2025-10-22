from typing import Union
from fastapi import FastAPI
from app.api.router import router as api_router

app = FastAPI()

app.include_router(api_router, prefix="/api/v1")

@app.get("/")
def read_root():
    return {"It's": "Running"}

