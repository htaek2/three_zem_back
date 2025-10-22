from app.models.model_loader import get_model
from app.utils.preprocessor import preprocess
from app.utils.postprocessor import postprocess

def predict_service(request):
    model = get_model()
    x = preprocess(request.features)
    pred = model.predict(x)[0]
    result = postprocess(pred)
    return result