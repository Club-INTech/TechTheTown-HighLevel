# -*- coding: utf-8 -*-
#!/usr/bin/env python

'''
Simple "Square Detector" program.
Loads several images sequentially and tries to find squares in each image.
'''

# Python 2/3 compatibility
import sys
PY3 = sys.version_info[0] == 3

if PY3:
    xrange = range

import numpy as np
import cv2 as cv

def angle_cos(p0, p1, p2):
    d1, d2 = (p0-p1).astype('float'), (p2-p1).astype('float')
    return abs( np.dot(d1, d2) / np.sqrt( np.dot(d1, d1)*np.dot(d2, d2) ) )

def find_squares(img,threshold1, threshold2, blurSize):
    img = cv.GaussianBlur(img, (blurSize, blurSize), 0)
    squares = []
    oneSquareFound=False
    maxArea=0
    for gray in cv.split(img):
        for thrs in xrange(0, 255, 26):
            if thrs == 0:
                bin = cv.Canny(gray, threshold1, threshold2, apertureSize=APERTURE_SIZE, L2gradient=True)
                bin = cv.dilate(bin, None)
            else:
                _retval, bin = cv.threshold(gray, thrs, 255, cv.THRESH_BINARY)
            bin, contours, _hierarchy = cv.findContours(bin, cv.RETR_LIST, cv.CHAIN_APPROX_SIMPLE)
            for cnt in contours:
                cnt_len = cv.arcLength(cnt, True)
                cnt = cv.approxPolyDP(cnt, LENGTH_MULTIPLIER*cnt_len, True)
                area=cv.contourArea(cnt)
                if len(cnt) == 4 and area >= maxArea and cv.isContourConvex(cnt):
                    cnt = cnt.reshape(-1, 2)
                    max_cos = np.max([angle_cos( cnt[i], cnt[(i+1) % 4], cnt[(i+2) % 4] ) for i in xrange(4)])
                    if max_cos < 0.5:
                        maxX=-1
                        minX=10000
                        maxY=-1
                        minY=10000
                        for array in cnt:
                            if array[0]>maxX:
                                maxX=array[0]
                            if array[0]<minX:
                                minX=array[0]
                            if array[1]>maxY:
                                maxY=array[1]
                            if array[1]<minY:
                                minY=array[1]
                        deltaX=maxX-minX
                        deltaY=maxY-minY
                        if deltaX>10 and deltaX<60 and deltaY>15 and deltaY<40 and deltaX<deltaY:
                            maxArea=area
                            squares=cnt
                            oneSquareFound=True
    if not oneSquareFound:
        return [-1,-1,10000,10000]
    else:
        minX=10000
        maxX=-1
        minY=10000
        maxY=-1
        for square in squares:
            if square[0]<minX:
                minX=square[0]
            if square[0]>maxX:
                maxX=square[0]
            if square[1]<minY:
                minY=square[1]
            if square[1]>maxY:
                maxY=square[1]
        return [minX, maxX, minY, maxY]

if __name__ == '__main__':
    fn='/tmp/ImageRaspi.jpeg'
    img = cv.imread(fn)
    XSTART=0
    YSTART=100
    WIDTH=200
    HEIGHT=250
    APERTURE_SIZE = 3
    LENGTH_MULTIPLIER = 0.02

    img = img[XSTART:XSTART+WIDTH,YSTART:YSTART+HEIGHT]

    square = find_squares(img,0,50,9)
    numpySquares=np.array([[[square[0],square[2]],[square[1],square[2]],[square[1],square[3]], [square[0],square[3]]]])
    #Pour savoir ou les carres ont ete identifies
    #cv.imshow("DEBUG",img)
    #Array de la forme : (x, y, width, height)
    if square != [-1,-1,10000,10000]:
        print(str(XSTART+square[0])+" "+str(YSTART+square[1])+" "+str(XSTART+square[2]+WIDTH)+" "+str(YSTART+square[3]+HEIGHT))
        cv.drawContours( img, numpySquares, -1, (255, 0, 0), 2)
        file=open("/tmp/LocalizationInfo.txt","w")
        file.write(str(XSTART+square[0])+" "+str(YSTART+square[1])+" "+str(XSTART+square[2]+WIDTH)+" "+str(YSTART+square[3]+HEIGHT))
        file.close()
    else:
        file=open("/tmp/LocalizationInfo.txt","w")
        file.write(str(square[0])+" "+str(square[1])+" "+str(square[2])+" "+str(square[3]))
        file.close()
        print(str(square[0])+" "+str(square[1])+" "+str(square[2])+" "+str(square[3]))
    file2=open("/tmp/LocalizationDone.lock","w")
    file2.close()
    ch = cv.waitKey()
    if ch == 27:
        cv.destroyAllWindows()
