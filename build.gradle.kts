plugins {
    id("java")
    id("application")
}

group = "com.viacheslav"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.7.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("com.google.code.gson:gson:2.10.1")
}


application {
    mainClass.set("com.viacheslav.MainApp")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    register<JavaExec>("runProducer") {
        group = "application"
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("com.viacheslav.producer.WeatherProducer")
    }

    register<JavaExec>("runConsumer") {
        group = "application"
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("com.viacheslav.consumer.WeatherConsumer")
    }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}