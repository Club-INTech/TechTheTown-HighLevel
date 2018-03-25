import numpy as np
import math
import cv2

cap = cv2.VideoCapture(0)

cap.set(3,1280) #on set la largeur a 1280
cap.set(4,720)  #on set la hauteur a 720

ret, frame = cap.read()
hsv = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)

#UTILISER DES FLOTTANTS
saturationMultiplier=1.0
brightnessMultiplier=1.0

hsv[:,:,1]=np.clip(np.around(hsv[:,:,1]*saturationMultiplier,1),0,255)
hsv[:,:,2]=np.clip(np.around(hsv[:,:,2]*brightnessMultiplier,1),0,255)

img=cv2.cvtColor(hsv,cv2.COLOR_HSV2RGB)

cv2.imwrite("/tmp/ImageRaspi.jpeg",img)

cap.release()
