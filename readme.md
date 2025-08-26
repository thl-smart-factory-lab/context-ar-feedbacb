# PrinterStatus Glass (Android Reference Implementation)

This repository contains the Android reference implementation accompanying the paper:

**“Step Into Context: Location-Aware AR Feedback for Smart Manufacturing Using Low-Cost Hardware”**  
Proceedings of the *International Conference on Indoor Positioning and Indoor Navigation (IPIN 2025)*.

---

## Overview
PrinterStatus Glass demonstrates how 3D printer status information can be streamed via MQTT and displayed directly on smart glasses.  
It reproduces the core results presented in the paper for context-aware AR feedback in a smart manufacturing setting.

Key features:
- MQTT client for subscribing to printer status topics  
- Parsing and structuring of printer state data  
- Real-time display on wearable device (tested with Google Glass EE2)  
- Modular components (`DataHolder`, `DataParser`, `MqttHandler`, `PrinterTopicSelector`)  
- Includes unit tests for core logic

---

## Requirements
- Android Studio Hedgehog (or newer)  
- Android device running Android 8.0 (API 26) or higher  
- MQTT broker and a 3D printer publishing status topics  

---

## Build & Run
1. Clone this repository:
   ```bash
   git clone https://github.com/USERNAME/PrinterStatus_Glass.git
   
2. Open in Android Studio.

3. Let Gradle sync dependencies.

4. Run the app module on a connected device or emulator.
   For Glass EE2: ensure developer mode is enabled and the device is paired.


## Citation
If you use this implementation in academic work, please cite:

```bibtex
@inproceedings{pelka2025context,
  title     = {Step Into Context: Location-Aware AR Feedback for Smart Manufacturing Using Low-Cost Hardware},
  author    = {Pelka, Mathias and Willemsen, Thomas},
  booktitle = {Proceedings of the International Conference on Indoor Positioning and Indoor Navigation (IPIN)},
  year      = {2025},
  note      = {Code: https://github.com/thl-smart-factory-lab/context-ar-feedback, Release v1.0.0}
}
