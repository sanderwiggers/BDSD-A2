# BDSD Assignment 2: Letterfrequenties
## Bewijs van de werking

De output van de totale code die we runnen op het door de HU gegeven bestand: </br>
![](Screenshots/screenshot2.png)

De code werkt goed als er 73 Nederlandstalige en 119 Engelstalige regels worden herkend. </br>
Zoals in de screenshot hierboven te zien is, herkent het programma **81 Nederlandstalige en 111 Engelstalige** regels.

Dit betekent dat: </br>
Van de in totaal 192 regels er 8 foutief worden herkend. </br>
Ofterwijl: 184 regels van de 192 worden juist herkend. </br>

184 / 192 * 100% = een accuratie van **95.83%**


De output van het runnen van de code van de JAR: </br>
![](Screenshots/screenshot1.png)

Zoals in de afbeelding hierboven te zien is, draait de code zonder complicaties. </br>

___
## Instructies

Om ons algoritme te laten werken hebben wij gebruik gemaakt van een matrix voor de Nederlandse taal en een matrix voor de Engelse taal. Deze staan als .txt bestand opgeslagen op ons hdfs systeem en worden tijdens de mapper ingelezen en vergeleken met de letter combinaties frequenties.

Met de GenerateMatrix class in onze repo kun je een string meegeven en aan de hand daarvan maakt hij een .txt bestand waarin de matrix staat. Deze matrixen gebruiken wij om de letter combinaties frequenties van een regel tekst mee te vergelijken.

Onze hdfs root url staat op `"hdfs://quickstart.cloudera:8020/"` en de Engelse matrix is dan te bereiken op `"hdfs://quickstart.cloudera:8020/engels/engels.txt"` en de Nederlandse matrix op `"hdfs://quickstart.cloudera:8020/nederlands/nederlands.txt"`. Om de jar zonder problemen uit te kunnen voeren zul je deze paths aan moeten passen.

We hebben de tekst die geanalyseerd moet worden door het algoritme op ons hdfs systeem staan in de folder `/analyse/analyseText.text` hierdoor kunnen wij onze jar op de volgende manier aanroepen.

`$ hadoop jar /home/cloudera/compareNLEN.jar CompareNLEN /analyse/analyseText.txt /output_new48`

Hierin geldt dat `/analyse/analyseText.txt` de input is en `/output_new48` de output folder is.

De uitkomst van de ons algoritme kunnen we dan uitlezen door het volgende commando.

`$ hdfs dfs -cat /output_new48/part-r-00000`
