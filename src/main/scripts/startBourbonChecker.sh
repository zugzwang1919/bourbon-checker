/!bin/bash
# This script should be typically started with
# nohup ./startBourbonChecker.sh > /dev/null 2>&1 &
java -Xms128m -Xmx1g -cp bourbon-checker-1.0.jar:lib/* com.wolfesoftware.bourbonchecker.BourbonCheckerApplication

