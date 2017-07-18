package com.csie_chicago.csie_chicago;

public class LPFAndroidDeveloper
  implements LowPassFilter
{
  private float alpha = 0.9F;
  private boolean alphaStatic = false;
  private int count = 0;
  private float dt = 0.0F;
  private float[] input = { 0.0F, 0.0F, 0.0F };
  private float[] output = { 0.0F, 0.0F, 0.0F };
  private float timeConstant = 0.18F;
  private float timestamp = (float)System.nanoTime();
  private float timestampOld = (float)System.nanoTime();

  public float[] addSamples(float[] paramArrayOfFloat)
  {
    System.arraycopy(paramArrayOfFloat, 0, this.input, 0, paramArrayOfFloat.length);
    if (!this.alphaStatic)
    {
      this.timestamp = ((float)System.nanoTime());
      this.dt = (1.0F / (this.count / ((this.timestamp - this.timestampOld) / 1.0E+009F)));
      this.alpha = (this.timeConstant / (this.timeConstant + this.dt));
    }
    this.count = (1 + this.count);
//  if (this.count > 5)
//    {
      this.output[0] = (this.alpha * this.output[0] + (1.0F - this.alpha) * this.input[0]);
      this.output[1] = (this.alpha * this.output[1] + (1.0F - this.alpha) * this.input[1]);
      this.output[2] = (this.alpha * this.output[2] + (1.0F - this.alpha) * this.input[2]);
//    }
    return this.output;
  }

  public void setAlpha(float paramFloat)
  {
    this.alpha = paramFloat;
  }

  public void setAlphaStatic(boolean paramBoolean)
  {
    this.alphaStatic = paramBoolean;
  }
}