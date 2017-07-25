#pragma version(1)
#pragma rs java_package_name(com.threedoorstudio.ingredients_app)
#pragma rs_fp_relaxed

#pragma initializer(mInit)


float contrast;
float value = 20;



void init() { //Runs once when the script is first called
contrast = (float) ((259*(value+255))/(255*(259-value)));}


uchar4 RS_KERNEL contrastAndBW(uchar4 in, uint32_t x, uint32_t y) {
  uchar4 out = in;
  int tempPixel;

  tempPixel = (299 * in.r + 587 * in.g + 114 * in.b) / 1000;


  //tempPixel = tempPixel-((255-tempPixel)*0.15);
  if (tempPixel > 255) {tempPixel = 255;}
  else if (tempPixel < 0) {tempPixel = 0;}
  //out.r=tempPixel;
  //out.r = tempPixel;
  out.r = (int)((contrast*(tempPixel-128))+128);
  if(out.r < 0 && tempPixel <128) { out.r = 0; }
  else if(out.r < 0 && tempPixel > 100) { out.r = 255; }
      else if(out.r > 255 && tempPixel > 128) { out.r = 255; }
      else if(out.r > 255 && tempPixel < 200) { out.r = 0; }


  out.g = out.r;
  out.b = out.r;
  //
  return out; //Returns the pixel
}
