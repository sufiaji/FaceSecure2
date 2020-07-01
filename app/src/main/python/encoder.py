import face_recognition
import numpy as np
import io
import base64
from PIL import Image

def encode(string64):
    try:
        imagedata = base64.b64decode(string64)
        image = Image.open(io.BytesIO(imagedata))
        array_np = np.array(image)
        h = array_np.shape[0]-1
        w = array_np.shape[1]-1
        face_encoding = face_recognition.face_encodings(array_np, known_face_locations=[[0,w-1,h-1,0]])[0]
        return face_encoding
    except Exception as ex:
        return [0]

def encode2(thumb):
    try:
        imagedata = base64.b64decode(thumb)
        image = Image.open(io.BytesIO(imagedata))
        array_np = np.array(image)
        h = array_np.shape[0]-1
        w = array_np.shape[1]-1
        enc = face_recognition.face_encodings(array_np, known_face_locations=[[0,w-1,h-1,0]])[0]
        str_ret = ""
        for e in enc:
            str_ret = str_ret + str(e) + "  |  "
        return str_ret
    except Exception as ex:
        return str(ex)