from pydantic import BaseModel

class PredictResponse(BaseModel):
    prediction: float


# class PredictResponse(BaseModel):
#     class_index: int
#     class_label: str