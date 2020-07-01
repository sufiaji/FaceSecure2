import cv2
import numpy as np
from os.path import dirname, join
import base64
from PIL import Image
import pickle
import io

class LivenessDetection:

    def __init__(self):
        pkl_filename = join(dirname(__file__), "pickle_print.pickle")
        self.clf = pickle.load(open(pkl_filename,'rb'))

    def isFakePrint(self, image_string64): #, print_threshold=0.35):
        imagedata = base64.b64decode(image_string64)
        image = Image.open(io.BytesIO(imagedata))
        array_np = np.array(image)
        frame = cv2.cvtColor(array_np, cv2.COLOR_RGB2BGR)
        feature_vector = self.getEmbeddings(frame)
        prediction = self.clf.predict_proba(feature_vector)
        #print("print ={:.2f}".format(np.mean(prediction[0][1])))
        m = np.mean(prediction[0][1])
        return m
        # if m >= print_threshold:
        #     return 'FAKE, m:' + str(m)
        # return 'REAL, m:' + str(m)

    def isFakeReplay(self, image_string64, replay_threshold=0.93):
        imagedata = base64.b64decode(image_string64)
        image = Image.open(io.BytesIO(imagedata))
        array_np = np.array(image)
        frame = cv2.cvtColor(array_np, cv2.COLOR_RGB2BGR)
        feature_vector = self.getEmbeddings(frame)
        prediction = self.clf.predict_proba(feature_vector)
        if np.mean(prediction[0][1]) >= replay_threshold:
            return 'FAKE'
        return 'REAL'

    def getEmbeddings(self, img):
        img_ycrcb = cv2.cvtColor(img, cv2.COLOR_BGR2YCR_CB)
        img_luv = cv2.cvtColor(img, cv2.COLOR_BGR2LUV)
        hist_ycrcb = self.calcHist(img_ycrcb)
        hist_luv = self.calcHist(img_luv)
        feature_vector = np.append(hist_ycrcb.ravel(), hist_luv.ravel())
        return feature_vector.reshape(1, len(feature_vector))

    def calcHist(self, img):
        histogram = [0] * 3
        for j in range(3):
            histr = cv2.calcHist([img], [j], None, [256], [0, 256])
            histr *= 255.0 / histr.max()
            histogram[j] = histr
        return np.array(histogram)
