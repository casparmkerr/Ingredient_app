#pragma version(1)
#pragma rs java_package_name(com.threedoorstudio.ingredients_app)
#pragma rs_fp_relaxed

#pragma initializer(mInit)

int tempPixel;
float contrast;
float value = 20;



void init() { //Runs once when the script is first called
contrast = (float) pow((100 + value) / 100, 2);}


uchar4 RS_KERNEL contrastAndBW(uchar4 in, uint32_t x, uint32_t y) {
  uchar4 out = in;


  tempPixel = (out.r + 2* out.g + out.b)/4; //Crude B&W-conversion

  //tempPixel = tempPixel-((255-tempPixel)*0.15);
  if (tempPixel > 255) {tempPixel = 255;}
  else if (tempPixel < 0) {tempPixel = 0;}
  out.r=tempPixel;

  //out.r = (int)(((((tempPixel / 255.0) - 0.5) * contrast) + 0.5) * 255.0);

  /*if (out.r == 255) {out.r = 0;} else */if (out.r > 255) {out.r = 255;} else if (out.r <0) {out.r = 0;}
  out.g = out.r;
  out.b = out.r;
  //
  return out; //Returns the pixel
}
