#!/bin/bash

mvn clean package &&

echo "Coping files to remote server..." &&

scp -rp ./target/juice-up-0.0.1-SNAPSHOT.jar root@92.53.115.123:/apps/juice_up/backend/target &&

echo "Deploy finish"