from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import pandas as pd
from prophet import Prophet
import datetime

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

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
