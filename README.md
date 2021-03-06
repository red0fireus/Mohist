<img src="https://i.loli.net/2020/02/28/vZRHJACadF7rgn5.png">

## Mohist-1.15.2(In development)

[![](https://ci.codemc.org/buildStatus/icon?job=Mohist-Community%2FMohist-1.15.2)](https://ci.codemc.org/job/Mohist-Community/job/Mohist-1.15.2/)
![](https://img.shields.io/github/stars/Mohist-Community/Mohist.svg?label=Stars)
![](https://img.shields.io/github/license/Mohist-Community/Mohist.svg)
[![](https://img.shields.io/badge/Paper-1.15.2-brightgreen.svg?colorB=DC3340)](https://papermc.io/downloads#Paper-1.15.2)
![](https://img.shields.io/badge/OracleJdk-8u251-brightgreen.svg?colorB=469C00)
![](https://img.shields.io/badge/Gradle-4.10.3-brightgreen.svg?colorB=469C00)

### Getting Help
   [**Home**](https://mohist.red/)
   [**bStats**](https://bstats.org/plugin/bukkit/Mohist)
   [**QQ**](https://jq.qq.com/?_wv=1027&k=5YIRYnH)  
   
Download
------
 * [**Jenkins**](https://ci.codemc.org/job/Mohist-Community/job/Mohist-1.15.2/)
   
Install
------
 1. This software requires Java 8.  
 2. [Download Installer](https://ci.codemc.org/job/Mohist-Community/job/Mohist-1.15.2/) and [Download libraries](https://github.com/Mohist-Community/Mohist/raw/1.15.2/libraries.zip).  
 3. Unzip libraries to a new directory  
 4. Like installing forge, run Installer and select server and select the directory where the libraries are located  
 5. Now launch it using the `java` command:
    ```sh
    java -jar yourJar.jar
    ```
   
Building
------
* Checkout project
  * You can use IDE or clone from console:
  `git clone -b 1.15.2 https://github.com/Mohist-Community/Mohist.git`
* Setup
  * Build with Linux:
  `bash gradlew setup`
  * Build with Windows:
  `gradlew.bat setup `
* Building
  * Build with Linux:
  `bash gradlew setup installerJar`
  * Build with Windows:
  `gradlew.bat setup installerJar `

All builds will be in `.projects\forge\build\libs\` 

Thanks for the following project
------
* [**Bukkit**](https://hub.spigotmc.org/stash/scm/spigot/bukkit.git) - plugin support.
* [**Paper**](https://github.com/PaperMC/Paper.git) - performance optimizations.
* [**CraftBukkit**](https://hub.spigotmc.org/stash/scm/spigot/craftbukkit.git) - plugin support.
* [**Spigot**](https://hub.spigotmc.org/stash/scm/spigot/spigot.git) - plugin support.
* [**MinecraftForge**](https://github.com/MinecraftForge/MinecraftForge.git) - mod support.

Special Thanks To:
-------------
<a href="https://serverjars.com/"><img src="https://serverjars.com/assets/img/logo_white.svg" width="200"></a>
<a href="https://ci.codemc.io/"><img src="https://i.loli.net/2020/03/11/YNicj3PLkU5BZJT.png" width="200"></a>

![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](http://www.yourkit.com/), makers of the outstanding java profiler, support open source projects of all kinds with their full featured [Java](https://www.yourkit.com/java/profiler/index.jsp) and [.NET](https://www.yourkit.com/.net/profiler/index.jsp) application profilers. We thank them for granting Mohist an OSS license so that we can make our software the best it can be.

