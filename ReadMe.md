# IngenicoWebTesting
Simple test case to cover API testing based on restAssured framework and UI testing based on selenium framework.

## Execution steps:
```bash
cd IngenicoWebTesting
mvn install
mvn test
```

## Headless mode:
```bash
cd IngenicoWebTesting
apt-get install -y xvfb
xvfb-run -a mvn test
```

## Test Result:
```
Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 14.837 s
[INFO] Finished at: 2018-12-15T03:33:41+02:00
[INFO] Final Memory: 11M/40M
[INFO] ------------------------------------------------------------------------
```
