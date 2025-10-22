def postprocess(prediction):
    """
    모델 예측 결과를 사람이 보기 좋은 형태로 변환.
    """
    return {"class_index": int(prediction), "class_label": str(prediction)}