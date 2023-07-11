#!/usr/bin/env bash

/opt/jdk-11-shenandoah/jdk/bin/java $@  -server -Xms1g -Xmx1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=1g -XX:SurvivorRatio=8 -XX:TargetSurvivorRatio=90 -XX:MinHeapFreeRatio=40 -XX:MaxHeapFreeRatio=90 -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -XX:MaxGCPauseMillis=10 -XX:InitiatingHeapOccupancyPercent=30 -XX:+UseTLAB -XX:CompileThreshold=100 -XX:ThreadStackSize=4096 -XX:MaxTenuringThreshold=5 -XX:ReservedCodeCacheSize=512m -XX:+UseStringDeduplication -XX:StringDeduplicationAgeThreshold=3 -XX:MaxInlineSize=200 -XX:MaxTrivialSize=12 -jar agent.jar
