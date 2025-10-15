# SmartSeat

**Projektteam:**  
Daniel Riemer, Joschua Auer, Nino Tesar, Stefan Meinhart  
**Klasse:** 4BHITM – Schuljahr 2025/26  

---

## Projektübersicht

### Projektbezeichnung
**SmartSeat**

### Projektauftraggeber
- Dietmar Steiner  
- Christian Aberger  
- Dejan Sivak  

---

## Projekthintergrund und Motivation

Lernkojen sind fast durchgehend belegt, jedoch ist für Schüler auf den ersten Blick nicht ersichtlich, welche Koje aktuell frei ist.  
Die Suche nach einem freien Platz kann bis zu 10 Minuten dauern – Zeit, die verloren geht, da man bald wieder in den Unterricht zurückkehrt.

Auch für Lehrkräfte entstehen Probleme:
- Kommunikation mit Schülern ist oft umständlich.  
- Informationen oder organisatorische Hinweise müssen mehrfach einzeln übermittelt werden.  
- Das führt zu Unübersichtlichkeit, Missverständnissen und Frustration.

Das Projekt kann künftig auch für andere Orte wie Kindergärten, Kantinen oder allgemeine Platzverwaltungssysteme eingesetzt werden.

---

## Projektziele und Meilensteine

### 4. Klasse – 1. Semester
1. Web-App-Layout in Figma erstellt  
2. Grundstruktur der Web-App  
3. REST API aufgesetzt  
4. Datenbank aufgesetzt  
5. Raspberry-Pi-Kommunikation aufgebaut  

### 4. Klasse – 2. Semester
1. Karte in die App eingebunden  
2. App-Layout verfeinert  
3. Login implementiert  
4. Lehrerverwaltung eingebunden  

---

## Zielgruppe

- Schülerinnen und Schüler  
- Lehrkräfte  

---

## Technologie-Stack

### Software
- Backend: Quarkus  
- Datenbank: H2  
- Frontend: HTML, CSS, JavaScript (optional: Angular oder Vue)  
- Containerisierung: Docker  
- Microcontroller: MicroPython / C  

### Hardware
- Raspberry Pi Pico 2WH  
- Physischer Knopf  
- Ultraschallsensoren  

---

## SWOT-Analyse

| Kategorie | Punkte |
|------------|---------|
| **Strengths** | Gute Kenntnisse in SQL, HTML, CSS und JS |
| **Weaknesses** | Angular wird erst später gelernt; Quarkus ist noch neu; Datenübertragung zwischen Raspberry Pi, Quarkus und Datenbank kann komplex sein |
| **Opportunities** | Erweiterbar für viele andere Bereiche (z. B. Kindergärten, Kantinen, Raumbelegung) |
| **Threats** | Raspberrys könnten durch unsachgemäße Nutzung beschädigt werden |

---

## Sinn, Zweck und USP

### Ziel des Endprodukts
Eine Web-App, über die man sehen kann:
- Welche Lernkojen frei sind  
- Wo sich diese befinden  

In jedem Klassenraum hängt ein QR-Code, der direkt zur Website führt.

### Funktionen
- **Für Schüler:**  
  Nach dem Scannen sehen sie sofort freie Lernkojen.  
- **Für Lehrkräfte:**  
  Sie sehen, wo Schüler sitzen, und können Nachrichten an einzelne Kojen oder an die ganze Klasse senden.

### USP (Unique Selling Proposition)
Unsere Web-App macht freie Lernkojen auf einen Blick sichtbar, ermöglicht Schülern eine schnelle Kojensuche per QR-Code und bietet Lehrkräften gleichzeitig einen Überblick sowie eine direkte Kommunikationsmöglichkeit mit einzelnen Kojen oder der gesamten Klasse.

---

## Projektorganisation

| Name | Verantwortungsbereich |
|------|------------------------|
| Nino Tesar | Backend |
| Daniel Riemer | Hauptsächlich Frontend |
| Joschua Auer | Hauptsächlich Frontend |
| Stefan Meinhart | Hardware |

---

© 2025 – SmartSeat Projektteam, 4BHITM