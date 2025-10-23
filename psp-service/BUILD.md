# Инструкция по сборке PSP Service

## Требования

- Java 21 или выше
- Gradle 8.x (опционально, можно использовать wrapper)

## Установка Gradle Wrapper (первый раз)

Если у вас установлен Gradle:

```bash
cd psp-service
gradle wrapper --gradle-version 8.11.1
```

Или скачать wrapper вручную:

```bash
# Download gradle wrapper files
curl -L https://services.gradle.org/distributions/gradle-8.11.1-bin.zip -o gradle.zip
unzip gradle.zip
./gradle-8.11.1/bin/gradle wrapper
rm -rf gradle-8.11.1 gradle.zip
```

## Сборка проекта

С wrapper (рекомендуется):

```bash
./gradlew clean build
```

Или напрямую через Gradle:

```bash
gradle clean build
```

## Запуск приложения

```bash
./gradlew bootRun
```

Или:

```bash
java -jar build/libs/psp-service-0.0.1-SNAPSHOT.jar
```

## Пропуск тестов

```bash
./gradlew clean build -x test
```

## Проверка кода

```bash
# Compile only
./gradlew compileJava

# Check style
./gradlew check
```

## Альтернатива: Использование Maven

Если предпочитаете Maven, создайте `pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
    </parent>
    
    <groupId>kg.demirbank</groupId>
    <artifactId>psp-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>psp-service</name>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

Затем:

```bash
mvn clean package
mvn spring-boot:run
```

## Проверка Java версии

```bash
java -version
```

Должно быть Java 21 или выше.

## Troubleshooting

### "gradle: command not found"

Установите Gradle:
- macOS: `brew install gradle`
- Linux: `sdk install gradle 8.11.1` (через SDKMAN)
- Windows: Скачайте с https://gradle.org/releases/

### "JAVA_HOME not set"

Установите переменную окружения:

```bash
# macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Добавьте в ~/.zshrc или ~/.bashrc для постоянного использования
```

### Ошибки компиляции

Убедитесь, что все зависимости скачаны:

```bash
./gradlew --refresh-dependencies
```

