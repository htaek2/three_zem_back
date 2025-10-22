from sklearn.datasets import load_iris
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
import joblib
from pathlib import Path

# 데이터 로드
X, y = load_iris(return_X_y=True)

# 파이프라인 (스케일러 + 분류기)
pipeline = Pipeline([
    ("scaler", StandardScaler()),
    ("clf", RandomForestClassifier(n_estimators=100, random_state=42))
])

# 학습
pipeline.fit(X, y)

# 모델 저장
model_path = Path("../app/models/model.pkl")
model_path.parent.mkdir(parents=True, exist_ok=True)
joblib.dump(pipeline, model_path)

print(f"Model saved to {model_path}")