#!/usr/bin/env python3
from PIL import Image

# Charger l'image actuelle
img = Image.open('src/main/resources/assets/modmarkus/textures/image.png')
print(f"Taille originale: {img.size}")

# Redimensionner à 512x256
new_size = (512, 256)
resized = img.resize(new_size, Image.Resampling.LANCZOS)

# Sauvegarder
resized.save('src/main/resources/assets/modmarkus/textures/image.png')
print(f"Redimensionnée à: {resized.size}")
print("✓ Fait!")
