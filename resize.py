from PIL import Image
import os

folder = "screenshots"
for filename in os.listdir(folder):
    if filename.endswith(".png") or filename.endswith(".jpg"):
        path = os.path.join(folder, filename)
        img = Image.open(path)
        # Максимум 400px ені
        img.thumbnail((400, 800))
        img.save(path)
        print(f"✅ {filename} кішірейтілді")