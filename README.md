![alt text][logo]

[logo]: http://bit.ly/Sky40Email "(c)2019 by Sky4.0 GmbH, www.sky40.de"
###### Dieser Quellcode ist urheberrechtlich geschützt und Eigentum der Sky4.0 GmbH, auch wenn er hier bei GitHub liegt. Wir erlauben den Download ausdrücklich zur eigenen Benutzung und Veränderung für Lern- und Demonstrationszwecke. Weitergabe nur unter Nennung der Quelle "(c)2019 by Sky4.0 GmbH". Lizensierung, Verkauf oder Verwendung zum gewerblichem Zweck nur mit unserer ausdrücklichen schriftlichen Genehmigung.

OracleNotificationService
============================
Demonstration-Project OracleNotificationService (ONS)

Abstrakt/Anriss:
---------------
Dieses Projekt besteht aus zwei Anwendungen: 

Einem Service namens `OracleNotificationService`, der die Veränderung von Tabellen einer *Oracle 10g Datenbank* (oder höher) überwacht und bei Änderungen RESTful diese als Nachrichten sendet. 

Einem Service `OracleNotificationConsumer` der Nachrichten über Änderungen (ebenfalls RESTful) konsumiert, d.h. empfängt und anzeigt. 

Ziel:
---------------------------------
Ziel des Projektes ist es, zu zeigen dass man auch ohne Polling beliebige Tabellen der Oracle DB auf Änderungen überwachen kann, um diese Änderungen ad-hoc als Nachrichten (z.B. auf einem *Enterprise Service Bus*) in der Service-Landschaft zu verteilen.   

