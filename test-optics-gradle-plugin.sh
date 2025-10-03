./gradlew :arrow-optics-plugin:publish :arrow-optics-ksp-plugin:publish :arrow-annotations:publish :arrow-optics:publish :arrow-core:publish :arrow-atomic:publish -PonlyLocal=true -Pversion=10.0-test
cd gradle-test
cd multiplatform
./gradlew build
cd ..
cd jvmOnly
./gradlew build
cd ..
cd ..