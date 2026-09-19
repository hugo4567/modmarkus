from PIL import Image
import os

img_path = "src/main/resources/assets/modmarkus/textures/image.png"
img = Image.open(img_path)
print(f"Size: {img.size}")
print(f"Mode: {img.mode}")
file_size = os.path.getsize(img_path)
print(f"File size: {file_size} bytes")

if img.mode == "RGBA":
    alpha = img.split()[3]
    extrema = alpha.getextrema()
    print(f"Alpha extrema: {extrema}")
    if extrema == (0, 0):
        print("WARNING: IMAGE TOTALEMENT TRANSPARENTE!")
    else:
        print(f"✓ Image has varying alpha: min={extrema[0]}, max={extrema[1]}")
else:
    print(f"Image mode: {img.mode}")
    pixels = img.getextrema()
    print(f"Pixel values: {pixels}")
