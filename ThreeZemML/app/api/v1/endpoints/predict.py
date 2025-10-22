from fastapi import APIRouter
from app.schemas.request import PredictRequest
from app.schemas.response import PredictResponse
from app.services.predict_service import predict_service

router = APIRouter()

@router.post("/", response_model=PredictResponse)
def predict(request: PredictRequest):
    result = predict_service(request)
    return PredictResponse(prediction=result)