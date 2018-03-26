# -*- coding: utf-8 -*-

import numpy as np
import math
import cv2

##### PARAMETRES A CHANGER #####
#Seront adaptes si la camera a une resolution trop faible
WIDTH=1280
HEIGHT=720

#Modifiers pour le traitement de l'image
#METTRE DES FLOTTANTS
saturationMultiplier=1.0
brightnessMultiplier=1.0

##### DEBUT DU CODE #####

#Indique si le while(True) doit break à la prochaine iteration
toBreak=False

#Définit le point qu'on bouge actuellement (1,2 ou 3), mis à défaut sur 0 pour singe-proof
currentCalque=0

cap = cv2.VideoCapture(0)

#On set la largeur et la hateur de l'image
cap.set(3,WIDTH)
cap.set(4,HEIGHT)

#Si la résolution de la caméra est trop faible, WIDTH et HEIGHT sont remis aux valeurs de la caméra
WIDTH=round(cap.get(3))
HEIGHT=round(cap.get(4))

while(True):
    ### CAPTURE DE LA VIDEO FRAME PAR FRAME
    ret, frame = cap.read()

    ### TRAITEMENT DE L'IMAGE
    hsv = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)

    #Change la saturation
    hsv[:,:,1]=np.clip(np.around(hsv[:,:,1]*saturationMultiplier,1),0,255)
    #Change la luminosite
    hsv[:,:,2]=np.clip(np.around(hsv[:,:,2]*brightnessMultiplier,1),0,255)

    img=cv2.cvtColor(hsv,cv2.COLOR_HSV2RGB)

    ### On sort de la boucle ici
    if toBreak:
        #SAUVEGARDER L'IMAGE ICI
        cv2.imwrite("/tmp/ImageRaspi.jpeg",img)
        break

    ##### A ENLEVER ######
    ### AFFICHAGE DE LA FRAME APRES AJOUT DES ANNOTATIONS
    cv2.imshow('frame',img)

    ###DECLENCHAGE DE LA SORTIE DE LA BOUCLE
    #On quitte quand on appuye sur Echap
    if key==27:
        toBreak=True

###LIBERATION DES RESSOURCES
#On libère la camera
cap.release()
#On detruit la fenetre
cv2.destroyAllWindows()
