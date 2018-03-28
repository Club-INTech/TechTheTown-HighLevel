# -*- coding: utf-8 -*-

import numpy as np
import math
import cv2

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


#############################################
##### PARAMETRES #####
XSTART=WIDTH-300
YSTART=250
LARGEUR_DISPLAY=300
HAUTEUR_DISPLAY=350

#Modifiers pour le traitement de l'image
#METTRE DES FLOTTANTS
saturationMultiplier=1.0
brightnessMultiplier=1.0
#############################################



##### DEBUT DU CODE #####

#Position du premier point
xCalque1=100
yCalque1=100

#Position du deuxieme point
xCalque2=120
yCalque2=120

#Position du troiseme point
xCalque3=140
yCalque3=140

#Indique si le while(True) doit break à la prochaine iteration
toBreak=False

#Définit le point qu'on bouge actuellement (1,2 ou 3), mis à défaut sur 0 pour singe-proof
currentCalque=0

def moveCalque(event,x,y,flags,params):
    global xCalque1,yCalque1,xCalque2,yCalque2,xCalque3,yCalque3,currentClaque
    if event==cv2.EVENT_LBUTTONDOWN:
        if currentCalque==1:
            xCalque1=x
            yCalque1=y
        elif currentCalque==2:
            xCalque2=x
            yCalque2=y
        elif currentCalque==3:
            xCalque3=x
            yCalque3=y

#On définit les variables permettant le clignotement des couleurs
i=0
colorValue1=255
colorValue2=0
colorValue3=0

while(True):
    ### CAPTURE DE LA VIDEO FRAME PAR FRAME
    ret, frame = cap.read()
    frame=frame[YSTART:YSTART+HAUTEUR_DISPLAY,XSTART:XSTART+LARGEUR_DISPLAY]
    cv2.namedWindow('frame')
    cv2.setMouseCallback('frame', moveCalque)

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
        #SAUVEGARDER LES POSITIONS DES CALQUES ICI
        file = open("/tmp/CoordsPatternVideo.txt","w")
        file.write(str(xCalque1+XSTART)+" "+str(yCalque1+YSTART)+" "+str(xCalque2+XSTART)+" "+str(yCalque2+YSTART)+" "+str(xCalque3+XSTART)+" "+str(yCalque3+YSTART))
        file.close()
        break

    ###AJOUT DES ANNOTATIONS SUR L'IMAGE
    #Assure le clignotement de l'image
    i+=1
    if i==20:
        tmp=colorValue3
        colorValue3=colorValue2
        colorValue2=colorValue1
        colorValue1=tmp
        i=0

    #On met les cercles et les numeros
    cv2.circle(img, (xCalque1,yCalque1),5,(colorValue1,colorValue2,colorValue3),1)
    cv2.putText(img,'1',(xCalque1-8,yCalque1-20),cv2.FONT_HERSHEY_TRIPLEX, 1, (colorValue1,colorValue2,colorValue3))
    cv2.circle(img, (xCalque2,yCalque2),5,(colorValue1,colorValue2,colorValue3),1)
    cv2.putText(img,'2',(xCalque2-8,yCalque2-20),cv2.FONT_HERSHEY_TRIPLEX, 1, (colorValue1,colorValue2,colorValue3))
    cv2.circle(img, (xCalque3,yCalque3),5,(colorValue1,colorValue2,colorValue3),1)
    cv2.putText(img,'3',(xCalque3-8,yCalque3-20),cv2.FONT_HERSHEY_TRIPLEX, 1, (colorValue1,colorValue2,colorValue3))


    #On ecoute les touches appuyees
    key = cv2.waitKey(1) & 0xFF
    if key==ord("0") or key==ord(" "):
        currentCalque=0
    elif key==ord("1"):
        currentCalque=1
    elif key==ord("2"):
        currentCalque=2
    elif key==ord("3"):
        currentCalque=3

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