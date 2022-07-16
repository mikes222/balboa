# balboa

Balboa Spa Whirlpool control API

A java program to get and control the most important settings for Balboa SPA Whirlpools. 

This program has NO UI and is intended to be used by smarthomes (I use it with HomeMatic) to quickly set and control the most important things. 

## Compilation

As usual for java download maven and compile the code with

    mvn install
    
## Executing

Start the program by installing Java Runtime Environment (JRE) at your machine and execute

    java.exe -jar balboa-0.0.1-SNAPSHOT.jar
    
For example to switch the light on execute the following:

    java.exe -jar balboa-0.0.1-SNAPSHOT.jar --lights ON
    
Type ``-h`` for help

In my case I have copied and renamed to jar file to /srv/balboa.jar at the CCU3 (HomeMatic) and execute it for example with the following script


    string cmd="/opt/java-azul/bin/java -jar /run/balboa.jar --settemp 35 --connect 10.10.20.43";

    WriteLine("" # cmd # ".");
    dom.GetObject ("CUxD.CUX2801002:3.CMD_SETS").State (cmd);
    dom.GetObject ("CUxD.CUX2801002:3.CMD_QUERY_RET").State(1);
    string x = dom.GetObject ("CUxD.CUX2801002:3.CMD_RETS").State();
    WriteLine("Result from Balboa: " # x # ".");
    
To read the temperatures I execute

    string cmd="/opt/java-azul/bin/java -jar /run/balboa.jar --temperatures PRINT --connect 10.10.20.43";
    
    WriteLine("" # cmd # ".");
    dom.GetObject ("CUxD.CUX2801002:3.CMD_SETS").State (cmd);
    dom.GetObject ("CUxD.CUX2801002:3.CMD_QUERY_RET").State(1);
    string x = dom.GetObject ("CUxD.CUX2801002:3.CMD_RETS").State();
    WriteLine("Result from Balboa: " # x # ".");
    
    if (x != "") {
        string var0 = x.StrValueByIndex(",", 0);
        string var1 = x.StrValueByIndex(",", 1);
        WriteLine(var0 # " , " # var1);
    
        if (var0 != "null") {
            dom.GetObject("Balboa Current Temp").State(var0);
        }
        if (var1 != "null") {
            dom.GetObject("Balboa Set Temp").State(var1);
        }
    }


## Contribution

Feel free to enhance the code and contribute to this repository. 

## See also

https://github.com/ccutrer/balboa_worldwide_app/blob/main/doc/protocol.md

https://github.com/ccutrer/balboa_worldwide_app/wiki

https://github.com/vincedarley/homebridge-plugin-bwaspa/blob/master/src/spaClient.ts

