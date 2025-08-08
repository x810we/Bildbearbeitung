# Dokumentation: ImageBlenderGUI

## Übersicht

Die Klasse `ImageBlenderGUI` stellt eine grafische Benutzeroberfläche (GUI) bereit, mit der zwei Bilder geladen, überblendet und das Ergebnis gespeichert werden kann.

## Hauptfunktionen

- **Bild 1 laden:** Öffnet einen Dialog zum Laden des ersten Bildes.
- **Bild 2 laden:** Öffnet einen Dialog zum Laden des zweiten Bildes.
- **Opacity-Slider:** Ermöglicht die Einstellung des Überblendungsgrades zwischen den beiden Bildern.
- **Bilder mischen:** Überblendet die beiden geladenen Bilder entsprechend dem eingestellten Opacity-Wert.
- **Bild speichern:** Speichert das gemischte Bild als PNG-Datei.

## Bedienung

1. Klicke auf „Bild 1 laden“ und wähle ein Bild aus.
2. Klicke auf „Bild 2 laden“ und wähle ein weiteres Bild aus.
3. Stelle mit dem Slider die gewünschte Überblendung ein.
4. Klicke auf „Bilder mischen“, um das Ergebnis anzuzeigen.
5. Klicke auf „Bild speichern“, um das gemischte Bild zu speichern.

## Technische Hinweise

- Unterstützte Bildformate: PNG, JPG, JPEG
- Die Bilder werden auf die kleinste gemeinsame Größe zugeschnitten.
- Die Überblendung erfolgt pixelweise unter Berücksichtigung des Alpha-Kanals.

## Autor

*Automatisch generiert*

