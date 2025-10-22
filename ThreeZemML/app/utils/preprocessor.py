import numpy as np

def preprocess(features):
    """
    클라이언트에서 받은 입력값(features)을
    모델이 예측할 수 있는 형태로 전처리합니다.
    """
    # 예: None 또는 문자열이 섞여있을 수 있으므로 변환
    clean_features = []
    for f in features:
        try:
            clean_features.append(float(f))
        except (ValueError, TypeError):
            clean_features.append(0.0)  # 기본값 대체
    
    # numpy array로 변환
    x = np.array(clean_features, dtype=np.float32)

    # 추가로 스케일링 예시 (선택)
    # x = (x - x.mean()) / (x.std() + 1e-8)
    
    return x


# def preprocess(features):
#     """
#     입력값(features)을 numpy 배열로 변환.
#     여기서는 간단히 수치형 리스트만 처리.
#     """
#     try:
#         x = np.array(features, dtype=np.float32).reshape(1, -1)
#     except Exception as e:
#         raise ValueError(f"Invalid features format: {e}")
#     return x