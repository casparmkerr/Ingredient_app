#pragma version(1)
#pragma rs java_package_name(com.threedoorstudio.ingredients_app)
#pragma rs_fp_relaxed

#pragma initializer(mInit)

float tempPixel;
float contrast;
float value = 20;

int threshold = 130;

int hueLimit0 = 0;
int hueLimit1 = 0;
int hueLimit2 = 0;
int hueLimit3 = 0;
int hueLimit4 = 0;
int hueLimit5 = 0;
int hueLimit6 = 0;
int hueLimit7 = 0;

float hue0;
float hue1;
float hue2;

float value0;
float value1;
float value2;

float value01;
float value11;
float value21;

float hueMax;
float hueMed;
float hueMin;

int check2;

void init() { //Runs once when the script is first called
    contrast = (float) pow((100 + value) / 100, 2);

    if (hue0 > hue1) {
        hueMax = hue0;
        hueMin = hue1;
    } else {
        hueMax = hue1;
        hueMin = hue0;
    }
    if (hueMax < hue2) {
        hueMed = hueMax;
        hueMax = hue2;
    } else if (hueMin > hue2) {
        hueMed = hueMin;
        hueMin = hue2;
    } else {
        hueMed = hue2;
    }


int check = 0;

if (value0 > value1-0.05f && value0 < value1+0.05f) {
    check2 = 1;
} else {
    check2 = 0;
}


//if (value0 > value1 - 0.05 && value0 < value1 + 0.05) { if( value0 > value1) {value01 = value0+0.15; check = 1;} else {value01 = value0-0.15; check = -1;} }

//if (value2 > value1 - 0.05 && value2 < value1 + 0.05) { if( value2 > value1) {value21 = value2+0.15;} else {value21 = value2-0.15;}}

//if (value0 > value2 - 0.05 && value0 < value2 + 0.05) { if( value0 > value2) {if(check !=-1){value01 = value0+0.15;} else {value01 = value0+0.25;} } else {if(check != 1) {value01 = value0-0.15;} else {value01 = value0-0.15;} } }

}


uchar4 RS_KERNEL contrastAndBW(uchar4 in, uint32_t x, uint32_t y) {
  uchar4 out = in;

  float r = in.r / 255.0;
  float g = in.g / 255.0;
  float b = in.b / 255.0;

  // convert rgb to hsv

  float minRGB = min( r, min( g, b ) );
  float maxRGB = max( r, max( g, b ) );
  float deltaRGB = maxRGB - minRGB;

  float h = 0.0;
  float s = maxRGB == 0 ? 0 : (maxRGB - minRGB) / maxRGB;
  float v = maxRGB;

  if (deltaRGB != 0) {

      if (r == maxRGB) {
          h = (g - b) / deltaRGB;
      }
      else {
          if (g == maxRGB) {
              h = 2 + (b - r) / deltaRGB;
          }
          else {
              h = 4 + (r - g) / deltaRGB;
          }
      }

      h *= 60;
      if (h < 0) { h += 360; }
      if (h == 360) { h = 0; }
  }

  if (h<(hue0 + 15) && h>(hue0-15)) { h = hue0;}
  else if (h<hue1 + 15 && h>hue1-15) { h = hue1; }
  else {h = hue2;}

  float normHue = h/360;

  if (v>0.7&&v<0.95) {v*=0.9;}
  else if (v>0.05&&v<0.4) {v *= 1.1;}

  if (h>=hueLimit0 && h<hueLimit1) { h = hueLimit0;}
  else if (h>=hueLimit1 && h<hueLimit2) { h = hueLimit1; v = 0.2;}
  else if (h>=hueLimit2 && h<hueLimit3) { h = hueLimit2; v = 0.3;}
  else if (h>=hueLimit3 && h<hueLimit4) { h = hueLimit3; v = 0.4;}
  else if (h>=hueLimit4 && h<hueLimit5) { h = hueLimit4; v = 0.5;}
  else if (h>=hueLimit5 && h<hueLimit6) { h = hueLimit5; v = 0.6;}
  else if (h>=hueLimit6 && h<hueLimit7) { h = hueLimit6; v = 0.7;}
  else if (h>=hueLimit7 || h<hueLimit0) { h = hueLimit7; v = 0.8;}



  if (s < 0.5) {s = 0;}
  //else if (s < 0.8) {s = 0.5; if(v ==0.5) {v = 0.8;}}
  else {s = 0;}

    if ((in.r+in.b+in.g)/3 > threshold) {
    tempPixel = maxRGB;
    } else {
    tempPixel = minRGB;
    }







  r = 0, g = 0, b = 0;
  
  	if (s == 0)
  	{
  		r = v;
  		g = v;
  		b = v;
  	}
  	else
  	{
  		int i;
  		double f, p, q, t;
  
  		if (h == 360)
  			h = 0;
  		else
  			h = h / 60;
  
  		i = (int)trunc(h);
  		f = h - i;
  
  		p = v * (1.0 - s);
  		q = v * (1.0 - (s * f));
  		t = v * (1.0 - (s * (1.0 - f)));
  
  		switch (i)
  		{
  		case 0:
  			r = v;
  			g = t;
  			b = p;
  			break;
  
  		case 1:
  			r = q;
  			g = v;
  			b = p;
  			break;
  
  		case 2:
  			r = p;
  			g = v;
  			b = t;
  			break;
  
  		case 3:
  			r = p;
  			g = q;
  			b = v;
  			break;
  
  		case 4:
  			r = t;
  			g = p;
  			b = v;
  			break;
  
  		default:
  			r = v;
  			g = p;
  			b = q;
  			break;
  		}
  
  	}
  float correctionVal = tempPixel+0.6;
  if (correctionVal >= 1) {correctionVal = 1;}

  
  out.r = 255*r*(correctionVal);
  out.g = 255*g*(correctionVal);
  out.b = 255*b*(correctionVal);


  
  
  
  
  
  
  

/*
    out.r = 255*normHue;
    out.g = 255*v;
    out.b = 255* ((normHue))*(v);



    out.r = 255*normHue;
    out.g = 255*v;
    out.b = 255*(normHue*v);

    tempPixel = (out.r+out.g+out.b)/3;

    out.r = out.g = out.b = tempPixel;
/*
 if ( (max(hue0, max(hue1, hue2)) - min(hue0, min(hue1, hue2)) < 30 || max(hue0, max(hue1, hue2)) - min(hue0, min(hue1, hue2)) > 330  ) && max(value0, max(value1, value2))-min(value0, min(value1, value2)) >0.1) {
    if (tempPixel > threshold) {
        tempPixel = max( out.r, max( out.g, out.b ) );
    } else {
        tempPixel = min( out.r, min( out.g, out.b ) );
    }
  } /*else if (fabs(max(value0, max(value1, value2))-min(value0, min(value1, value2)) )<0.1  ){
        tempPixel = 255*normHue;

  } else {
    tempPixel = 255*normHue*v;
    out.r = 255*normHue;
    out.g = 255*v;
    out.b = 0;
   }*/
  //tempPixel = 255*v;

/*
  if (h < hue0 + 15 && h > hue0 -15 && v < value0 + 0.20 && v > value0 -0.20) {
    tempPixel = value0*255;

  } else if (h < hue1 + 15 && h > hue1 -15 && v < value1 + 0.20 && v > value1 -0.20) {
    tempPixel = value1*255;
  } else {
    tempPixel = value2*255;
  }

  //if (tempPixel > threshold) {
  //  if (out.r > out.g) {tempPixel = out.r;} else {tempPixel = out.g;}
  //  if (out.b > tempPixel) {tempPixel = out.b;}
  //} else {
  //  if (out.r < out.g) {tempPixel = out.r;} else {tempPixel = out.g;}
  //      if (out.b < tempPixel) {tempPixel = out.b;}
  //}

  //tempPixel = tempPixel-((255-tempPixel)*0.15);
  if (tempPixel > 255) {tempPixel = 255;}
  else if (tempPixel < 0) {tempPixel = 0;} */
  //out.r=tempPixel;

  //out.r = (int)(((((tempPixel / 255.0) - 0.5) * contrast) + 0.5) * 255.0);

  /* if (out.r == 255) {out.r = 0;} else if (out.r > 255) {out.r = 255;} else if (out.r <0) {out.r = 0;} */

  //out.g = out.r;
  //out.b = out.r;

  return out; //Returns the pixel
}
