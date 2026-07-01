from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
from prophet import Prophet
import datetime
from sklearn.ensemble import IsolationForest
import numpy as np
from collections import Counter

app = FastAPI(title="Microservicio Predictivo de Ventas", version="1.0.0")

class VentaDiaria(BaseModel):
    fecha: str
    ingresos: float

class PrediccionResponse(BaseModel):
    fecha: str
    prediccion: float

@app.post("/predict", response_model=List[PrediccionResponse])
async def predict_ventas(ventas: List[VentaDiaria]):
    if not ventas:
        raise HTTPException(status_code=400, detail="La lista de ventas no puede estar vacía")
    
    if len(ventas) < 5:
        # Prophet needs at least a few points to fit properly, though mathematically it can fit with fewer, 
        # it's best practice to have at least a small history. We'll allow it but maybe results aren't great.
        pass

    try:
        # Convert input to DataFrame
        df = pd.DataFrame([{"ds": v.fecha, "y": v.ingresos} for v in ventas])
        df['ds'] = pd.to_datetime(df['ds'])
        
        # Initialize Prophet model
        # Prophet automatically detects daily/weekly/yearly seasonality if data allows it
        # Since we know there's weekly seasonality (weekends), Prophet will pick it up automatically
        # if there's enough data.
        m = Prophet(daily_seasonality=False)
        m.fit(df)
        
        # Predict next 90 days (approx 3 months)
        future = m.make_future_dataframe(periods=90)
        forecast = m.predict(future)
        
        # Filter only the future predictions (we don't need the historical fit)
        last_date = df['ds'].max()
        future_forecast = forecast[forecast['ds'] > last_date]
        
        # Prepare response
        response = []
        for index, row in future_forecast.iterrows():
            # Ensure prediction is not negative (sales can't be negative)
            pred_val = max(0.0, row['yhat'])
            response.append(PrediccionResponse(
                fecha=row['ds'].strftime('%Y-%m-%d'),
                prediccion=round(pred_val, 2)
            ))
            
        return response
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error interno procesando la predicción: {str(e)}")

class RecommendRequest(BaseModel):
    historial: List[List[int]]
    carrito: List[int]
    top_k: int = 3

class RecommendResponse(BaseModel):
    recomendaciones: List[int]

class AnomalyRequest(BaseModel):
    historial_montos: List[float]
    monto_actual: float

class AnomalyResponse(BaseModel):
    is_anomaly: bool
    score: float

@app.post("/recommend", response_model=RecommendResponse)
async def recommend_products(req: RecommendRequest):
    if not req.carrito:
        return RecommendResponse(recomendaciones=[])
        
    co_occurrences = Counter()
    cart_set = set(req.carrito)
    
    for transaction in req.historial:
        tx_set = set(transaction)
        # If any cart item is in this transaction
        if cart_set.intersection(tx_set):
            # Add all other items from this transaction to our count
            for item in tx_set:
                if item not in cart_set:
                    co_occurrences[item] += 1
                    
    # Get top_k most common items
    top_items = [item for item, count in co_occurrences.most_common(req.top_k)]
    return RecommendResponse(recomendaciones=top_items)

@app.post("/detect_anomaly", response_model=AnomalyResponse)
async def detect_anomaly(req: AnomalyRequest):
    if len(req.historial_montos) < 5:
        # Not enough data to confidently say it's an anomaly
        return AnomalyResponse(is_anomaly=False, score=1.0)
        
    try:
        # Reshape data for sklearn
        X_train = np.array(req.historial_montos).reshape(-1, 1)
        X_test = np.array([[req.monto_actual]])
        
        # Fit Isolation Forest
        clf = IsolationForest(contamination=0.05, random_state=42)
        clf.fit(X_train)
        
        # Predict: returns -1 for outliers and 1 for inliers
        prediction = clf.predict(X_test)[0]
        score = clf.decision_function(X_test)[0]
        
        is_anomaly = bool(prediction == -1)
        
        return AnomalyResponse(is_anomaly=is_anomaly, score=float(score))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error in anomaly detection: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
