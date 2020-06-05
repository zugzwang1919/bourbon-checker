/!bin/bash
# This script should be typically started with
# nohup ./startBourbonChecker.sh > /dev/null 2>&1 &
# It seems like it's also important to exit the terminal session immediately (before ChromeDriver starts)
# otherwise, when the session closes, it seems like the chrome processes are killed when the session closes
java -Xms128m -Xmx1g -cp bourbon-checker-1.0.jar:lib/* com.wolfesoftware.bourbonchecker.BourbonCheckerApplication

