# OpenCVServer
Written in java graphical application. It used to show user received images from raspberry pi.

## Requires
    JRE >= 1.8 (run application only)
    JDK >= 1.8 (build and run application)
    Graphical envirenment

## How to build
    dialight@watermelon:~$ git clone https://github.com/DiaLight/OpenCVServer.git
    dialight@watermelon:~$ cd OpenCVServer/
    dialight@watermelon:~/OpenCVServer$ ./gradlew build
    
## Usage
    dialight@watermelon:~/OpenCVServer$ java -jar build/libs/opencvserver-1.0-SNAPSHOT.jar <port>

## Setup your IDE

This application I develop in Intellij Idea Community Edition.

### Intellij Idea
* Select `Import Project` or open `File/New/Project from Existing Sources...`
* Select `Import project from external model`, then select `Gradle`
* `Next`
* Remove selection from `Create separate module per source set`
* Set your `Gradle JVM` to **JDK** (Gradle requires `javac` in JDK to build java)
* `Finish`
* Main class where application starts from is `stud.opencv.server.MainFrame`
