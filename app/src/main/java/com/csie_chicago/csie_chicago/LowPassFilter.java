package com.csie_chicago.csie_chicago;
public abstract interface LowPassFilter
{
  public abstract float[] addSamples(float[] paramArrayOfFloat);

  public abstract void setAlpha(float paramFloat);

  public abstract void setAlphaStatic(boolean paramBoolean);
}