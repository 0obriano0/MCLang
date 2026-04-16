# REQUIRED ASSETS (Minecraft UI)

To complete the Minecraft UI look for the MCLang Web Portal, you **must download** and place the following image files into this `img` folder. The filenames must match exactly.

## 1. Backgrounds & Textures

> [!TIP]
> You can find many of these by searching for "Minecraft GUI pack" or taking screenshots in-game.

- `library_bg_blur.jpg`
  - **Description**: A blurred background image of a Minecraft library or bookshelf structure.
  - **Size**: 1920x1080 (Recommended)
- `dark_oak_planks.png`
  - **Description**: The square pixel texture for Dark Oak Planks, used as the repeating background for the sidebar.
  - **Size**: 16x16 or 32x32 pixels.
- `cobblestone.png`
  - **Description**: The square pixel texture for Cobblestone, used for the border separating the sidebar from the main content.
  - **Size**: 16x16 or 32x32 pixels.
- `button_normal.png`
  - **Description**: The texture for a standard Minecraft wooden or stone button.
  - **Size**: 200x20 pixels (Typical Minecraft UI ratio) or dynamically scaled pixel art.
- `open_book_gui.png`
  - **Description**: The large open book interface (like the Book and Quill GUI). The UI will place the text *inside* this book image.
  - **Size**: Around 256x256 or higher (Make sure the aspect ratio leaves room for margins).

## 2. Icon Set

> [!TIP]
> You can download these exact icons from the [Minecraft Wiki (Items)](https://minecraft.wiki/w/Item) by saving the images.

- `icon_book.png` (Standard Book item icon)
- `icon_key.png` (Tripwire Hook or nametag, or a custom key icon if you prefer)
- `icon_rocket.png` (Firework Rocket item icon)
- `icon_box.png` (Chest, Barrel, or Planks block icon)
- `icon_paper.png` (Paper item icon)
- `icon_potion.png` (Any Potion item icon, e.g. Glass Bottle or Potion of experience)

## Note on Resolution
The CSS uses `image-rendering: pixelated;`. This means you should prioritize getting small, accurate pixel-art (like 16x16 sprites) over high-resolution filtered images. They will be scaled up cleanly and accurately.
