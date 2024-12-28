# Bildbearbeitung

Dieses Projekt ist eine Java-Anwendung zur Bildbearbeitung. Es bietet verschiedene Funktionen zur Manipulation und Verarbeitung von Bildern, einschließlich Farbkorrektur, Filteranwendung und Bildtransformationen.

## Projektstruktur

## Verzeichnisübersicht

- [`.idea`](.idea ): Enthält Projektkonfigurationsdateien für die IDE.
- [`Bildbearbeitung.iml`](Bildbearbeitung.iml ): Moduldatei für das Projekt.
- [`mysql-connector-j-9.0.0.jar`](mysql-connector-j-9.0.0.jar ): MySQL-Connector-Bibliothek.
- [`Pictures`](Pictures ): Verzeichnis für Bilddateien.
- [`README.md`](README.md ): Diese Datei.
- [`src`](src ): Quellcode des Projekts.

## Hauptklassen und -dateien

### JpgPng

- [`CornerPoints`](src/JpgPng/CornerPoints.java ): Findet Eckpunkte in einem Bild.
- [`DYCanvas`](src/JpgPng/DYCanvas.java ): Methoden zur Farbmischung und Bildmanipulation.
- [`DYColor`](src/JpgPng/DYColor.java ): Farbmanipulationen.
- [`DYMosaic`](src/JpgPng/DYMosaic.java ): Erzeugt Mosaikbilder.
- [`ImageFX`](src/JpgPng/ImageFX.java ): Verschiedene Bildbearbeitungseffekte.
- [`MyImage`](src/JpgPng/MyImage.java ): Erzeugt [`BufferedImage`](src/JpgPng/ImageFX.java )-Objekte.
- [`Threshold`](src/JpgPng/Threshold.java ): Schwellenwertoperationen auf Bildern.

### Test

- [`AbdunkelnZweiEbenen`](src/Test/AbdunkelnZweiEbenen.java ): Beispiel für das Abdunkeln von Bildern.
- [`AdobeRGBToSRGBConverter`](src/Test/AdobeRGBToSRGBConverter.java ): Konvertiert Bilder von Adobe RGB zu sRGB.
- [`AufhellenZweiEbenen`](src/Test/AufhellenZweiEbenen.java ): Beispiel für das Aufhellen von Bildern.
- [`DynamikAdjustment`](src/Test/DynamikAdjustment.java ): Dynamikanpassung von Bildern.
- [`FrequencySeparation`](src/Test/FrequencySeparation.java ): Frequenztrennung von Bildern.
- [`ImageProcessingGUI`](src/Test/ImageProcessingGUI.java ): GUI für die Bildbearbeitung.
- [`PhotoshopAutomation`](src/Test/PhotoshopAutomation.java ): Automatisierung von Photoshop-Aufgaben.
- [`Sepia`](src/Test/Sepia.java ): Anwendung eines Sepia-Filters auf Bilder.
- [`TiffMetdataTMySQL`](src/Test/TiffMetdataTMySQL.java ): Speichert TIFF-Metadaten in einer MySQL-Datenbank.
- [`WhiteBalance2`](src/Test/WhiteBalance2.java ): Weißabgleich von Bildern.

## Abhängigkeiten

- [`dyimagefx`](src/JpgPng/CornerPoints.java ): Eine externe Bibliothek für Bildverarbeitungsfunktionen.
- [`mysql-connector-j-9.0.0.jar`](mysql-connector-j-9.0.0.jar ): MySQL-Connector für die Datenbankanbindung.

## Lizenz

Dieses Projekt steht unter der MIT-Lizenz. Weitere Informationen finden Sie in der Datei `LICENSE`.

## Autor

Yusuf Shakeel

## Weitere Informationen

Weitere Informationen und Beispiele finden Sie auf der [GitHub-Seite des Projekts](https://www.github.com/yusufshakeel/Java-Image-Processing-Project).