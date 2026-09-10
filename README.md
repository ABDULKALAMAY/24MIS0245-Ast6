# Real-Time Emergency Ambulance Dispatch and Tracking System

Java 17 Maven application for prioritised emergency dispatch, ambulance state tracking, queue management, ETA calculation, and emergency history.

## Verify locally

```text
mvn clean test
mvn clean package
```

The Jenkins pipeline expects globally configured tools named `Maven3` and `JDK17`. On a Windows Jenkins agent, the committed `Jenkinsfile` uses `bat` steps.

## Build Status
Verified locally with `mvn test` (all 6 tests passing) and integrated with Jenkins CI/CD.