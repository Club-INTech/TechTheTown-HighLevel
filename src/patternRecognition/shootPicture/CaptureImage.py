# -*- coding: utf-8 -*-

import numpy as np
import math
import cv2
import os.path

#############################################
WIDTH=1280
HEIGHT=720
cap = cv2.VideoCapture(0)

#On set la largeur et la hateur de l'image
cap.set(3,WIDTH)
cap.set(4,HEIGHT)

#Si la résolution de la caméra est trop faible, WIDTH et HEIGHT sont remis aux valeurs de la caméra
WIDTH=round(cap.get(3))
HEIGHT=round(cap.get(4))
#############################################

locked=True
taskFile="/tmp/TakePicture.task"

#On est oblige de prendre une photo avant de pouvoir continuer
while (locked):
    ret, frame = cap.read()
    if os.path.exists(taskFile):
        locked=False

hsv = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)

#UTILISER DES FLOTTANTS
#saturationMultiplier=1.0
#brightnessMultiplier=1.0

hsv[:,:,1]=np.clip(np.around(hsv[:,:,1]*saturationMultiplier,1),0,255)
hsv[:,:,2]=np.clip(np.around(hsv[:,:,2]*brightnessMultiplier,1),0,255)

img=cv2.cvtColor(hsv,cv2.COLOR_HSV2RGB)

cv2.imwrite("/tmp/ImageRaspi.jpeg",img)

doneFile=open("/tmp/TakePicture.done","w")
doneFile.close()

cap.release()
