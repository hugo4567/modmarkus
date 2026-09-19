#!/usr/bin/env python3
import os
import sys
from PIL import Image
import subprocess

# Chemins
workspace = r"c:\Users\hugo4\Documents\ProjetsH\modmarkus"
jpg_file = os.path.join(workspace, "mkus.jpg")
mp3_file = os.path.join(workspace, "banjo.mp3")

png_dest = os.path.join(workspace, r"src\main\resources\assets\modmarkus\textures\image.png")
ogg_dest = os.path.join(workspace, r"src\main\resources\assets\modmarkus\sounds\music\banjo.ogg")

# Convertir JPG en PNG
try:
    print("Conversion JPG -> PNG...")
    img = Image.open(jpg_file)
    img = img.convert('RGBA')
    img.save(png_dest)
    print(f"✓ Image sauvegardée: {png_dest}")
except Exception as e:
    print(f"✗ Erreur conversion image: {e}")
    sys.exit(1)

# Convertir MP3 en OGG
try:
    print("Conversion MP3 -> OGG...")
    # Essayer avec ffmpeg
    cmd = [
        "ffmpeg",
        "-i", mp3_file,
        "-q:a", "5",
        ogg_dest,
        "-y"
    ]
    subprocess.run(cmd, check=True, capture_output=True)
    print(f"✓ Audio sauvegardée: {ogg_dest}")
except FileNotFoundError:
    print("FFmpeg non trouvé, essai avec pydub...")
    try:
        from pydub import AudioSegment
        print("Conversion avec pydub...")
        audio = AudioSegment.from_mp3(mp3_file)
        audio.export(ogg_dest, format="ogg")
        print(f"✓ Audio sauvegardée: {ogg_dest}")
    except Exception as e2:
        print(f"✗ Erreur conversion audio: {e2}")
        print("Essai installation pydub et ffmpeg-python...")
        os.system("pip install pydub ffmpeg-python")
        sys.exit(1)
except Exception as e:
    print(f"✗ Erreur conversion audio: {e}")
    sys.exit(1)

print("\n✓ Conversion terminée!")
