import numpy as np
import math
import cv2

cap = cv2.VideoCapture(0)

#Seront adaptes si la camera a une resolution trop faible
WIDTH=1280
HEIGHT=720


cap.set(3,WIDTH)
cap.set(4,HEIGHT)

WIDTH=round(cap.get(3))
HEIGHT=round(cap.get(4))

i=0
colorValue1=255
colorValue2=0
colorValue3=0


while(True):
    i+=1
    # Capture frame-by-frame
    ret, frame = cap.read()

    # Do operations
    hsv = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)

    #METTRE DES FLOTTANTS
    saturationMultiplier=1.0
    brightnessMultiplier=1.0

    #change saturation
    hsv[:,:,1]=np.clip(np.around(hsv[:,:,1]*saturationMultiplier,1),0,255)
    #change brightness
    hsv[:,:,2]=np.clip(np.around(hsv[:,:,2]*brightnessMultiplier,1),0,255)

    img=cv2.cvtColor(hsv,cv2.COLOR_HSV2RGB)

    if i==20:
        tmp=colorValue3
        colorValue3=colorValue2
        colorValue2=colorValue1
        colorValue1=tmp
        i=0
    cv2.circle(img, (100,140), 3, (colorValue1,colorValue2,colorValue3),1)
    cv2.circle(img, (120,120), 3, (colorValue1,colorValue2,colorValue3),1)
    cv2.circle(img, (140,100), 3, (colorValue1,colorValue2,colorValue3),1)
    cv2.circle(img, (WIDTH-100,140), 3, (colorValue1,colorValue2,colorValue3),1)
    cv2.circle(img, (WIDTH-120,120), 3, (colorValue1,colorValue2,colorValue3),1)
    cv2.circle(img, (WIDTH-140,100), 3, (colorValue1,colorValue2,colorValue3),1)

    # Display the resulting frame
    cv2.imshow('frame',img)
    if cv2.waitKey(1) & 0xFF == 27:
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
