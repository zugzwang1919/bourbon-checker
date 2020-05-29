/!bin/bash
# This script should be typically started with
# nohup ./startBourbonChecker.sh > bourbon-checker.out 2>&1 &
java -cp bourbon-checker-1.0.jar:lib/* com.wolfesoftware.bourbonchecker.BourbonCheckerApplication

