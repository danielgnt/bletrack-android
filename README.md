# bletrack-android
Android Prototype of "Tracing Contacts to Control the COVID-19 Pandemic" ( https://arxiv.org/abs/2004.00517 )

This app sends a beacon with a specified service uuid and upon connection from another device ( this does not require pairing and therefore no user interaction) it will share its unique user id. Furthermore the app also monitors for other beacons transmitting the specified service uuid. When a foreign beacon is found the app will connect with that device and query its unique id. After receiving this id it is added to a list. The app works Android <--> iOS and Android <--> Android

### Import project ( You will need Android Studio )
This repository only contains the source, so you will need to create a project in Android Studio yourself.
Then copy in the soucre code, res folder and the AndroidManifest.xml
