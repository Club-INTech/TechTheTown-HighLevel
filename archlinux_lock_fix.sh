#!/bin/bash

echo "Ce fix utilise sudo, vous devrez entrer votre mot de passe."

cd /var
sudo chmod -R 777 lock
cd lock
sudo chmod -R 777 *

echo "Permissions réglèes, veuillez retenter de lancer votre JUnit"
